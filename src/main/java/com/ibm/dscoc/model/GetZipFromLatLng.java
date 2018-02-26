package com.ibm.dscoc.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class GetZipFromLatLng extends Geo {

	public GetZipFromLatLng(String apiGeoUrl, String strLat, String strLng, String apiKey) throws Exception {
		super(apiGeoUrl, "", strLat, strLng, "", apiKey);
	}
	
	public static String getGeo(String url, String strLat, String strLng, String key) throws Exception {
		GetZipFromLatLng geo = new GetZipFromLatLng(url, strLat, strLng, key);
		geo.setLatLng();
	    return geo.getLat() + "," + geo.getLng() + "," + geo.getCounty(); 
	}
	
	public static String getZip(String url, String strLat, String strLng, String key) throws Exception {
		GetZipFromLatLng geo = new GetZipFromLatLng(url, strLat, strLng, key);
		geo.geoZip();
		return geo.getZip();
	}
	
	public void geoZip() {
		JSONObject objJson, obj2 = null;
		JSONArray arr1, arr2 = null;
		
		try {
			if (this.getGeoJson() == null || this.getGeoJson().trim().length() == 0) {
				this.apiGetRevGeoJson();
			}
			
			objJson = new JSONObject(this.getGeoJson());
			arr1 = objJson.getJSONArray("results");
			
			obj2 = arr1.getJSONObject(0);
			arr2 = obj2.getJSONArray("address_components");
			if (arr2 != null) {
				JSONArray  types  = null;
				JSONObject obj4   = null;
				
				for (int j=0;j<arr2.length();j++) {
					obj4 = arr2.getJSONObject(j);
					types = obj4.getJSONArray("types");
					
					if (types != null && (types).toString().indexOf("postal_code") > -1) {
						this.setZip(obj4.getString("short_name").toString());
						break;
					}
				} //for
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//overloading parent's implementation
	public void setLatLng() {
		JSONObject objJson, obj2, obj3 = null;
		JSONArray arr1, arr2 = null;
		
		try {
			if (this.getGeoJson() == null || this.getGeoJson().trim().length() == 0) {
				this.apiGetGeoJson();
			}
			
			objJson = new JSONObject(this.getGeoJson());
			arr1 = objJson.getJSONArray("results");
			
			obj2 = arr1.getJSONObject(0);
			obj3 = obj2.getJSONObject("geometry").getJSONObject("location");
			arr2 = obj2.getJSONArray("address_components");
			this.setLat(obj3.get("lat").toString());
			this.setLng(obj3.get("lng").toString());

			if (arr2 != null) {
				JSONArray  types  = null;
				JSONObject obj4   = null;
				
				for (int j=0;j<arr2.length();j++) {
					obj4 = arr2.getJSONObject(j);
					types = obj4.getJSONArray("types");
					
					if (types != null && (types).toString().indexOf("administrative_area_level_2") > -1) {
						this.setCounty(obj4.getString("short_name").toString().replaceAll(" County", ""));
						break;
					}
				} //for
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String msg = "Usage Syntax is: GoogleRevGeo <api_geo_url_endpoint> <lat> <lng> <api_key> \nExample: GoogleRevGeo \"https://maps.googleapis.com/maps/api/geocode/json?\" \"40.714232\" \"-73.9612889\" \"\" \"google_api_key\" ";
		
		if(args == null || args.length != 4) {
	        System.out.println(msg);
	        System.exit(0);
	    }
	    try {
	    	String rtn = GetZipFromLatLng.getZip(args[0], args[1], args[2], args[3]);
	    } catch(Exception e) {
	    	System.out.println("Error msg: " + e.toString());
	    }
	}//main

}
