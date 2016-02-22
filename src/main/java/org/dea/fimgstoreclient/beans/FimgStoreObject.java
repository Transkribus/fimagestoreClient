package org.dea.fimgstoreclient.beans;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

public abstract class FimgStoreObject implements Serializable{
	private static final long serialVersionUID = 1L;
	protected String key;
	protected String fileName;
	protected URI uri;

	protected String stringRep = null;
	//if this is true then stringRep is rebuilt when calling toString()
	protected boolean isAltered = true;
	protected Date dlTime;

	public FimgStoreObject(){
	}
	
	public FimgStoreObject(String key){
		this.key = key;
	}
	
	public FimgStoreObject(String key, String fileName) {
		this.key = key;
		this.fileName = fileName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.isAltered = true;
	}
	
	public Date getTime() {
		return dlTime;
	}
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
//	@Override
//	public String toString(){
//		if(isAltered){
//			final String sep = " | ";
//			StringBuffer sb = new StringBuffer("{");
//			sb.append(key);
//			sb.append(sep);
//			sb.append(fileName);
//			sb.append(sep);
//			sb.append(params == null ? null : params.toString());
//			sb.append("} ");
//			sb.append(this.getClass().getCanonicalName());
//			stringRep = sb.toString();
//			isAltered = false;
//		}
//		return this.stringRep;
//	}
}
