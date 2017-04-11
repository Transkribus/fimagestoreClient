package org.dea.fimgstoreclient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthenticationException;
import org.dea.fimgstoreclient.AbstractClient.Scheme;
import org.dea.fimgstoreclient.FimgStoreDelClient;
import org.dea.fimgstoreclient.FimgStorePostClient;

public class FimgStorePostClientTest {
	
	//Trp Test Doc
	static String collName = "TrpTestDoc";
	static String basePath = "/mnt/dea_scratch/TRP/TrpTestDoc/";
	static String[] fileNames = new String[]{"StAZ-Sign.2-1_001"};//, "StAZ-Sign.2-1_002", "StAZ-Sign.2-1_003"};
	static String[] fileTypes = new String[]{".jpg"}; //,".xml"};
	
	static FimgStorePostClient fiscPo;
	static FimgStoreDelClient fiscDel;
	
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

	public static void main(String[] args) throws Exception {
		fiscPo = new FimgStorePostClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestoreTrp", args[0], args[1]);
		fiscDel = new FimgStoreDelClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestoreTrp", args[0], args[1]);
		
		testPostWithTimeout();
//		testOther();
	}

}
