package main.java.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RSS {

	public String name;
	public String description;
	public String link;

	public static List<RSS> getFeed() throws IOException, ParserConfigurationException, SAXException {

		List<RSS> list = new ArrayList<RSS>();

		URLConnection connection = new URL("http://25livepub.collegenet.com/calendars/25live-student-events.rss").openConnection();
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		InputStream response = connection.getInputStream();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		Document doc = db.parse(response);
		
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("item");
		
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node node = nList.item(temp);
			System.out.println("");
			
			Element eElement = (Element) node;
			String name = eElement.getElementsByTagName("title").item(0).getTextContent();
			if(!(name.contains("Shacharis") || name.contains("Mincha") || name.contains("Minyan"))) {
				RSS rss = new RSS();
				rss.name = name;
				rss.description = eElement.getElementsByTagName("description").item(0).getTextContent();
				rss.link = eElement.getElementsByTagName("link").item(0).getTextContent();
				list.add(rss);
			}
		}
		return list;
		
	}
}
