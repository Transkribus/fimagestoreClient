package org.dea.fimgstoreclient.responsehandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.dea.fimgstoreclient.utils.OtherUtils;

/**
 * Handle responses from the FImagestore Get servlet and create the respective return value object of type {@link T}. 
 * <br><br>
 * Use with the {@link CloseableHttpClient#execute(org.apache.http.client.methods.HttpUriRequest, org.apache.http.client.ResponseHandler, org.apache.http.protocol.HttpContext)} 
 * method and benefit from the integrated resource handling (consume entity, close response) in order to avoid resource leaks.
 *  
 * @param <T>
 */
public abstract class AFimgStoreResponseWithAttachmentHandler<T> extends AbstractResponseHandler<T> {
	
	private final URI requestUri;
	
	public AFimgStoreResponseWithAttachmentHandler(URI uri) {
		super();
		this.requestUri = uri;
	}
	
	@Override
	public T handleResponse(HttpResponse response) throws HttpResponseException, IOException {
		this.checkAttachmentInfo(response);
		if (response.getStatusLine().getStatusCode() == 404) {
			throw new FileNotFoundException("No resource found with URI: " + requestUri.toString());
		}
		return super.handleResponse(response);
	}
	
	public URI getRequestUri() {
		return requestUri;
	}
	
	/**
	 * Extract and validate Content-Disposition header. Rebuild original attachment
	 * file name.
	 * 
	 * @param response
	 *            the response to parse
	 * @return original file name
	 * @throws FileNotFoundException
	 *             if no file is contained in response or the header is damaged
	 */
	private String checkAttachmentInfo(HttpResponse response) throws FileNotFoundException {
		String fn = null;
		Header header = response.getFirstHeader("Content-Disposition");
		if (header == null) {
			throw new FileNotFoundException("No Content-Disposition header found!");
		}
		int fnindex = header.getValue().indexOf("filename=");
		if (fnindex == -1) {
			throw new FileNotFoundException("No valid Content-Disposition header found!");
		}
		fn = OtherUtils.trimQuotes(header.getValue().substring(fnindex + 9));
		return fn;
	}
}
