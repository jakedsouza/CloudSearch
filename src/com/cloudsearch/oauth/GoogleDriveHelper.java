package com.cloudsearch.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class GoogleDriveHelper {

  private static String CLIENT_ID = "972332122073-56tbnko1n7ku10ehcts1v8ohcvvueda2.apps.googleusercontent.com";
  private static String CLIENT_SECRET = "ux4ydPmVAtzlwz4phP7kQ_6t";

  private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
  
  public static void main(String[] args) throws IOException {
    HttpTransport httpTransport = new NetHttpTransport();
    JsonFactory jsonFactory = new JacksonFactory();
   
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
        .setAccessType("online")
        .setApprovalPrompt("auto").build();
    
    String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    System.out.println("Please open the following URL in your browser then type the authorization code:");
    System.out.println("  " + url);
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String code = br.readLine();
    
    GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
    
    //Create a new authorized API client
    Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
//
//    //Insert a file  
    File body = new File();
//    body.setTitle("My document");
//    body.setDescription("A test document");
//    body.setMimeType("text/plain");
//    
//    java.io.File fileContent = new java.io.File("document.txt");
//    FileContent mediaContent = new FileContent("text/plain", fileContent);
//
//    File file = service.files().insert(body, mediaContent).execute();
//    System.out.println("File ID: " + file.getId());
    //System.out.println(retrieveAllFiles(service));
    List<File>  files= retrieveAllFiles(service);
    
    for (File file : files) {
    	printFile(service, file.getId());	
    	downloadFile(service, file);
	}
    
  }
  
  private static void printFile(Drive service, String fileId) {

	    try {
	      File file = service.files().get(fileId).execute();

	      System.out.println("Title: " + file.getTitle());
	      System.out.println("Description: " + file.getDescription());
	      System.out.println("MIME type: " + file.getMimeType());
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	    }
	  }

	  /**
	   * Download a file's content.
	   *
	   * @param service Drive API service instance.
	   * @param file Drive File instance.
	   * @return InputStream containing the file's content if successful,
	   *         {@code null} otherwise.
	   */
	  private static void downloadFile(Drive service, File file) {
	    if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
	      try {
	        HttpResponse resp =
	            service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
	                .execute();
	        InputStream inputStream = resp.getContent();
	        
	    	OutputStream out = new FileOutputStream(new java.io.File("data/"+file.getTitle()));
	    	int read = 0;
	    	byte[] bytes = new byte[102400];
	     
	    	while ((read = inputStream.read(bytes)) != -1) {
	    		out.write(bytes, 0, read);
	    	}
	     
	    	inputStream.close();
	    	out.flush();
	    	out.close();
	      } catch (IOException e) {
	        // An error occurred.
	        e.printStackTrace();
	       // return null;
	      }
	    } else {
	      // The file doesn't have any content stored on Drive.
	     // return null;
	    }
	  }

  private static List<File> retrieveAllFiles(Drive service) throws IOException {
	    List<File> result = new ArrayList<File>();
	    Files.List request = service.files().list();

	    do {
	      try {
	        FileList files = request.execute();

	        result.addAll(files.getItems());
	        request.setPageToken(files.getNextPageToken());
	      } catch (IOException e) {
	        System.out.println("An error occurred: " + e);
	        request.setPageToken(null);
	      }
	    } while (request.getPageToken() != null &&
	             request.getPageToken().length() > 0);

	    return result;
	  }
}