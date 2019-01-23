package org.dea.fimgstoreclient;

import java.io.File;
import java.io.IOException;

import org.apache.http.auth.AuthenticationException;

public interface IFimgStorePostClient extends IFimgStoreClientBase {
	public String postFile(byte[] data, final String fileName, final String isPartOf, final int nrOfRetries) throws IOException;
	/**
	 * post a file to the image store and get the retrieval key
	 * 
	 * @param ulFile file to upload
	 * @param isPartOf a String that identifies the collection
	 * @param nrOfRetries nr of times to retry on failure
	 * @param timeoutMinutes specifies the number of minutes after the file will be deleted on the fimagestore; null means no timeout
	 * @return fileKey for subsequent retrieval of file
	 * @throws IOException if network error occurs
	 * @throws AuthenticationException if authentication fails
	 */
	public String postFile(File ulFile, String isPartOf, int nrOfRetries, Integer timeoutMinutes) throws IOException, AuthenticationException;
}
