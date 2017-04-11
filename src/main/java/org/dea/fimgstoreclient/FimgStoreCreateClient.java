package org.dea.fimgstoreclient;

import java.awt.Point;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.dea.fimgstoreclient.responsehandler.FimgStoreUploadResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for creating new versions of already stored objects.
 * @author sebic
 */
public class FimgStoreCreateClient extends AbstractClient {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreCreateClient.class);

	public FimgStoreCreateClient(Scheme scheme, String host, String serverContext,
			String username, String password) {
		super(scheme, host, null, serverContext, username, password);
	}
	
	public FimgStoreCreateClient(Scheme scheme, String host, Integer port, String serverContext,
			String username, String password) {
		super(scheme, host, port, serverContext, username, password);
	}
	
	/**
	 * Wrapper method for creating a blackened image out of an existing one on the fimagestore
	 * @param imgKey key to the original image
	 * @param isPartOf the collection name
	 * @param timeout in milliseconds
	 * @param replaceKey the object to replace. can be null, then new key is created
	 * @param polygonPtsList points of the polygon to be blackened
	 * @return the filekey for the result object
	 * @throws AuthenticationException if authentication fails
	 * @throws ClientProtocolException client side error
	 * @throws IOException if network error occurs
	 */
	@SafeVarargs
	public final String createBlackenedImage(String imgKey, String isPartOf, Integer timeout, String replaceKey, List<Point> ...polygonPtsList) throws AuthenticationException, ClientProtocolException, IOException {
		URI blackenUri = uriBuilder.getImgBlackenedUri(imgKey, polygonPtsList);
		
		return createFile(blackenUri, isPartOf, timeout, replaceKey);
	}
	

	/**
	 * Creates a new file on the fimagestore using the given getUri (constructed e.g. using the {@link org.dea.fimgstoreclient.utils.FimgStoreUriBuilder}) and the additional
	 * parameters isPartOf, timeout and replaceKey which all can be null.\n
	 * The key of the new file is returned.
	 * @param getUri the URI to the original object
	 * @param isPartOf the collection name
	 * @param timeout in milliseconds
	 * @param replaceKey the object to replace. can be null, then new key is created
	 * @return the filekey for the result object
	 * @throws AuthenticationException if authentication fails
	 * @throws ClientProtocolException client side error
	 * @throws IOException if network error occurs
	 */
	public String createFile(URI getUri, String isPartOf, Integer timeout, String replaceKey) throws AuthenticationException, ClientProtocolException, IOException {
		
		URI createUri = uriBuilder.getCreateUri(getUri, isPartOf, timeout, replaceKey);
		
		String key = createFile(createUri, new FimgStoreUploadResponseHandler());
		
		return key;
	}
	
	public String createFile(URI createUri, ResponseHandler<String> responseHandler) throws AuthenticationException, ClientProtocolException, IOException {
		HttpPut httpPut = new HttpPut(createUri);
				
		logger.debug("PUT: " + createUri.toString());
//		logger.debug("Using scheme: " + this.scheme.toString());
		logger.debug("FimgStore user: " + creds.getUserPrincipal());
		
		if(this.scheme.equals(Scheme.https)) {
			BasicScheme authScheme = new BasicScheme();
			//authenticate. Header is (or should be! FIXME) added automatically by this call
			Header authHeader = authScheme.authenticate(creds, httpPut, context);
			
//			logger.debug("AuthHeader " + authHeader.getName() + ": " + authHeader.getValue());
			httpPut.setHeader(authHeader);
//			for(Header h : httpPost.getAllHeaders()){
//				logger.debug("Header " + h.getName() + ": " + h.getValue());
//			}
			
		}
		
		CloseableHttpClient httpClient = builder.build();
		final String result = httpClient.execute(httpPut, responseHandler, context);
		logger.debug("create done: " + result);
		//DO NOT CLOSE or connection pool will shut down (httpClient 4.4)
//		httpClient.close();
		return result;
		
	}
}
