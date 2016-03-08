package org.dea.fimgstoreclient;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

public class FimgStoreConstants {
//		private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);
		private static final Properties props = loadProps("FimgStoreClient.properties");
		
		// TODO put those to property file
		public static final String FILE_TYPE_PARAM = "fileType";
		public static final String SCALE_PERC_PARAM = "scalePerc";
		public static final String SCALE_X_Y_PARAM = "scaleXY";
		public static final String MULT_LITERAL = "x";
		public static final String CROP_PARAM = "crop";
		public static final String CONVERT_OPTS_PARAM = "convertOpts";
		public static final String CONVERT_EXT_PARAM = "convertExt";
		public static final String MD_FILETYPE_VALUE = "metadata";
		public static final String ID_PARAM = "id";
		public static final String PART_OF_VAR_NAME = FimgStoreConstants.getString("partOfVarName")!=null ? FimgStoreConstants.getString("partOfVarName") : "is_part_of";
		public static final String FILE_VAR_NAME = FimgStoreConstants.getString("fileVarName")!=null ? FimgStoreConstants.getString("fileVarName") : "file";
		public static final String REPLACE_ID_VAR_NAME = FimgStoreConstants.getString("replaceIdVarName")!=null ? FimgStoreConstants.getString("replaceIdVarName") : "replaceId";
		public static final String TIMEOUT_PARAM = FimgStoreConstants.getString("timeoutParam")!=null ? FimgStoreConstants.getString("timeoutParam") : "timeout";		

		public static final String GET_ACTION_PATH = getString("getActionPath");
		public static final String PUT_ACTION_PATH = getString("putActionPath");
		public static final String DEL_ACTION_PATH = getString("delActionPath");
		public static final String CREATE_ACTION_PATH = getString("createActionPath");
		
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
