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

public class CIFLocationTerminus extends CIFLocation implements CIFLocationArrival {
	public CIFLocationTerminus(String record) {
		super(record);
				
		String arrival = record.substring(10, 15).trim();
		this.arrival = arrival.length() == 0 ? null : Integer.valueOf(arrival.substring(0, 4) + (arrival.length() == 4 ?  "00" : "30"));
		
		String public_arrival = record.substring(15, 19).trim();
		this.public_arrival = public_arrival.length() == 0 || public_arrival.equals("0000") ? null : Integer.valueOf(public_arrival);
		
		platform = record.substring(19, 22).trim();
		path = record.substring(22, 25).trim();
		
		computeActivity(record.substring(25, 37));
		
		public_call = !(ac_n || this.public_arrival == null);
		actual_call = true;
	}

	private String path;
	private Integer arrival, public_arrival;

	@Override
	public String getLocationType() {
		return "LT";
	}
	
	@Override
	public Integer getArrival() {
		return arrival;
	}

	@Override
	public Integer getPublic_arrival() {
		return public_arrival;
	}
	
	@Override
	public String getPath() {
		return path;
	}
}
