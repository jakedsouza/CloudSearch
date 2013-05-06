package com.cloudsearch.model;

import org.apache.log4j.Logger;

import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.owlike.genson.annotation.JsonProperty;

public class User {
	static Logger log = Logger.getLogger(User.class);

	String cloudSearchID;
	@JsonProperty(value="id")
	String googleAuthId ;	
	@JsonProperty(value="email")
	String email;
	@JsonProperty(value="verified_email")
	boolean verifiedEmail;
	@JsonProperty
	String name;
	@JsonProperty(value="given_name")
	String givenName;
	@JsonProperty(value="family_name")
	String familyName;
	@JsonProperty(value="link")
	String link;
	@JsonProperty(value="picture")
	String picture;
	@JsonProperty(value="gender")
	String gender;
	@JsonProperty(value="birthday")
	String birthday;
	@JsonProperty(value="locale")
	String locale;
	boolean isGoogleDriveEnabled;
	boolean isDropboxEnabled;
	 
}
