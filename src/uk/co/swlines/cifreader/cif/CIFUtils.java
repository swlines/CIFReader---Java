package uk.co.swlines.cifreader.cif;

public final class CIFUtils {
	public static final String convertDate(String date) {
		// edge case
		if(date.equals("999999")) return "9999-99-99";
		else if(date.trim().equals("")) return null;
		
		String returnData = "";
		
		int year = Integer.valueOf(date.substring(0, 2));
		
		if(year >= 60 && year <= 99) return "19" + year + "-"+ date.substring(2, 4) + "-" + date.substring(4, 6);
		else return "20" + year + "-"+ date.substring(2, 4) + "-" + date.substring(4, 6);		
	}
}
