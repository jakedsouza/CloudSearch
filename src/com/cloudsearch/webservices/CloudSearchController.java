package com.cloudsearch.webservices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

	private @Context HttpServletRequest request ;
	/**
	 * Gets the url for login for oauth
	 * 
	 * @return
	 */
	
	public CloudSearchController(@Context HttpServletRequest request){
		this.request = request;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLoginURL")
	public String getLoginURL(@Context HttpServletRequest request2) {
		GoogleAuthHelper helper = new GoogleAuthHelper();
		String url = helper.buildLoginUrl();
		return url;
	}
	
	@GET
	@Path("/getUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserInfo(@QueryParam("code") String code)
			throws IOException {
		GoogleAuthHelper helper = new GoogleAuthHelper();
		User user = helper.getUserInfo(code);
		return user;
	}
	
	
	
	public void getGoogleDriveAuthUrl(){
		
	}
	
	public void indexAllDocuments(){
		
	}
	
	
	
}
