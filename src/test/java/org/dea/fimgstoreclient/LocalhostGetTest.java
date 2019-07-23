package org.dea.fimgstoreclient;

import java.io.IOException;

import org.dea.fimagestore.core.util.SebisStopWatch.SSW;
import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.beans.ImgType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalhostGetTest {
	private static final Logger logger = LoggerFactory.getLogger(LocalhostGetTest.class);
	
	@Test
	public void testGetImg() throws IOException {
		
		int iterations = 100;
		final String key = "ZOAYSAVROONEAPCFCGVPYSKQ";
		
		SSW sw = new SSW();
		
		FimgStoreGetClient getter = new FimgStoreGetClient(Scheme.http, "localhost", 8080, "/fimagestore-webapp-1.5.5");
		sw.start();
		for(int i = 0; i < iterations; i++) {
			logger.debug("i = " + i);
			logger.debug(getter.getImg(key, ImgType.view).toString());
		}
		long newTime = sw.stop();
		getter.close();
		
		
		
	}
}
