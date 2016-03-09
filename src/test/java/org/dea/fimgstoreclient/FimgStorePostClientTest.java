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

	public static void main(String[] args) {

		
		//Trp Test Doc
		final String collName = "TrpTestDoc";
		
		final String basePath = "/mnt/dea_scratch/TRP/TrpTestDoc/";
		
		final String[] fileNames = new String[]{"StAZ-Sign.2-1_001"};//, "StAZ-Sign.2-1_002", "StAZ-Sign.2-1_003"};
		final String[] fileTypes = new String[]{".jpg"}; //,".xml"};
		
		FimgStorePostClient fiscPo = new FimgStorePostClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestore", args[0], args[1]);
//		StringBuffer sb = new StringBuffer();
		
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
			FimgStoreDelClient fiscDel = new FimgStoreDelClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestore", args[0], args[1]);
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

}
