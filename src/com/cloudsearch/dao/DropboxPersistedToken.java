package com.cloudsearch.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.dropbox.client2.session.RequestTokenPair;
import com.google.api.client.auth.oauth2.Credential;

@DynamoDBTable(tableName = "cloudsearch")
public class DropboxPersistedToken {
	private String key;
	private String requestTokenPairKey;
	private String requestTokenPairSecret;

	public DropboxPersistedToken() {

	}

	public DropboxPersistedToken(RequestTokenPair requestTokenPair) {
		if (requestTokenPair == null) {
			throw new NullPointerException("Key Is null !!");
		}
		this.requestTokenPairKey = requestTokenPair.key;
		this.requestTokenPairSecret = requestTokenPair.secret;
		this.key = requestTokenPair.key;
	}

	@DynamoDBHashKey(attributeName = "userId")
	public String getKey() {
		return key;
	}

	@DynamoDBAttribute(attributeName = "requestTokenPairKey")
	public String getRequestTokenPairKey() {
		return requestTokenPairKey;
	}

	@DynamoDBAttribute(attributeName = "requestTokenPairSecret")
	public String getRequestTokenPairSecret() {
		return requestTokenPairSecret;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setRequestTokenPairKey(String requestTokenPairKey) {
		this.requestTokenPairKey = requestTokenPairKey;
	}

	public void setRequestTokenPairSecret(String requestTokenPairSecret) {
		this.requestTokenPairSecret = requestTokenPairSecret;
	}

}
