package com.cloudsearch.dao;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;

public class GoogleDynamoDBCredentialStore implements CredentialStore {
	private AmazonDynamoDBClient client;
	private AWSCredentials credentials;

	public GoogleDynamoDBCredentialStore() {
		if (client != null && credentials != null) {
			return;
		}

		try {
			credentials = new PropertiesCredentials(
					GoogleDynamoDBCredentialStore.class
							.getResourceAsStream("/AwsCredentials.properties"));
			client = new AmazonDynamoDBClient(credentials);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean load(String userId, Credential credential) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		GoogleDynamoDBPersistedCredential persistedCredential = mapper.load(
				GoogleDynamoDBPersistedCredential.class, userId);
		persistedCredential.load(credential);
		return true;
	}

	@Override
	public void store(String userId, Credential credential) throws IOException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		GoogleDynamoDBPersistedCredential persistedCredential = new GoogleDynamoDBPersistedCredential(
				userId, credential);
		mapper.save(persistedCredential);
	}
	public void store(String userId, GoogleDynamoDBPersistedCredential credential) throws IOException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
//		GoogleDynamoDBPersistedCredential persistedCredential = new GoogleDynamoDBPersistedCredential(
//				userId, credential);
		mapper.save(credential);
	}
	@Override
	public void delete(String userId, Credential credential) throws IOException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		GoogleDynamoDBPersistedCredential persistedCredential = new GoogleDynamoDBPersistedCredential(
				userId, credential);
		mapper.delete(persistedCredential);
	}
	
	public static void main(String[] args) throws IOException {
		GoogleDynamoDBPersistedCredential credential = new GoogleDynamoDBPersistedCredential("1","1","1",(long)1 );
		GoogleDynamoDBCredentialStore store = new GoogleDynamoDBCredentialStore();
		//store.load("105248847399825755850", credential);		
	}

}
