package com.cloudsearch.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.cloudsearch.dao.DropboxPersistedToken;
import com.cloudsearch.model.DropBoxDocument;
import com.cloudsearch.model.RequestModel;
import com.cloudsearch.oauth.DropboxAuthHelper;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.WebAuthSession;

@Path("/dropbox")
public class DropBoxService {

	static Logger log = Logger.getLogger(DropBoxService.class);

	@GET
	@Path("/indexBad")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> doList(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email) {
		code = (code.equals("")) ? null : code;
		email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		state = (state.equals("")) ? null : state;
		RequestModel httpRequest = new RequestModel(state, userId, code, email);
		log.info("Dropbox service called " + httpRequest.toString());

		return null;

	}

	@GET
	@Path("/index")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> dropboxDownload(
			@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("code") String code, @QueryParam("email") String email,
			@QueryParam("uid") String uid,
			@QueryParam("oauth_token") String outhToken)
			throws DropboxException {

		//code = (code.equals("")) ? null : code;
		//email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		//state = (state.equals("")) ? null : state;
		uid = (uid.equals("")) ? null : uid;
		outhToken = (outhToken.equals("")) ? null : outhToken;
		RequestModel httpRequest = new RequestModel(state, userId, code, email);
		DropboxAuthHelper helper = null;
		if (uid == null && outhToken == null) {
			// return "Error occured";
			// helper = new DropboxAuthHelper();
		} else {
			DropboxPersistedToken storeTokenPair = DropboxAuthHelper.store
					.load(outhToken);
			RequestTokenPair requestTokenPair = new RequestTokenPair(
					storeTokenPair.getRequestTokenPairKey(),
					storeTokenPair.getRequestTokenPairSecret());
			helper = new DropboxAuthHelper(requestTokenPair,httpRequest);
		}
		Map<String, String> jsonMap = new HashMap<String, String>();

		// helper.indexAllFiles();
		Boolean success = helper.indexAllFiles();
		jsonMap.put("http_code", "200");
		jsonMap.put("success", success.toString());
		log.info("Successfully indexed :" + jsonMap);
		return jsonMap;
	}

	@GET
	@Path("/getDropboxloginUrl")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDropboxlogin() {
		RequestModel request = new RequestModel(null, null, null, null);
		DropboxAuthHelper helper = new DropboxAuthHelper(request);
		return helper.buildLoginUrl();
	}
}
