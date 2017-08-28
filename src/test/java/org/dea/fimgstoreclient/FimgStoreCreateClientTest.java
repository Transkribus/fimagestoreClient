package org.dea.fimgstoreclient;
import java.awt.Point;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.FimgStoreCreateClient;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;

import ch.qos.logback.classic.Logger;

public class FimgStoreCreateClientTest {
	
	static FimgStoreGetClient fisc = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "fimagestoreTrp");
//	static FimgStorePostClient fisc = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "fimagestoreTrp");
	
	static FimgStoreUriBuilder uriBuilder = fisc.getUriBuilder();
	
	public static void testCreateBlackenedImage(String user, String pw) {
		FimgStoreCreateClient fiscCr = new FimgStoreCreateClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestoreTrp", user, pw);
		
		final String testImgKey = "WIVJRXZOGQWBYOGAOGRRXKWZ";
		
		FimgStoreImg result = null;
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

		try {
			URI getUri = uriBuilder.getImgBlackenedUri(testImgKey, polygonPts1, polygonPts2);
			System.out.println("blacken uri: " + getUri);
			
//			URI createUri = uriBuilder.getCreateUri(getUri, "newIsPartOf", 10, null);
//			System.out.println("create uri: "+createUri.toString());
			
			String key = fiscCr.createFile(getUri, "newIsPartOf", 10, null);
						
			System.out.println("created new file with key = "+key);
			
			String key2 = fiscCr.createBlackenedImage(testImgKey, "newIsPartOf2", 10, null, polygonPts1, polygonPts2);
			System.out.println("created blackened image key = "+key2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		testCreateBlackenedImage(args[0], args[1]);
	}
}
