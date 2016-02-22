package org.dea.fimgstoreclient.beans;

import java.util.Date;

public class FimgStoreFileMd extends FimgStoreObject {

	private static final long serialVersionUID = 1L;
	protected String partOf;
	protected Date uploadDate;
	protected String IP;
	protected String user;
	protected String filetype;
	protected String mimetype;
	protected int size;
	protected String checksum;
	
	public FimgStoreFileMd(String key) {
		super(key);
	}

	public FimgStoreFileMd(String key, String fileName) {
		super(key, fileName);
	}

	public FimgStoreFileMd() {
		super();
	}

	public String getPartOf() {
		return partOf;
	}

	public void setPartOf(String partOf) {
		this.partOf = partOf;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		this.IP = iP;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
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
			sb.append(uri == null ? null : uri.toString());
			sb.append("} ");
			sb.append(this.getClass().getCanonicalName());
			sb.append("\n=============== Content ===============\n");
			sb.append("partOf=" + partOf + "\n");
			sb.append("uploadDate=" + uploadDate + "\n");
			sb.append("IP=" + IP + "\n");
			sb.append("User=" + user + "\n");
			sb.append("filetype=" + filetype + "\n");
			sb.append("mimetype=" + mimetype + "\n");
			sb.append("size=" + size + "\n");
			sb.append("checksum=" + checksum);
			sb.append("\n=======================================");
			stringRep = sb.toString();
			isAltered = false;
		}
		return this.stringRep;
	}
}
