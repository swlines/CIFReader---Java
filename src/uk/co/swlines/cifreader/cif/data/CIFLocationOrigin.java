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

public class CIFLocationOrigin extends CIFLocation implements CIFLocationDepart {
	public CIFLocationOrigin(String record) {
		super(record);
		
		String departure = record.substring(10, 15).trim();
		this.departure = departure.length() == 0 ? null : Integer.valueOf(departure.substring(0, 4) + (departure.length() == 4 ?  "00" : "30"));
		
		String public_departure = record.substring(15, 19).trim();
		this.public_departure = public_departure.length() == 0 || public_departure.equals("0000") ? null : Integer.valueOf(public_departure);
		
		platform = record.substring(19, 22).trim();
		line = record.substring(22, 25).trim();
		
		String allowance_engineering = record.substring(25, 27).trim();
		this.allowance_engineering = getAllowanceInSeconds(allowance_engineering);

		String allowance_pathing = record.substring(27, 29).trim();
		this.allowance_pathing = getAllowanceInSeconds(allowance_pathing);

		String allowance_performance = record.substring(41, 43).trim();
		this.allowance_pathing = getAllowanceInSeconds(allowance_performance);
				
		computeActivity(record.substring(29, 41));
		
		public_call = !(ac_n || this.public_departure == null);
		actual_call = true;
	}

	private String line;
	int allowance_engineering, allowance_pathing, allowance_performance;
	private Integer departure, public_departure;

	@Override
	public String getLocationType() {
		return "LO";
	}

	@Override
	public Integer getDeparture() {
		return departure;
	}

	@Override
	public Integer getPublic_departure() {
		return public_departure;
	}
	
	@Override
	public String getLine() {
		return line;
	}

	@Override
	public int getAllowance_engineering() {
		return allowance_engineering;
	}

	@Override
	public int getAllowance_pathing() {
		return allowance_pathing;
	}

	@Override
	public int getAllowance_performance() {
		return allowance_performance;
	}
}
