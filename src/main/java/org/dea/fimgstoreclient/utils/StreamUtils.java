package org.dea.fimgstoreclient.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class StreamUtils {
	
	/**
	 * Reads an input stream and copies its content to a byte[]
	 * @param is InputStream
	 * @return data as byte[]
	 * @throws IOException
	 */
	public static ByteArrayOutputStream writeStreamToByteArr(InputStream is) throws IOException {	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		BufferedInputStream bis = new BufferedInputStream(is);

		int inByte;
		while ((inByte = bis.read()) != -1){
			baos.write(inByte);
		}
		bis.close();
		return baos;
	}

	public static String writeStreamToString(InputStream is, String charsetName) throws IOException {
		return writeStreamToString(is, Charset.forName(charsetName));
	}
	
	public static String writeStreamToString(InputStream is) throws IOException {
		return writeStreamToString(is, Charset.defaultCharset());
	}
	
	public static String writeStreamToString(InputStream is, Charset charset) throws IOException {
		BufferedReader br = null;
		StringBuffer result = new StringBuffer();
		try {		
			InputStream in = new DataInputStream(is);
			br = new BufferedReader(new InputStreamReader(in, charset));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				result.append(strLine + "\n");
			}
		} finally {
			br.close();
		}
		return result.toString();
	}
	
	/**
	 * Reads an input stream and writes its content to a file
	 * 
	 * @param is
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File writeStreamToFile(InputStream is, String path)
			throws IOException {
		File file = new File(path);
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		int inByte;

		while ((inByte = bis.read()) != -1) {
			bos.write(inByte);
		}
		bis.close();
		bos.close();
		return file;
	}
}
