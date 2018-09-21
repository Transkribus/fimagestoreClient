package org.dea.fimgstoreclient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.auth.AuthenticationException;
import org.dea.fimagestore.core.util.SebisStopWatch.SSW;
import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FimgStorePostClientTest {
	private static final Logger logger = LoggerFactory.getLogger(FimgStorePostClientTest.class);
	//Trp Test Doc
	static String collName = "TrpTestDoc";
	static String basePath = "/mnt/dea_scratch/TRP/TrpTestDoc/";
	static String[] fileNames = new String[]{"StAZ-Sign.2-1_001"};//, "StAZ-Sign.2-1_002", "StAZ-Sign.2-1_003"};
	static String[] fileTypes = new String[]{".jpg"}; //,".xml"};
	
	static FimgStorePostClient fiscPo;
	static FimgStoreDelClient fiscDel;
	static FimgStoreGetClient getter;
	
	public static void testPostWithTimeout() throws Exception {
		String file = basePath + fileNames[0] + fileTypes[0];
		int timeoutMinutes = 1;
		
		String key = fiscPo.postFile(new File(file), collName, 5, timeoutMinutes);
		System.out.println("posted file with timeoutMinutes = "+timeoutMinutes+" key = "+key);
	}
	
	public static void testOther() {
		try {
			List<String> keys = new ArrayList<>(6);
			for(String s : fileNames){
				
				for(String t : fileTypes){
					final File ul = new File(basePath + s + t);
					
//					sb.append(s + t + " -> ");
					final String key = fiscPo.postFile(ul, collName, 5);
					keys.add(key);
//					sb.append(key + "\n");
					System.out.println(ul.getAbsolutePath() + " -> " + key);
				}
			}
			
			for(String key : keys){
				fiscDel.deleteFile(key, 5);
			}
//			FileUtils.writeStringToFile(sb.toString(), basePath + "fileKeys.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPost() {
		
		SSW sw = new SSW();
			
		File f = new File("/mnt/dea_scratch/TRP/Bentham_box_002/002_080_001.jpg");
		
//		sw.start();
//		postAndDelete(Scheme.http, "dea-moe.uibk.ac.at", 8081, "/fimagestore", "Transcript", "Munuti81", f);
//		sw.stop("dea-moe http post and delete", logger);
		
		sw.start();
		postAndDelete(Scheme.https, "dbis-thure.uibk.ac.at", 443, "/fimagestoreTest", "Transcript", "Munuti81", f);
		sw.stop("dbis-thure post and delete", logger);
		
//		sw.start();
//		postAndDelete(Scheme.https, "files-test.transkribus.eu", 443, "/", "Transcript", "Munuti81", f);
//		sw.stop("files-test.transkribus.eu post and delete", logger);
		
	}
	
	private void postAndDelete(Scheme scheme, String host, int port, String context, String user, String pw, File testFile) {
		FimgStorePostClient poster = new FimgStorePostClient(scheme, host, port, context, user, pw);
		
		String key = null;
		try {
			key = poster.postFile(testFile, "http-client test", 2);
		} catch (AuthenticationException | IOException e) {
			e.printStackTrace();
		}		
		
		if(key != null) {
			FimgStoreDelClient deller = new FimgStoreDelClient(scheme, host, port, context, user, pw);
			try {
				deller.deleteFile(key, 2);
			} catch (AuthenticationException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void testUploadEncoding() throws AuthenticationException, IOException {		
		File testFile = new File("/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_001.jpg");
		
		final String filename = "messedUpÄÄÄÜÜÜÜ.jpg";
		
		File toUpload = new File("/tmp/" + filename);
		FileUtils.copyFile(testFile, toUpload);
		toUpload.deleteOnExit();

		String key = fiscPo.postFile(toUpload, "test", 2);
		

		FimgStoreImg img = getter.getImg(key);
		
		logger.info("stored: " + img.getFileName() + " <-> sent: " + filename);
		
		logger.info("From md: " + getter.getFileMd(key).getFileName());
		
		fiscDel.deleteFile(key, 0);
	}

	public static void main(String[] args) throws Exception {
		fiscPo = new FimgStorePostClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestoreTrp", args[0], args[1]);
		fiscDel = new FimgStoreDelClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestoreTrp", args[0], args[1]);
		getter = new FimgStoreGetClient(Scheme.https, "dbis-thure.uibk.ac.at", 443, "fimagestoreTrp");
		
		testPostWithTimeout();
//		testOther();
		testUploadEncoding(); //fimagestore issue #3
	}

}
