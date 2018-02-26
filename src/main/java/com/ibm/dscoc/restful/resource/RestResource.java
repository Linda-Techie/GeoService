package com.ibm.dscoc.restful.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.servlet.ServletHolder;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.ibm.dscoc.model.*;


@Path("/ibmdscoc")
public class RestResource {

	ServletHolder sh = new ServletHolder(ServletContainer.class);
	static String msg = "URL Example: http://<deployed_server>:8086/ibmdscoc/google/mapapi/https%3A%2F%2Fmaps.googleapis.com%2Fmaps%2Fapi%2Fgeocode%2Fjson%3Faddress%3D&&1500+MARILLA+ST%2C+DALLAS%2C+TX+75201&&<api_key>";

	@GET
    @Path("/google/mapapi/{query}")
    public String getGeoData (@PathParam("query") String query ) throws UnsupportedEncodingException, Exception {
		if (query == null || query.length()<1) return msg;

		String[] params = query.split("&");
		System.out.print(params);
		return GoogleGeo.getGeo(URLDecoder.decode(params[0], "UTF-8"),
								URLDecoder.decode(params[1], "UTF-8"),
								URLDecoder.decode(params[2], "UTF-8"),
								URLDecoder.decode(params[3], "UTF-8"),
								URLDecoder.decode(params[4], "UTF-8"));

    }

	@GET
    @Path("/test/xml/{name}")
    @Produces(MediaType.APPLICATION_XML)
    public Test getXMLData( @PathParam("name") String name ) {
		return new Test("Hello " + name,"Greetings from IBM DS COC Team!!");
    }

	@GET
    @Path("/test/json/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Test getJASONData (@PathParam("name") String name ) {
		return new Test("Hello " + name,"Greetings from IBM DS COC Team!!");
    }

}
