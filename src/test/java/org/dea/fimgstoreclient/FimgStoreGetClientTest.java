package org.dea.fimgstoreclient;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.dea.fimgstoreclient.beans.FimgStoreXml;
import org.dea.fimgstoreclient.beans.ImgType;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FimgStoreGetClientTest {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreGetClientTest.class);
	
	static FimgStoreGetClient fisc = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "fimagestoreTrp");
	static FimgStoreUriBuilder uriBuilder = fisc.getUriBuilder();
	
	static String TMP_DIR = System.getProperty("java.io.tmpdir");
	
	final String testImgKey = "WIVJRXZOGQWBYOGAOGRRXKWZ";
	
	@Test
	public void testDownloadAndMetadata() throws IllegalArgumentException, IOException {
//		final String testImgKey = "WIVJRXZOGQWBYOGAOGRRXKWZ";

		FimgStoreImg result = null;
		File download = null;

//		try {
			URI uri = uriBuilder.getFileUri(testImgKey);
			System.out.println("UriBuilder test: " + uri.toString());
			download = fisc.saveFile(uri, TMP_DIR); //TODO get stream here rather than having client save stuff
			result = fisc.getImg(testImgKey, ImgType.view);
//		} catch (IllegalArgumentException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// image metadata testing:
//		try {
			System.out.println(fisc.getFileMd(testImgKey));
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println(result.toString());
		System.out.println(download.getAbsolutePath());	
	}
	
	@Test
	public void testBlackeningImage() throws IllegalArgumentException, IOException {
		File download = null;
		List<Point> polygonPts1 = new ArrayList<>();
		polygonPts1.add(new Point(1908, 189));
		polygonPts1.add(new Point(2462, 189));
		polygonPts1.add(new Point(2462, 418));
		polygonPts1.add(new Point(1908, 418));
		
		List<Point> polygonPts2 = new ArrayList<>();
		polygonPts2.add(new Point(100, 100));
		polygonPts2.add(new Point(200, 50));
		polygonPts2.add(new Point(300, 100));
		polygonPts2.add(new Point(200, 150));
		polygonPts2.add(new Point(100, 100));

//		try {
			URI uri = uriBuilder.getImgBlackenedUri(testImgKey, polygonPts1, polygonPts2);
			System.out.println("blacken uri: " + uri.toString());
			download = fisc.saveFile(uri, TMP_DIR); //TODO get stream here rather than having client save stuff
			System.out.println("stored blacked image at: "+download.getAbsolutePath());
			
//			ImagePanel.showImage(download.getAbsolutePath());
//			FimgStoreImg result = fisc.getImg(testImgKey, ImgType.view);
//		} catch (IllegalArgumentException | IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
	/**
	 * Test to check if the client can decode content correctly.
	 * It does not yet check if encoding is enabled on the server though!
	 * By enabling debug logging for org.apache.* the content length of the response is visible for comparison. 
	 *  
	 * @throws IOException
	 */
	@Test
	public void getGzippedXmlTest() throws IOException {
		final String xmlKey = "KEMNGBPFAQPZKPAYZOXAZKDP";
		try (FimgStoreGetClient getter = new FimgStoreGetClient("files-test.transkribus.eu", "/")) {
			FimgStoreXml xml = getter.getXml(xmlKey);
			String content = new String(xml.getData());
			logger.info("Received content of size {}:\n{}", content.length(), content);
			Assert.assertTrue(content.startsWith("<?xml"));
		}
		
	}

	public static void main(String[] args) throws Exception {
		FimgStoreGetClientTest t = new FimgStoreGetClientTest();
		t.testBlackeningImage();

//		testDownloadAndMetadata();	
	}
}
