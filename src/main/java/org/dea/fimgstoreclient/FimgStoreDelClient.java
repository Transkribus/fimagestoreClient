package org.dea.fimgstoreclient;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.dea.fimagestore.core.client.IFImagestoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fimagestore Client for Java-side deletion of files.
 * 
 * @author philip
 * 
 */
public class FimgStoreDelClient extends AbstractBasicAuthHttpClient implements IFimgStoreDelClient {
	
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreDelClient.class);
	
	public FimgStoreDelClient(final IFImagestoreConfig config) {
		super(config);
	}
	
	public FimgStoreDelClient(Scheme scheme, String host, String serverContext, String username,
			String password) {
		this(scheme, host, null, serverContext, username, password);
	}
	
	public FimgStoreDelClient(Scheme scheme, String host, Integer port, String serverContext, String username,
			String password) {
		super(scheme, host, port, serverContext, username, password);
	}

	/**
	 * Delete the file with this key. If request fails, then retry for nrOfRetries times.
	 * @param fileKey the key of the file to be deleted
	 * @param nrOfRetries retry for nrOfRetries times
	 * @return true if deletion was success
	 * @throws IOException of network error occurs
	 * @throws AuthenticationException if authentication fails
	 */
	public boolean deleteFile(final String fileKey, int nrOfRetries) throws IOException {
		logger.debug("deleteFile()");
		if(FImagestoreClientConfig.DUMMY_IMAGE_KEY.equals(fileKey)) {
			//the dummy image will not be deleted. Just pretend it was successful
			return true;
		}
		CloseableHttpResponse response = null;
		URI uri = getUriBuilder().getDeleteUri(fileKey);
		logger.debug("Calling URL: " + uri.toString());
		int status;
		int retries = 0;
		Exception throwup;
		
		do {
			if(retries > 0){
				try {
					logger.info("GET Retry nr. " + retries + ". Waiting 5 sec...");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					logger.error("Interrupted sleep()!", e);
				}
			}
			try {
				response = delete(uri);
				status = response.getStatusLine().getStatusCode();
				logger.debug("Response: " + response.getStatusLine().toString());
				throwup = null;
			} catch(Exception e){
				logger.error("Error during HTTP GET: " + e.getMessage());
				status = response.getStatusLine().getStatusCode();
				throwup = e;
			}
		} while(throwup != null && retries++ < nrOfRetries);
		if(response != null) {
			response.close();
		}
		//check if the last retry did also fail
		if(throwup != null){
			logger.error("Error during HTTP GET: All retries failed! " + throwup.getMessage());
			throw new IOException(throwup);
		}
		return (status < 300) ? true : false;
	}
	
//	public boolean deleteFile(final String fileKey) throws IOException, AuthenticationException {
//		return deleteFile(fileKey, 0);
//	}
//
//	public void deleteFiles(List<String> fileKeysToDelete) {
//		deleteFiles(fileKeysToDelete, 0);
//	}

	public void deleteFiles(List<String> fileKeysToDelete, int nrOfRetries) {
		logger.debug("deleteFiles()");
		if(fileKeysToDelete != null && !fileKeysToDelete.isEmpty()){
			int fails = 0;
			for(String key : fileKeysToDelete){
				logger.debug("Trying to delete file with key: " + key);
				try {
					boolean success = deleteFile(key, nrOfRetries);
					if(success) logger.debug("succeeded.");
					else logger.error("File " + key + " could not be deleted!");
					// Do not flood the fimagestore.
					Thread.sleep(500);
				} catch(IOException e){
					logger.error("Failed to remove image with key: " + key);
					logger.error(e.getMessage());
					e.printStackTrace();
					fails++;
				} catch (InterruptedException ie){
					logger.debug("Interrupted sleep.", ie);
				}
			}
//			logger.debug(fileKeysToDelete.size() + " files deleted.");
			if( fails != 0) logger.error(fails + " remaining on server!");
		}
		
	}
}
