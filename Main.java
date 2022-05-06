package yardSaleApp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Main {
	@SuppressWarnings({ "unused", "resource", "null" })
	public static void main(String[] args) {
		System.out.println("Allow Location Services?");
		 new LocButton();
		 new offerupAPI();
		 new MapIndex();
		 
		 Scanner sc = new Scanner(System.in);
		 System.out.println("Enter email:");
		 String email = sc.nextLine();
		 System.out.println("Enter zip code:");
		 String zip = sc.nextLine();
		 System.out.println("Searching your area for yard sales.");
		 
		  //Displaying the map
		 Map<String, Main> map = new HashMap<String, Main>();
			Iterator<Map.Entry<String, Main>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry<String, Main> entry = entries.next();
			    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}
		System.out.println("Would you like to post a sale?");
		String yn = sc.nextLine();
		

	    if (yn.equals("yes")) {
	        System.out.println("Enter address:");
	        String address = sc.nextLine();
	        
	    } else {
	    	Entry<String, Main> entry = null;
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
	    }
}
}
