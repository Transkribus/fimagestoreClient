package org.dea.fimgstoreclient.beans;

import java.awt.Dimension;
import java.io.Serializable;

public class FimgStoreImgMd extends FimgStoreFileMd implements Serializable {
	private static final long serialVersionUID = 1L;


	protected int width;
	protected int height;
	protected double xResolution;
	protected double yResolution;
	protected int bitdepth;
	protected int nComponents;
	protected String compression;
	protected String orientation;

	public FimgStoreImgMd(String key) {
		super(key);
	}

	public FimgStoreImgMd(String key, String fileName) {
		super(key, fileName);
	}

	public FimgStoreImgMd() {
		super();
	}

	public Dimension getDimension() {
		return new Dimension(this.getWidth(), this.getHeight());
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getXResolution() {
		return xResolution;
	}

	public void setXResolution(double xResolution) {
		this.xResolution = xResolution;
	}

	public double getYResolution() {
		return yResolution;
	}

	public void setYResolution(double yResolution) {
		this.yResolution = yResolution;
	}

	public int getBitdepth() {
		return bitdepth;
	}

	public void setBitdepth(int bitdepth) {
		this.bitdepth = bitdepth;
	}

	public int getNComponents() {
		return nComponents;
	}

	public void setNComponents(int nComponents) {
		this.nComponents = nComponents;
	}

	public String getCompression() {
		return compression;
	}

	public void setCompression(String compression) {
		this.compression = compression;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
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
			sb.append("checksum=" + checksum + "\n");
			sb.append("width=" + width + "\n");
			sb.append("height=" + height + "\n");
			sb.append("xResolution=" + xResolution + "\n");
			sb.append("yResolution=" + yResolution + "\n");
			sb.append("bitdepth=" + bitdepth + "\n");
			sb.append("nComponents=" + nComponents + "\n");
			sb.append("compression=" + compression);
			sb.append("\n=======================================");
			stringRep = sb.toString();
			isAltered = false;
		}
		return this.stringRep;
	}
}
