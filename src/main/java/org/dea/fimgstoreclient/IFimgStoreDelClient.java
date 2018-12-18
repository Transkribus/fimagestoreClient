package org.dea.fimgstoreclient;

import java.io.IOException;

public interface IFimgStoreDelClient {
	public boolean deleteFile(final String fileKey, int nrOfRetries) throws IOException;
}
