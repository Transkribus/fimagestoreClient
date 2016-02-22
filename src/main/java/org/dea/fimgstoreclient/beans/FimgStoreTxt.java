package org.dea.fimgstoreclient.beans;

import java.io.Serializable;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

public class FimgStoreTxt extends FimgStoreObject implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String text;

	public FimgStoreTxt(String key) {
		super(key);
	}

	public FimgStoreTxt(String key, String fileName, String text, URI uri) {
		super(key);
		this.uri = uri;
		this.fileName = fileName;
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.dlTime = new Date();
		this.isAltered = true;
	}

	public Date getTime() {
		return dlTime;
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
