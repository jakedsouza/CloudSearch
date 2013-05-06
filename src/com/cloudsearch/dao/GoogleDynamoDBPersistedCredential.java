package com.cloudsearch.dao;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.google.api.client.auth.oauth2.Credential;

@DynamoDBTable(tableName="cloudsearch")
public class GoogleDynamoDBPersistedCredential {
	static Logger log = Logger.getLogger(GoogleDynamoDBPersistedCredential.class);

	private String userId;
	private String accessToken;
	private String refreshToken;
	private long expirationTimeMillis;

	/**
	 * @param userId
	 *            user ID whose credential needs to be stored
	 * @param credential
	 *            credential whose {@link Credential#getAccessToken access
	 *            token}, {@link Credential#getRefreshToken refresh token}, and
	 *            {@link Credential#getExpirationTimeMilliseconds expiration
	 *            time} need to be stored
	 */
	public GoogleDynamoDBPersistedCredential(){
		
	}
	
	public GoogleDynamoDBPersistedCredential(String userId, Credential credential) {
		if(userId == null){
			throw new NullPointerException("User ID Is null !!");
		}
		this.userId = userId;
		accessToken = credential.getAccessToken();
		refreshToken = credential.getRefreshToken();
		expirationTimeMillis = credential.getExpirationTimeMilliseconds();
	}
	public GoogleDynamoDBPersistedCredential(String userId,String accessToken,String refreshToken,Long expirationTimeMilliseconds) {
		if(userId == null){
			throw new NullPointerException("User ID Is null !!");
		}
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expirationTimeMillis = expirationTimeMilliseconds;
	}

	/**
	 * @param credential
	 *            credential whose {@link Credential#setAccessToken access
	 *            token}, {@link Credential#setRefreshToken refresh token}, and
	 *            {@link Credential#setExpirationTimeMilliseconds expiration
	 *            time} need to be set if the credential already exists in
	 *            storage
	 */
	void load(Credential credential) {
		credential.setAccessToken(accessToken);
		credential.setRefreshToken(refreshToken);
		credential.setExpirationTimeMilliseconds(expirationTimeMillis);
	}
    @DynamoDBHashKey(attributeName="userId")
	public String getUserId() {
		return userId;
	}

    @DynamoDBAttribute(attributeName="accessToken")
	public String getAccessToken() {
		return accessToken;
	}
    
    @DynamoDBAttribute(attributeName="refreshToken")
	public String getRefreshToken() {
		return refreshToken;
	}
    @DynamoDBAttribute(attributeName="expirationTimeMillis")
	public long getExpirationTimeMillis() {
		return expirationTimeMillis;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setExpirationTimeMillis(long expirationTimeMillis) {
		this.expirationTimeMillis = expirationTimeMillis;
	}

}
