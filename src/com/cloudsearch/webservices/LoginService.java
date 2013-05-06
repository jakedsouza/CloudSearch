package com.cloudsearch.webservices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.cloudsearch.model.RequestModel;
import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;

/**
 * Servlet to check that the current user is authorized and to serve the start
 */

@Path("/validate")
public class LoginService extends CloudSearchService {
	static Logger log = Logger.getLogger(LoginService.class);

	/**
	 * Ensure that the user is authorized
	 * 
	 * @return
	 */
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> validateUser(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException, ServletException {
		code = (code.equals("")) ? null : code;
		email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		state = (state.equals("")) ? null : state;
		RequestModel httpRequest = new RequestModel(state, userId, code, email);
		log.info("Login service called " + httpRequest.toString());
		Map<String, String> jsonMap = new HashMap<String, String>();
		String url = null;

		// Making sure the code gets processed
		String clientID = null;
		try {
			clientID = getClientId(httpRequest, USER_SCOPES);
		} catch (NoRefreshTokenException e) {
			url = e.getAuthorizationUrl();
		}
		if (clientID == null) {
			jsonMap.put("url", url); // google authentication url
			jsonMap.put("http_code", "307");// temporary redirect to google
			log.info("Sending for authorization");
			log.info(jsonMap);
			return jsonMap;
		}
		url = "main.html";
		jsonMap.put("url", url);
		jsonMap.put("clientId", clientID);
		jsonMap.put("http_code", "200");
		jsonMap.put("email", httpRequest.getEmail());
		jsonMap.put("userId", httpRequest.getUserId());
		log.info("User authorised : " + jsonMap);
		return jsonMap;
	}

}