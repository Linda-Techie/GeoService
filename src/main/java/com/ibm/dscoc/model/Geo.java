package com.ibm.dscoc.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

public class Geo {
	public static final int    INT_ERROR_EXIT_0 = 0;
	public static final int    INT_ERROR_EXIT_1 = 1;
	public static final String DESC_ERROR_EXIT_0 = "SUCCEED";
	public static final String DESC_ERROR_EXIT_1 = "Missing manadatory input parameters. Please provide API Endpoint, detail address (including city, state, and zip).";
	
	private String lat;
	private String lng;
	private String key;
	private String zip;
	
	private String apiAddrGeoUrl;
	private String apiBaseGeoUrl;
	private String apiGeometryUrl;
	private String stdAddress;
	private String address;
	private String county;
	private String geoJson;
	private String geometryJson;
	private String district_id;
	private String defaultParams;
	
	public String getLat()           { return (lat==null)?"":lat.trim();           }
	public String getLng()           { return (lng==null)?"":lng.trim();           }
	public String getKey()           { return key;           }
	public String getZip()           { return zip;           }
	public String getApiAddrGeoUrl() { return apiAddrGeoUrl;     }
	public String getApiBaseGeoUrl() { return apiBaseGeoUrl;     }
	public String getApiGeometryUrl(){ return apiGeometryUrl;}
	public String getAddress()       { return address;       }
	public String getStdAddress()    { return (stdAddress==null)?"":stdAddress.trim();       }
	public String getCounty()        { return county;        }
	public String getGeoJson()       { return geoJson;       }
	public String getGeometryJson()  { return geometryJson;  }
	public String getDistrictID()    { return (district_id==null)?"":district_id.trim();   }
	public String getDefaultParams() { return defaultParams; }
	
	public void setLat(String x)            { lat           = x;  }
	public void setLng(String y)            { lng           = y;  }
	public void setKey(String k)            { key           = k;  }
	public void setZip(String k)            { zip           = k;  }

	public void setApiBaseGeoUrl(String u)  { apiBaseGeoUrl = u;  }
	public void setApiAddrGeoUrl(String u)  { apiAddrGeoUrl = u;  }
	public void setApiGeometryUrl(String u) { apiGeometryUrl= u;  }
	public void setAddress(String d)        { address       = d;  }
	public void setStdAddress(String d)     { stdAddress    = d;  }
	public void setCounty(String c)         { county        = c;  }
	public void setGeoJson(String j)        { geoJson       = j;  }
	public void setGeometryJson(String j)   { geometryJson  = j;  }
	public void setDistrictID(String p)     { district_id   = p;  }
	public void setDefaultParams(String dp) { defaultParams = dp; }
	
    @Produces(MediaType.APPLICATION_JSON)
	public String toCsvString() {
		return this.getGeoJson();
		
	}
	
	/**
	 * 
	 * @param apiGeoUrl
	 * @param apiGeometryUrl
	 * @param strAddr
	 * @param params
	 * @param apiKey
	 * @throws Exception
	 */
	public Geo(String url, String apiGeometryUrl, String strAddr, String params, String apiKey) throws Exception {
		if (url == null || strAddr == null) throw new Exception(Geo.DESC_ERROR_EXIT_1);
		
		this.setApiAddrGeoUrl(url);
		this.setApiGeometryUrl(apiGeometryUrl);
	    this.setAddress(strAddr);
	    this.setDefaultParams(params);
	    this.setKey(apiKey);
	}

	public Geo(String url, String apiGeometryUrl, String strLat, String strLng, String params, String apiKey) throws Exception {
		if (url == null || strLat == null || strLng == null) throw new Exception(Geo.DESC_ERROR_EXIT_1);
		
		this.setApiBaseGeoUrl(url);
		this.setApiGeometryUrl(apiGeometryUrl);
	    this.setLat(strLat);
	    this.setLng(strLng);
	    this.setDefaultParams(params);
	    this.setKey(apiKey);
	}

	/**
	 * 
	 */
	public void setLatLng() {
		JSONObject objJson, obj2, obj3 = null;
		JSONArray arr1 = null;
		
		try {
			if (this.getGeoJson() == null || this.getGeoJson().trim().length() == 0) {
				this.apiGetGeoJson();
			}
			
			objJson = new JSONObject(this.getGeoJson());
			
			arr1 = objJson.getJSONArray("candidates");
			obj2 = arr1.getJSONObject(0);
			obj3 = obj2.getJSONObject("location");
			this.setLng(obj3.get("x").toString());
			this.setLat(obj3.get("y").toString());
			this.setStdAddress(obj2.get("address").toString());
			
		} catch (Exception e) {
			//e.printStackTrace();
			this.setLng("");
			this.setLat("");
		}

	}

	public void setZip() {
		JSONObject objJson, obj2, obj3 = null;
		JSONArray arr1, arr2 = null;
		
		try {
			if (this.getGeoJson() == null || this.getGeoJson().trim().length() == 0) {
				this.apiGetGeoJson();
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

	public void setGeometryPolygon() {
		JSONObject objJson, obj2, obj3 = null;
		JSONArray arr1 = null;
		
		try {
			if (this.lat == null || this.lng == null || this.lat.equals("") || this.lng.equals("")) {
				this.setDistrictID("");
				return; //early return due to bad address...
			}
			
			if (this.getGeometryJson() == null || this.getGeometryJson().trim().length() == 0) {
				this.apiGetGeometry(lng, lat);
			}
			
			objJson = new JSONObject(this.getGeometryJson());
			
			arr1 = objJson.getJSONArray("features");
			obj2 = arr1.getJSONObject(0);
			obj3 = obj2.getJSONObject("attributes");
			
			this.setDistrictID(obj3.get("DISTRICT").toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	private void apiGetGeometry(String x, String y) throws Exception {
		
		String x_value = (x==null || x.trim().length()<1)?"":(x.trim());
		String y_value = (y==null || y.trim().length()<1)?"":(y.trim());
		String geometry = x_value + "," + y_value;
		
		URL                urlObj  = null;		
		HttpURLConnection  conn    = null;
		HttpsURLConnection conn_s  = null;
		BufferedReader     in      = null;
		InputStream        ins     = null;
		InputStreamReader  isr     = null;
		StringBuffer       sb      = new StringBuffer();
		
		try {
			
			String url = apiGeometryUrl + "geometry=" + geometry + defaultParams;
			
			urlObj  = new URL(url);
			if (apiGeometryUrl.contains("https")) {
				conn_s  = (HttpsURLConnection) urlObj.openConnection();
				conn_s.setRequestMethod("GET");
				conn_s.setDoOutput(true);
				
				ins = conn_s.getInputStream();				
			} else {
				conn = (HttpURLConnection) urlObj.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoOutput(true);
				
				ins = conn.getInputStream();
			}
			
			isr = new InputStreamReader(ins);
			in = new BufferedReader(isr);
			
			String line = null;
			while((line = in.readLine()) != null) {
				sb.append(line);
			}
			this.geometryJson = sb.toString();
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (in != null) in.close();
				if (conn != null) conn.disconnect();
				if (conn_s != null) conn_s.disconnect();
			} catch(Exception e) {}

		}

	}
	
	public void apiGetRevGeoJson() throws Exception {
		URL                urlObj  = null;		
		HttpURLConnection  conn    = null;
		HttpsURLConnection conn_s  = null;
		BufferedReader     in      = null;
		InputStream        ins     = null;
		InputStreamReader  isr     = null;
		StringBuffer       sb      = new StringBuffer();
		
		try {
			
			lat = URLEncoder.encode(this.lat, "UTF-8");
			lng = URLEncoder.encode(this.lng, "UTF-8");
			String url     = apiBaseGeoUrl + "latlng=" + lat + "," + lng + defaultParams + ((key==null || key.trim().length()<1)?"":("&key=" + key));
			urlObj  = new URL(url);
			if (apiBaseGeoUrl.contains("https")) {
				conn_s  = (HttpsURLConnection) urlObj.openConnection();
				conn_s.setRequestMethod("GET");
				conn_s.setDoOutput(true);
				
				ins = conn_s.getInputStream();				
			} else {
				conn = (HttpURLConnection) urlObj.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoOutput(true);
				
				ins = conn.getInputStream();
			}
			
			isr = new InputStreamReader(ins);
			in = new BufferedReader(isr);
			
			String line = null;
			while((line = in.readLine()) != null) {
				sb.append(line);
			}
			this.geoJson = sb.toString().trim();
			//System.out.println("json = " + this.getGeoJson());

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (in != null) in.close();
				if (conn != null) conn.disconnect();
				if (conn_s != null) conn_s.disconnect();
			} catch(Exception e) {}

		}

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void apiGetGeoJson() throws Exception {
		URL                urlObj  = null;		
		HttpURLConnection  conn    = null;
		HttpsURLConnection conn_s  = null;
		BufferedReader     in      = null;
		InputStream        ins     = null;
		InputStreamReader  isr     = null;
		StringBuffer       sb      = new StringBuffer();
		
		try {
			
			address = URLEncoder.encode(this.address, "UTF-8");
			String url     = apiAddrGeoUrl + address + defaultParams + ((key==null || key.trim().length()<1)?"":("&key=" + key));
			urlObj  = new URL(url);
			if (apiAddrGeoUrl.contains("https")) {
				conn_s  = (HttpsURLConnection) urlObj.openConnection();
				conn_s.setRequestMethod("GET");
				conn_s.setDoOutput(true);
				
				ins = conn_s.getInputStream();				
			} else {
				conn = (HttpURLConnection) urlObj.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoOutput(true);
				
				ins = conn.getInputStream();
			}
			
			isr = new InputStreamReader(ins);
			in = new BufferedReader(isr);
			
			String line = null;
			while((line = in.readLine()) != null) {
				sb.append(line);
			}
			this.geoJson = sb.toString().trim();
			//System.out.println("json = " + this.getGeoJson());

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (in != null) in.close();
				if (conn != null) conn.disconnect();
				if (conn_s != null) conn_s.disconnect();
			} catch(Exception e) {}

		}

	}
}
