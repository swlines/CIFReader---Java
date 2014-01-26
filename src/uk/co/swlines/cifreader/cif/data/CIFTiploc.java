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

public class CIFTiploc {
	private String tiploc, nlc, tps_description, stanox, crs, description;
	
	public CIFTiploc(String record) {
		if(record.substring(0, 2).equals("TI") || record.substring(72, 79).trim().equals("")) 
			tiploc = record.substring(2, 9).trim();
		else 
			tiploc = record.substring(72, 79).trim();
		
		nlc = record.substring(11, 17).trim();
		tps_description = CIFUtils.capitaliseString(record.substring(18, 44).trim());
		stanox = record.substring(44, 49).trim();
		crs = record.substring(53, 56).trim();
		description = CIFUtils.capitaliseString(record.substring(56, 72).trim());
	}

	public String getTiploc() {
		return tiploc;
	}

	public String getNlc() {
		return nlc;
	}

	public String getTps_description() {
		return tps_description;
	}

	public String getStanox() {
		return stanox;
	}

	public String getCrs() {
		return crs;
	}

	public String getDescription() {
		return description;
	}
}
