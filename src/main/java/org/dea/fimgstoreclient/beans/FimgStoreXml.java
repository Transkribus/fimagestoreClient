package org.dea.fimgstoreclient.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FimgStoreXml extends FimgStoreObject implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Document xml;
	protected byte[] data;

	public FimgStoreXml(String key) {
		super(key);
	}

	public FimgStoreXml(String key, String fileName, byte[] data, URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(key, fileName);
		this.data = data;
		this.uri = uri;
		DocumentBuilder db;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(data));
		this.setXmlDoc(doc);
	}

	public Document getXmlDoc() {
		return xml;
	}

	public void setXmlDoc(Document doc) {
		this.xml = doc;
		this.dlTime = new Date();
		this.isAltered = true;
	}

	public Date getTime() {
		return dlTime;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
		this.dlTime = new Date();
		this.isAltered = true;
	}

	@Override
	public String toString() {
		if (isAltered) {
			final String sep = " | ";
			StringBuffer sb = new StringBuffer("{");
			sb.append(key);
			sb.append(sep);
			sb.append(fileName);
			sb.append(sep);
			sb.append(DateFormat.getInstance().format(dlTime));
			// sb.append(sep);
			// sb.append("xml size = " + xml.get + " bytes"); TODO
			sb.append(sep);
			sb.append(uri == null ? null : uri.toString());
			sb.append("} ");
			sb.append(this.getClass().getCanonicalName());
			stringRep = sb.toString();
			isAltered = false;
		}
		return this.stringRep;
	}
}
