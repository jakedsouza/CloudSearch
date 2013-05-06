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

import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.cloudsearch.model.RequestModel;
import com.cloudsearch.oauth.CredentialMediator;
import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * Servlet that returns the profile of the currently logged-in user.
 * 
 * @author nivco@google.com (Nicolas Garnier)
 */
@Path("/userService")
public class UserService extends CloudSearchService {
	static Logger log = Logger.getLogger(UserService.class);

	/**
	 * Returns a JSON representation of the user's profile.
	 * 
	 * @return
	 */
	@GET
	@Path("/getUserInfo")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUserInfo(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {
		RequestModel httpRequest = new RequestModel(state, userId, code, email);

		Oauth2 service = null;
		try {
			service = getOauth2Service(httpRequest);
		} catch (NoRefreshTokenException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Userinfo about = service.userinfo().get().execute();
			about.setId(CredentialMediator.modifyUserIdByScope(about.getId(),
					USER_SCOPES));
			System.out.println(about.toString());
			// httpResponse.setContentType(JSON_MIMETYPE);
			// httpResponse.getWriter().print(new Gson().toJson(about));
			return about.toString();
			//return new Gson().toJson(about);
		} catch (GoogleJsonResponseException e) {
			if (e.getStatusCode() == 401) {
				// The user has revoked our token or it is otherwise bad.
				// Delete the local copy so that their next page load will
				// recover.
				try {
					deleteCredential(httpRequest, USER_SCOPES);
				} catch (NoRefreshTokenException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// sendError(httpResponse, 401, "Unauthorized");
				return null;
			}
		}
		return null;
	}

	/**
	 * Build and return an Oauth2 service object based on given request
	 * parameters.
	 * 
	 * @param req
	 *            Request to use to fetch code parameter or accessToken session
	 *            attribute.
	 * @param resp
	 *            HTTP response to use for redirecting for authorization if
	 *            needed.
	 * @return Drive service object that is ready to make requests, or null if
	 *         there was a problem.
	 * @throws NoRefreshTokenException
	 */
	private Oauth2 getOauth2Service(RequestModel req)
			throws NoRefreshTokenException {
		Credential credentials = getCredential(req, USER_SCOPES);
		return new Oauth2.Builder(TRANSPORT, JSON_FACTORY, credentials).build();
	}
}