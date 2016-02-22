package org.dea.fimgstoreclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.dea.fimgstoreclient.beans.FimgStoreFileMd;
import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.dea.fimgstoreclient.beans.FimgStoreTxt;
import org.dea.fimgstoreclient.beans.FimgStoreXml;
import org.dea.fimgstoreclient.beans.ImgType;
import org.dea.fimgstoreclient.utils.FimgStoreMdParser;
import org.dea.fimgstoreclient.utils.StreamUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Fimagestore Client for Java-side access to files.
 * 
 * @author philip
 * 
 */
public class FimgStoreGetClient extends AbstractClient {

	public FimgStoreGetClient(String host, String serverContext) {
		super(host, serverContext);
	}
	
	public FimgStoreGetClient(String host, Integer port, String serverContext) {
		super(host, port, serverContext);
	}

	public FimgStoreGetClient(URL url) throws ProtocolException {
		super(url);
	}

	/**
	 * Gets the entity content (attached file) as InputStream
	 * 
	 * @param fileKey
	 * @param params
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public InputStream getResourceAsStream(final URI uri) throws IOException {
		CloseableHttpResponse response = null;
		InputStream is = null;

		response = get(uri);

		// just call this to validate header
		getAttachmInfo(response);

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			is = entity.getContent();
		}
		
		return is;
	}

	/**
	 * Gets an Image from Fimagestore and returns a {@link FimgstoreFile}
	 * containing orig. filename, the file data as byte[] and the download time
	 * 
	 * @param imgKey
	 * @param params
	 *            Fimgstore parameters
	 * @return FimgstoreFile
	 * @throws IllegalArgumentException
	 *             no
	 * @throws IOException
	 */
	private FimgStoreImg getImg(final String imgKey, final URI uri)
			throws IOException {
		CloseableHttpResponse response = null;
		ByteArrayOutputStream data = null;
		response = get(uri);

		final String fileName = getAttachmInfo(response);

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			data = StreamUtils.writeStreamToByteArr(entity.getContent());
		}
		EntityUtils.consume(entity);
		response.close();
		return new FimgStoreImg(imgKey, fileName, data.toByteArray(), uri);
	}

	public FimgStoreImg getImg(final String imgKey) throws IOException {
		// validate params and build Uri
		final URI uri = uriBuilder.getFileUri(imgKey);
		return getImg(imgKey, uri);
	}

	/**
	 * If a specified fileType is not available yet (since the background
	 * convert thread has not finished yet!), a corresponding error message will
	 * be returned! If a specified fileType is not available for the given key,
	 * a filenotfoundexception will be returned!
	 * 
	 * @param imgKey
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public FimgStoreImg getImg(final String imgKey, final ImgType type)
			throws IOException {

		final URI uri = uriBuilder.getImgUri(imgKey, type);
		FimgStoreImg img = (FimgStoreImg) getImg(imgKey, uri);

		img.setImgType(type);

		return img;
	}

	/**
	 * scalePerc=percentageOfScaling
	 * 
	 * @param imgKey
	 * @param scalePerc
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public FimgStoreImg getImgPercScaled(final String imgKey, final int scalePerc)
			throws IOException {

		final URI uri = uriBuilder.getImgPercScaledUri(imgKey, scalePerc);
		
		return (FimgStoreImg) getImg(imgKey, uri);
	}

	/**
	 * 
	 * 
	 * @param imgKey
	 * @param xPixels
	 * @param yPixels
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public FimgStoreImg getImgXyScaled(final String imgKey, final int xPixels, final int yPixels,
			boolean preserveAspect) throws IOException {
		
		final URI uri = uriBuilder.getImgXyScaledUri(imgKey, xPixels, yPixels, preserveAspect);
		return (FimgStoreImg) getImg(imgKey, uri);
	}

	/**
	 * 
	 * crop=posX x posY x width x height
	 * 
	 * @param imgKey
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public FimgStoreImg getImgCropped(final String imgKey, final int posX, final int posY,
			final int width, final int height) throws IOException {

		final URI uri = uriBuilder.getImgCroppedUri(imgKey, posX, posY, width, height);
		return (FimgStoreImg) getImg(imgKey, uri);
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
	 * Examples: for rotation about 35 degress and conversion to png:
	 * convertOpts=-rotate 35 convertExt=png
	 * http://localhost:8880/imagestore/GetImage
	 * ?id=DWWAGAYXTSHYTZVPLTYJSKBF&convertOpts=-rotate+35&convertExt=png note
	 * that the above url is encoded into UTF-8 format!
	 * 
	 * Use the convenience functions in util.GetImageClient to create valid
	 * retrieval urls!)
	 * 
	 * @param imgKey
	 * @param convertOps
	 * @param convertExt
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public FimgStoreImg getImgConv(final String imgKey, final String convertOps,
			final String convertExt) throws IOException {
		
		final URI uri = uriBuilder.getImgConvUri(imgKey, convertOps, convertExt);

		return (FimgStoreImg) getImg(imgKey, uri);
	}

	/**
	 * Get the metadata for an img
	 * 
	 * @param key
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public FimgStoreFileMd getFileMd(final String key) throws IOException {
		CloseableHttpResponse response = null;
		InputStream is = null;
		
		final URI uri = uriBuilder.getImgMdUri(key);

		response = get(uri);

		// just call this to validate header
		getAttachmInfo(response);

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			is = entity.getContent();
		}

		FimgStoreFileMd md = FimgStoreMdParser.parse(is);
		md.setUri(uri);
		EntityUtils.consume(entity);
		response.close();
		return md;
	}

	/**
	 * Gets an Xml File from Fimagestore and returns a {@link FimgStoreXml}
	 * containing orig. filename, the file content as Document and the download
	 * time
	 * 
	 * @param fileKey
	 * @param params
	 *            Fimgstore parameters
	 * @return FimgstoreFile
	 * @throws IllegalArgumentException
	 *             no
	 * @throws IOException
	 */
	public FimgStoreXml getXml(final String fileKey) throws IOException {
		CloseableHttpResponse response = null;
		Document doc = null;

		// validate params and build Uri
		final URI uri = uriBuilder.getFileUri(fileKey);

		// System.out.println(uri);

		response = get(uri);

		final String fileName = getAttachmInfo(response);

		HttpEntity entity = response.getEntity();
		FimgStoreXml xml = null;
		if (entity != null) {
			try {
				byte[] data = StreamUtils.writeStreamToByteArr(entity.getContent()).toByteArray();
				xml = new FimgStoreXml(fileKey, fileName, data, uri);
			} catch (ParserConfigurationException | IllegalStateException | SAXException e) {
				throw new IOException("Error while loading XML Document " + fileKey + ": "
						+ e.getMessage());
			} finally {
				EntityUtils.consume(entity);
				response.close();
			}
		}
		return xml;
	}
	
	public FimgStoreTxt getTxt(String fileKey) throws IOException {
		CloseableHttpResponse response = null;
		String text = null;
		// validate params and build Uri
		final URI uri = uriBuilder.getFileUri(fileKey);

		response = get(uri);

		final String fileName = getAttachmInfo(response);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try {
				text = StreamUtils.writeStreamToString(entity.getContent(), "UTF-8");
			} finally {
				EntityUtils.consume(entity);
				response.close();
			}
		}
		return new FimgStoreTxt(fileKey, fileName, text, uri);
	}

	/*
	 * TODO - Image delete: URL = https://my.host.com/imagestore/DeleteImage?id=
	 * Deletes the image with the specified id
	 * 
	 * - Image search: URL = https://my.host.com/imagestore/SearchImages
	 * Searches for images using the embedded database and returns all matching
	 * file infos. Parameters: fnMask, isPartOfMask specifying the search masks
	 * for the filename and is_part_of fields. Currently you can use * as a
	 * wildcard Note that the return data is encoded as a JSON array!
	 * 
	 * - Image statistics: URL = https://my.host.com/imagestore/GetStats?id=
	 * Returns retrieval statistics for the file with the given id (last access
	 * date, last user, last ip, number of retrievals)
	 */

	public File saveImg(final String key, final ImgType type, final String path)
			throws IOException {
		final URI uri = uriBuilder.getImgUri(key, type);
		File imgFile = saveFile(uri, path);
		return imgFile;
	}
	
	public File saveFile(final String key, final String path) throws IOException{
		final URI uri = uriBuilder.getFileUri(key);
		File file = saveFile(uri, path);
		return file;
	}
	
	public File saveFile(final String key, final String path, final String filename) throws IOException{
		final URI uri = uriBuilder.getFileUri(key);
		File file = saveFile(uri, path, filename);
		return file;
	}
	
	public File saveFile(final URI uri, String path)
			throws IllegalArgumentException, IOException {
		return saveFile(uri, path, null);
	}
	
	/**
	 * rather save stuff outside to file than here. not sure if needed...
	 * 
	 * @param fileKey
	 *            file id in the fimagestore
	 * 
	 * @param path
	 *            the directory(!) where the file is stored with its original
	 *            file name
	 * 
	 * @param params
	 *            all the GET parameters that should be used
	 * 
	 * @return File object reference to the created file in the filesystem
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @throws IOException
	 */
	public File saveFile(final URI uri, String path, String fileName)
			throws IllegalArgumentException, IOException {
		CloseableHttpResponse response = null;
		File file = null;

		response = get(uri);
		try{
			String origFileName = getAttachmInfo(response);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
	
				if (!path.endsWith(File.separator)) {
					path += File.separator;
				}
				// build the filePath
				String filePath = path + (fileName == null || fileName.isEmpty() ? origFileName : fileName);
				file = StreamUtils.writeStreamToFile(entity.getContent(), filePath);
				EntityUtils.consume(entity);
			}
		}finally {
			response.close();
		}
		return file;
	}

	/*******************
	 * Utility methods *
	 *******************/

	/**
	 * Extract and validate Content-Disposition header. Rebuild original
	 * attachment file name.
	 * 
	 * @param response
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
		fn = trimQuotes(header.getValue().substring(fnindex + 9));
		return fn;
	}

	private String trimQuotes(String fn) {
		if(fn == null || fn.isEmpty()){
			return fn;
		}
		final String QUOTES = "\"";
		final boolean isFirst = fn.startsWith(QUOTES);
		final boolean isLast = fn.endsWith(QUOTES);
		if(isFirst && isLast){
			fn = fn.substring(1, fn.length()-1);
		} else if(isFirst){
			fn = fn.substring(1);
		} else if(isLast){
			fn = fn.substring(0, fn.length()-1);
		}
		// remove quotes in filenames:
		if (fn.startsWith("\"")) {
			fn = fn.substring(1);
		}
		if (fn.endsWith("\"")) {
			fn = fn.substring(0, fn.length()-1);
		}
		
		return fn;
	}
	
}
