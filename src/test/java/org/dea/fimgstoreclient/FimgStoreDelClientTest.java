package org.dea.fimgstoreclient;
import org.dea.fimgstoreclient.AbstractClient.Scheme;
import org.dea.fimgstoreclient.FimgStoreDelClient;

public class FimgStoreDelClientTest {

	public static void main(String[] args) {
	
		String[] fileKeys = {"DACRGKYTEAKRPQUIQQSNLKJZ"};
		FimgStoreDelClient delClient = 
				new FimgStoreDelClient(Scheme.https, "dbis-thure.uibk.ac.at", "fimagestore", args[0], args[1]);
		
			for(String key : fileKeys){
				try{
					System.out.println("Delete key: " + key);
					System.out.println(delClient.deleteFile(key, 5));
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			
	}
}