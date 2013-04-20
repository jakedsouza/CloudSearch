package com.cloudsearch.oauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * A helper class for Google's OAuth2 authentication API.

 */
public final class GoogleAuthHelper {

	/**
	 * Please provide a value for the CLIENT_ID constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_ID = "741352604053-7mppvnl6d0f6922amcsqia4aq9lvpodm.apps.googleusercontent.com";
	/**
	 * Please provide a value for the CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_SECRET = "0aXTzZoh8QkPjXltVz7c_aE6";

	/**
	 * Callback URI that google will redirect to after successful authentication
	 */
	//private static final String CALLBACK_URI = "http://localhost:8080/oauth1/rest/oauth/oauth2callback";
	  private static final String CALLBACK_URI = "http://localhost:8080/CloudSearch/index.html";
	// start google authentication constants
	private static final Iterable<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	// end google authentication constants
	
	
	private final GoogleAuthorizationCodeFlow flow;
	
	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
	 */
	public GoogleAuthHelper() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope 
	 */
	public String buildLoginUrl() {
		final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		return url.setRedirectUri(CALLBACK_URI).setState("google").build();
	}
	
	/**
	 * Expects an Authentication Code, and makes an authenticated request for the user's profile information
	 * @return JSON formatted user profile information
	 * @param authCode authentication code provided by google
	 */
	public String getUserInfoJson(final String authCode)  {

		GoogleTokenResponse response;
		try {
			response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
			final Credential credential = flow.createAndStoreCredential(response, null);
			final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
			// Make an authenticated request
			final GenericUrl url = new GenericUrl(USER_INFO_URL);
			final HttpRequest request = requestFactory.buildGetRequest(url);
			request.getHeaders().setContentType("application/json");
			final String jsonIdentity = request.execute().parseAsString();
			return jsonIdentity;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String args[]){
		String authCode = "4/2dvnyTDFNNLbNctyw0d8uzD36KOX.QjfhrerTCKYfaDn_6y0ZQNikhh1LfAI";
		GoogleAuthHelper helper = new GoogleAuthHelper();
		System.out.println(helper.buildLoginUrl());
		String info = helper.getUserInfoJson(authCode);
		System.out.println(info);
	}

	

}
