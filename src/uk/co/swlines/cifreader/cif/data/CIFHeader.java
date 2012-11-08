package uk.co.swlines.cifreader.cif.data;

public class CIFHeader {
	private String mainframe_identity, user_identity, user_extract_date, extract_date, extract_time, file_ref, last_file_ref, cif_version, user_extract_start_date, user_extract_end_date;
	private boolean fullExtract;
	
	public CIFHeader(String line) {
		// extract everything from CIF
		mainframe_identity = line.substring(2, 22);
		extract_date = line.substring(22, 28);
		extract_time = line.substring(28, 32);
		file_ref = line.substring(32, 39);
		last_file_ref = line.substring(39, 46);
		fullExtract = line.substring(46, 47).equals("F");
		cif_version = line.substring(47, 48);
		user_extract_start_date = line.substring(48, 54);
		user_extract_end_date = line.substring(54, 60);
		
		// extract relevant fields from mainframe identity
		user_identity = mainframe_identity.substring(4, 10);
		user_extract_date = mainframe_identity.substring(14, 20);
	}
}
