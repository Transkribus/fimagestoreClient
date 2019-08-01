package org.dea.fimgstoreclient;

import java.io.File;
import java.io.IOException;

import org.dea.fimgstoreclient.beans.FimgStoreImg;
import org.dea.fimgstoreclient.beans.FimgStoreXml;
import org.dea.fimgstoreclient.beans.ImgType;

public interface IFimgStoreGetClient extends IFimgStoreClientBase {

	public File saveImg(final String fileKey, final ImgType type, final String path, final String filename) throws IOException;
	public File saveFile(final String fileKey, String path, String fileName) throws IllegalArgumentException, IOException;
	public byte[] getData(final String fileKey) throws IllegalArgumentException, IOException;
	public FimgStoreXml getXml(final String fileKey) throws IOException;
	/**
	 * Get a cropped version of the image
	 * 
	 * crop=posX x posY x width x height
	 * 
	 * @param imgKey
	 *            the key of the file to crop
	 * @param posX
	 *            offset left
	 * @param posY
	 *            offset top
	 * @param width
	 *            width of the snippet
	 * @param height
	 *            height of the snippet
	 * @return {@link org.dea.fimgstoreclient.beans.FimgStoreImg}
	 * @throws IOException
	 *             if network error occurs
	 * @throws IllegalArgumentException
	 *             if any param is in a bad format
	 */
	public FimgStoreImg getImgCropped(final String imgKey, final int posX, final int posY, final int width,
			final int height) throws IOException;
}
