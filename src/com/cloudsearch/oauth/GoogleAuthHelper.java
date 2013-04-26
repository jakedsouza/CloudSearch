package com.cloudsearch.oauth;

import com.cloudsearch.model.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.owlike.genson.Genson;
import com.owlike.genson.TransformationException;
import com.owlike.genson.stream.JsonType;

import java.io.IOException;
import java.util.Arrays;

/**
 * A helper class for Google's OAuth2 authentication API.
 */
public final class GoogleAuthHelper {

	private static final String CLIENT_ID = "741352604053-7mppvnl6d0f6922amcsqia4aq9lvpodm.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "0aXTzZoh8QkPjXltVz7c_aE6";

	private final String CALLBACK_URI = "http://localhost:8080/CloudSearch/main.html";
	// start google authentication constants
	private final Iterable<String> SCOPE = Arrays
			.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email"
					.split(";"));
	private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	private JsonFactory jsonFactory;
	private HttpTransport httpTransport;

	private final GoogleAuthorizationCodeFlow flow;

	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT
	 * ID, SECRET, and SCOPE
	 */
	public GoogleAuthHelper() {
		jsonFactory = new JacksonFactory();
		httpTransport = new NetHttpTransport();
		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope
	 */
	public String buildLoginUrl() {
		final GoogleAuthorizationCodeRequestUrl url = flow
				.newAuthorizationUrl();
		return url.setRedirectUri(CALLBACK_URI).setState("google").build();
	}
	
	public void exchangeAuthCodeForTokens(String authCode){
		GoogleAuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(authCode);
		GoogleTokenResponse googleTokenResponse ;
		try {
			googleTokenResponse = tokenRequest.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public User getUserInfo(final String authCode){
		GoogleTokenResponse response;
		try {
			GoogleAuthorizationCodeTokenRequest tokenRequest = flow
					.newTokenRequest(authCode);
			tokenRequest.setRedirectUri(CALLBACK_URI);
			response = tokenRequest.execute();
			final Credential credential = flow.createAndStoreCredential(
					response, null);

			final HttpRequestFactory requestFactory = httpTransport
					.createRequestFactory(credential);
			// Make an authenticated request
			final GenericUrl url = new GenericUrl(USER_INFO_URL);
			final HttpRequest request = requestFactory.buildGetRequest(url);
			request.getHeaders().setContentType("application/json");
			final String jsonIdentity = request.execute().parseAsString();
			Genson genson = new Genson();
			User user = genson.deserialize(jsonIdentity, User.class);
			return user;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformationException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String args[]) throws TransformationException {
		String authCode = "";
		GoogleAuthHelper helper = new GoogleAuthHelper();
		System.out.println(helper.buildLoginUrl());
		User user = helper.getUserInfo(authCode);
		System.out.println(user);
	}

}
