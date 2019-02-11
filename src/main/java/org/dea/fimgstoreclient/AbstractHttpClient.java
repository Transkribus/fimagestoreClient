package org.dea.fimgstoreclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.dea.fimgstoreclient.responsehandler.AFimgStoreResponseWithAttachmentHandler;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpClient implements IFimgStoreClientBase, AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(AbstractHttpClient.class);
	protected final static String userAgent = "DEA Fimagestore Client 0.3";
	
	private final HttpClientBuilder builder;
	protected final HttpClientContext context;
	private CloseableHttpClient httpClient = null;
	
	protected String serverContext;
	protected String host;
	protected int port;
	protected Scheme scheme;
	
	/**
	 * If set to false, each request will be executed using a new CloseableHttpClient instance as it was since v0.1, if true one instance will be reused.<br> 
	 * Current docs for apache http client say that reuse is better. So this is here for doing a comparison test.
	 */
	private boolean REUSE_HTTP_CLIENT_INSTANCE = false;
	
	protected AbstractHttpClient(final String host, final String serverContext) {
		this(Scheme.https, host, null, serverContext);
	}
	
	protected AbstractHttpClient(final String host, final Integer port, final String serverContext) {
		this(Scheme.https, host, port, serverContext);
	}

	public AbstractHttpClient(URL url) throws ProtocolException {
		this();
		final String scheme = url.getProtocol();
		if(!scheme.equals("http") && !scheme.equals("https")){
			throw new ProtocolException("Constructor not applicable for protocol: " + scheme);
		}
		this.scheme = Scheme.https; //FIXME http not supported in this constructor!
		this.host = url.getHost();
		this.port = url.getPort();
		final String path = url.getPath();
		this.serverContext = path.substring(0, path.lastIndexOf('/'));	
		enablePooling();
	}
	
	protected AbstractHttpClient(final Scheme scheme, final String host, final Integer port, final String serverContext) {
		this();
		if(scheme == null) {
			logger.warn("Null was passed as URI scheme! Defaulting to https.");
			this.scheme = Scheme.https;
		} else {
			this.scheme = scheme;
		}
		this.host = host;
		this.port = port != null ? port : AuthScope.ANY_PORT;
		this.serverContext = serverContext;
		enablePooling();
	}

	private AbstractHttpClient() {
		
		// use system properties, such as proxy settings
		builder = initClientBuilder();
		context = HttpClientContext.create();
		
		
		/* not possible with CloseableHttpClient… check alternatives
		if(Constants.getBool("doRetry")){
			final int nrOfRetries = Constants.getInt("nrOfRetries"); // e.g. 3
			DefaultHttpRequestRetryHandler retryHandler = 
					new DefaultHttpRequestRetryHandler(nrOfRetries, true);
			httpClient.setHttpRequestRetryHandler(retryHandler);
		}
		*/
	}

	private HttpClientBuilder initClientBuilder() {
		HttpClientBuilder builder = HttpClients.custom().useSystemProperties();
		builder.setUserAgent(userAgent);	
		return builder;
	}

	/**
	 * Perform HTTP GET and check status code in response. Response is returned and content handled outside.
	 * 
	 * @param uri the fimagestore URI to an object
	 * @return the response
	 * @throws IOException
	 *             if status code is not 200
	 */
	protected CloseableHttpResponse get(URI uri) throws IOException {
		HttpGet httpget = new HttpGet(uri);
		CloseableHttpClient httpClient = getHttpClient();
		CloseableHttpResponse response = httpClient.execute(httpget, context);
		//DO NOT CLOSE or connection pool will shut down (httpClient 4.4)
//		httpClient.close();
		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 404) {
			response.close();
			throw new FileNotFoundException("No resource found with URI: " + uri.toString());
		} 
		if (statusCode >= 300) {
			response.close();
			throw new IOException("Error while getting " + uri.toString() + ": " 
					+ response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
		}
		
		return response;
	}
	
	protected <T> T get(URI uri, AFimgStoreResponseWithAttachmentHandler<T> responseHandler) throws IOException {
		HttpGet httpget = new HttpGet(uri);
		CloseableHttpClient httpClient = getHttpClient();
		return httpClient.execute(httpget, responseHandler, context);
	}
	
	/**
	 * Perform HTTP DELETE and check status code in response. Response is returned and content handled outside.
	 * 
	 * @param uri the fimagestore URI to an object
	 * @return the response
	 * @throws IOException
	 *             if status code is not 200
	 */
	protected CloseableHttpResponse delete(URI uri) throws IOException {
		HttpDelete httpDel = new HttpDelete(uri);
		CloseableHttpClient httpClient = getHttpClient();
		CloseableHttpResponse response = httpClient.execute(httpDel, context);
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
	 * post an entity to the fimagestore. 
	 * 
	 * @param entity the object entity to be posted
	 * @param responseHandler the Handler object that checks status codes and produces the result String
	 * @return the file key to the newly created file
	 * @throws IOException if any error happens on the network
	 * @throws AuthenticationException if authentication fails
	 */
	protected String post(HttpEntity entity, ResponseHandler<String> responseHandler) throws IOException, AuthenticationException {
		URI uri = getUriBuilder().getBasePutUri();

		HttpPost httpPost = new HttpPost(uri);
		
		logger.debug("POST: " + uri.toString());
		
		// send and get response:
		httpPost.setEntity(entity);
		
		CloseableHttpClient httpClient = getHttpClient();
		final String result = httpClient.execute(httpPost, responseHandler, context);
		logger.debug("Upload done: " + result);
		//DO NOT CLOSE or connection pool will shut down (httpClient 4.4)
//		httpClient.close();
		return result;
	}
	
	
	/**
	 * Get info on the used scheme
	 * 
	 * @return either http or https
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

	public int getPort() {
		return port;
	}
	
	public enum Scheme {
		http, https;
	}
	
	public FimgStoreUriBuilder getUriBuilder() {
		return new FimgStoreUriBuilder(scheme.toString(), host, port, serverContext);
	}

	public void setProxy(String host, int port){
		setProxy(host, port, null, null, null, null);
	}
	
	public void setProxy(String host, int port, String user, String password, String localMachineName, String domainName){
		if(host == null || host.isEmpty() || port == 0){
			logger.error("Setting no proxy! Host or port is empty!");
			return;
		}
		HttpHost httpHost = new HttpHost(host, port);
		builder.setProxy(httpHost);
		//TODO test with proxy authentication
		if(user != null && !user.isEmpty() && password != null && !password.isEmpty()){
			NTCredentials ntCreds = new NTCredentials(user, password, localMachineName, domainName );
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials( new AuthScope(host,port), ntCreds);
			builder.setDefaultCredentialsProvider(credsProvider);
			builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		}
		if(httpClient != null) {
			httpClient = null;
		}
	}
	
	protected HttpClientConnectionManager buildDefaultPooledConnectionManager() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		// Increase max connections for localhost:80 to 50
		HttpHost httpHost = new HttpHost(host, port);
		cm.setMaxPerRoute(new HttpRoute(httpHost), 50);
		return cm;
	}
	
	public void enablePooling() {
		HttpClientConnectionManager connMgr = buildDefaultPooledConnectionManager();
		this.builder.setConnectionManager(connMgr);
		if(httpClient != null) {
			httpClient = null;
		}
	}

	protected CloseableHttpClient getHttpClient() {
		if(!REUSE_HTTP_CLIENT_INSTANCE) {
			return builder.build();
		}
		if(httpClient == null) {
			httpClient = builder.build();
		}
		return httpClient;
	}
	
	@Override
	public void close() throws IOException {
		if(httpClient != null) {
			httpClient.close();
		}
	}
}
