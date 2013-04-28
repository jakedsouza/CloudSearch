package com.cloudsearch.oauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;

public class MySQLCredentialStore implements CredentialStore {

	@Override
	public boolean load(String userId, Credential credential) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void store(String userId, Credential credential) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String userId, Credential credential) {
		// TODO Auto-generated method stub

	}

}
