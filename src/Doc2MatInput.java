import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;

import org.xml.sax.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Doc2MatInput implements Properties{

	public static HashSet<String> docTextSet = new HashSet<String>();

	public static void main(String[] args) {

		Date d = new Date();
		FileWriter fw = null;
		File doc2MatInputFolder= new File(DOC2MAT_IP_FILE_FOLDER);
		if(!doc2MatInputFolder.exists())
			doc2MatInputFolder.mkdirs();
			
		File inputFolder = null;
		File[] dataFiles = null;
		AddSynonyms t= new AddSynonyms();
		// String tempdir="E:\\spandan\\";
		try {
			fw = new FileWriter(DOC2MATINPUT_FILE);
			inputFolder = new File(DATASET_FOLDER);
			// int i=0;
			dataFiles = inputFolder.listFiles();
			for (File dataFile : dataFiles) {
				// FileWriter fw1=new FileWriter(new
				// File(tempdir+(i++)+".txt"));
				// String title = dataFile.getName();
				String doctext = new Parser().parseData(dataFile);

				if (doctext != null) {
					if (docTextSet.add(doctext)) {
						
						fw.write(t.process(doctext));
						fw.write(10);
						// fw1.write(doctext);
					}
				}
				// fw1.close();
			}
			docTextSet.clear();
			fw.close();

			System.out.println("Generated Input File for Doc2Mat in "
					+ (new Date().getTime() - d.getTime())+" Seconds");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	static class Parser {
		String docText = "";
		boolean isText = false;

		private DefaultHandler getHandler() {
			DefaultHandler handler = new DefaultHandler() {

				public void startDocument() throws SAXException {
				}

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {

					if (qName.equalsIgnoreCase("TEXT")) {
						isText = true;
						docText = "";
					}
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					if (qName.equalsIgnoreCase("TEXT")) {
						isText = false;

					}

				}

				public void characters(char ch[], int start, int length)
						throws SAXException {

					if (isText) {
						for (int i = start; i < length; i++) {
							if (ch[i] == '\n' || ch[i] == '\r') {
								docText += ' ';
								continue;
							}
							docText += ch[i];
						}
					}
				}

			};
			return handler;
		}

		public String parseData(File file) throws ParserConfigurationException,
				SAXException, IOException {

			InputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(is, getHandler());
			return docText;
		}
	}
}