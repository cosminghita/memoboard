package com.espresoh.memoboard.rcp.session;

import com.espresoh.memoboard.rcp.Activator;
import com.espresoh.memoboard.rcp.preferences.IPreferenceKeys;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class RESTSession
{

	// ==================== 1. Static Fields ========================

//	public final static String MAIN_URL = "com.espresoh.memoboard.server/rest/";
	
	
	// ====================== 2. Instance Fields =============================
	
	private static WebResource webResource;
	
//	private static String hostName = "localhost";
//	private static String portName = "8080";

	
	// ==================== 7. Getters & Setters ====================

	public static WebResource getWebResource() 
	{

		if (webResource == null) 
		{
			final Client c = Client.create();
//			c.addFilter(new GZIPContentEncodingFilter());
			
//			StringBuilder mainUrl = new StringBuilder();
//			
//			mainUrl.append("http://").append(hostName);
//			
//			if (!portName.isEmpty())
//				mainUrl.append(":").append(portName);
//			
//			mainUrl.append("/").append(MAIN_URL);
//			
//			webResource = c.resource(mainUrl.toString());
			
			webResource = c.resource(Activator.getDefault().getPreferenceStore().getString(IPreferenceKeys.SERVER_URL));
//			c.getHeadHandler().handle(new URLConnectionClientHandler());
//			webResource = c.resource("http://" + hostName + ":" + portName + "/" + PONTAJ_MAIN_URL);
		}
		
		return webResource;
	}
	

}
