package org.dea.fimgstoreclient.responsehandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.dea.fimagestore.core.MetadataReader;
import org.dea.fimagestore.core.beans.FileMetadata;
import org.dea.fimgstoreclient.FimgStoreConstants;

public class FileMetadataResponseHandler extends AFimgStoreResponseWithAttachmentHandler<FileMetadata> {

	public FileMetadataResponseHandler(URI uri) {
		super(uri);
	}

	@Override
	public FileMetadata handleEntity(HttpEntity entity) throws IOException {
		if(entity == null) {
			throw new IOException("No entity was sent by the server.");
		}
		InputStream is = entity.getContent();
		FileMetadata md = MetadataReader.readFileMetadata(is, FimgStoreConstants.DEFAULT_CHARSET);
		md.setUri(super.getRequestUri());
		return md;
	}
}
