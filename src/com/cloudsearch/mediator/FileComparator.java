package com.cloudsearch.mediator;

import java.util.Comparator;

import com.google.api.services.drive.model.File;

public class FileComparator implements Comparator<File>{

	@Override
	public int compare(File o1, File o2) {
		String i1 = o1.getId();
		String i2 = o2.getId();
		return i1.compareToIgnoreCase(i2);
	}

}
