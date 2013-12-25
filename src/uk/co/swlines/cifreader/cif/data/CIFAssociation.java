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

public class CIFAssociation {
	private String main_train_uid, associated_train_uid, date_from, date_to, category, location;
	private char date_indicator, location_main_suffix, location_associated_suffix, association_type, stp_indicator;
	private boolean runs_mo, runs_tu, runs_we, runs_th, runs_fr, runs_sa, runs_su;
	
	public CIFAssociation(String record) {
		main_train_uid = record.substring(3, 9);
		associated_train_uid = record.substring(9, 15);
		date_from = CIFUtils.convertDate(record.substring(15, 21));
		date_to = CIFUtils.convertDate(record.substring(21, 27));
	
		String runs = record.substring(27, 34);
		category = record.substring(34, 36);
		
		date_indicator = record.charAt(36);
		location = record.substring(37, 44);
		location_main_suffix = record.charAt(44);
		location_associated_suffix = record.charAt(45);
		// diagram type at 46 is no longer used
		association_type = record.charAt(47);
		
		// 48-79 spare
		stp_indicator = record.charAt(79);
		
		// pull stuff out of cif
		runs_mo = runs.charAt(0) == '1';
		runs_tu = runs.charAt(1) == '1';
		runs_we = runs.charAt(2) == '1';
		runs_th = runs.charAt(3) == '1';
		runs_fr = runs.charAt(4) == '1';
		runs_sa = runs.charAt(5) == '1';
		runs_su = runs.charAt(6) == '1';
	}

	public String getMain_train_uid() {
		return main_train_uid;
	}

	public String getAssociated_train_uid() {
		return associated_train_uid;
	}

	public String getDate_from() {
		return date_from;
	}

	public String getDate_to() {
		return date_to;
	}

	public String getCategory() {
		return category;
	}

	public String getLocation() {
		return location;
	}

	public char getDate_indicator() {
		return date_indicator;
	}

	public char getLocation_main_suffix() {
		return location_main_suffix;
	}

	public char getLocation_associated_suffix() {
		return location_associated_suffix;
	}

	public char getAssociation_type() {
		return association_type;
	}

	public char getStp_indicator() {
		return stp_indicator;
	}

	public boolean isRuns_mo() {
		return runs_mo;
	}

	public boolean isRuns_tu() {
		return runs_tu;
	}

	public boolean isRuns_we() {
		return runs_we;
	}

	public boolean isRuns_th() {
		return runs_th;
	}

	public boolean isRuns_fr() {
		return runs_fr;
	}

	public boolean isRuns_sa() {
		return runs_sa;
	}

	public boolean isRuns_su() {
		return runs_su;
	}
}
