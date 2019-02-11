package org.dea.fimgstoreclient;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
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
				logger.trace("i = " + i);
				getter.getFileMd(key);
			} else {
				logger.trace("i = " + i + " -> getting 404 file");
				try {
					getter.getFileMd(key404);
				} catch(FileNotFoundException e) {
					//this file does not exist
				}
			}
		}
		getter.close();
	}

}
