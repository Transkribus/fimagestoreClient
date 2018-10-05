package org.dea.fimgstoreclient;

import java.net.URISyntaxException;

import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FimgStoreUriBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreUriBuilderTest.class);
	
	@Test
	public void testPathNormalization() {
		Assert.assertEquals("/", FimgStoreUriBuilder.normalizeContextPath(null));
		Assert.assertEquals("/", FimgStoreUriBuilder.normalizeContextPath("/"));
		Assert.assertEquals("/test/", FimgStoreUriBuilder.normalizeContextPath("test"));
		Assert.assertEquals("/test/", FimgStoreUriBuilder.normalizeContextPath("test/"));
		Assert.assertEquals("/test/", FimgStoreUriBuilder.normalizeContextPath("/test"));
		Assert.assertEquals("/te/st/", FimgStoreUriBuilder.normalizeContextPath("te/st"));
		Assert.assertEquals("/te/st/", FimgStoreUriBuilder.normalizeContextPath("te////st"));
		Assert.assertEquals("/", FimgStoreUriBuilder.normalizeContextPath("//"));
		Assert.assertEquals("/", FimgStoreUriBuilder.normalizeContextPath("/////////////////"));
	}
	
	@Test
	public void testBaseUris() throws URISyntaxException {
		FimgStoreUriBuilder builder = new FimgStoreUriBuilder(""+Scheme.https, "files-test.transkribus.eu", null, null);
		Assert.assertEquals("https://files-test.transkribus.eu/Get", ""+builder.getBaseGetUri());
		
		builder = new FimgStoreUriBuilder(""+Scheme.https, "files-test.transkribus.eu", -1, null);
		Assert.assertEquals("https://files-test.transkribus.eu/Get", ""+builder.getBaseGetUri());
		
		builder = new FimgStoreUriBuilder(""+Scheme.https, "files-test.transkribus.eu", null, "f");
		Assert.assertEquals("https://files-test.transkribus.eu/f/Get", ""+builder.getBaseGetUri());
		
		builder = new FimgStoreUriBuilder(""+Scheme.https, "files-test.transkribus.eu", null, "/f");
		Assert.assertEquals("https://files-test.transkribus.eu/f/Get", ""+builder.getBaseGetUri());
	}

}
