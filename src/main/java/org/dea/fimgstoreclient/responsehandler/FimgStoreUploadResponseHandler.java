package org.dea.fimgstoreclient.responsehandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.dea.fimagestore.core.FImagestoreConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ResponseHandler} that takes the Response from a FimgStore POST
 * Request and returns the generated file key as String for successful (2xx)
 * responses. If the response code was &gt;= 300, the response body is consumed and
 * an {@link HttpResponseException} is thrown.<br>
 * Based on {@link AbstractResponseHandler} and in apache http client 4.5.5
 * <p>
 * If this is used with
 * {@link org.apache.http.client.HttpClient#execute(org.apache.http.client.methods.HttpUriRequest, ResponseHandler)}
 * , HttpClient may handle redirects (3xx responses) internally.
 * 
 * @author philip
 */
public class FimgStoreUploadResponseHandler extends AbstractResponseHandler<String> {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreUploadResponseHandler.class);
	/**
	 * Pattern for extracting the filekey from the returned URL
	 */
	private final static Pattern KEY_PATTERN = Pattern.compile(".*(" + FImagestoreConst.FILEKEY_PATTERN_STR + ").*");

	/**
	 * Extracts the file key from the response body and returns it as a String
	 * if the response was successful (a 2xx status code). If no response body
	 * exists, this returns null. If the response was unsuccessful (&gt;= 300
	 * status code) or no valid image key could be found, throws an {@link HttpResponseException}.<br>
	 * Is called by {@link org.apache.http.impl.client.AbstractResponseHandler#handleResponse(HttpResponse)}
	 * @see org.apache.http.impl.client.AbstractResponseHandler#handleEntity(org.apache.http.HttpEntity)
	 */
	@Override
	public String handleEntity(HttpEntity entity) throws IOException {
		final String respLine = EntityUtils.toString(entity);
		logger.debug("Handling fimagestore upload response: " + respLine);
		Matcher m = KEY_PATTERN.matcher(respLine);
		if (m.find()) {
			return m.group(1);
		} else {
			throw new HttpResponseException(422, "Strange response from fimagestore: "
					+ respLine);
		}
	}

}