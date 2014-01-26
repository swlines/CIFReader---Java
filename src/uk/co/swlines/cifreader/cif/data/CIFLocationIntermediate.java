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

public class CIFLocationIntermediate extends CIFLocation implements CIFLocationArrival, CIFLocationDepart {
	private CIFLocationEnRouteChange change_record;
	
	public CIFLocationIntermediate(String record) {
		super(record);
		
		String arrival = record.substring(10, 15).trim();
		this.arrival = arrival.length() == 0 ? null : Integer.valueOf(arrival.substring(0, 4) + (arrival.length() == 4 ?  "00" : "30"));
		
		String departure = record.substring(15, 20).trim();
		this.departure = departure.length() == 0 ? null : Integer.valueOf(departure.substring(0, 4) + (departure.length() == 4 ?  "00" : "30"));
		
		String pass = record.substring(20, 25).trim();
		this.pass = pass.length() == 0 ? null : Integer.valueOf(pass.substring(0, 4) + (pass.length() == 4 ?  "00" : "30"));
		
		String public_arrival = record.substring(25, 29).trim();
		this.public_arrival = public_arrival.length() == 0 || public_arrival.equals("0000") ? null : Integer.valueOf(public_arrival);
		
		String public_departure = record.substring(29, 33).trim();
		this.public_departure = public_departure.length() == 0 || public_departure.equals("0000") ? null : Integer.valueOf(public_departure);
		
		platform = record.substring(33, 36).trim();
		line = record.substring(36, 39).trim();
		path = record.substring(39, 42).trim();
		
		String allowance_engineering = record.substring(54, 56).trim();
		this.allowance_engineering = getAllowanceInSeconds(allowance_engineering);
		
		String allowance_pathing = record.substring(56, 58).trim();
		this.allowance_pathing = getAllowanceInSeconds(allowance_pathing);
		
		String allowance_performance = record.substring(58, 60).trim();
		this.allowance_pathing = getAllowanceInSeconds(allowance_performance);
		
		computeActivity(record.substring(42, 54));
		
		public_call = !ac_n && (this.public_arrival != null || this.public_departure != null);
		actual_call = this.arrival != null || this.departure != null;
	}
	
	public CIFLocationIntermediate(String record, CIFLocationEnRouteChange change_record) {
		this(record);
		
		this.change_record = change_record;
	}

	private String line, path;
	int allowance_engineering, allowance_pathing, allowance_performance;
	private Integer arrival, public_arrival, departure, public_departure, pass;

	@Override
	public String getLocationType() {
		return "LI";
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
	public Integer getDeparture() {
		return departure;
	}

	@Override
	public Integer getPublic_departure() {
		return public_departure;
	}

	public Integer getPass() {
		return pass;
	}
	
	@Override
	public String getLine() {
		return line;
	}
	
	@Override
	public String getPath() {
		return path;
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
	
	public CIFLocationEnRouteChange getChangeRecord() {
		return change_record;
	}
}
