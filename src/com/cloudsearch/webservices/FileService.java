package com.cloudsearch.webservices;

import java.io.IOException;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cloudsearch.abstractwebservices.CloudSearchService;
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
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public void doList(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {

		RequestModel httpRequest = new RequestModel(state, userId, code, email);

		Drive service = null;
		try {
			service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Files files = service.files();
		List list = files.list();
		FileList fileList = list.execute();
		java.util.List<File> list2 = fileList.getItems();
		for (File file : list2) {
			System.out.println(file.getTitle());
		}

	}

	/**
	 * Given a {@code file_id} URI parameter, return a JSON representation of
	 * the given file.
	 */
	public void doGet(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {
		// HttpServletRequest httpRequest = reqT.get();
		// HttpServletResponse httpResponse= resT.get();
		RequestModel httpRequest = new RequestModel(state, userId, code, email);

		Drive service = null;
		try {
			service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String fileId = httpRequest.getParameter("file_id");

		if (fileId == null) {
			// sendError(httpResponse, 400,
			// "The `file_id` URI parameter must be specified.");
			return;
		}

		File file = null;
		try {
			file = service.files().get(fileId).execute();
		} catch (GoogleJsonResponseException e) {
			if (e.getStatusCode() == 401) {
				// The user has revoked our token or it is otherwise bad.
				// Delete the local copy so that their next page load will
				// recover.
				try {
					deleteCredential(httpRequest, DRIVE_SCOPE);
				} catch (NoRefreshTokenException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// sendError(httpResponse, 401, "Unauthorized");
				return;
			}
		}

		if (file != null) {
			String content = downloadFileContent(service, file);
			if (content == null) {
				content = "";
			}
			// httpResponse.setContentType(JSON_MIMETYPE);
			// httpResponse.getWriter().print(new ClientFile(file,
			// content).toJson());
		} else {
			// sendError(httpResponse, 404, "File not found");
		}
	}

	/**
	 * Create a new file given a JSON representation, and return the JSON
	 * representation of the created file.
	 */

	public void doPost(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {
		// HttpServletRequest httpRequest = reqT.get();
		// HttpServletResponse httpResponse= resT.get();
		RequestModel httpRequest = new RequestModel(state, userId, code, email);

		try {
			Drive service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ClientFile clientFile = new ClientFile(httpRequest.getReader());
		// File file = clientFile.toFile();
		//
		// if (!clientFile.content.equals("")) {
		// file = service.files().insert(file,
		// ByteArrayContent.fromString(clientFile.mimeType, clientFile.content))
		// .execute();
		// } else {
		// file = service.files().insert(file).execute();
		// }

		// httpResponse.setContentType(JSON_MIMETYPE);
		// httpResponse.getWriter().print(new
		// Gson().toJson(file.getId()).toString());
	}

	/**
	 * Update a file given a JSON representation, and return the JSON
	 * representation of the created file.
	 */

	public void doPut(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {
		// HttpServletRequest httpRequest = reqT.get();
		// HttpServletResponse httpResponse= resT.get();
		RequestModel httpRequest = new RequestModel(state, userId, code, email);

		boolean newRevision = httpRequest.getParameter("newRevision").equals(
				Boolean.TRUE);
		try {
			Drive service = getDriveService(httpRequest);
		} catch (NoRefreshTokenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ClientFile clientFile = new ClientFile(httpRequest);
		// File file = clientFile.toFile();
		// // If there is content we update the given file
		// if (clientFile.content != null) {
		// file = service.files().update(clientFile.resource_id, file,
		// ByteArrayContent.fromString(clientFile.mimeType, clientFile.content))
		// .setNewRevision(newRevision).execute();
		// } else { // If there is no content we patch the metadata only
		// file = service.files().patch(clientFile.resource_id,
		// file).setNewRevision(newRevision).execute();
		// }

		// httpResponse.setContentType(JSON_MIMETYPE);
		// httpResponse.getWriter().print(new
		// Gson().toJson(file.getId()).toString());
	}

	/**
	 * Download the content of the given file.
	 * 
	 * @param service
	 *            Drive service to use for downloading.
	 * @param file
	 *            File metadata object whose content to download.
	 * @return String representation of file content. String is returned here
	 *         because this app is setup for text/plain files.
	 * @throws IOException
	 *             Thrown if the request fails for whatever reason.
	 */
	private String downloadFileContent(Drive service, File file)
			throws IOException {
		GenericUrl url = new GenericUrl(file.getDownloadUrl());
		HttpResponse response = service.getRequestFactory()
				.buildGetRequest(url).execute();
		try {
			return new Scanner(response.getContent()).useDelimiter("\\A")
					.next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}
//
//	private Map<String, String> returnDriveInfo() {
//		return null;
//
//	}

	/**
	 * Build and return a Drive service object based on given request
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
	private Drive getDriveService(RequestModel req)
			throws NoRefreshTokenException {
		Credential credentials = getCredential(req, DRIVE_SCOPE);

		return new Drive.Builder(TRANSPORT, JSON_FACTORY, credentials).build();
	}
}