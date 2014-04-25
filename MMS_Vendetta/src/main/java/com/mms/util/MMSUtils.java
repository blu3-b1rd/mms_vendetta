package com.mms.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;

import com.mms.app.MMSConfig;

public class MMSUtils {
	
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	public static String demoUrl(String demoPath){
		return MMSConfig.MMS_BASE_CONTENT_URL + demoPath;
	}
	
	public static String imageUrl(String imagePath){
		return MMSConfig.MMS_BASE_CONTENT_URL + imagePath;
	}
	
	public static String scaledImageUrl(String imagePath, int width){
		/*
		 http://musicmultimediastore.com/Image/Vendetta/PICTURE/1392086_10151764956024132_837699595_n.png
		 */
		Pattern pattern = Pattern.compile("^/Image/.+(/.+)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(imagePath);
		if(!matcher.find()){
			return MMSConfig.MMS_BASE_CONTENT_URL + imagePath;
		}
		
		try {
			String resource = matcher.group(1);
			StringBuilder builder = new StringBuilder("");
			builder.append("/").append(width).append("/").append(width)
				.append(resource);
			return MMSConfig.MMS_BASE_CONTENT_URL + 
					imagePath.replace(resource, builder.toString());			
		} catch(Exception e) {
			return MMSConfig.MMS_BASE_CONTENT_URL + imagePath;
		}
	}
	
	public static String formatedDate(Date date){
		DateFormat dateFormater = new SimpleDateFormat(
				DATE_FORMAT, Locale.getDefault());
		return dateFormater.format(date);
	}
	
	public static int colorWithAlpha(int color, float factor) {
	    int alpha = Math.round(Color.alpha(color) * factor);
	    int red = Color.red(color);
	    int green = Color.green(color);
	    int blue = Color.blue(color);
	    return Color.argb(alpha, red, green, blue);
	}
	
}
