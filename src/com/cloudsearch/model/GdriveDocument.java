package com.cloudsearch.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;
import com.google.common.collect.Sets;
import com.owlike.genson.annotation.JsonIgnore;

public class GdriveDocument {
	@JsonIgnore
	public static Set<String> ALLOWED_MIME_TYPES = Sets
			.newHashSet(
					"text/html",
					"text/plain",
					"application/rtf",
					"application/vnd.oasis.opendocument.text",
					"application/pdf",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					"application/x-vnd.oasis.opendocument.spreadsheet",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation",
					"application/vnd.oasis.opendocument.text",
					"application/x-vnd.oasis.opendocument.spreadsheet");

	private String id;
	private String title;
	@JsonIgnore
	private String mimeType;
	@JsonIgnore
	private String downloadURL;
	@JsonIgnore
	private String fileExtension;
	
	private String data;

	private String createdDate;
	private Map<String, String> exportLinks;
	@JsonIgnore
	private Map<String, String> exportLinksDownloadURL;
	private final String type = "GDRIVEDOC";
	private String alternateLink;
	private String thumbnailLink;
	private String modifiedDate;
	private String iconLink;
	@JsonIgnore
	private String computedDownloadURL;

	public GdriveDocument() {
	}

	public GdriveDocument(File file) {
		this.id = file.getId();
		this.title = file.getTitle();
		this.mimeType = file.getMimeType();
		this.downloadURL = file.getDownloadUrl();
		this.fileExtension = file.getFileExtension();
		this.exportLinks = file.getExportLinks();
		this.alternateLink = file.getAlternateLink();
		this.thumbnailLink = file.getThumbnailLink();
		Map<String, Object> map = file.getUnknownKeys();
		if (map.containsKey("iconLink")) {
			this.iconLink = (String) map.get("iconLink");
		}

		this.setCreatedDate(file.getCreatedDate().toString());
		this.setModifiedDate(file.getModifiedDate().toString());

		if (file.getDownloadUrl() == null
				|| file.getDownloadUrl().length() <= 0) {
			Map<String, String> exportLinks = file.getExportLinks();
			if (exportLinks != null) {
				for (String type : ALLOWED_MIME_TYPES) {
					if (exportLinks.containsKey(type)) {
						this.setComputedDownloadURL(exportLinks.get(type));
					}
				}
			}
		} else {
			this.setComputedDownloadURL(file.getDownloadUrl());
		}
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getData() {
		return data;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public Map<String, String> getExportLinks() {
		return exportLinks;
	}

	public Map<String, String> getExportLinksDownloadURL() {
		return exportLinksDownloadURL;
	}

	public String getType() {
		return type;
	}

	public String getAlternateLink() {
		return alternateLink;
	}

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setExportLinks(Map<String, String> exportLinks) {
		this.exportLinks = exportLinks;
	}

	public void setExportLinksDownloadURL(
			Map<String, String> exportLinksDownloadURL) {
		this.exportLinksDownloadURL = exportLinksDownloadURL;
	}

	public void setAlternateLink(String alternateLink) {
		this.alternateLink = alternateLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getIconLink() {
		return iconLink;
	}

	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}

	public String getComputedDownloadURL() {
		return computedDownloadURL;
	}

	public void setComputedDownloadURL(String computedDownloadURL) {
		this.computedDownloadURL = computedDownloadURL;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ID = " + this.id + ",");
		sb.append(" Title = " + this.title + ",");
		sb.append(" MimeType = " + this.mimeType + ",");
		sb.append(" FileExtension = " + this.fileExtension + ",");
		sb.append(" CreatedDate  = " + this.createdDate + ",");
		sb.append("alternateLink = " + this.alternateLink);
		sb.append("downloadURL = " + this.downloadURL);
		sb.append("thumbnailLink = " + this.thumbnailLink);
		sb.append("modifiedDate = " + this.modifiedDate);
		sb.append(" ExportLinks = " + this.exportLinks + "}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof GdriveDocument) {
			GdriveDocument d = (GdriveDocument) obj;
			if ((d.id == this.id)) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}
}