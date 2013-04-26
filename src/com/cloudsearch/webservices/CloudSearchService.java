/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.cloudsearch.webservices;

import com.cloudsearch.model.RequestModel;
import com.cloudsearch.model.ResponseModel;
import com.cloudsearch.oauth.CredentialMediator;
import com.cloudsearch.oauth.CredentialMediator.InvalidClientSecretsException;
import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.sun.research.ws.wadl.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

/**
 * Abstract service that sets up credentials and provides some convenience
 * methods.
 */
public abstract class CloudSearchService {

	protected static final HttpTransport TRANSPORT = new NetHttpTransport();
	protected static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Default MIME type of files created or handled by DrEdit.
	 * 
	 * This is also set in the Google APIs Console under the Drive SDK tab.
	 */
	public static final String DEFAULT_MIMETYPE = "text/plain";

	/**
	 * MIME type to use when sending responses back to DrEdit JavaScript client.
	 */
	public static final String JSON_MIMETYPE = "application/json";

	/**
	 * Path component under war/ to locate client_secrets.json file.
	 */
	public static final String CLIENT_SECRETS_FILE_PATH = "/WEB-INF/client_secrets.json";

	/**
	 * Scopes for which to request access from the user.
	 */
	public static final List<String> DRIVE_SCOPE = Arrays.asList(
	// Required to access and manipulate files.
	// "https://www.googleapis.com/auth/drive.file",
			"https://www.googleapis.com/auth/drive"// ,
			// Required to identify the user in our data store.
			// "https://www.googleapis.com/auth/userinfo.email",
			// "https://www.googleapis.com/auth/userinfo.profile"
			);

	public static final List<String> USER_SCOPES = Arrays.asList(
			// Required to identify the user in our data store.
			"https://www.googleapis.com/auth/userinfo.email",
			"https://www.googleapis.com/auth/userinfo.profile");

	// @Context
	// protected
	// ThreadLocal<HttpServletRequest> reqT;
	// protected @Context
	// ThreadLocal<HttpServletResponse> resT;
	protected @Context
	ServletContext context;

	protected ResponseModel sendError(int code, String message) {
		return new ResponseModel(code, message);
	}

	protected InputStream getClientSecretsStream() {
		return context.getResourceAsStream(CLIENT_SECRETS_FILE_PATH);
	}

	protected CredentialMediator getCredentialMediator(RequestModel req,
			Collection<String> scopes)
			throws CredentialMediator.NoRefreshTokenException {
		// Authorize or fetch credentials. Required here to ensure this happens
		// on first page load. Then, credentials will be stored in the user's
		// session.
		CredentialMediator mediator;
		try {
			mediator = new CredentialMediator(req, getClientSecretsStream(),
					scopes);
			mediator.getActiveCredential();
			return mediator;
		} catch (CredentialMediator.NoRefreshTokenException e) {
			throw e;
		} catch (InvalidClientSecretsException e) {
			String message = String.format(
					"This application is not properly configured: %s",
					e.getMessage());
			ResponseModel resp = sendError(500, message);
			throw new RuntimeException(resp.toString());
		} catch (IOException e) {
			String message = String.format(
					"An error happened while reading credentials: %s",
					e.getMessage());
			ResponseModel resp = sendError(500, message);
			throw new RuntimeException(resp.toString());
		}
	}

	protected Credential getCredential(RequestModel req,
			Collection<String> scopes)
			throws CredentialMediator.NoRefreshTokenException {
		try {
			CredentialMediator mediator = getCredentialMediator(req, scopes);
			return mediator.getActiveCredential();
		} catch (CredentialMediator.NoRefreshTokenException e) {
			throw e;
			// try {
			// resp.sendRedirect(e.getAuthorizationUrl());
			// return null;
			// } catch (IOException ioe) {
			// ioe.printStackTrace();
			// throw new RuntimeException(
			// "Failed to redirect for authorization.");
			// }
		} catch (IOException e) {
			String message = String.format(
					"An error happened while reading credentials: %s",
					e.getMessage());
			ResponseModel resp = sendError(500, message);
			throw new RuntimeException(resp.toString());
			// resp = sendError(resp, 500, message);
			// throw new RuntimeException(message);
		}
	}

	protected String getClientId(RequestModel req, Collection<String> scopes)
			throws NoRefreshTokenException {
		CredentialMediator mediator = getCredentialMediator(req, scopes);
		if (mediator == null) {
			return null;
		} else {
			return mediator.getClientSecrets().getWeb().getClientId();
		}
	}

	protected ResponseModel deleteCredential(RequestModel req,
			Collection<String> scopes) throws NoRefreshTokenException {
		CredentialMediator mediator = getCredentialMediator(req, scopes);
		try {
			mediator.deleteActiveCredential();

			return new ResponseModel(200, null);
		} catch (IOException e) {
			String message = String.format(
					"An error happened while reading credentials: %s",
					e.getMessage());
			ResponseModel resp = sendError(500, message);
			throw new RuntimeException(resp.toString());
		}
	}
}