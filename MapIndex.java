package yardSaleApp;

public class MapIndex {

		'use strict';
		const tress = require('tress');
		const request = require('request');
		const NodeGeocoder = require('node-geocoder');
		const cheerio = require('cheerio');

		function _getLocation(location){
		    return new Promise(function(resolve, reject){

		        if(!OfferUp.googleApi){
		            reject(new Error('\'Google Map Api Key\' is not defined. Please use method setGoogleMapApi to defined it'));
		        }
		        if(!location){
		            reject(new Error('\'location\' parameter is required'));
		        }
		        if(!(typeof location == 'string')){
		            reject(new Error('\'location\' parameter should be \'string\''));
		        }


		        var options = {
		            provider: 'google',

		            httpAdapter: 'https',
		            apiKey: OfferUp.googleApi,
		            formatter: null
		        };

		        var geocoder = NodeGeocoder(options);

		        geocoder.geocode(location, function(err, res) {
		            if(err){
		                throw new Error(err);
		            }else{

		                if(res.length && res[0].latitude && res[0].longitude){
		                    var result = {};
		                    result.success = true;
		                    result.data = {
		                        latitude: res[0].latitude,
		                        longitude: res[0].longitude
		                    }
		                    resolve(result);
		                }else{
		                    reject(new Error('\'location\' is not defined'));
		                }
		                
		            }
		        });
		    });
		}

		function _clear(text, option, replace){
		    if(!text){
		        return '';
		    }
		    if(!(typeof text == 'string')){
		        text = text.toString();
		    }
		    if(option){
		        if(typeof option == 'string'){
		            if(replace){
		                text = text.replace(option, replace);
		            }else{
		                text = text.replace(option, '');
		            }
		        }

		        if(Array.isArray(option)){
		            for(var i=0;i<option.length;i++){
		                text = text.replace(option[i], '');
		            }
		        }
		        
		    }
		    text = text.replace(/\s\s+/g, ' ');
		    text = text.replace(/\n/g, '');
		    text = text.replace(/^\s/g, '');
		    text = text.replace(/\s$/g, '');
		    return text;
		}

		function getItemInformation(id, resolve, reject){
		    if(!id){
		        reject(new Error('id is not defined'));
		    }
		    if(!(typeof id == 'string')){
		        reject(new Error('id should be string'));
		    }
		    id = +id;
		    if(!(typeof id == 'number')){
		        reject(new Error('id is not found'));
		    }
		    var url = 'https://offerup.com/item/detail/' + id + '/';
		    request({
		        url: url
		    }, function(error, response){
		        if(!error && response.statusCode == 200){
		            var $ = cheerio.load(response.body, {
		                normalizeWhitespace: true
		            });
		            var title_box = $('.box-main-alt').children().eq(2).children().eq(1).children();
		            var desc_box = $('.box-main-alt').children().eq(4).find('div');

		            resolve({
		                success: "success",
		                data: {
		                    title: title_box.eq(0).html(),
		                    location: title_box.eq(1).children().last().html(),
		                    posted_ago: _clear(title_box.eq(2).html(), '&#xA0;', ' '),
		                    description: ((desc_box.eq(3).text())) ? _clear(desc_box.eq(1).html()) : '',
		                    condition: ((desc_box.eq(3).text())) ? _clear(desc_box.eq(3).html()) : _clear(desc_box.eq(1).html()),
		                    price: _clear($('.size-large-xx').html()),
		                    owner: {
		                        id: _clear($('#profile_pic').children().eq(0).attr('href'), ['https://offerup.com/p/', '/']),
		                        name: _clear($('#profile_pic').children().eq(1).html())
		                    }
		                }
		            });

		        }else{
		            if(!error && response.statusCode == 404){
		                resolve({
		                    success: "warning",
		                    data: {
		                        message: "item is not found"
		                    }
		                });
		            }else{
		                reject(new Error(error));
		            }
		        }
		    })
		}


		function getUserProfile(id, resolve, reject){
		    var page = 1;
		    var index = 0;
		    var result = {};
		    var items = [];
		    var q = tress(function(URL, callback){
		        request({
		            url: URL
		        }, function(error, response){
		            if(!error && response.statusCode == 200){
		                var $ = cheerio.load(response.body, {
		                    normalizeWhitespace: true
		                });
		                var userInfo = $('.box-sidebar').eq(0);
		                var type = userInfo.find('.header').text(); // SELLER
		                var name = (userInfo.find('div').eq(1).children().eq(0).children().last().attr('itemprop') == 'name') ? userInfo.find('div').eq(1).children().eq(0).children().last().text() : ''; // name
		                var member_since = userInfo.find('div').eq(1).children().eq(1).text() || ''; // member_since
		                var reviews = (userInfo.find('div').eq(1).children().eq(2).children().last().children().eq(0).children().attr('itemprop') == 'ratingCount') ? userInfo.find('div').eq(1).children().eq(2).children().last().children().eq(0).children().text() : '0';
		                var location = $('.box-sidebar').eq(1).find('.header').text();
		                var followers = $('.box-sidebar').eq(2).find('.header').find('a').html();
		                var items_array = $('#item-collection-profile-list').children().children();
		              
		                if(items_array.length > 0){
		                    for(var i=0;i<items_array.length;i++){
		                        index++;
		                        items.push({
		                            id: _clear(items_array.eq(i).find('.item_listing_id').val()),
		                            title: _clear(items_array.eq(i).find('.item-info-title').children().html()),
		                            img_src: _clear(items_array.eq(i).find('.item-pic').children().children().attr('src')),
		                            price: _clear(items_array.eq(i).find('.item-info-price').html(), '$'),
		                            location: _clear(items_array.eq(i).find('.item-info-distance').text()),
		                            link: _clear(items_array.eq(i).find('.item-pic').children().attr('href'), '?ref=UserProfile')
		                        });
		                    }
		                    page++;
		                    q.push('https://offerup.com/p/' + id + '/?type=profile-list&page=' + page);
		                }


		                if(!member_since || !(RegExp('Member Since').test(member_since))){
		                    member_since = '';
		                }
		                result = {
		                    type: _clear(type),
		                    name: _clear(name),
		                    member_since: _clear(member_since, 'Member Since '),
		                    reviews: _clear(reviews),
		                    location: _clear(location),
		                    followers: _clear(followers, ['(', ')']),
		                };

		                
		                callback();

		            }else{
		                if(!error && response.statusCode == 404){
		                    callback(new Error('not found'))
		                }
		            }
		        });
		    });

		    q.error = function(err) {
		        resolve({
		            success: "warning",
		            data: {
		                message: "User is not found"
		            }
		        });
		    };

		    q.drain = function(){

		        var data = Object.assign(result, {
		            items: items
		        });
		        resolve({success: 'success', data: data});
		    }

		    var url = 'https://offerup.com/p/' + id + '/?type=profile-list&page=' + page;
		    q.push(url);
		}


		function getFullListByQuery(query, location, resolve, reject){
		    
		    var results = [], k = 0, search_after = '', price_min = '', price_max = '';


		    var q = tress(function(query, callback){

		        if(query.search_after){
		            if(!(typeof query.search_after == 'number')){
		                reject(new Error('search_after is not a number'));
		            }
		            if(query.search_after < 10){
		                reject(new Error('search after should be more than 10'));
		            }
		            if(!query.cursor_id){
		                reject(new Error('cursor_id is not defined'));
		            }
		            if(!(typeof(query.cursor_id == 'number'))){
		                reject(new Error('cursor_id is not a number'));
		            }
		            var search_after_number = query.search_after;
		            var i = 0;

		            while(search_after_number > 10){
		                i++;
		                search_after_number = search_after_number / 10;
		            }
		            search_after = '%22search_after%22%3A' + search_after_number + 'E' + i + '%2C%22cursor_id%22%3A' + query.cursor_id + '%7D';
		        }
		        if(query.price_min){
		            price_min = '&price_min=' + query.price_min;
		        }
		        if(query.price_max){
		            price_max = '&price_max=' + query.price_max;
		        }

		        var url = 'https://offerup.com/webapi/search/v2/feed/?q='+query.search+'&radius='+query.radius+''+price_min+''+price_max+'&platform=web&experiment_id=experimentmodel24&lon='+location.longitude+'&lat='+location.latitude+'&page_cursor=%7B%22index%22%3A31%2C%22external_ads_cursor%22%3A%22b54522541be8463aa9e9536c099430cf%22%2C' + search_after + '&limit=1000';
		        
		        request({url: url}, function(error, response, body){
		            if(!error && response.statusCode == 200){

		                var r = JSON.parse(body);

		                for(var i=0,l=r.data.feed_items.length;i<l;i++){
		                    if(k === query.limit) break;
		                    if(r.data.feed_items[i].type == 'item'){
		                        results.push(r.data.feed_items[i].item);
		                        k++;
		                    }
		                }

		                if(k < query.limit){
		                    var s = JSON.parse(r.data.next_page_cursor);
		                    query.search_after = s.search_after;
		                    query.cursor_id = s.cursor_id;
		                    q.push(query)
		                }
		                

		                callback();
		            }else{
		                reject(new Error('Request error: ' + error + ', statusCode: ' + response.statusCode));
		            }
		        })
		    });

		    q.drain = function(){
		        resolve(results);
		    }

		    q.push(query);

		    
		}

		    var OfferUp = {};

		    OfferUp.setGoogleMapApi = function(apikey){
		        if(this.googleApi){
		            reject(new Error('You already have an API KEY'));
		        }
		        if(!apikey){
		            reject(new Error('\'API Key\' is not defined'));
		        }
		        if(!(typeof apikey == 'string')){
		            reject(new Error('\'API Key\' should be \'string\''));
		        }
		        this.googleApi = apikey;
		    }

		    OfferUp.getItemInformation = function(id){
		        return new Promise(function(resolve, reject){
		            if(!id){
		                reject(new Error('\'id\' is not defined'));
		            }
		            if(!(typeof id == 'string')){
		                reject(new Error('\'id\' should be \'string\''));
		            }
		            getItemInformation(id, resolve, reject);
		        });
		    }

		    OfferUp.getUserProfile = function(id){
		        return new Promise(function(resolve, reject){
		            if(!id){
		                reject(new Error('\'id\' is not defined'));
		            }
		            if(!(typeof id == 'string')){
		                reject(new Error('\'id\' should be \'string\''));
		            }
		            getUserProfile(id, resolve, reject);
		        });
		    }

		    OfferUp.setDefaults = function(defaults){
		        if(!defaults){
		            reject(new Error('defaults is not defined'));
		        }
		        if(!(typeof defaults == 'object')){
		            reject(new Error('defaults is not an object'));
		        }
		        this.defaults = defaults;
		    }

		    OfferUp.getFullListByQuery = function(query){
		        return new Promise(function(resolve, reject){
		            if(!query){
		                reject(new Error('\'query\' is not defined'));
		            }
		            if(!(query instanceof Object)){
		                reject(new Error('Object is not defined'));
		            }

		            if(OfferUp.defaults){
		                var _defaults = OfferUp.defaults;
		                var _keys = Object.keys(_defaults);
		                for(var i=0,keys = _keys.length;i<keys;i++){

		                    if(!query[_keys[i]]){
		                        query[_keys[i]] = _defaults[_keys[i]];
		                    }
		                }
		            }
		            if(!query.location){
		                reject( new Error('\'location\' parameter is required'));
		            }
		            if(!query.search){
		                reject(new Error('\'search\' parameter is required'));
		            }
		            if(!(typeof query.search == 'string')){
		                reject(new Error('\'search\' parameter should be \'string\''));
		            }
		            if(query.radius){
		                if(!(typeof query.radius == 'number')){
		                    reject(new Error('\'radius\' parameter should be \'number\''));
		                }
		                if(query.radius < 5 || query.radius > 50){
		                    reject(new Error('\'radius\' parameter should be from 5 to 50'));
		                }
		            }else{

		                query.radius = 30;
		            }
		            if(query.limit){
		                if(query.limit < 1 || query.limit > 1000){
		                    reject(new Error('\'limit\' parameter should be from 1 to 1000'));
		                }
		            }else{

		                query.limit = 50;
		            }
		            if(!(typeof query.location == 'string')){
		                reject(new Error('\'location\' parameter should be \'string\''));
		            }
		            if(!(query.price_min === undefined) && !(query.price_max === undefined)){
		                if(query.price_min > query.price_max){
		                    reject(new Error('\'price_max\' should be more than \'price_min\''));
		                }
		            }
		            if(!(query.price_min === undefined)){
		                if(!(typeof query.price_min == 'number')){
		                    reject(new Error('\'price_min\' shoube be \'number\''));
		                }
		                if(query.price_min < 0){
		                    reject(new Error('\'price_min\' shoube be more than 0'));
		                }
		                
		            }
		            if(!(query.price_max === undefined)){
		                if(!(typeof query.price_max == 'number')){
		                    reject(new Error('\'price_max\' shoube be \'number\''));
		                }
		                if(query.price_max <= 0){
		                    reject(new Error('\'price_max\' shoube be more than 0'));
		                }
		            }

		            _getLocation(query.location).then(function(response){
		                if(response && response.success && response.data){
		                    getFullListByQuery(query, response.data, resolve, reject);
		                }else{
		                    reject(new Error('\'location\' is not defined'));
		                }
		                
		            }).catch(function(error){
		                console.log(error)
		            });

		            
		        });
		    }


		    module.exports = OfferUp;



}
