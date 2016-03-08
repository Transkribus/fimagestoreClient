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

}
