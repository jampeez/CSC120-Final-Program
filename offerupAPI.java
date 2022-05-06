package yardSaleApp;

public class offerupAPI {
	 var offerup = require('offerup');

	offerup.setGoogleMapApi('AIzaSyA3G0rnMQg4x4zd52uI0HAbw2juZebPRvA');

	offerup.getFullListByQuery({
	    location: 'Phoenix',
	    search: 'iphone',
	    radius: 50,
	    limit: 100,
	    price_min: 100,
	    price_max: 1000
	    }).then(function success(response){

	    }, function error(response){
	        console.log(response);
	    });
	offerup.setDefaults({
	    location: 'Phoenix',
	    radius: 20,
	    limit: 1000,
	    price_min: 100,
	    price_max: 1000
	});
	offerup.getUserProfile('112390').then(function(response){
	    if(response && response.success == 'success'){
	        console.log(response.data);

	    }
	}, function error(response){
	    console.log(response);
	});
	offerup.getItemInformation('393936205').then(function success(response){
	    if(response && response.success == 'success'){
	        console.log(response.data);

	    }
	}, function error(response){
	    console.log(response);
	});
}
