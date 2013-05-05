package com.cloudsearch.mediator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.cloudsearch.model.GdriveDocument;
import com.cloudsearch.model.RequestModel;
import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;
import com.cloudsearch.webservices.FileService;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpClient;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.owlike.genson.Genson;
import com.owlike.genson.reflect.VisibilityFilter;

public class GdriveDocumentMediator {

	private Drive drive;
	private String userID;
	private static Set<String> GET_ALL_MIME = new HashSet<String>();

	// retains alphabets, numbers and whitespaces
	private static CharMatcher matcher = CharMatcher.inRange('a', 'z')
			.or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.is(' '))
			.or(CharMatcher.inRange('0', '9'))
			.or(CharMatcher.BREAKING_WHITESPACE).precomputed();

	public GdriveDocumentMediator(Drive drive, String userID) {
		this.drive = drive;
		this.userID = userID.toLowerCase();

	}

	public ArrayList<GdriveDocument> createIndexableDocuments() {
		ArrayList<GdriveDocument> resultList = new ArrayList<GdriveDocument>();
		// get all users files
		List<File> allFiles = getAllFiles();
		// for each file convert into gdrivedocument and set data
		for (File file : allFiles) {
			GdriveDocument document = new GdriveDocument(file);
			// if (isDownloadable(file)) {
			// InputStream stream = getFileStream(file);
			// document.setData(downloadFile(stream));
			// }
			resultList.add(document);
		}
		return resultList;
	}

	public List<File> getAllFiles() {
		List<File> result = new ArrayList<File>();
		// FileComparator comparator = new FileComparator();
		// TreeSet<File> resultSet = new TreeSet<File>(comparator);
		// HashSet<File> resultSet = new HashSet<File>();
		// HashMap<String, File> hashMap = new HashMap<String,File>();
		Files.List request = null;
		long time = System.currentTimeMillis();
		try {
			request = drive.files().list();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		do {
			try {
				// System.out.println("Current size :" + result.size());

				// request.setMaxResults(999);
				// request.setMaxResults(Integer.MAX_VALUE);
				FileList files = request.execute();
				result.addAll(files.getItems());
				// resultSet.addAll(files.getItems());
				// List<File> list = files.getItems();
				// for (File file : list) {
				// hashMap.put(file.getId(),file);
				// }
				System.out.println(result.size());
				request.setPageToken(files.getNextPageToken());
				// System.out.println(resultSet.last().getTitle());
				System.out.println(result.size());
				// if (result.size() > 1500){
				// System.out.println("emptying duplicates");
				// System.out.println("Initial size - " + result.size());
				// boolean ch = resultSet.addAll(result);
				// System.out.println(ch);
				// result = new ArrayList<File>();
				// //result.clear();
				// System.out.println("new size - " + resultSet.size());
				// }

			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);
		System.out.println("Time to get all files "
				+ (System.currentTimeMillis() - time));
		// resultSet.addAll(result);
		// result.clear();
		// result.addAll(resultSet);
		// result.addAll(hashMap.values());
		return result;
	}

	public File getFile(String fileId) {
		File file = null;
		try {
			file = drive.files().get(fileId).execute();
		} catch (IOException e) {
			System.out.println("An error occured: " + e);
		}
		return file;
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
		System.out.println("Title: " + metadata.get("title"));
		System.out.println("Author: " + metadata.get("Author"));

		String alphaAndDigits = matcher.retainFrom(textHandler.toString());
		alphaAndDigits.toLowerCase();
		// removes extra whitespaces (of any kind)
		Pattern whitespace = Pattern.compile("\\s+");
		Matcher whiteMatcher = whitespace.matcher(alphaAndDigits);
		if (whiteMatcher.find())
			alphaAndDigits = whiteMatcher.replaceAll(" ");
		return alphaAndDigits;
	}

	public InputStream getFileStream(String url) {
		if (url == null) {
			return null;
		}
		HttpResponse resp = null;
		try {
			resp = drive.getRequestFactory()
					.buildGetRequest(new GenericUrl(url)).execute();
			return resp.getContent();
		} catch (IOException e) {
			// An error occurred.
			e.printStackTrace();
			return null;
		}
	}

	public boolean isDownloadable(String mimetype) {
		String mimeType = mimetype.toLowerCase();
		boolean isValidMIME = (GdriveDocument.ALLOWED_MIME_TYPES
				.contains(mimeType)) ? true : false;
		return isValidMIME;
	}

	public void sendToSearchEngine(java.util.List<GdriveDocument> documents) {
		if (documents == null) {
			return;
		}
		for (GdriveDocument gdriveDocument : documents) {
			sendToSearchEngine(gdriveDocument);
		}
	}

	public GdriveDocument updateData(GdriveDocument document) {
		if (document.getData() != null) {
			return document;
		}
		String url = document.getComputedDownloadURL();
		if (isDownloadable(document.getMimeType())) {
			InputStream input = getFileStream(url);
			String data = getFileData(input);
			document.setData(data);
		}
		return document;
	}

	public void sendToSearchEngine(GdriveDocument document) {
		try {
			String url = "http://localhost:9200/";
			url = url + userID + "/user/" + document.getId();
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut putRequest = new HttpPut(url);
			Genson genson = new Genson.Builder().setUseGettersAndSetters(false).setFieldFilter(VisibilityFilter.ALL).create();
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
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws NoRefreshTokenException,
			IllegalStateException, IOException {
		// RequestModel model = new RequestModel(null,
		// "105248847399825755850DRIVE_SCOPE", null,
		// "jakedsouza88@gmail.com");
		RequestModel model = new RequestModel(null,
				"101203193690484215892DRIVE_SCOPE", null,
				"matthewpeter34@gmail.com");
		FileService service = new FileService();
		Drive drive = service.getDriveService(model);
		GdriveDocumentMediator mediator = new GdriveDocumentMediator(drive,
				"101203193690484215892DRIVE_SCOPE");
		List<GdriveDocument> documents = mediator.createIndexableDocuments();
		System.out.println(GET_ALL_MIME);
		// File file = mediator
		// .getFile("0AucTBT3rzeT_dDQxQW1hMjA1YmJ6WW9YNzlIZU9vcnc");
		// GdriveDocument document = new GdriveDocument(file);
		for (GdriveDocument gdriveDocument : documents) {
			System.out.println(gdriveDocument.getData());
			mediator.sendToSearchEngine(gdriveDocument);
		}
		// List<File> list = mediator.getAllFiles();
		// for (File file : list) {
		// System.out.println(file.getTitle() + "," +file.getId());
		// }
		// System.out.println(GET_ALL_MIME);
	}

}
