package org.dea.fimgstoreclient.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.dea.fimgstoreclient.beans.FimgStoreFileMd;
import org.dea.fimgstoreclient.beans.FimgStoreImgMd;

public class FimgStoreMdParser {
	//from org.dea.imagestore.img.UploadedImageMetadata.DATE_FORMAT
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	// Key = DYQLMRPHLXBKCQFRXBMKXRTF
	// OrigFilename = wp54808274x-00044.tif
	// IsPartOf =
	// UploadDate = 2013/08/08 10:34:55
	// IP = 138.232.114.197
	// User = FEP
	// Filetype = TIFF
	// Mimetype = image/tiff
	// Size = 4124424
	// Checksum = 5fddbb36bb7e2d760d9048cf37520361
	// Width = 2629
	// Height = 3111
	// XResolution = 300.0
	// YResolution = 300.0
	// Bitdepth = 8
	// NComponents = 1
	// Compression = LZW
	 private static final String KEY = "Key";
	 private static final String FILENAME = "OrigFilename";
	 private static final String PART_OF = "IsPartOf";
	 private static final String UL_DATE = "UploadDate";
	 private static final String IP = "IP";
	 private static final String USER = "User";
	 private static final String FTYPE = "Filetype";
	 private static final String MIMETYPE = "Mimetype";
	 private static final String SIZE = "Size";
	 private static final String CHECKSUM = "Checksum";
	 private static final String WIDTH = "Width";
	 private static final String HEIGHT = "Height";
	 private static final String XRES = "XResolution";
	 private static final String YRES = "YResolution";
	 private static final String BITS = "Bitdepth";
	 private static final String NCOMPONENTS = "NComponents";
	 private static final String COMPR = "Compression";
	
	/**
	 * Parses the InputStream for FileMetadata and determines whether to return a Image or FileMetadata Object
	 * @param is
	 * @return {@link FimgStoreImgMd} or {@link FimgStoreFileMd}
	 * @throws IOException
	 */
	public static FimgStoreFileMd parse(InputStream is) throws IOException {
		FimgStoreImgMd md = null;
		
		Properties props = new Properties();
		props.load(is);
		md = new FimgStoreImgMd(props.getProperty(KEY));
		md.setFileName(props.getProperty(FILENAME));
		md.setPartOf(props.getProperty(PART_OF));
		try{
			md.setUploadDate(parseDate(props.getProperty(UL_DATE)));
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new IOException("Strange date format: " + props.getProperty(UL_DATE) + ". Check org.dea.imagestore.img.UploadedImageMetadata.DATE_FORMAT");
		}
		md.setIP(props.getProperty(IP));
		md.setUser(props.getProperty(USER));
		md.setFiletype(props.getProperty(FTYPE));
		md.setMimetype(props.getProperty(MIMETYPE));
		md.setSize(parseInt(props.getProperty(SIZE)));
		md.setChecksum(props.getProperty(CHECKSUM));
				
		if(!isImage(props.getProperty(MIMETYPE))){
			return (FimgStoreFileMd)md;
		} else {
//			add image specific md
			md.setWidth(parseInt(props.getProperty(WIDTH)));
			md.setHeight(parseInt(props.getProperty(HEIGHT)));
			md.setXResolution(parseDouble(props.getProperty(XRES)));
			md.setYResolution(parseDouble(props.getProperty(YRES)));
			md.setBitdepth(parseInt(props.getProperty(BITS)));
			md.setNComponents(parseInt(props.getProperty(NCOMPONENTS)));
			md.setCompression(props.getProperty(COMPR));
			
			return md;
		}
	}
	
	private static Integer parseInt(final String s){
		if(s != null){
			return Integer.parseInt(s);
		} else {
			return null;
		}
	}
	
	private static Double parseDouble(final String s){
		if(s != null){
			return Double.parseDouble(s);
		} else {
			return null;
		}
	}
	
	private static Date parseDate(final String value) throws ParseException{
		if(value != null && value.length() == DATE_FORMAT.length()){
			return new SimpleDateFormat(DATE_FORMAT).parse(value);
		} else { return null; }
	}
	
	public static boolean isImage(String mimetype) {
		return mimetype.startsWith("image/");
	}
}
