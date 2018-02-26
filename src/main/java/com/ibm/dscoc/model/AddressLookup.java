package com.ibm.dscoc.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddressLookup {

	public static String GOOGLE_API_ADDR_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
	public static String GOOGLE_API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
	public static String KEYWORD_LAT    = "lat";
	public static String KEYWORD_LNG    = "lng";
	public static String KEYWORD_COUNTY = "county";
	
	public String api_url = null;
	public String address = null;
	public String api_key = null;
	
	//getters
	public String getApiUrl()  { return this.api_url; }
	public String getApiKey()  { return this.api_key; }
	public String getAddress() { return this.address; }
	
	//setters
	public void setApiUrl (String s) { this.api_url = s; }
	public void setApiKey (String s) { this.api_key = s; }
	public void setAddress(String s) { this.address = s; }
	
	public AddressLookup() {		
	}
	
	public static String getAddrJSON(String url, String key, String address) {
		if (url == null || url.equals("")) {
			url = GOOGLE_API_ADDR_URL;
		}
		String httpsUrl = url + address.replaceAll(" ", "+") + "&key="+key;
		
		URL urlObj = null;		
		HttpsURLConnection conn  = null;
		BufferedReader in = null;
		InputStream ins = null;
		InputStreamReader isr = null;
		
		StringBuffer sb = new StringBuffer();
		try {
			urlObj = new URL(httpsUrl);
			conn = (HttpsURLConnection) urlObj.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			
			ins = conn.getInputStream();
			isr = new InputStreamReader(ins);
			in = new BufferedReader(isr);
			
			String line = null;
			while((line = in.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("json = " + sb.toString());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
				if (conn != null) conn.disconnect();
			} catch(Exception e) {}
		}
		
		return sb.toString();
	}
	
	public static String[] getGeoArray(String url, String key, String address) {
		String json = AddressLookup.getAddrJSON(url, key, address);
		return AddressLookup.getGeo(json);
	}

	public static String getGeoUDF(String url, String key, String address) {
		String[] geo = AddressLookup.getGeoArray(url, key, address);
		
		return geo[0] + "," + geo[1] + "," + geo[2];
	}

	/**
	 * Return lat, lng, and county data in a Map object
	 * @param json
	 * @return
	 */
	public static String[] getGeo(String jsonAddr) {
		String[] geo = new String[3];
		JSONObject obj1, obj2, obj3, obj4;
		JSONArray arr1, arr2 = null;
		
		try {
			obj1 = new JSONObject(jsonAddr);
			arr1 = obj1.getJSONArray("results");
			
			for (int i=0;i<arr1.length();i++) {
				obj2 = arr1.getJSONObject(i);
				obj3 = obj2.getJSONObject("geometry").getJSONObject("location");
				arr2 = obj2.getJSONArray("address_components");
						
				String lat = (obj3.get("lat")).toString();
				String lng = (obj3.get("lng")).toString();

				geo[0] = lat;
				geo[1] = lng;
				
				System.out.println("lat = " + lat + ", lng = " + lng);

				if (arr2 == null) {
					geo[2] = "";
					return geo;
				}
				
				JSONArray types = null;
				String county = null;
				
				for (int j=0;j<arr2.length();j++) {
					obj4 = arr2.getJSONObject(j);
					types = obj4.getJSONArray("types");
					
					if (types != null && (types).toString().indexOf("administrative_area_level_2") > -1) {
						county = obj4.getString("short_name").toString().replaceAll(" County", "");
						break;
					}
				}
				geo[2] = county;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return geo;
	}

	public static void main(String[] args) {
		// Check how many arguments were passed in
	    if(args == null || args.length < 3) {
	        System.out.println("Proper Usage is: AddressLookup <rest_api_url> <address> <api_key>");
	        System.exit(0);
	    }
	    System.out.println("Input 0: " + args[0]);
	    System.out.println("Input 1: " + args[1]);
	    System.out.println("Input 2: " + args[2]);

	    String url     = args[0];
	    String key     = args[1];
	    String address = args[2];
	    
	    String[] geo = AddressLookup.getGeoArray(url, key, address);
	    System.out.print("geo0: " + geo[0]);
	    System.out.print("geo1: " + geo[1]);
	    System.out.print("geo2: " + geo[2]);
	}

}
