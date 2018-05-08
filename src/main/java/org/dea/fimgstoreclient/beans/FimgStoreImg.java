package org.dea.fimgstoreclient.beans;

import java.io.Serializable;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

/**
 * 
 * @author philip
 *
 */
public class FimgStoreImg extends FimgStoreObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected byte[] data;
	protected Date dlTime;
	protected ImgTypeName imgType;
	
	public FimgStoreImg(String key){
		super(key);
	}
	
	public FimgStoreImg(String key, String fileName, byte[] data, URI uri) {
		super(key, fileName);
		setData(data);
		this.uri = uri;
	}
	
	public FimgStoreImg(String key, String fileName, byte[] data, URI uri, ImgTypeName type) {
		super(key, fileName);
		setData(data);
		this.uri = uri;
		this.imgType = type;
	}
	
	public ImgTypeName getImgType() {
		return imgType;
	}

	public void setImgType(ImgTypeName imgType) {
		this.imgType = imgType;
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
	public String toString(){
		if(isAltered){
			final String sep = " | ";
			StringBuffer sb = new StringBuffer("{");
			sb.append(key);
			sb.append(sep);
			sb.append(imgType);
			sb.append(sep);
			sb.append(fileName);
			sb.append(sep);
			sb.append(DateFormat.getInstance().format(dlTime));
			sb.append(sep);
			sb.append("img size = " + data.length + " bytes");
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
