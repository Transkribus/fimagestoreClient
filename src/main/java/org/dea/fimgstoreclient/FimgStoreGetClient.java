package org.dea.fimgstoreclient;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.dea.fimagestore.core.MetadataReader;
import org.dea.fimagestore.core.beans.FileMetadata;
import org.dea.fimagestore.core.beans.ImageMetadata;
import org.dea.fimagestore.core.client.IFImagestoreConfig;
import org.dea.fimagestore.core.util.FilekeyUtils;
import org.dea.fimagestore.core.util.StreamUtils;
import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.dea.fimgstoreclient.beans.FimgStoreTxt;
import org.dea.fimgstoreclient.beans.FimgStoreXml;
import org.dea.fimgstoreclient.beans.ImgType;
import org.dea.fimgstoreclient.utils.OtherUtils;
import org.xml.sax.SAXException;

/**
 * Fimagestore Client for Java-side access to files.
 * 
 * @author philip
 * 
 */
public class FimgStoreGetClient extends AbstractHttpClient implements IFimgStoreGetClient {

	public FimgStoreGetClient(IFImagestoreConfig config) {
		super(config.getHostName(), config.getPort(), config.getContext());
	}
	
	public FimgStoreGetClient(String host, String serverContext) {
		super(host, serverContext);
	}

	public FimgStoreGetClient(Scheme scheme, String host, Integer port, String serverContext) {
		super(scheme, host, port, serverContext);
	}
	
	public FimgStoreGetClient(String host, Integer port, String serverContext) {
		super(host, port, serverContext);
	}

	public FimgStoreGetClient(URL url) throws ProtocolException {
		super(url);
	}

	/**
	 * Gets an Image from Fimagestore and returns a
	 * {@link org.dea.fimgstoreclient.beans.FimgStoreImg} containing orig. filename,
	 * the file data as byte[] and the download time
	 * 
	 * @param imgKey
	 *            the key to the image to be retrieved
	 * @param uri
	 *            the complete URI to the object to be retrieved
	 * @return a FimgStoreImg object containing all data from the response
	 * @throws IOException
	 *             of network error occurs
	 */
	private FimgStoreImg getImg(final String imgKey, final URI uri) throws IOException {
		HttpEntity entity = null;
		ByteArrayOutputStream data = null;
		try (CloseableHttpResponse response = get(uri);) {
			
			final String fileName = getAttachmInfo(response);
	
			entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No data was sent by the server.");
			}
			data = StreamUtils.writeStreamToByteArr(entity.getContent());
			return new FimgStoreImg(imgKey, fileName, data.toByteArray(), uri);
		} finally {
			if(data != null) {
				data.close();
			}
			EntityUtils.consume(entity);
		}
	}
	
	public byte[] getData(final String key) throws IOException {
		final URI uri = getUriBuilder().getFileUri(key);
		return getData(uri);
	}

	public byte[] getData(final URI uri) throws IOException {
		HttpEntity entity = null;
		ByteArrayOutputStream data = null;
		try (CloseableHttpResponse response = get(uri);) {
			entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No data was sent by the server.");
			}
			data = StreamUtils.writeStreamToByteArr(entity.getContent());
			return data.toByteArray();
		} finally {
			if(data != null) {
				data.close();
			}
			EntityUtils.consume(entity);
		}
	}

	/**
	 * Gets an Image from Fimagestore and returns a
	 * {@link org.dea.fimgstoreclient.beans.FimgStoreImg} containing orig. filename,
	 * the file data as byte[] and the download time
	 * 
	 * @param imgKey
	 *            the key to the image to be retrieved
	 * @return a FimgStoreImg object containing all data from the response
	 * @throws IOException
	 *             of network error occurs
	 */
	public FimgStoreImg getImg(final String imgKey) throws IOException {
		// validate params and build Uri
		final URI uri = getUriBuilder().getFileUri(imgKey);
		return getImg(imgKey, uri);
	}

	/**
	 * If a specified fileType is not available yet (since the background convert
	 * thread has not finished yet!), a corresponding error message will be
	 * returned! If a specified fileType is not available for the given key, a
	 * filenotfoundexception will be returned!
	 * 
	 * @param imgKey
	 *            the key to the image to be retrieved
	 * @param type
	 *            {@link org.dea.fimgstoreclient.beans.ImgType} enum
	 * @return a {@link org.dea.fimgstoreclient.beans.FimgStoreImg} object
	 *         containing all data from the response
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if a parameter is bad
	 */
	public FimgStoreImg getImg(final String imgKey, final ImgType type) throws IOException {

		final URI uri = getUriBuilder().getImgUri(imgKey, type);
		FimgStoreImg img = getImg(imgKey, uri);

		img.setImgType(type);

		return img;
	}

	@SafeVarargs
	public final FimgStoreImg getImgBlackened(String imgKey, List<Point>... polygonPtsList) throws IOException {
		URI uri = getUriBuilder().getImgBlackenedUri(imgKey, polygonPtsList);
		return getImg(imgKey, uri);
	}

	/**
	 * get a percentage scaled version of the image
	 * 
	 * @param imgKey
	 *            the key of the image
	 * @param scalePerc
	 *            = percentageOfScaling
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreImg}
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if any param is in a bad format
	 */
	public FimgStoreImg getImgPercScaled(final String imgKey, final int scalePerc) throws IOException {

		final URI uri = getUriBuilder().getImgPercScaledUri(imgKey, scalePerc);

		return getImg(imgKey, uri);
	}

	/**
	 * get a version of the image that is scaled to a given width/height
	 * 
	 * @param imgKey
	 *            the key of the image
	 * @param xPixels
	 *            the width in pixels
	 * @param yPixels
	 *            the height in pixels
	 * @param preserveAspect
	 *            if true, aspect is preserved and the height (yPixels) is
	 *            overwritten
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreImg}
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if any param is in a bad format
	 */
	public FimgStoreImg getImgXyScaled(final String imgKey, final int xPixels, final int yPixels,
			boolean preserveAspect) throws IOException {

		final URI uri = getUriBuilder().getImgXyScaledUri(imgKey, xPixels, yPixels, preserveAspect);
		return getImg(imgKey, uri);
	}

	public FimgStoreImg getImgCropped(final String imgKey, final int posX, final int posY, final int width,
			final int height) throws IOException {

		final URI uri = getUriBuilder().getImgCroppedUri(imgKey, posX, posY, width, height);
		return getImg(imgKey, uri);
	}

	/**
	 * - any option string for the GraphicsMagick (or ImageMagick) convert
	 * 
	 * command can be used (cf http://www.graphicsmagick.org/convert.html) by
	 * specifying the parameters convertOpts and convertExt: convertOpts ...
	 * specifies the option string for the convert command convertExt ... specifies
	 * the extension of the output file (without the dot!), default = jpg
	 * 
	 * Examples: for rotation about 35 degress and conversion to png:<br>
	 * <code>convertOpts=-rotate 35 convertExt=png</code><br>
	 * <code>http://localhost:8880/imagestore/GetImage?id=DWWAGAYXTSHYTZVPLTYJSKBF&amp;convertOpts=-rotate+35&amp;convertExt=png</code>
	 * note that the above url is encoded into UTF-8 format!
	 * 
	 * 
	 * @param imgKey
	 *            key of the file to retrieve
	 * @param convertOps
	 *            convert options string
	 * @param convertExt
	 *            extension of the file to be returned, e.g. jpg
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreImg}
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if any param is in a bad format
	 */
	public FimgStoreImg getImgConv(final String imgKey, final String convertOps, final String convertExt)
			throws IOException {

		final URI uri = getUriBuilder().getImgConvUri(imgKey, convertOps, convertExt);

		return getImg(imgKey, uri);
	}

	/**
	 * Get the metadata for an img
	 * 
	 * @param key
	 *            the file key
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreFileMd}
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if any param is in a bad format
	 */
	public FileMetadata getFileMd(final String key) throws IOException {
		final URI uri = getUriBuilder().getImgMdUri(key);
		HttpEntity entity = null;
		try (CloseableHttpResponse response = get(uri);) {
			// just call this to validate header
			getAttachmInfo(response);
	
			entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No data was sent by the server.");
			}
			InputStream is = entity.getContent();
			
			FileMetadata md = MetadataReader.readFileMetadata(is, FimgStoreConstants.DEFAULT_CHARSET);
			md.setUri(uri);
			return md;
		} finally {
			EntityUtils.consume(entity);
		}
	}
	
	public FileMetadata getFileMd(URL url) throws IOException {
		if(url == null) {
			throw new IllegalArgumentException("URL is null.");
		}
		if(!this.getHost().equals(url.getHost())) {
			throw new IllegalArgumentException("Hostname in URL does not match the configuration of this client.");
		}
		final String key;
		try {
			key = FilekeyUtils.extractKey(url);
		} catch (URISyntaxException e) {
			throw new IOException("Could not extract key from url: " + url.toString(), e);
		}

		return getFileMd(key);
	}
	
	public ImageMetadata getImgMd(URL url) throws IOException {
		FileMetadata md = getFileMd(url);

		if (!(md instanceof ImageMetadata)) {
			throw new IOException("File with key " + md.getKey() + " is not an image!");
		}
		return (ImageMetadata) md;
	}

	/**
	 * Gets an Xml File from Fimagestore and returns a {@link FimgStoreXml}
	 * containing orig. filename, the file content as Document and the download time
	 * 
	 * @param fileKey
	 *            the file key
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreXml}
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if any param is in a bad format
	 */
	public FimgStoreXml getXml(final String fileKey) throws IOException {
		// validate params and build Uri
		final URI uri = getUriBuilder().getFileUri(fileKey);
		HttpEntity entity = null;
		try (CloseableHttpResponse response = get(uri);) {
			final String fileName = getAttachmInfo(response);
			entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No data was sent by the server.");
			}
			byte[] data = StreamUtils.writeStreamToByteArr(entity.getContent()).toByteArray();
			return new FimgStoreXml(fileKey, fileName, data, uri);
		} catch (ParserConfigurationException | IllegalStateException | SAXException e) {
			throw new IOException("Error while loading XML Document " + fileKey + ": " + e.getMessage());
		} finally {
			EntityUtils.consume(entity);
		}
	}

	/**
	 * Get a txt file from the fimagestore
	 * 
	 * @param fileKey
	 *            the file key
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreTxt}
	 * @throws IOException
	 *             if network error occurs
	 */
	public FimgStoreTxt getTxt(String fileKey) throws IOException {
		// validate params and build Uri
		final URI uri = getUriBuilder().getFileUri(fileKey);
		HttpEntity entity = null;
		try (CloseableHttpResponse response = get(uri);) {
			final String fileName = getAttachmInfo(response);
			entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No data was sent by the server.");
			}
			String text = StreamUtils.writeStreamToString(entity.getContent(), "UTF-8");
			return new FimgStoreTxt(fileKey, fileName, text, uri);
		} finally {
			EntityUtils.consume(entity);
		}
	}

	/*
	 * TODO - Image search: URL = https://my.host.com/imagestore/SearchImages
	 * Searches for images using the embedded database and returns all matching file
	 * infos. Parameters: fnMask, isPartOfMask specifying the search masks for the
	 * filename and is_part_of fields. Currently you can use * as a wildcard Note
	 * that the return data is encoded as a JSON array!
	 * 
	 * - Image statistics: URL = https://my.host.com/imagestore/GetStats?id= Returns
	 * retrieval statistics for the file with the given id (last access date, last
	 * user, last ip, number of retrievals)
	 */
	
	public File saveImg(final String key, final ImgType type, final String path) throws IOException {
		return saveImg(key, type, path, null);
	}
	
	public File saveImg(final String key, final ImgType type, final String path, final String filename) throws IOException {
		final URI uri = getUriBuilder().getImgUri(key, type);
		return saveFile(uri, path, filename);
	}

	public File saveFile(final String key, final String path) throws IOException {
		final URI uri = getUriBuilder().getFileUri(key);
		return saveFile(uri, path);
	}

	public File saveFile(final String key, final String path, final String filename) throws IOException {
		final URI uri = getUriBuilder().getFileUri(key);
		return saveFile(uri, path, filename);
	}

	public File saveFile(final URI uri, String path) throws IllegalArgumentException, IOException {
		return saveFile(uri, path, null);
	}

	public File saveFile(final URI uri, String path, String fileName) throws IllegalArgumentException, IOException {
		File file;
		HttpEntity entity = null;
		try (CloseableHttpResponse response = get(uri);) {
			String origFileName = getAttachmInfo(response);
			entity = response.getEntity();
			if (entity == null) {
				throw new IOException("No data was sent by the server.");
			}

			if (!path.endsWith(File.separator)) {
				path += File.separator;
			}
			// build the filePath
			String filePath = path + (fileName == null || fileName.isEmpty() ? origFileName : fileName);
			file = StreamUtils.writeStreamToFile(entity.getContent(), filePath);
		} finally {
			EntityUtils.consume(entity);
		}
		return file;
	}

	public File saveFile(byte[] data, String path, String fileName) throws IllegalArgumentException, IOException {
		if (path == null)
			path = ".";
		if (fileName == null || fileName.isEmpty())
			throw new IllegalArgumentException("invalid fileName: " + fileName);

		File outFile = new File(path + "/" + fileName);

		FileUtils.writeByteArrayToFile(outFile, data);
		return outFile;
	}

	/*******************
	 * Utility methods *
	 *******************/

	/**
	 * Extract and validate Content-Disposition header. Rebuild original attachment
	 * file name.<br>
	 * The caller has to close the Response if an exception is thrown!
	 * 
	 * @param response
	 *            the response to parse
	 * @return original file name
	 * @throws FileNotFoundException
	 *             if no file is contained in response or the header is damaged
	 */
	private String getAttachmInfo(HttpResponse response) throws FileNotFoundException {
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
