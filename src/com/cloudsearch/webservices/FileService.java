package com.cloudsearch.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.cloudsearch.mediator.GdriveDocumentMediator;
import com.cloudsearch.model.GdriveDocument;
import com.cloudsearch.model.RequestModel;
import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 * Servlet providing a small API for the DrEdit JavaScript client to use in
 * manipulating files. Each operation (GET, POST, PUT) issues requests to the
 * Google Drive API.
 * 
 */
@Path("/file")
public class FileService extends CloudSearchService {
	// HttpServletRequest httpRequest ;
	// HttpServletResponse httpResponse ;

	@GET
	@Path("/index")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> doList(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {
		code = (code.equals("")) ? null : code;
		email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		state = (state.equals("")) ? null : state;
		RequestModel httpRequest = new RequestModel(state, userId, code, email);
		String url = null;
		Map<String, String> jsonMap = new HashMap<String, String>();

		Drive service = null;
		try {
			service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e) {
			e.getAuthorizationUrl();
			url = e.getAuthorizationUrl();
			System.out.println("Sending for authorization");
		}
		if (service == null) {
			jsonMap.put("url", url); // google authentication url
			jsonMap.put("http_code", "307");// temporary redirect to google
			return jsonMap;
		} else {
			if(code != null){
				userId = httpRequest.getUserId();
			}
			Boolean success = indexDocuments(service, userId);
			jsonMap.put("http_code", "200");
			jsonMap.put("success", success.toString());
			jsonMap.put("email", httpRequest.getEmail());
			jsonMap.put("userId", httpRequest.getUserId());
			
			return jsonMap;
		}
	}
 
	public static boolean indexDocuments(Drive service, String userId) {
		GdriveDocumentMediator mediator = new GdriveDocumentMediator(service,
				userId);
		ArrayList<GdriveDocument> documents = mediator
				.createIndexableDocuments();
		System.out.println("Total Docs " + documents.size());
		int i = 0 ;
		for (GdriveDocument gdriveDocument : documents) {
			System.out.println("Remaining docs : " + (documents.size()-i));
			gdriveDocument = mediator.updateData(gdriveDocument);
			mediator.sendToSearchEngine(gdriveDocument);
			i++;
		}
		return true;
	}

	public Drive getDriveService(RequestModel req)
			throws NoRefreshTokenException {
		Credential credentials = getCredential(req, DRIVE_SCOPE);

		return new Drive.Builder(TRANSPORT, JSON_FACTORY, credentials).build();
	}
}