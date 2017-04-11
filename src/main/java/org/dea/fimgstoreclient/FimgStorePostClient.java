package org.dea.fimgstoreclient;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.dea.fimgstoreclient.responsehandler.FimgStoreUploadResponseHandler;
import org.dea.fimgstoreclient.utils.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for posting stuff to the fimagestore.
 * 
 * TODO Batch upload methods
 * 
 * @author philip
 *
 */
public class FimgStorePostClient extends AbstractClient {
	private static final Logger logger = LoggerFactory.getLogger(FimgStorePostClient.class);

	public FimgStorePostClient(Scheme scheme, String host, String serverContext,
			String username, String password) {
		super(scheme, host, null, serverContext, username, password);
	}
	
	public FimgStorePostClient(Scheme scheme, String host, Integer port, String serverContext,
			String username, String password) {
		super(scheme, host, port, serverContext, username, password);
	}
	
	public String postFile(File ulFile, String isPartOf, int nrOfRetries) throws IOException, AuthenticationException {
		return postFile(ulFile, isPartOf, nrOfRetries, null);
	}
	
	/**
	 * post a file to the image store and get the retrieval key
	 * 
	 * @param ulFile file to upload
	 * @param isPartOf a String that identifies the collection
	 * @param nrOfRetries nr of times to retry if failure
	 * @param timeoutMinutes specifies the number of minutes after the file will be deleted on the fimagestore; null means no timeout
	 * @return fileKey for subsequent retrieval of file
	 * @throws IOException if network error occurs
	 * @throws AuthenticationException if authentication fails
	 */
	public String postFile(File ulFile, String isPartOf, int nrOfRetries, Integer timeoutMinutes) throws IOException, AuthenticationException {
		if (!ulFile.canRead()) {
			throw new IOException("UploadFile " + ulFile.getAbsoluteFile() + " is not readable.");
		}
		ContentType contentType = getContentType(ulFile.getName());

		// build content:
		final FileBody fileBody = new FileBody(ulFile, contentType, ulFile.getName());

		//post and return key
		return postContent(fileBody, isPartOf, null, nrOfRetries, timeoutMinutes);
	}
	
//	public String postFile(File ulFile, String isPartOf) throws IOException, AuthenticationException {
//		return postFile(ulFile, isPartOf, 0);
//	}
	
	public String postFile(byte[] data, final String fileName, final String isPartOf, final int nrOfRetries) throws IOException, AuthenticationException {
		return postFile(data, fileName, isPartOf, nrOfRetries, null);
	}
	
	/**
	 * Post raw data as a file to the image store and get the retrieval key
	 * @param data the bytes to post
	 * @param fileName the original file name
	 * @param isPartOf the collection name
	 * @param nrOfRetries nr of times to retry if failure
	 * @return the key of the object on the image store
	 * @throws IOException if network error occurs
	 * @throws AuthenticationException if authentication fails
	 */
	public String postFile(byte[] data, final String fileName, final String isPartOf, final int nrOfRetries, Integer timeoutMinutes) throws IOException, AuthenticationException {

		if (data == null) {
			throw new IOException("Data is NULL.");
		} 
		if (fileName == null || fileName.isEmpty()){
			throw new IOException("fileName is NULL or empty.");
		}

		final ContentType contentType = getContentType(fileName);
		final ByteArrayBody fileBody = new ByteArrayBody(data, contentType, fileName);
		//post and return key
		return postContent(fileBody, isPartOf, null, nrOfRetries, timeoutMinutes);
	}
	
	public String replaceFile(final String key, File ulFile, String isPartOf, int nrOfRetries) throws IOException, AuthenticationException {
		if (!ulFile.canRead()) {
			throw new IOException("UploadFile " + ulFile.getAbsoluteFile() + " is not readable.");
		}
		if (key == null){
			throw new IOException("fileKey is NULL.");
		}
		ContentType contentType = getContentType(ulFile.getName());

		// build content:
		final FileBody fileBody = new FileBody(ulFile, contentType, ulFile.getName());

		//post and return key
		return postContent(fileBody, isPartOf, key, nrOfRetries, null);
	}
	
	/**
	 * Replace a file at the image store and get the retrieval key
	 * @param key the key of the file to be replaced
	 * @param data raw bytes to be posted
	 * @param fileName the original file's name
	 * @param isPartOf the collection name on the fimagestore
	 * @param nrOfRetries nr of times to retry if failure
	 * @return the key of the newly created object
	 * @throws IOException if network error occurs
	 * @throws AuthenticationException if authentication fails
	 */
	public String replaceFile(final String key, byte[] data, final String fileName, final String isPartOf, final int nrOfRetries) throws IOException, AuthenticationException {

		if (data == null) {
			throw new IOException("Data is NULL.");
		} 
		if (key == null){
			throw new IOException("fileKey is NULL.");
		}

		final ContentType contentType = getContentType(fileName);
		final ByteArrayBody fileBody = new ByteArrayBody(data, contentType, fileName);
		//post and return key
		return postContent(fileBody, isPartOf, key, nrOfRetries, null);
	}

	private String postContent(ContentBody body, final String isPartOf, final int nrOfRetries) throws IOException, AuthenticationException {
		return postContent(body, isPartOf, null, nrOfRetries, null);
	}
	
	private String postContent(ContentBody body, final String isPartOf, final String key, int nrOfRetries, Integer timeoutMinutes) throws IOException, AuthenticationException {

		if (body == null) {
			throw new IOException("Data is NULL.");
		}

		// create multipart message:
		MultipartEntityBuilder entBuilder = MultipartEntityBuilder.create();

		// browser-compatible mode, i.e. only write Content-Disposition; use
		// content charset
		entBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// add is_part_of:
		if(isPartOf != null){
			final StringBody stringBody = new StringBody(isPartOf, ContentType.TEXT_PLAIN);
			entBuilder.addPart(FimgStoreConstants.PART_OF_VAR_NAME, stringBody);
		}
		
		if(key != null){
			// replace file. set parameter...
			final StringBody stringBody = new StringBody(key, ContentType.TEXT_PLAIN);
			entBuilder.addPart(FimgStoreConstants.REPLACE_ID_VAR_NAME, stringBody);
		}
		
		if (timeoutMinutes != null) {
			entBuilder.addPart(FimgStoreConstants.TIMEOUT_PARAM, new StringBody(""+timeoutMinutes, ContentType.TEXT_PLAIN));
		}
		
		// add content
		entBuilder.addPart(FimgStoreConstants.FILE_VAR_NAME, body);

		// post stuff and get the file key
		ResponseHandler<String> responseHandler = new FimgStoreUploadResponseHandler();
		String response = null;
		int retries = 0;
		Exception throwup;
		
		do {
			if(retries > 0){
				try {
					logger.info("POST Retry nr. " + retries + ". Waiting 5 sec...");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					logger.error("Interrupted sleep()!", e);
				}
			}
			try {
				response = this.post(entBuilder.build(), responseHandler);
				throwup = null;
			} catch(Exception e){
				logger.error("Error during HTTP POST: " + e.getMessage(), e);
				throwup = e;
			}
		} while(throwup != null && retries++ < nrOfRetries);
		//check if the last retry did also fail
		if(throwup != null){
			logger.error("Error during HTTP POST: All retries failed! " + throwup.getMessage());
			throw new IOException(throwup);
		}
		
		return response;
	}

	private ContentType getContentType(final String fileName) throws IOException {
		final String extension = FilenameUtils.getExtension(fileName);
		final String mimeType = MimeTypes.getMimeType(extension);
		if (mimeType == null) {
			throw new IOException("Unknown extension: " + fileName);
		}
		return ContentType.create(mimeType);
	}
}
