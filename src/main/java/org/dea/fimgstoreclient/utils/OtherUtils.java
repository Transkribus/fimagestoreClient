package org.dea.fimgstoreclient.utils;

import java.awt.Point;
import java.util.List;

public class OtherUtils {
	
	public static String pointsToString(List<Point> pts) {
		String ptsStr="";
		for (Point pt : pts) {
			ptsStr += pt.x+","+pt.y+" ";
		}
		return ptsStr.trim();
	}
	
	public static String trimQuotes(String fn) {
		if (fn == null || fn.isEmpty()) {
			return fn;
		}
		final String QUOTES = "\"";
		final boolean isFirst = fn.startsWith(QUOTES);
		final boolean isLast = fn.endsWith(QUOTES);
		if (isFirst && isLast) {
			fn = fn.substring(1, fn.length() - 1);
		} else if (isFirst) {
			fn = fn.substring(1);
		} else if (isLast) {
			fn = fn.substring(0, fn.length() - 1);
		}
		// remove quotes in filenames:
		if (fn.startsWith("\"")) {
			fn = fn.substring(1);
		}
		if (fn.endsWith("\"")) {
			fn = fn.substring(0, fn.length() - 1);
		}

		return fn;
	}
}
