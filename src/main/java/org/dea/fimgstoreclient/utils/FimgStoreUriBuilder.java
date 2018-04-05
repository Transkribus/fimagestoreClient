package org.dea.fimgstoreclient.utils;

import java.awt.Point;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.dea.fimgstoreclient.FimgStoreConstants;
import org.dea.fimgstoreclient.beans.ImgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FimgStoreUriBuilder {
	
	private final static Pattern fileKeyPattern = FimgStoreConstants.getPattern("fileKeyPattern");
	private final static String defaultHost = FimgStoreConstants.getString("host");
	private final static String defaultServerContext = FimgStoreConstants.getString("context");
	private static String getActionPath;
	private static String putActionPath;
	private static String delActionPath;
	private static String createActionPath;
	
	private String serverContext;
	private URIBuilder uriBuilder;
	
	private final static Logger logger = LoggerFactory.getLogger(FimgStoreUriBuilder.class);
	
	/**
	 * Empty Constructor uses HTTPS and hostname, context and paths from fimgstoreclient.properties
	 */
	public FimgStoreUriBuilder(){
		this("https", defaultHost, null, defaultServerContext);
	}
	
	public FimgStoreUriBuilder(final String scheme){
		this(scheme, defaultHost, null, defaultServerContext);
	}
	
	public FimgStoreUriBuilder(final String scheme, final String host, final Integer port, final String context){
		this.serverContext = (context.startsWith("/")) ? context : "/" + context;
		getActionPath = this.serverContext + FimgStoreConstants.GET_ACTION_PATH;
		putActionPath = this.serverContext + FimgStoreConstants.PUT_ACTION_PATH;
		delActionPath = this.serverContext + FimgStoreConstants.DEL_ACTION_PATH;
		createActionPath = this.serverContext + FimgStoreConstants.CREATE_ACTION_PATH;
		this.uriBuilder = new URIBuilder().setScheme(scheme).setHost(host);
		if(port != null && port != 80 && port != 443){
			this.uriBuilder.setPort(port);
		}
	}
	

	public URI getFileUri(final String fileKey) throws IllegalArgumentException {
		return buildURI(fileKey, (NameValuePair[]) null);
	}

	/**
	 * If a specified fileType is not available yet (since the background
	 * convert thread has not finished yet!), a corresponding error message will
	 * be returned! If a specified fileType is not available for the given key,
	 * a filenotfoundexception will be returned!
	 * 
	 * @param imgKey key of the file
	 * @param type {@link ImgType}
	 * @return URI of the specified file type
	 * @throws IllegalArgumentException if a parameter is bad
	 */
	public URI getImgUri(final String imgKey, final ImgType type)
			throws IllegalArgumentException {

		NameValuePair param = new BasicNameValuePair(FimgStoreConstants.FILE_TYPE_PARAM, type.toString());

		return buildURI(imgKey, param);
	}
	
	public URI getCreateUri(URI baseGetUri, String isPartOf, Integer timeout, String replaceKey) throws IllegalArgumentException {				
		uriBuilder.clearParameters().setPath(createActionPath).setParameters(new URIBuilder(baseGetUri).getQueryParams());

		if (isPartOf != null && !isPartOf.isEmpty())
			uriBuilder.setParameter(FimgStoreConstants.PART_OF_VAR_NAME, isPartOf);
			
		if (timeout!=null && timeout>0)
			uriBuilder.setParameter(FimgStoreConstants.TIMEOUT_PARAM, ""+timeout);
		
		if (replaceKey!=null && !replaceKey.isEmpty())
			uriBuilder.setParameter(FimgStoreConstants.REPLACE_ID_VAR_NAME, replaceKey);
		
		URI uri;
		try {
			uri = uriBuilder.build();
		} catch(URISyntaxException e){
			throw new IllegalArgumentException("Fimagestore create URL could not be build: "+e.getMessage(), e);
		}		
		
		return uri;
		
	}
		
	/**
	 * Returns the image with the specified key, drawing a black filled 
	 * polygon over it as specified by polygonPts
	 * @param imgKey key of the file
	 * @param polygonPtsList points of polygons to be blackened
	 * @return the URI of the blackened image
	 * @throws IllegalArgumentException if a parameter is bad
	 */
	@SafeVarargs
	public final URI getImgBlackenedUri(final String imgKey, List<Point> ...polygonPtsList)
			throws IllegalArgumentException {
		
		String convertOpts =  "-fill black";
		
		for (List<Point> polygonPts : polygonPtsList) {
			if (polygonPts== null || polygonPts.size() < 3)
				throw new IllegalArgumentException("Polygon is null or has less than 3 points!");
		
			String ptsStr = OtherUtils.pointsToString(polygonPts);
			convertOpts += " -draw 'polygon "+ptsStr+"'";
//			convertOpts += " -draw 'polygon whatever'"; // TEST: corrupt convert opts
		}
		
		logger.debug("convertOpts = "+convertOpts);

		NameValuePair param = new BasicNameValuePair(FimgStoreConstants.CONVERT_OPTS_PARAM, convertOpts);
		return buildURI(imgKey, param);
	}

	/**
	 * scalePerc=percentageOfScaling
	 * 
	 * @param imgKey key of original file
	 * @param scalePerc percentage to scale
	 * @return URI to scaled image
	 * @throws IllegalArgumentException if a parameter is bad
	 */
	public URI getImgPercScaledUri(final String imgKey, final int scalePerc)
			throws IllegalArgumentException {

		// scalePerc = percent of imagesize, e.g. 30 => 30%
		if (scalePerc < 1) {
			throw new IllegalArgumentException("Scale percentage is zero or negative.");
		}

		NameValuePair param = new BasicNameValuePair(FimgStoreConstants.SCALE_PERC_PARAM, "" + scalePerc);
		return buildURI(imgKey, param);
	}

	public URI getImgXyScaledUri(final String imgKey, final int xPixels, final int yPixels,
			boolean preserveAspect) throws IllegalArgumentException {
		// scaleXY=pixelsX x pixelsY[!] (the ! means 'do NOT respect aspect
		// ratio',
		// cf GraphicsMagick documentation!)
		String presAspMarker = "";
		if (!preserveAspect) {
			presAspMarker = "!";
		}

		final String scaleXY = xPixels + "x" + yPixels + presAspMarker;

		NameValuePair param = new BasicNameValuePair(FimgStoreConstants.SCALE_X_Y_PARAM, scaleXY);
		return buildURI(imgKey, param);
	}

	/**
	 * 
	 * crop=posX x posY x width x height
	 * 
	 * @param imgKey key of original image
	 * @param posX x offset 
	 * @param posY y offset
	 * @param width width of snippet
	 * @param height height of snippet
	 * @return URI to cropped image
	 * @throws IllegalArgumentException if a parameter is bad
	 */
	public URI getImgCroppedUri(final String imgKey, final int posX, final int posY,
			final int width, final int height) throws IllegalArgumentException {
		final String X = FimgStoreConstants.MULT_LITERAL;
		final String crop = posX + X + posY + X + width + X + height;
		NameValuePair param = new BasicNameValuePair(FimgStoreConstants.CROP_PARAM, crop);
		return buildURI(imgKey, param);
	}

	/**
	 * - any option string for the GraphicsMagick (or ImageMagick) convert
	 * 
	 * command can be used (cf http://www.graphicsmagick.org/convert.html) by
	 * specifying the parameters convertOpts and convertExt: convertOpts ...
	 * specifies the option string for the convert command convertExt ...
	 * specifies the extension of the output file (without the dot!), default =
	 * jpg
	 * 
	 * Examples: for rotation about 35 degress and conversion to png:<br>
	 * <code>convertOpts=-rotate 35 convertExt=png</code><br>
	 * <code>http://localhost:8880/imagestore/GetImage?id=DWWAGAYXTSHYTZVPLTYJSKBF&amp;convertOpts=-rotate+35&amp;convertExt=png</code> note
	 * that the above url is encoded into UTF-8 format!
	 * 
	 * Use the convenience functions in util.GetImageClient to create valid
	 * retrieval urls!)
	 * 
	 * @param imgKey original file's key
	 * @param convertOps convert option string
	 * @param convertExt extension of the result file
	 * @return URI to the converted image
	 * @throws IllegalArgumentException if a parameter is bad
	 */
	public URI getImgConvUri(final String imgKey, final String convertOps,
			final String convertExt) throws IllegalArgumentException {
		NameValuePair ops = new BasicNameValuePair(FimgStoreConstants.CONVERT_OPTS_PARAM, convertOps);
		NameValuePair ext = new BasicNameValuePair(FimgStoreConstants.CONVERT_EXT_PARAM, convertExt);

		return buildURI(imgKey, ops, ext);
	}

	/**
	 * Get the metadata for an img
	 * 
	 * @param imgKey key of the file
	 * @return URI to the metadata of the file
	 * @throws IllegalArgumentException if imgKey is in bad format
	 */
	public URI getImgMdUri(final String imgKey) throws IllegalArgumentException {
		NameValuePair param = new BasicNameValuePair(FimgStoreConstants.FILE_TYPE_PARAM, FimgStoreConstants.MD_FILETYPE_VALUE);
		return buildURI(imgKey, param);
	}

	/*******************
	 * Utility methods *
	 *******************/

	/**
	 * Check the GET parameters and build the fimagestore URL
	 * 
	 * @param fileKey key of the file
	 * @param params params to use in the URI to build
	 * @return a URI with all parameters
	 * @throws IllegalArgumentException if a parameter is bad
	 */
	private URI buildURI(String fileKey, NameValuePair... params) throws IllegalArgumentException {
		List<NameValuePair> paramsList;
		URI uri = null;

		// validate parameters
		if (fileKey == null) {
			throw new IllegalArgumentException("The fileKey is null.");
		} else if (!fileKeyPattern.matcher(fileKey).matches()) {
			throw new IllegalArgumentException("The fileKey's format is currupt: " + fileKey);
		}

		if (params == null) { // if no params just use the fileKey
			paramsList = new ArrayList<NameValuePair>(1);
		} else if (params.length == 1) {
			paramsList = new ArrayList<NameValuePair>(1);
			paramsList.add(params[0]);
		} else {
			paramsList = Arrays.asList(params);
		}
		paramsList.add(new BasicNameValuePair(FimgStoreConstants.ID_PARAM, fileKey));

		// reset parameters on UriBuilder Object
		uriBuilder.clearParameters();
		// and set the new ones
		uriBuilder.setPath(getActionPath).addParameters(paramsList);

		// build the URI
		try {
			uri = uriBuilder.build();
		} catch (URISyntaxException use) {
			throw new IllegalArgumentException("Fimagestore URL could not be build for filekey "
					+ fileKey + " and parameters " + params.toString());
		}

		return uri;
	}


	public URI getPostUri(){
		URI uri = null; 
		uriBuilder.clearParameters();
		uriBuilder.setPath(putActionPath);
		try{
			uri = uriBuilder.build();
		} catch(URISyntaxException e){
			//getCoffee()
			e.printStackTrace();
		}
		
		return uri;
	}
		
	public URI getDeleteUri(String fileKey){
		URI uri = null;

		// validate parameters
		if (fileKey == null) {
			throw new IllegalArgumentException("The fileKey is null.");
		} else if (!fileKeyPattern.matcher(fileKey).matches()) {
			throw new IllegalArgumentException("The fileKey's format is currupt: " + fileKey);
		}
		
		uriBuilder.clearParameters();
		uriBuilder.setPath(delActionPath).addParameter(FimgStoreConstants.ID_PARAM, fileKey);
		try{
			uri = uriBuilder.build();
		} catch(URISyntaxException e){
			//der Kaffee schmeckt heut irgendwie scheiße
			e.printStackTrace();
		}
		
		return uri;
	}

	public URI getBaseUri() throws URISyntaxException {
		uriBuilder.clearParameters();
		uriBuilder.setPath(serverContext);
		return uriBuilder.build();
	}
}
