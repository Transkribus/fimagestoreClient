package org.dea.fimgstoreclient;

import java.io.IOException;

import org.dea.fimagestore.core.MetadataReader;
import org.dea.fimagestore.core.beans.FileMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharsetTests {
	private static final Logger logger = LoggerFactory.getLogger(CharsetTests.class);

	@Test
	public void testFilenameEncoding() throws IOException {
		final String key = "CLLMVGTUQWKRACMHDKBNGRLB";
		FimgStoreGetClient getter = new FimgStoreGetClient("files-test.transkribus.eu", "/");
		FileMetadata mdHttp = getter.getFileMd(key);
		logger.debug(mdHttp.toString());
		
		final String path = "/mnt/nmtera1/Content/fimagestore_trp_test/C/L/CLLMVGTUQWKRACMHDKBNGRLB/metadata.txt";
		try {
			FileMetadata mdDisk  = MetadataReader.readFileMetadataFromMdFile(path);
			logger.debug(mdDisk.toString());
			
			Assert.assertEquals(mdDisk.getOrigFilename(), mdHttp.getOrigFilename());
		} catch (IOException e) {
			logger.info("metadata file is not readable. Skipping this test.");
			return;
		}
	}
	


}
