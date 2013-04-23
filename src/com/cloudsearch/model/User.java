package com.cloudsearch.model;

import com.owlike.genson.annotation.Creator;
import com.owlike.genson.annotation.JsonProperty;

public class User {

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
	@JsonProperty(value="pinture")
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