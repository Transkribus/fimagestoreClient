package org.dea.fimgstoreclient;

import java.io.IOException;
import java.util.List;

public interface IFimgStoreDelClient {
	public boolean deleteFile(final String fileKey, int nrOfRetries) throws IOException;
	public void deleteFiles(List<String> fileKeysToDelete, int nrOfRetries);
}
