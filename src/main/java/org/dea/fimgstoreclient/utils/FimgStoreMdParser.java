package org.dea.fimgstoreclient.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Properties;

import org.dea.fimagestore.core.FImagestoreConst;
import org.dea.fimgstoreclient.beans.FimgStoreFileMd;
import org.dea.fimgstoreclient.beans.FimgStoreImgMd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FimgStoreMdParser {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreMdParser.class);
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
	 private static final String ORIENTATION = "Orientation";
	
	/**
	 * Parses the InputStream for FileMetadata and determines whether to return a Image or FileMetadata Object
	 * @param is InputStream to parse
	 * @return {@link FimgStoreImgMd} or {@link FimgStoreFileMd}
	 * @throws IOException if network error occurs or stream can't be read
	 */
	public static FimgStoreFileMd parse(InputStream is) throws IOException {
		FimgStoreImgMd md = null;
		DateFormat df = FImagestoreConst.newDateFormat();
		
		
		Properties props = new Properties();
		props.load(is);
		md = new FimgStoreImgMd(props.getProperty(KEY));
		md.setFileName(props.getProperty(FILENAME));
		md.setPartOf(props.getProperty(PART_OF));
		try{
			md.setUploadDate(df.parse(props.getProperty(UL_DATE)));
		} catch (Exception pe) {
			logger.warn(md.getKey() + ": Encountered strange date format: " + props.getProperty(UL_DATE), pe);
			throw new IOException("Strange date format: " + props.getProperty(UL_DATE) + ". Check org.dea.imagestore.img.UploadedImageMetadata.DATE_FORMAT", pe);
		}
		md.setIP(props.getProperty(IP));
		md.setUser(props.getProperty(USER));
		md.setFiletype(props.getProperty(FTYPE));
		md.setMimetype(props.getProperty(MIMETYPE));
		md.setSize(parseInt(props.getProperty(SIZE)));
		md.setChecksum(props.getProperty(CHECKSUM));
		md.setOrientation(props.getProperty(ORIENTATION));
				
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
	
	public static boolean isImage(String mimetype) {
		return mimetype.startsWith("image/");
	}
}
