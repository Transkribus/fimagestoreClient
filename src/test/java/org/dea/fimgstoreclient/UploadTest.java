package org.dea.fimgstoreclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.auth.AuthenticationException;
import org.dea.fimagestore.core.util.SebisStopWatch.SSW;
import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upload connectivity test to be run on servers (to test network config such as proxy settings etc.)<br><br>
 * In order to make this run, place a file "fimagestore_creds.properties" with values for "username" and "pw" into the root folder of the FimagestoreClient project.
 * 
 * <ul>
 * <li>The credentials have to be allowed on the target fimagestore for uploading files.</li>
 * <li>The IP address of the machine this is run on must be included in the write_ip list of the fimagestore too.</li>
 * <li>FImagestore addresses that will be tested are defined in the {@link #HOSTS} field.</li>
 * <li>If the properties file is not found the test will be skipped!</li>
 * </ul>
 */
public class UploadTest {
	private static final Logger logger = LoggerFactory.getLogger(UploadTest.class);
	
	private static final String[][] HOSTS = new String[][] {
		{ "files-test.transkribus.eu", "/" },
		{ "read04.uibk.ac.at", "/" }
	};
	
	private FimgStorePostClient poster;
	private FimgStoreDelClient deller;
	
	private static String username;
	private static String pw;
	
	
	@BeforeClass
	public static void readCreds() throws IOException {
		Properties props = new Properties();
		File propFile = new File("fimagestore_creds.properties");
		logger.debug(propFile.getAbsolutePath() + " exists = " + propFile.isFile());
		Assume.assumeTrue("Skipping as file does not exist: " + propFile.getAbsolutePath(), propFile.isFile());
		try (InputStream is = new FileInputStream(propFile)) {
			props.load(is);
			username = props.getProperty("username");
			pw = props.getProperty("pw");
		}
	}
	
	public void init(String hostname, String context) {
		poster = new FimgStorePostClient(Scheme.https, hostname, context, username, pw);
		deller = new FimgStoreDelClient(Scheme.https, hostname, context, username, pw);
	}
	
	/**
	 * @see javadoc of {@link UploadTest}
	 */
	@Test
	public void testPost() {
		SSW sw = new SSW();
			
		final File f = new File("/mnt/dea_scratch/TRP/Bentham_box_002/002_080_001.jpg");
		
		for(String[] h : HOSTS) {
			sw.start();
			try {
				postAndDelete(h[0], h[1], f);
				sw.stop("OK: " + h[0] + " post and delete", logger);
			} catch (Exception e) {
				sw.stop();
				logger.error("FAIL: " + h[0] + " " + e.getMessage());
			}
		}
	}
	
	private void postAndDelete(String hostname, String context, File testFile) throws Exception {
		init(hostname, context);
		String key = null;
		Exception ex = null;
		try {
			key = poster.postFile(testFile, "http-client test", 0);
		} catch (AuthenticationException | IOException e) {
			ex = e;
		} finally {
			if(key != null) {
				try {
					deller.deleteFile(key, 0);
				} catch (IOException e) {
					logger.error("Deletion failed.", e);
				}
			}
		}
		if(ex != null) {
			throw ex;
		}
	}
}
