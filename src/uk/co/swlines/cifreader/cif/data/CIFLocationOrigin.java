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

public class CIFLocationOrigin extends CIFLocation {
	public CIFLocationOrigin(String record) {
		super(record);
		
		departure = record.substring(10, 15).trim();
		public_departure = record.substring(15, 19).trim();
		platform = record.substring(19, 22).trim();
		line = record.substring(22, 25).trim();
		allowance_engineering = record.substring(25, 27).trim();
		allowance_pathing = record.substring(27, 29).trim();
		allowance_performance = record.substring(41, 43).trim();
				
		computeActivity(record.substring(29, 41));
	}

	private String departure, public_departure, line, allowance_engineering, allowance_pathing, allowance_performance;
	
}
