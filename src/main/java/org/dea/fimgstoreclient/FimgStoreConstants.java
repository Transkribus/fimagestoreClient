package org.dea.fimgstoreclient;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import org.dea.fimagestore.core.FImagestoreConst;

public class FimgStoreConstants {
		private static final Properties props = loadProps("FimgStoreClient.properties");
		
		public static final String FILE_TYPE_PARAM =FImagestoreConst.FILE_TYPE_PARAM;
		public static final String SCALE_PERC_PARAM = FImagestoreConst.SCALE_PERC_PARAM;
		public static final String SCALE_X_Y_PARAM = FImagestoreConst.SCALE_X_Y_PARAM;
		public static final String MULT_LITERAL = FImagestoreConst.MULT_LITERAL;
		public static final String CROP_PARAM = FImagestoreConst.CROP_PARAM;
		public static final String CONVERT_OPTS_PARAM = FImagestoreConst.CONVERT_OPTS_PARAM;
		public static final String CONVERT_EXT_PARAM = FImagestoreConst.CONVERT_EXT_PARAM;
		public static final String MD_FILETYPE_VALUE = FImagestoreConst.FILE_TYPE_METADATA;
		public static final String ID_PARAM = FImagestoreConst.ID_PARAM;
		public static final String PART_OF_VAR_NAME = FImagestoreConst.IS_PART_OF_FIELD_NAME;
		public static final String FILE_VAR_NAME = FImagestoreConst.FILE_FIELD_NAME;
		public static final String REPLACE_ID_VAR_NAME = FImagestoreConst.REPLACE_ID_FIELD_NAME;
		public static final String TIMEOUT_PARAM = FImagestoreConst.TIMEOUT_FIELD_NAME;		

		public static final String GET_ACTION_PATH =  FImagestoreConst.GET_FILE_SERVLET_URL;
		public static final String PUT_ACTION_PATH = FImagestoreConst.UPLOAD_FILE_SERVLET_URL;
		public static final String DEL_ACTION_PATH = FImagestoreConst.DELETE_FILE_SERVLET_URL;
		public static final String CREATE_ACTION_PATH = FImagestoreConst.CREATE_FILE_SERVLET_URL;
		
		public static final String DUMMY_IMAGE_KEY = getString("dummyImageKey");
		
		public static String getString(String name){
			return props.getProperty(name);
		}
		
		public static Integer getInt(String name){
			final String prop = props.getProperty(name);
			Integer value = null;
			if(prop != null){
				try{
					value = Integer.parseInt(props.getProperty(name));
				} catch (NumberFormatException nfe){
					nfe.printStackTrace();
				}
			}
			return value;
		}
		
		public static Pattern getPattern(String name){
			return Pattern.compile(props.getProperty(name));
		}
		
		public static boolean getBool(String name){
			final String value = props.getProperty(name);
			boolean bool = false;
			if(value.equals("1") || value.equalsIgnoreCase("true")){
				bool = true;
			}
			return bool;
		}

		private static Properties loadProps(String filename){
			Properties props = new Properties();
			try{
				InputStream inputStream = FimgStoreConstants.class.getClassLoader().getResourceAsStream(filename);
				props.load(inputStream);
			} catch (Exception e) {
//				LOGGER.debug("Could not find properties file: " + filename);
				System.out.println("Could not find properties file: " + filename);
			}
			return props;
		}
}
