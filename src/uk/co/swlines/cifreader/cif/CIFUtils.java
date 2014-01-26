package uk.co.swlines.cifreader.cif;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

public final class CIFUtils {
	public static final String STRING_TO_UPPER = " ()&.";
	
	public static final String convertDate(String date) {
		// edge case
		if(date.equals("999999")) return "9999-99-99";
		else if(date.trim().equals("")) return null;
		
		String returnData = "";
		
		int year = Integer.valueOf(date.substring(0, 2));
		
		if(year >= 60 && year <= 99) return "19" + year + "-"+ date.substring(2, 4) + "-" + date.substring(4, 6);
		else return "20" + year + "-"+ date.substring(2, 4) + "-" + date.substring(4, 6);		
	}
	
	public static final String convertForwardDate(String date) {
		if(date.equals("999999")) return "9999-99-99";
		else if(date.trim().equals("")) return null;
		
		String returnData = "";
		int year = Integer.valueOf(date.substring(4, 6));
		
		if(year >= 60 && year <= 99) return "19" + year + "-"+ date.substring(2, 4) + "-" + date.substring(0, 2);
		else return "20" + year + "-"+ date.substring(2, 4) + "-" + date.substring(0, 2);		
	}
	
	public static final String capitaliseString(String string) {
		if(string == null || string.length() == 0) return "";
		
		return WordUtils.capitalizeFully(string.toLowerCase(), STRING_TO_UPPER.toCharArray());
	}
}
