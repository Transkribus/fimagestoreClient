package org.dea.fimgstoreclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBasicAuthHttpClient extends AbstractHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(AbstractBasicAuthHttpClient.class);
	
	protected AbstractBasicAuthHttpClient(final Scheme scheme, final String host, final Integer port, final String serverContext, String username, String password) {
		super(scheme, host, port, serverContext);
		if(username == null || password == null) throw new IllegalArgumentException("Credentials may not be null!");
		Credentials creds = new UsernamePasswordCredentials(username, password);
		
		int authPort = (port == null ? 443 : port);
		HttpHost targetHost = new HttpHost(host, authPort, scheme == null ? null : scheme.toString());
		AuthScope scope = new AuthScope(host, authPort);

		AuthCache authCache = new BasicAuthCache();
		authCache.put(targetHost, new BasicScheme());
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(scope, creds);
		
		context.setCredentialsProvider(credsProvider);	
		context.setAuthCache(authCache);
	}	
	
	@Override
	protected String post(HttpEntity entity, ResponseHandler<String> responseHandler) throws IOException, AuthenticationException {
		hasCreds(host, port);		
		return super.post(entity, responseHandler);
	}
	
	protected boolean hasCreds(String host, Integer port) {
		AuthScope scope = new AuthScope(host, port);
		Credentials creds = context.getCredentialsProvider().getCredentials(scope);
		logger.debug("FimgStore user: " + (creds == null ? null : creds.getUserPrincipal()));
		return creds != null;
	}
}
