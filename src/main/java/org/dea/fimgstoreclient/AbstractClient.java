package org.dea.fimgstoreclient;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;

public abstract class AbstractClient {
	private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
	protected final static String userAgent = "DEA Fimagestore Client 0.1";
	
//	protected URIBuilder uriBuilder;
//	private static CloseableHttpClient httpClient;
	protected static HttpClientBuilder builder;
	protected static HttpClientContext context;
	
	protected String serverContext;
	protected String host;
	protected Integer port;
	protected Scheme scheme;
	protected FimgStoreUriBuilder uriBuilder;
	
	private Credentials creds = null;
	
//	private AuthCache authCache = new BasicAuthCache();
	private AuthScope scope = null;

//	/**
//	 * Create client with default host (dbis-thure) using https
//	 */
//	protected AbstractClient() {
////		creds = new UsernamePasswordCredentials(Constants.getString("username"), Constants.getString("pw"));
//		creds = new UsernamePasswordCredentials("", "");
//		this.initialize(Scheme.https, defaultHost, defaultServerContext, creds);
//	}
	
	protected AbstractClient(final String host, final String serverContext) {
		this.initialize(Scheme.https, host, null, serverContext, null);
	}
	
	protected AbstractClient(final String host, final Integer port, final String serverContext) {
		this.initialize(Scheme.https, host, port, serverContext, null);
	}
	
	protected AbstractClient(final Scheme scheme, final String host, final Integer port, final String serverContext, String username, String password) {
		if(username == null || password == null) throw new IllegalArgumentException("Credentials may not be null!");
		creds = new UsernamePasswordCredentials(username, password);
		this.initialize(scheme, host, port, serverContext, creds);
	}

	/**
	 * This constructor is only applicable for http connection!
	 * 
	 * @param url
	 * @throws ProtocolException
	 */
	public AbstractClient(URL url) throws ProtocolException {
		final String host = url.getHost();
		
		final String scheme = url.getProtocol();
		
		if(!scheme.equals("http") && !scheme.equals("https")){
			throw new ProtocolException("Constructor not applicable for protocol: " + scheme);
		}
		
		// -1 OR 80 OR 443 => ignore
//		final int port = url.getPort();
			
		final String path = url.getPath();
		final String serverContext = path.substring(0, path.lastIndexOf('/'));
		this.initialize(Scheme.https, host, null, serverContext, null);
	}

	protected void initialize(final Scheme scheme, final String host, final Integer port, final String serverContext, Credentials creds) {
		uriBuilder = new FimgStoreUriBuilder(scheme.toString(), host, port, serverContext);
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		builder = HttpClients.custom().setConnectionManager(cm);
		builder.setUserAgent(userAgent);

		context = HttpClientContext.create();
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		this.serverContext = serverContext;
		this.creds = creds;
		
		/* not possible with CloseableHttpClient… check alternatives
		if(Constants.getBool("doRetry")){
			final int nrOfRetries = Constants.getInt("nrOfRetries"); // e.g. 3
			DefaultHttpRequestRetryHandler retryHandler = 
					new DefaultHttpRequestRetryHandler(nrOfRetries, true);
			httpClient.setHttpRequestRetryHandler(retryHandler);
		}
		*/
		
		if (this.scheme.equals(Scheme.https) && creds != null) {
			scope = new AuthScope(host, 443);
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(scope, creds);
			context.setCredentialsProvider(credsProvider);		
		}
	}

//	protected void finalize() {
//		if (httpClient != null) {
//			try {
//				httpClient.close();
//			} catch (IOException e) {
//				// ignore this
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * Perform HTTP GET and check status code in response. Response is returned and content handled outside.
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 *             if status code is not 200
	 */
	protected CloseableHttpResponse get(URI uri) throws IOException {
		HttpGet httpget = new HttpGet(uri);
		CloseableHttpClient httpClient = builder.build();
		CloseableHttpResponse response = httpClient.execute(httpget, context);
		//DO NOT CLOSE or connection pool will shut down (httpClient 4.4)
//		httpClient.close();
		if (response.getStatusLine().getStatusCode() >= 300) {
			final String statusLine = response.getStatusLine().toString();
			logger.debug("Error while getting " + uri.toString() + "! "
					+ statusLine);
		}
		
		return response;
	}
	
	/**
	 * Perform HTTPS GET (with authentication) and check status code in response. Response is returned and content handled outside.
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 *             if status code is not 200
	 * @throws AuthenticationException 
	 */
	protected CloseableHttpResponse getAndAuthenticate(URI uri) throws IOException, AuthenticationException {
		if(creds == null){ 
			throw new IllegalArgumentException("No user credentials are known!");
		}
		
		HttpGet httpget = new HttpGet(uri);
		
		if(this.scheme.equals(Scheme.https)){
			BasicScheme authScheme = new BasicScheme();
			//authenticate. Header is added automatically by this call

			Header authHeader = authScheme.authenticate(creds, httpget, context);
			
//			logger.debug("AuthHeader " + authHeader.getName() + ": " + authHeader.getValue());
			httpget.setHeader(authHeader);
//			for(Header h : httpget.getAllHeaders()){
//				logger.debug("Header " + h.getName() + ": " + h.getValue());
//			}
			
		}
		CloseableHttpClient httpClient = builder.build();
		CloseableHttpResponse response = httpClient.execute(httpget, context);
		logger.debug("Request executed!");
		//DO NOT CLOSE or connection pool will shut down (httpClient 4.4)
//		httpClient.close();
		if (response.getStatusLine().getStatusCode() >= 300) {
			final String statusLine = response.getStatusLine().toString();
			logger.debug("Error on HTTP GET " + uri.toString() + "! "
					+ statusLine);
		}
		return response;
	}

	/**
	 * post an entity to the fimgstore. 
	 * 
	 * @param entity
	 * @param responseHandler the Handler object that checks status codes and produces the result String
	 * @return
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	protected String post(HttpEntity entity, ResponseHandler<String> responseHandler) throws IOException, AuthenticationException {
		if(creds == null){ 
			throw new IllegalArgumentException("No user credentials are known!");
		}
		URI uri = uriBuilder.getPostUri();

		HttpPost httpPost = new HttpPost(uri);
		
		// send and get response:
		httpPost.setEntity(entity);
		
		logger.debug("POST: " + uri.toString());
//		logger.debug("Using scheme: " + this.scheme.toString());
		logger.debug("FimgStore user: " + creds.getUserPrincipal());
		
		if(this.scheme.equals(Scheme.https)){
			BasicScheme authScheme = new BasicScheme();
			//authenticate. Header is (or should be! FIXME) added automatically by this call
			Header authHeader = authScheme.authenticate(creds, httpPost, context);
			
//			logger.debug("AuthHeader " + authHeader.getName() + ": " + authHeader.getValue());
			httpPost.setHeader(authHeader);
//			for(Header h : httpPost.getAllHeaders()){
//				logger.debug("Header " + h.getName() + ": " + h.getValue());
//			}
			
		}
		CloseableHttpClient httpClient = builder.build();
		final String result = httpClient.execute(httpPost, responseHandler, context);
		logger.debug("Upload done: " + result);
		//DO NOT CLOSE or connection pool will shut down (httpClient 4.4)
//		httpClient.close();
		return result;
	}

	/**
	 * Causes the client to be reinitialized with the specified scheme and old host/user/pw.
	 *  
	 * @param scheme
	 */
	public void setScheme(Scheme scheme) {
		// reinitialize the client
		this.initialize(scheme, host, port, serverContext, creds);
	}

	/**
	 * Get info on the used scheme
	 * 
	 * @return
	 */
	public String getScheme() {
		return scheme.toString();
	}

	public String getServerContext() {
		return serverContext;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public enum Scheme {
		http, https;
	}
	
	public FimgStoreUriBuilder getUriBuilder(){
		return uriBuilder;
	}
}
