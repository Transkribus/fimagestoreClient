package org.dea.fimgstoreclient;

import java.io.File;
import java.io.IOException;

import org.dea.fimgstoreclient.beans.FimgStoreXml;
import org.dea.fimgstoreclient.beans.ImgType;

public interface IFimgStoreGetClient extends IFimgStoreClientBase {

	public File saveImg(final String fileKey, final ImgType type, final String path, final String filename) throws IOException;
	public File saveFile(final String fileKey, String path, String fileName) throws IllegalArgumentException, IOException;
	public FimgStoreXml getXml(final String fileKey) throws IOException;
}
