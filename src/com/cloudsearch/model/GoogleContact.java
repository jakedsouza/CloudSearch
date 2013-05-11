package com.cloudsearch.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;

public class GoogleContact {

	private String id;
	private String name;
	private String namePrefix;
	private String givenName;
	private String additionalName;
	private String familyName;
	private String nameSuffix;
	private List<String> emails;
	private List<String> ims;
	private List<String> phoneNumbers;
	private String photoLink;

	public GoogleContact(ContactEntry e, String accessToken) {
		e.getId();
		this.emails = new ArrayList<String>();
		this.ims = new ArrayList<String>();
		this.phoneNumbers = new ArrayList<String>();
		Name name = e.getName();
		Link photoLink = e.getContactPhotoLink();
		List<Email> emails = e.getEmailAddresses();
		String id = e.getId();
		List<Im> ims = e.getImAddresses();
		List<PhoneNumber> phoneNumbers = e.getPhoneNumbers();
		this.id = id;
		String[] splitId = id.split("/");
		this.id = splitId[splitId.length - 1];
		if (name != null) {
			if (name.hasFullName()) {
				this.name = name.getFullName().getValue();
				if (name.getFullName().hasYomi()) {
					this.name += " (" + name.getFullName().getYomi() + ")";
				}
			}
			if (name.hasNamePrefix()) {
				this.namePrefix = name.getNamePrefix().getValue();
			}
			if (name.hasGivenName()) {
				this.givenName = name.getGivenName().getValue();
				if (name.getGivenName().hasYomi()) {
					this.givenName += " (" + name.getGivenName().getYomi()
							+ ")";
				}
			}
			if (name.hasAdditionalName()) {
				this.additionalName = name.getAdditionalName().getValue();
				if (name.getAdditionalName().hasYomi()) {
					this.additionalName += " ("
							+ name.getAdditionalName().getYomi() + ")";
				}
			}

			if (name.hasFamilyName()) {
				this.familyName = name.getFamilyName().getValue();
				if (name.getFamilyName().hasYomi()) {
					this.familyName += " (" + name.getFamilyName().getYomi()
							+ ")";
				}
			}
			if (name.hasNameSuffix()) {
				this.nameSuffix = name.getNameSuffix().getValue();
			}
		}
		if (emails != null) {
			for (Email email : emails) {
				String a = email.getAddress();
				this.emails.add(email.getAddress());
			}
		}
		if (ims != null) {
			for (Im im : ims) {
				this.ims.add(im.getAddress());
			}
		}
		if (phoneNumbers != null) {

			for (PhoneNumber phoneNumber : phoneNumbers) {
				this.phoneNumbers.add(phoneNumber.getPhoneNumber());
			}
		}
		if (photoLink != null) {
			if (photoLink.getHref() != null && photoLink.getEtag() != null) {
				this.photoLink = photoLink.getHref();
				this.photoLink += "?access_token=" + accessToken;
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public String getGivenName() {
		return givenName;
	}

	public String getAdditionalName() {
		return additionalName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getNameSuffix() {
		return nameSuffix;
	}

	public List<String> getEmails() {
		return emails;
	}

	public List<String> getIms() {
		return ims;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public String getPhotoLink() {
		return photoLink;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public void setIms(List<String> ims) {
		this.ims = ims;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void setPhotoLink(String photoLink) {
		this.photoLink = photoLink;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GoogleContact [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", namePrefix=");
		builder.append(namePrefix);
		builder.append(", givenName=");
		builder.append(givenName);
		builder.append(", additionalName=");
		builder.append(additionalName);
		builder.append(", familyName=");
		builder.append(familyName);
		builder.append(", nameSuffix=");
		builder.append(nameSuffix);
		builder.append(", emails=");
		builder.append(emails);
		builder.append(", ims=");
		builder.append(ims);
		builder.append(", phoneNumbers=");
		builder.append(phoneNumbers);
		builder.append(", photoLink=");
		builder.append(photoLink);
		builder.append("]");
		return builder.toString();
	}
}
