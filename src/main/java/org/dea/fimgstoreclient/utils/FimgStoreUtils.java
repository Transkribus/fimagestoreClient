package org.dea.fimgstoreclient.utils;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dea.fimgstoreclient.FimgStoreConstants;

public class FimgStoreUtils {
	private static final String fileKeyPatternStr = FimgStoreConstants.getString("fileKeyPattern");
	public static final Pattern keyPattern = Pattern.compile(fileKeyPatternStr);
	
	public static boolean isFimgStoreKey(final String imgKey){
		return keyPattern.matcher(imgKey).matches();
	}

	public static String extractKey(URL url) throws URISyntaxException {
		String key;
		final String query = url.getQuery();
		if(query == null || query.isEmpty()){
			throw new URISyntaxException(url.toString(), "Contains no query part!");
		}
		
		final String idQueryPattern = FimgStoreConstants.ID_PARAM + "=" + fileKeyPatternStr;
		Matcher m = Pattern.compile(idQueryPattern).matcher(query);
		
		if(m.find()){
			key = m.group(1);
		} else {
			throw new URISyntaxException(url.toString(), "Contains no key!");
		}
		
		return key;
	}
}
