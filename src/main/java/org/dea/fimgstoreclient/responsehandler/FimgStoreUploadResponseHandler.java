package org.dea.fimgstoreclient.responsehandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.dea.fimgstoreclient.FimgStoreConstants;

/**
 * A {@link ResponseHandler} that takes the Response from a FimgStore POST
 * Request and returns the generated file key as String for successful (2xx)
 * responses. If the response code was >= 300, the response body is consumed and
 * an {@link HttpResponseException} is thrown.<br/>
 * Based on {@link BasicResponseHandler} in apache http client 4.3
 * <p/>
 * If this is used with
 * {@link org.apache.http.client.HttpClient#execute(org.apache.http.client.methods.HttpUriRequest, ResponseHandler)}
 * , HttpClient may handle redirects (3xx responses) internally.
 * 
 * @author philip
 */
@Immutable
public class FimgStoreUploadResponseHandler implements ResponseHandler<String> {

	/**
	 * Extracts the file key from the response body and returns it as a String
	 * if the response was successful (a 2xx status code). If no response body
	 * exists, this returns null. If the response was unsuccessful (>= 300
	 * status code) or no valid image key could be found, throws an {@link HttpResponseException}.
	 */
	public String handleResponse(final HttpResponse response) throws HttpResponseException,
			IOException {
		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			EntityUtils.consume(entity);
			throw new HttpResponseException(statusLine.getStatusCode(),
					statusLine.getReasonPhrase());
		}

		String respLine = null;
		final Pattern p = Pattern.compile(".*(" + FimgStoreConstants.getString("fileKeyPattern") + ").*");

		if (entity != null) {
			respLine = EntityUtils.toString(entity);
			// System.out.println("ResponseHandler is handling: " + respLine);
			Matcher m = p.matcher(respLine);

			if (m.find()) {
				respLine = m.group(1);
			} else {
				throw new HttpResponseException(422, "Strange response from fimagestore: "
						+ respLine);
			}
		}
		return respLine;
	}

}