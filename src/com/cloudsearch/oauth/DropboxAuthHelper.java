package com.cloudsearch.oauth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import com.cloudsearch.dao.DropboxTokenStore;
import com.cloudsearch.mediator.GdriveDocumentMediator;
import com.cloudsearch.model.DropBoxDocument;
import com.cloudsearch.model.GdriveDocument;
import com.cloudsearch.model.RequestModel;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import com.owlike.genson.Genson;
import com.owlike.genson.reflect.VisibilityFilter;

public class DropboxAuthHelper {
	static Logger log = Logger.getLogger(DropboxAuthHelper.class);

	private final String APP_KEY = "9k735vtgqpp2rvi";
	private final String APP_SECRET = "p5j7jousyuil801";
	private final AccessType ACCESS_TYPE = AccessType.DROPBOX;
	public DropboxAPI<WebAuthSession> mDBApi;
	private WebAuthSession session;
	private AccessTokenPair tokenPair;

	private RequestModel request;

	// retains alphabets, numbers and whitespaces
	private static CharMatcher matcher = CharMatcher.inRange('a', 'z')
			.or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.is(' '))
			.or(CharMatcher.inRange('0', '9'))
			.or(CharMatcher.BREAKING_WHITESPACE).precomputed();
	public static Set<String> ALLOWED_MIME_TYPES = Sets
			.newHashSet(
					"text/html",
					"text/plain",
					"application/rtf",
					"text/css",
					"application/xml",
					"text/x-java",
					"application/vnd.oasis.opendocument.text",
					"application/pdf",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					"application/x-vnd.oasis.opendocument.spreadsheet",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation",
					"application/vnd.oasis.opendocument.text",
					"application/x-vnd.oasis.opendocument.spreadsheet");

	// public static Map<String, RequestTokenPair> inMemoryTokens = new
	// HashMap<String, RequestTokenPair>();
	public static DropboxTokenStore store = new DropboxTokenStore();

	public DropboxAuthHelper(RequestModel request) {
		this.request = request;
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		session = new WebAuthSession(appKeys, ACCESS_TYPE);

		mDBApi = new DropboxAPI<WebAuthSession>(session);
		tokenPair = mDBApi.getSession().getAccessTokenPair();

		// Initialize DropboxAPI object

		DropboxAPI.Entry entry = new DropboxAPI.Entry();
	}

	public DropboxAuthHelper(RequestTokenPair requestTokenPair,RequestModel request)
			throws DropboxException {
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		this.request = request;

		session = new WebAuthSession(appKeys, ACCESS_TYPE);
		session.retrieveWebAccessToken(requestTokenPair);
		mDBApi = new DropboxAPI<WebAuthSession>(session);

	}

	public String buildLoginUrl() {
		mDBApi = new DropboxAPI<WebAuthSession>(session);
		AccessTokenPair tokenPair = mDBApi.getSession().getAccessTokenPair();
		try {
			WebAuthSession session = mDBApi.getSession();
			// WebAuthInfo info = session
			// .getAuthInfo("http://localhost:8080/CloudSearch/rest/dropbox/download");
			WebAuthInfo info = session
					.getAuthInfo("http://localhost:8080/CloudSearch/");

			// info.
			store.store(info.requestTokenPair);
			// inMemoryTokens.put(info.requestTokenPair.key,
			// info.requestTokenPair);
			return info.url;
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Boolean indexAllFiles() throws DropboxException {
		ArrayList<DropBoxDocument> allFiles = getAllFiles();
		log.info("Total Docs " + allFiles.size());
		int i = 0;
		for (DropBoxDocument dropBoxDocument : allFiles) {
			log.info("Remaining docs : " + (allFiles.size() - i));
			dropBoxDocument = updateData(dropBoxDocument);
			sendToSearchEngine(dropBoxDocument);
			i++;
		}
		// similar code if file mime type is cool get the contents and index
		// else just index and send to search engine
		// return allFiles;
		return true;
	}

	public ArrayList<DropBoxDocument> getAllFiles() {
		ArrayList<DropBoxDocument> resultDocuments = new ArrayList<DropBoxDocument>();

		Queue<DropBoxDocument> queue = new LinkedList<DropBoxDocument>();
		ArrayList<DropBoxDocument> rootDocuments = getRootFiles();
		resultDocuments.addAll(rootDocuments);
		// init queue with root directories
		for (DropBoxDocument dropBoxDocument : rootDocuments) {
			if (dropBoxDocument.isDir()) {
				queue.add(dropBoxDocument);
			}
		}

		while (!queue.isEmpty()) {
			DropBoxDocument doc = queue.remove();
			ArrayList<DropBoxDocument> children = getFiles(doc.getPath());
			resultDocuments.addAll(children);
			// for each child document
			for (DropBoxDocument document : children) {
				if (document.isDir()) {
					queue.add(document);
				}
			}
		}
		return resultDocuments;
	}

	/**
	 * Gets a list of file in the users' root directory
	 * 
	 * @return a list of files in the users' root directory
	 */
	public ArrayList<DropBoxDocument> getRootFiles() {

		final String ROOT = "/";
		Entry entry = null;
		try {
			entry = mDBApi.metadata(ROOT, 0, null, true, null);
		} catch (DropboxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<Entry> list;
		ArrayList<DropBoxDocument> files = new ArrayList<DropBoxDocument>(100);

		if (!entry.isDir) {
			list = null;
			throw new Error("Given path is not a Folder");

		} else {
			list = entry.contents;

			for (Entry e : list) {
				files.add(new DropBoxDocument(e));
			}
		}
		return files;
	}

	public ArrayList<DropBoxDocument> getFiles(String directoryPath) {

		Entry entry = null;
		try {
			entry = mDBApi.metadata(directoryPath, 0, null, true, null);
		} catch (DropboxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (entry == null) {
			throw new Error("Specified path (" + directoryPath
					+ ") was not found");
		}

		List<Entry> list;
		ArrayList<DropBoxDocument> files = new ArrayList<DropBoxDocument>(100);

		if (!entry.isDir) {
			list = null;
			throw new Error("Given path is not a Folder");
		} else {
			list = entry.contents;
			for (Entry e : list) {
				files.add(new DropBoxDocument(e));
			}
		}
		return files;
	}

	public boolean isDownloadable(String mimetype) {
		if(mimetype == null)
			return false;
		String mimeType = mimetype.toLowerCase();
		boolean isValidMIME = (GdriveDocument.ALLOWED_MIME_TYPES
				.contains(mimeType)) ? true : false;
		return isValidMIME;
	}

	public void sendToSearchEngine(List<DropBoxDocument> documents) {
		if (documents == null) {
			return;
		}
		for (DropBoxDocument dropBoxDocument : documents) {
			sendToSearchEngine(dropBoxDocument);
		}
	}

	public DropBoxDocument updateData(DropBoxDocument document) {
		if (document.getData() != null) {
			return document;
		}
		if (isDownloadable(document.getMimeType())) {
			DropboxInputStream input = null;
			try {
				input = mDBApi.getFileStream(document.getPath(), null);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String data = getFileData(input);
			document.setData(data);
		}
		return document;
	}

	public void sendToSearchEngine(DropBoxDocument document) {
		try {
			String url = "http://localhost:9200/";
			url = url + request.getUserId().toLowerCase() + "/db/" + document.getId();
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut putRequest = new HttpPut(url);
			Genson genson = new Genson.Builder().setUseGettersAndSetters(false)
					.setFieldFilter(VisibilityFilter.ALL).create();
			// genson.serialize(document);
			// StringEntity input = new StringEntity(new
			// Gson().toJson(document));
			String doc = genson.serialize(document);
			StringEntity input = new StringEntity(genson.serialize(document));
			input.setContentType("application/json");
			putRequest.setEntity(input);

			org.apache.http.HttpResponse response = httpClient
					.execute(putRequest);

			if (response.getStatusLine().getStatusCode() != 201) {
				// throw new RuntimeException("Failed : HTTP error code : "
				// + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));
			String output;
			log.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				log.info(output);
			}

			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getFileData(InputStream input) {
		if (input == null) {
			return null;
		}

		BodyContentHandler textHandler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		ParseContext contxt = new ParseContext();
		AutoDetectParser parser = new AutoDetectParser();
		try {
			parser.parse(input, textHandler, metadata, contxt);
			input.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String alphaAndDigits = matcher.retainFrom(textHandler.toString());
		alphaAndDigits.toLowerCase();
		// removes extra whitespaces (of any kind)
		Pattern whitespace = Pattern.compile("\\s+");
		Matcher whiteMatcher = whitespace.matcher(alphaAndDigits);
		if (whiteMatcher.find())
			alphaAndDigits = whiteMatcher.replaceAll(" ");
		return alphaAndDigits;
	}
}
