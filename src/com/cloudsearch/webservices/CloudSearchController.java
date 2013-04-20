package com.cloudsearch.webservices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.cloudsearch.model.User;
import com.cloudsearch.oauth.GoogleAuthHelper;

@Path("/cloud")
public class CloudSearchController {

	/**
	 * Gets the url for login for oauth
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLoginURL")
	public String getLoginURL() {
		GoogleAuthHelper helper = new GoogleAuthHelper();
		String url = helper.buildLoginUrl();
		return url;
	}
	
	@GET
	@Path("/getUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserInfo(@QueryParam("code") String code)
			throws IOException {
		GoogleAuthHelper helper = new GoogleAuthHelper();
		String json = helper.getUserInfoJson(code);
		return json;
	}
	
	
	public boolean isUserRegistered(@QueryParam("code") String code){
		return false;
		
	}
	
	public User registerNewUser(){
		return null;
		
	}
	
	public void getGoogleDriveAuthUrl(){
		
	}
	
	public void indexAllDocuments(){
		
	}
	
	
	
}
