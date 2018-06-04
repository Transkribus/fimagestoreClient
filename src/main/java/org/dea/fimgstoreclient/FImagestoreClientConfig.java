package org.dea.fimgstoreclient;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.dea.fimagestore.core.FImagestoreConfig;
import org.dea.fimagestore.core.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Not needed anymore as now constants from fimagestore-core are used
 * 
 * Single remaining property: dummy image key
 * TODO how to manage filekeys that are forbidden to delete? e.g. symbolic image for broken files in transkribus
 * 
 * @author philip
 *
 */
@Deprecated
public class FImagestoreClientConfig {
		private static final Logger logger = LoggerFactory.getLogger(FImagestoreClientConfig.class);
		/**
		 * Default config file "{@value FImagestoreConfig#DEFAULT_PROPS_FILE_NAME}" is 
		 * loaded from classpath by default mainly to not require explicit loading in unit tests.<br/>
		 * webapp will load its config specifically during initialization.  
		 */
		private static final String DEFAULT_PROPS_FILE_NAME = "FimgStoreClient.properties";
		/**
		 * If a specific properties file is successfully loaded currentPropFileName will store its name for further reference
		 */
		private static String currentPropFileName = DEFAULT_PROPS_FILE_NAME;	
		
		private static Properties props = loadDefault();
		
		public static final String DUMMY_IMAGE_KEY = getProperty("dummyImageKey");
		
		private FImagestoreClientConfig() {}
		
		/**
		 * Load properties from a file on this path in classpath
		 * 
		 * @param propsFileName
		 * @throws IOException 
		 */
		public static void loadFromFile(String propsFileName) throws IOException {
			props = Util.loadPropertiesFromClasspath(propsFileName);
			currentPropFileName = propsFileName;
			logger.debug("Loaded properties from " + propsFileName);
		}
		
		protected static Properties loadDefault() {
			Properties props = new Properties();
			try {	
				props = Util.loadPropertiesFromClasspath(currentPropFileName);
			} catch (IOException e) {
				logger.debug("Loading " + currentPropFileName + " failed! No properties will be set.", e);
			}
			return props;
		}


		public static String getProperty(String key) {
			return props.getProperty(key);
		}
		
		public static String getProperty(String key, String defaultValue) {
			return props.getProperty(key, defaultValue);
		}
		
		public static Integer getIntProperty(String key) {
			return getIntProperty(key, null);
		}
		
		public static Integer getIntProperty(String key, Integer defaultValue) {
			String propStr = getProperty(key);
			if(propStr == null) {
				return defaultValue;
			}
			Integer retVal = defaultValue;
			try {
				retVal = Integer.parseInt(propStr);
			} catch (NumberFormatException nfe) {}
			return retVal;
		}
		
		public static String getCurrentPropsFileName() {
			return currentPropFileName;
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
}
