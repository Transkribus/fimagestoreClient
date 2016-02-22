package org.dea.fimgstoreclient;

import java.io.File;
import java.io.IOException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dea.fimgstoreclient.responsehandler.FimgStoreUploadResponseHandler;
import org.dea.fimgstoreclient.utils.MimeTypes;

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
	protected final static String PART_OF_VAR_NAME = FimgStoreConstants.getString("partOfVarName");
	protected final static String FILE_VAR_NAME = FimgStoreConstants.getString("fileVarName");
	protected final static String REPLACE_ID_VAR_NAME = FimgStoreConstants.getString("replaceIdVarName");

	public FimgStorePostClient(Scheme scheme, String host, String serverContext,
			String username, String password) {
		super(scheme, host, null, serverContext, username, password);
	}
	
	public FimgStorePostClient(Scheme scheme, String host, Integer port, String serverContext,
			String username, String password) {
		super(scheme, host, port, serverContext, username, password);
	}

	
	/**
	 * post a file to the image store and get the retrieval key
	 * 
	 * @param ulFile file to upload
	 * @param isPartOf a String that identifies the collection
	 * @param nrOfRetries 
	 * @return fileKey for subsequent retrieval of file
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public String postFile(File ulFile, String isPartOf, int nrOfRetries) throws IOException, AuthenticationException {
		if (!ulFile.canRead()) {
			throw new IOException("UploadFile " + ulFile.getAbsoluteFile() + " is not readable.");
		}
		ContentType contentType = getContentType(ulFile.getName());

		// build content:
		final FileBody fileBody = new FileBody(ulFile, contentType, ulFile.getName());

		//post and return key
		return postContent(fileBody, isPartOf, null, nrOfRetries);
	}
	
//	public String postFile(File ulFile, String isPartOf) throws IOException, AuthenticationException {
//		return postFile(ulFile, isPartOf, 0);
//	}
	
	/**
	 * post data as a file to the image store and get the retrieval key
	 * 
	 */
	public String postFile(byte[] data, final String fileName, final String isPartOf, final int nrOfRetries) throws IOException, AuthenticationException {

		if (data == null) {
			throw new IOException("Data is NULL.");
		} 
		if (fileName == null || fileName.isEmpty()){
			throw new IOException("fileName is NULL or empty.");
		}

		final ContentType contentType = getContentType(fileName);
		final ByteArrayBody fileBody = new ByteArrayBody(data, contentType, fileName);
		//post and return key
		return postContent(fileBody, isPartOf, nrOfRetries);
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
		return postContent(fileBody, isPartOf, key, nrOfRetries);
	}
	
	/**
	 * replace a file at the image store and get the retrieval key
	 * 
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
		return postContent(fileBody, isPartOf, key, nrOfRetries);
	}

	private String postContent(ContentBody body, final String isPartOf, final int nrOfRetries) throws IOException, AuthenticationException {
		return postContent(body, isPartOf, null, nrOfRetries);
	}
	
	private String postContent(ContentBody body, final String isPartOf, final String key, int nrOfRetries) throws IOException, AuthenticationException {

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
			entBuilder.addPart(PART_OF_VAR_NAME, stringBody);
		}
		
		if(key != null){
			// replace file. set parameter...
			final StringBody stringBody = new StringBody(key, ContentType.TEXT_PLAIN);
			entBuilder.addPart(REPLACE_ID_VAR_NAME, stringBody);
		}
		
		// add content
		entBuilder.addPart(FILE_VAR_NAME, body);

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
