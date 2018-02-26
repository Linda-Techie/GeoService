package com.ibm.dscoc.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleGeo extends Geo {

	public GoogleGeo(String apiGeoUrl, String apiGeometryUrl, String strAddr, String params, String apiKey) throws Exception {
		super(apiGeoUrl, apiGeometryUrl, strAddr, params, apiKey);
	}
	
	public static String getGeo(String apiGeoUrl, String apiGeometryUrl, String address, String param, String key) throws Exception {
		GoogleGeo geo = new GoogleGeo(apiGeoUrl, apiGeometryUrl, address, param, key);
		geo.setLatLng();
		return geo.toCsvString();
		//return geo.getLat() + "," + geo.getLng() + "," + geo.getCounty(); 
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
		String msg = "Usage Syntax is: GoogleGeo <api_geo_url_endpoint> <api_geometry_url_endpoint> <address_detail> <preset_parameters> <api_key> \nExample: GoogleGeo \"https://maps.googleapis.com/maps/api/geocode/json?address=\" \"1500 Marilla st, Dallas TX 75201\" \"\" \"google_api_key\" ";
	    if(args == null || args.length != 5) {
	        System.out.println(msg);
	        System.exit(0);
	    }

	    try {
	    	String rtn = GoogleGeo.getGeo(args[0], args[1], args[2], args[3], args[4]);
	    } catch(Exception e) {
	    	System.out.println("Error msg: " + e.toString());
	    }
	}//main

}
