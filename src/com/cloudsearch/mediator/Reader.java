package com.cloudsearch.mediator;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.*;
import org.xml.sax.SAXException;

import com.cloudsearch.abstractwebservices.CloudSearchService;
import com.google.common.base.CharMatcher;
public class Reader {
	private File file;
	static Logger log = Logger.getLogger(Reader.class);

	public Reader(File file){
		this.file = file;
	}
	
	/*
	 * Get the input stream
	 */
	public InputStream parseAny() throws IOException, SAXException, TikaException{
		InputStream input = new FileInputStream(file);
		BodyContentHandler textHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		ParseContext contxt = new ParseContext(); 
		AutoDetectParser parser = new AutoDetectParser();
		parser.parse(input, textHandler, metadata, contxt);
		input.close();
		//System.out.println("Title: " + metadata.get("title"));
		//System.out.println("Author: " + metadata.get("Author"));
		//System.out.println("content: " + textHandler.toString());
		System.out.println(textHandler.toString());
		InputStream is = new ByteArrayInputStream(textHandler.toString().getBytes());
		final CharMatcher ALNUM =
				  CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'))
				  .or(CharMatcher.inRange('0', '9')).precomputed();		
		String alphaAndDigits = ALNUM.trimFrom(textHandler.toString());
		System.out.println(alphaAndDigits);

		return is;
	}
	
	/*
	 * Example
	 */
	public static void main(String[] args) throws Exception {
		Reader read = new Reader(new File("abc.docx"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(read.parseAny()));
		 
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		
		
	}
}
