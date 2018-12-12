package org.dea.fimgstoreclient;

import java.io.InputStream;
import java.util.Properties;

public class FimgStoreConstants {
		private static final Properties props = loadProps("FimgStoreClient.properties");
		
		public static final String DUMMY_IMAGE_KEY = props.getProperty("dummyImageKey");

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
