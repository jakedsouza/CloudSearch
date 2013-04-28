package com.cloudsearch.oauth;

import java.io.InputStream;
import java.util.Collection;

import com.cloudsearch.model.RequestModel;
import com.google.api.services.drive.model.File;

public class tdaf extends CredentialMediator{

	public tdaf(RequestModel request, InputStream clientSecretsStream,
			Collection<String> scopes) throws InvalidClientSecretsException {
		super(request, clientSecretsStream, scopes);
		// TODO Auto-generated constructor stub
	}

	public  static String modifyUserIdByScope(String userId,Collection<String> scopes){
		return userId;
		
	}
	
	public static void main(String[] args) throws InvalidClientSecretsException {
//		CredentialMediator mediator = new tdaf(null, null, null);
//		tdaf tdaf = new tdaf(null, null, null);
//		CredentialMediator mediator2 = (CredentialMediator) new tdaf(null, null, null);
//		
//		File file ;
		
		
		
		
	}
	
}
