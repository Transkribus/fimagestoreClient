package org.dea.fimgstoreclient;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.beans.ImgType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolTest {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolTest.class);
	
	/**
	 * Provoke issue #5
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGet() throws IOException {
		FimgStoreGetClient getter = new FimgStoreGetClient(Scheme.https, "files-test.transkribus.eu", null, "/");
		
		final String key = "ZOAYSAVROONEAPCFCGVPYSKQ";
		final String key404 = "UNKRNHSATTZGUUMKZBSBNOUC";
		final int iterations = 1000;
		for(int i = 0; i < iterations; i++) {
			if(i%2 == 0) {
				logger.debug("i = " + i);
				getter.getFileMd(key);
			} else {
				logger.debug("i = " + i + " -> getting 404 file");
				try {
					getter.getFileMd(key404);
				} catch(FileNotFoundException e) {
					//this file does not exist
				}
			}
		}
		getter.close();
	}
	
	@Test
	public void testGetImg() throws IOException {
		FimgStoreGetClient getter = new FimgStoreGetClient(Scheme.https, "files-test.transkribus.eu", null, "/");
		final String key = "ZOAYSAVROONEAPCFCGVPYSKQ";
		final String key404 = "UNKRNHSATTZGUUMKZBSBNOUC";
		final int iterations = 1000;
		for(int i = 0; i < iterations; i++) {
			if(i%2 == 0) {
				logger.debug("i = " + i);
				logger.debug(getter.getImg(key, ImgType.view).toString());
			} else {
				logger.debug("i = " + i + " -> getting 404 file");
				try {
					getter.getImg(key404, ImgType.view);
				} catch(FileNotFoundException e) {
					//this file does not exist
				}
			}
		}
		getter.close();
	}

}
