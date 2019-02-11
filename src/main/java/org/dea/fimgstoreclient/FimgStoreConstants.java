package org.dea.fimgstoreclient;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FimgStoreConstants {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreConstants.class);

	private static final Properties props = loadProps("FimgStoreClient.properties");

	public static final String DUMMY_IMAGE_KEY = props.getProperty("dummyImageKey");

	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	private static Properties loadProps(String filename) {
		Properties props = new Properties();
		try {
			InputStream inputStream = FimgStoreConstants.class.getClassLoader().getResourceAsStream(filename);
			props.load(inputStream);
		} catch (Exception e) {
			logger.error("Could not find properties file: " + filename, e);
		}
		return props;
	}
}
