package org.dea.fimgstoreclient;

import java.io.IOException;

public interface IFimgStorePostClient extends IFimgStoreClientBase {
	public String postFile(byte[] data, final String fileName, final String isPartOf, final int nrOfRetries) throws IOException;
}
