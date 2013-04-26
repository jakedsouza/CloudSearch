package com.cloudsearch.test;

import java.io.File;
import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public class testStuff {

	public static void main(String args[]) throws IOException {
		String home = System.getProperty("user.home");
		File f = new File(home + "/a");
		JsonFactory JSON_FACTORY = new JacksonFactory();
	//	JSON_FACTORY.
		CredentialStore store = new FileCredentialStore(f, JSON_FACTORY);
		
		store.store("test1", buildEmptyCredential());
	//	boolean credential = store.load("test1", buildEmptyCredential());
		
		
	}
	
	private static Credential buildEmptyCredential() {
//	    Credential c= new GoogleCredential.Builder()
//	        .setClientSecrets(new GoogleClientSecrets())
//	        .setTransport(new NetHttpTransport())
//	        .setJsonFactory(new JacksonFactory())
//	        .build();
//	    c.setAccessToken("boo");
//	    c.setExpirationTimeMilliseconds((long) 10000);
//	    c.setRefreshToken("aaa");
		
	    return new GoogleCredential.Builder()
        .setClientSecrets(new GoogleClientSecrets())
        .setTransport(new NetHttpTransport())
        .setJsonFactory(new JacksonFactory())
        .build();
	    
	  }
}
