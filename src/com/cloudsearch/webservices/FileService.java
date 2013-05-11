package com.cloudsearch.webservices;

import java.io.IOException;
import java.net.URI;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

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
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.sun.jersey.api.client.Client;

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
	static Logger log = Logger.getLogger(FileService.class);

	@GET
	@Path("/index")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> doList(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("gDriveuserId") String gDriveuserId,
			@QueryParam("code") String code, @QueryParam("email") String email,
			@Context UriInfo uriInfo) throws IOException {
		code = (code.equals("")) ? null : code;
		email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		gDriveuserId = (gDriveuserId.equals("")) ? null : gDriveuserId;
		state = (state.equals("")) ? null : state;
		RequestModel httpRequest = new RequestModel(state, gDriveuserId, code,
				email);
		log.info("File service called " + httpRequest.toString());

		String url = null;
		Map<String, String> jsonMap = new HashMap<String, String>();

		Drive service = null;
		try {
			service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e) {
			e.getAuthorizationUrl();
			url = e.getAuthorizationUrl();

		}
		if (service == null) {
			jsonMap.put("url", url); // google authentication url
			jsonMap.put("http_code", "307");// temporary redirect to google
			log.info("Sending for authorization");
			log.info(jsonMap);
			return jsonMap;
		} else {
			if (code != null) {
				// userId = httpRequest.getUserId();
			}

			UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
			uriBuilder = uriBuilder.path("/file/indexDocs");
			uriBuilder.queryParam("email", httpRequest.getEmail() == null ? ""
					: httpRequest.getEmail());
			uriBuilder.queryParam("state", httpRequest.getState() == null ? ""
					: httpRequest.getState());
			uriBuilder.queryParam("userId", userId == null ? "" : userId);
			uriBuilder.queryParam("code", httpRequest.getCode() == null ? ""
					: httpRequest.getCode());
			uriBuilder.queryParam(
					"gDriveuserId",
					httpRequest.getUserId() == null ? "" : httpRequest
							.getUserId());

			URI uri = uriBuilder.build();
			AsyncHttpClient client = new AsyncHttpClient();
			BoundRequestBuilder builder = client.prepareGet(uri.toURL()
					.toString());
			ListenableFuture<com.ning.http.client.Response> resp1 = builder.execute();
			
			// Boolean success = indexDocuments(service, userId);
			jsonMap.put("http_code", "200");
			jsonMap.put("success", "true");
			jsonMap.put("email", httpRequest.getEmail());
			jsonMap.put("userId", httpRequest.getUserId());
			log.info("Successfully indexed :" + jsonMap);
			return jsonMap;
		}
	}

	@GET
	@Path("/indexDocs")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean indexDocuments(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("gDriveuserId") String gDriveuserId,
			@QueryParam("code") String code, @QueryParam("email") String email,
			@Context UriInfo uriInfo) {

		code = (code.equals("")) ? null : code;
		email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		gDriveuserId = (gDriveuserId.equals("")) ? null : gDriveuserId;
		state = (state.equals("")) ? null : state;
		RequestModel httpRequest = new RequestModel(state, gDriveuserId, code,
				email);
		log.info("Indexing files " + httpRequest.toString());

		String url = null;
		Map<String, String> jsonMap = new HashMap<String, String>();

		Drive service = null;
		try {
			service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e) {
			log.error("Auth exception");
			return false ;
		}
		GdriveDocumentMediator mediator = new GdriveDocumentMediator(service,
				userId);
		ArrayList<GdriveDocument> documents = mediator
				.createIndexableDocuments();
		log.info("Total Docs " + documents.size());
		int i = 0;
		for (GdriveDocument gdriveDocument : documents) {
			log.info("Remaining docs : " + (documents.size() - i));
			gdriveDocument = mediator.updateData(gdriveDocument);
			mediator.sendToSearchEngine(gdriveDocument);
			System.gc();
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