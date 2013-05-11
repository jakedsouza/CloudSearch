package com.cloudsearch.webservices;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
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

import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.cloudsearch.mediator.ContactDocumentMediator;
import com.cloudsearch.mediator.GdriveDocumentMediator;
import com.cloudsearch.model.GdriveDocument;
import com.cloudsearch.model.GoogleContact;
import com.cloudsearch.model.RequestModel;
import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.gdata.client.AuthTokenFactory.AuthToken;
import com.google.gdata.client.GoogleAuthTokenFactory;
import com.google.gdata.client.GoogleAuthTokenFactory.OAuth2Token;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.xml.XmlWriter;

@Path("/contact")
public class ContactService extends CloudSearchService {
	static Logger log = Logger.getLogger(ContactService.class);

	@GET
	@Path("/index")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> doList(@QueryParam("state") String state,
			@QueryParam("userId") String userId,
			@QueryParam("contactUserId") String contactUserId,
			@QueryParam("code") String code, @QueryParam("email") String email)
			throws IOException {
		code = (code.equals("")) ? null : code;
		email = (email.equals("")) ? null : email;
		userId = (userId.equals("")) ? null : userId;
		contactUserId = (contactUserId.equals("")) ? null : contactUserId;
		state = (state.equals("")) ? null : state;
		RequestModel httpRequest = new RequestModel(state, contactUserId, code,
				email);
		log.info("Contact service called " + httpRequest.toString());
		String url = null;
		Map<String, String> jsonMap = new HashMap<String, String>();
		ContactsService service = null;
		try {
			service = getContactService(httpRequest);
		} catch (NoRefreshTokenException e) {
			e.getAuthorizationUrl();
			url = e.getAuthorizationUrl();
		}

		if (service == null) {
			jsonMap.put("url", url); // google authentication url
			jsonMap.put("http_code", "307");// temporary redirect to google
			log.info("Sending for authorization");
			log.info(jsonMap);
			return jsonMap;
		} else {
			if (code != null) {
				// userId = httpRequest.getUserId();
			}
			Boolean success = null;
			try {
				success = indexContacts(service, userId,httpRequest.getAccessToken());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonMap.put("http_code", "200");
			jsonMap.put("success", success.toString());
			jsonMap.put("email", httpRequest.getEmail());
			jsonMap.put("userId", httpRequest.getUserId());
			log.info("Successfully indexed :" + jsonMap);
			return jsonMap;
		}

	}

	public ContactsService getContactService(RequestModel req)
			throws NoRefreshTokenException, MalformedURLException {
		Credential credentials = getCredential(req, CONTACT_SCOPE);

		ContactsService contactsService = new ContactsService("Cloud Search");
		contactsService.setOAuth2Credentials(credentials);
		req.setAccessToken(credentials.getAccessToken());
		return contactsService;

	}

	public static boolean indexContacts(ContactsService service, String userId,String accessToken)
			throws IOException, ServiceException {
		URL feedUrl = null;
		feedUrl = new URL(
				"https://www.google.com/m8/feeds/contacts/default/full?max-results=10000");
	//	ContactDocumentMediator.printAllContacts(service);
		ContactDocumentMediator mediator = new ContactDocumentMediator(service,
				userId);
		ContactFeed resultFeed = service.getFeed(feedUrl, ContactFeed.class);
		List<ContactEntry> contactEntries = resultFeed.getEntries();
		log.info("Total Contacts : " + contactEntries.size());
		int i = 0 ;
		for (ContactEntry contactEntry : contactEntries) {			
			    GoogleContact c = new GoogleContact(contactEntry,accessToken);
			System.out.println(c.getPhotoLink());
			mediator.sendToSearchEngine(c);
			log.info("Total Contacts : " + (contactEntries.size()-i));
			i++;
		}
		return true;
	}
}
