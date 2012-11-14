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

public class CIFLocationIntermediate extends CIFLocation {
	public CIFLocationIntermediate(String record) {
		super(record);
		
		arrival = record.substring(10, 15).trim();
		departure = record.substring(15, 20).trim();
		pass = record.substring(20, 25).trim();
		
		public_arrival = record.substring(25, 29).trim();
		public_departure = record.substring(29, 33).trim();
		
		platform = record.substring(33, 36).trim();
		line = record.substring(36, 39).trim();
		path = record.substring(39, 42).trim();
		
		allowance_engineering = record.substring(54, 56).trim();
		allowance_pathing = record.substring(56, 58).trim();
		allowance_performance = record.substring(58, 60).trim();
		
		computeActivity(record.substring(42, 54));
	}

	private String arrival, public_arrival, departure, public_departure, pass, line, path, allowance_engineering, allowance_pathing, allowance_performance;
}
