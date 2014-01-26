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

public class CIFLocationEnRouteChange {
	private String category, train_identity, headcode, service_code, power_type, timing_load, speed, 
		catering_code, service_branding, uic, rsid;
	
	private boolean oc_b, oc_c, oc_d, oc_e, oc_g, oc_m, oc_p, oc_q, oc_r, oc_s, oc_y, oc_z;
	
	private char portion_id, train_class, sleeper, reservations;
	
	public CIFLocationEnRouteChange(String record) {
		category = record.substring(10, 12).trim();
		train_identity = record.substring(12, 16).trim();
		headcode = record.substring(16, 20).trim();
		
		service_code = record.substring(21, 29).trim();
		portion_id = record.charAt(29);
		power_type = record.substring(30, 33).trim();
		timing_load = record.substring(33, 37).trim();
		speed = record.substring(37, 40).trim();
		String operating_characteristics = record.substring(40, 46).trim();
		
		train_class = record.charAt(46);
		sleeper = record.charAt(47);
		reservations = record.charAt(48);
		// connection indicator at 49 not used
		
		catering_code = record.substring(50, 54);
		service_branding = record.substring(54, 58);
		// traction class between 58 & 62 not used
		uic = record.substring(62, 67);
		rsid = record.substring(67, 75);
		
		oc_b = operating_characteristics.contains("B");
		oc_c = operating_characteristics.contains("C");
		oc_d = operating_characteristics.contains("D");
		oc_e = operating_characteristics.contains("E");
		oc_g = operating_characteristics.contains("G");
		oc_m = operating_characteristics.contains("M");
		oc_p = operating_characteristics.contains("P");
		oc_q = operating_characteristics.contains("Q");
		oc_r = operating_characteristics.contains("R");
		oc_s = operating_characteristics.contains("S");
		oc_y = operating_characteristics.contains("Y");
		oc_z = operating_characteristics.contains("Z");
	}

	public String getCategory() {
		return category;
	}

	public String getTrain_identity() {
		return train_identity;
	}

	public String getHeadcode() {
		return headcode;
	}

	public String getService_code() {
		return service_code;
	}

	public String getPower_type() {
		return power_type;
	}

	public String getTiming_load() {
		return timing_load;
	}

	public String getSpeed() {
		return speed;
	}

	public String getCatering_code() {
		return catering_code;
	}

	public String getService_branding() {
		return service_branding;
	}

	public String getUic() {
		return uic;
	}

	public String getRsid() {
		return rsid;
	}

	public boolean isOc_b() {
		return oc_b;
	}

	public boolean isOc_c() {
		return oc_c;
	}

	public boolean isOc_d() {
		return oc_d;
	}

	public boolean isOc_e() {
		return oc_e;
	}

	public boolean isOc_g() {
		return oc_g;
	}

	public boolean isOc_m() {
		return oc_m;
	}

	public boolean isOc_p() {
		return oc_p;
	}

	public boolean isOc_q() {
		return oc_q;
	}

	public boolean isOc_r() {
		return oc_r;
	}

	public boolean isOc_s() {
		return oc_s;
	}

	public boolean isOc_y() {
		return oc_y;
	}

	public boolean isOc_z() {
		return oc_z;
	}

	public char getPortion_id() {
		return portion_id;
	}

	public char getTrain_class() {
		return train_class;
	}

	public char getSleeper() {
		return sleeper;
	}

	public char getReservations() {
		return reservations;
	}
	
	
}
