/*
 *  CIFReader - parser of timetable info files
 *  Copyright (C) 2012 Tom Cairns
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License 
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package uk.co.swlines.cifreader.cif.data;

import uk.co.swlines.cifreader.cif.CIFUtils;

public class CIFHeader {
	private String mainframe_identity, user_identity, user_extract_date, extract_date, extract_time, file_ref, last_file_ref, cif_version, user_extract_start_date, user_extract_end_date;
	private boolean fullExtract;
	
	public CIFHeader(String line) {
		// extract everything from CIF
		mainframe_identity = line.substring(2, 22);
		extract_date = CIFUtils.convertForwardDate(line.substring(22, 28));
		extract_time = line.substring(28, 32);
		file_ref = line.substring(32, 39);
		last_file_ref = line.substring(39, 46);
		fullExtract = line.substring(46, 47).equals("F");
		cif_version = line.substring(47, 48);
		user_extract_start_date = CIFUtils.convertForwardDate(line.substring(48, 54));
		user_extract_end_date = CIFUtils.convertForwardDate(line.substring(54, 60));
		
		// extract relevant fields from mainframe identity
		user_identity = mainframe_identity.substring(5, 11);
		user_extract_date = mainframe_identity.substring(14, 20);
	}

	public String getMainframe_identity() {
		return mainframe_identity;
	}

	public String getUser_identity() {
		return user_identity;
	}

	public String getUser_extract_date() {
		return user_extract_date;
	}

	public String getExtract_date() {
		return extract_date;
	}

	public String getExtract_time() {
		return extract_time;
	}

	public String getFile_ref() {
		return file_ref;
	}

	public String getLast_file_ref() {
		return last_file_ref;
	}

	public String getCif_version() {
		return cif_version;
	}

	public String getUser_extract_start_date() {
		return user_extract_start_date;
	}

	public String getUser_extract_end_date() {
		return user_extract_end_date;
	}

	public boolean isFullExtract() {
		return fullExtract;
	}
}
