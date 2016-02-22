import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.auth.AuthenticationException;
import org.dea.fimgstoreclient.AbstractClient.Scheme;
import org.dea.fimgstoreclient.FimgStoreDelClient;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.FimgStorePostClient;


public class StressTest {
	
	public static void main(String[] args) {
		File testFile = new File("/home/sebastianc/Bilder/hans_maulwurf.tif");
		byte[] bFile = new byte[(int) testFile.length()];
		try {
            //convert file into array of bytes
			FileInputStream fileInputStream = new FileInputStream(testFile);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
 
//	    for (int i = 0; i < bFile.length; i++) {
//	       	System.out.print((char)bFile[i]);
//            }
 
//	    System.out.println("Done");
        }catch(Exception e){
        	e.printStackTrace();
        }		
		
		String host = "dbis-thure.uibk.ac.at";
		String serverContext = "fimagestoreTrp";
		
		FimgStoreGetClient get = new FimgStoreGetClient(host, serverContext);
		FimgStorePostClient post = new FimgStorePostClient(Scheme.https, host, serverContext, "admin", "secretadminpw");
		FimgStoreDelClient del = new FimgStoreDelClient(Scheme.https, host, serverContext, "admin", "secretadminpw");
		
		int N = 1000;
		int nRetries = 3;
		
		String key = null;
		for (int i=1; i<=N; ++i) {
			System.out.println("i = "+i);
			try {
//				if (key == null) {
//					System.out.println("post file...");
					key = post.postFile(testFile, "stresstest", nRetries);
					System.out.println(key);
//				}
//				else {
					
//					System.out.println("replacing file "+key);
//					post.replaceFile(key, bFile, "fn", "stresstest", nRetries);
//				}
//				post.pos
//				System.out.println("trying to delete posted file "+key);
//				del.deleteFile(key, nRetries);
				System.out.println("DONE");
//				FimgStoreFileMd md = get.getFileMd("YOVOXBSEOSHXBBPTJRJFHTBJ");
//				System.out.println(md);
			} catch (AuthenticationException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
