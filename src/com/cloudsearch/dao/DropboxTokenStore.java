package com.cloudsearch.dao;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.dropbox.client2.session.RequestTokenPair;
import com.google.api.client.auth.oauth2.Credential;

public class DropboxTokenStore {
	static Logger log = Logger.getLogger(DropboxTokenStore.class);
	private AmazonDynamoDBClient client;
	private AWSCredentials credentials;
	
	public DropboxTokenStore() {
		if (client != null && credentials != null) {
			return;
		}

		try {
			credentials = new PropertiesCredentials(
					DropboxTokenStore.class
							.getResourceAsStream("/AwsCredentials.properties"));
			client = new AmazonDynamoDBClient(credentials);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DropboxPersistedToken load(String requestTokenPairKey) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DropboxPersistedToken persistedToken = mapper.load(
				DropboxPersistedToken.class, requestTokenPairKey);
		//persistedCredential.load(credential);
		return persistedToken;
	}
	
	public void store(RequestTokenPair requestTokenPair) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DropboxPersistedToken persistedToken = new DropboxPersistedToken(requestTokenPair);
		mapper.save(persistedToken);
	}
	
	public void delete(RequestTokenPair requestTokenPair)  {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DropboxPersistedToken persistedToken = new DropboxPersistedToken(requestTokenPair);
		mapper.delete(persistedToken);
	}
	
	public static void main(String[] args) {
		RequestTokenPair requestTokenPair = new RequestTokenPair("jakeKey", "jakeSecret");
		DropboxPersistedToken token = new DropboxPersistedToken(requestTokenPair);
		DropboxTokenStore store = new DropboxTokenStore();
		//store.store(requestTokenPair);
		//DropboxPersistedToken persistedToken = store.load("jakeKey");
		//RequestTokenPair tokenPair  = new RequestTokenPair(persistedToken.getRequestTokenPairKey(), persistedToken.getRequestTokenPairSecret());
		//System.out.println(tokenPair);
		store.delete(requestTokenPair);
		
		
	}
}
