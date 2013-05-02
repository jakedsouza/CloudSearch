package com.cloudsearch.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import com.cloudsearch.oauth.CredentialMediator.NoRefreshTokenException;
import com.cloudsearch.webservices.FileService;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.File.IndexableText;

public class GdriveDocument {
	private String id;
	private String title;
	private String mimeType;
	private String downloadURL;
	private String fileExtension;
	private String data;
	private DateTime createdDate;
	private Map<String, String> exportLinks;
	private Map<String, String> exportLinksDownloadURL;
	private final String type = "GDRIVEDOC";
	private String alternateLink;
	private String thumbnailLink;
	public GdriveDocument() {
	}

	public GdriveDocument(File file) {
		this.id = file.getId();
		this.title = file.getTitle();
		this.mimeType = file.getMimeType();
		this.downloadURL = file.getDownloadUrl();
		this.fileExtension = file.getFileExtension();
		this.createdDate = file.getCreatedDate();
		this.exportLinks = file.getExportLinks();
		this.setAlternateLink(file.getAlternateLink());
		this.setThumbnailLink(file.getThumbnailLink());
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public Map<String, String> getExportLinks() {
		return exportLinks;
	}

	public void setExportLinks(Map<String, String> exportLinks) {
		this.exportLinks = exportLinks;
	}

	public Map<String, String> getExportLinksDownloadURL() {
		return exportLinksDownloadURL;
	}

	public void setExportLinksDownloadURL(
			Map<String, String> exportLinksDownloadURL) {
		this.exportLinksDownloadURL = exportLinksDownloadURL;
	}

	public String getAlternateLink() {
		return alternateLink;
	}

	public void setAlternateLink(String alternateLink) {
		this.alternateLink = alternateLink;
	}

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}

	public static void main(String[] args) throws NoRefreshTokenException,
			IOException {
		RequestModel model = new RequestModel(null,
				"105248847399825755850DRIVE_SCOPE", null,
				"jakedsouza88@gmail.com");

		FileService service = new FileService();
		Drive drive = service.getDriveService(model);
		java.util.List<File> list = retrieveAllFiles(drive);
		for (File file : list) {
			retrieveFileData(drive, file.getId());
			// System.out.println("a");
		}
		System.out.println(drive.toString());
	}

	private static java.util.List<File> retrieveAllFiles(Drive service)
			throws IOException {
		java.util.List<File> result = new ArrayList<File>();
		Files.List request = service.files().list();

		do {
			try {
				FileList files = request.execute();
				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
			// break;
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		return result;
	}

	private static void retrieveFileData(Drive service, String fileId)
			throws IOException {
		Files.Get request = service.files().get(fileId);
		File file = request.execute();
		if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {

			HttpResponse resp = service.getRequestFactory()
					.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
					.execute();
			OutputStream outputStream = new FileOutputStream(new java.io.File(
					"/home/jake/Desktop/d/" + file.getTitle()));
			resp.download(outputStream);
			Map<String, String> links = file.getExportLinks();
			if (links != null) {
				System.out.println(file.getTitle());
				System.out.println(links.keySet());
				System.out.println(links.values());
			}

			// System.out.println("aa");
			System.out.println(file.getTitle() + " downloadable");

		} else {
			System.out.println(file.getTitle() + " not downloadable");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{ ID = " + this.id + ",");
		sb.append(" Title = " + this.title + ",");
		sb.append(" MimeType = " + this.mimeType + ",");
		sb.append(" FileExtension = " + this.fileExtension + ",");
		sb.append(" Date = " + this.createdDate + ",");
		sb.append(" ExportLinks = " + this.exportLinks + "}");

		return sb.toString();
	}
}