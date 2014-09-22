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

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.swlines.cifreader.cif.CIFUtils;

public class CIFSchedule {
	/**
	 * Fields shown are collected from CIF, with exception of Unique ID which is generated using a combination
	 * of UID, Date From and STP.
	 */
	
	private String uid, unique_id, date_from, date_to, category, train_identity, headcode, 
		power_type, timing_load, speed, catering_code, service_branding, uic_code, atoc_code, rsid;
	
	private char bank_holiday, status, portion_id, train_class, sleepers, reservations, stp_indicator, ats, source;
	
	private boolean runs_mo, runs_tu, runs_we, runs_th, runs_fr, runs_sa, runs_su;
	private boolean oc_b, oc_c, oc_d, oc_e, oc_g, oc_m, oc_p, oc_q, oc_r, oc_s, oc_y, oc_z;
	
	private int databaseId;
	private Integer service_code;
	
	ArrayList<CIFLocation> locations = null;
	
	public CIFSchedule(String line) {
		// extract everything from CIF
		uid = line.substring(3, 9).trim();
		date_from = CIFUtils.convertDate(line.substring(9, 15).trim());
		date_to = CIFUtils.convertDate(line.substring(15, 21).trim());
		String runs = line.substring(21, 28);
		bank_holiday = line.charAt(28);
		status = line.charAt(29);
		category = line.substring(30, 32).trim();
		train_identity = line.substring(32, 36).trim();
		headcode = line.substring(36, 40).trim();
		// course indicator isn't used
		String service_code = line.substring(41, 49).trim();
		this.service_code = service_code.length() == 0 ? null : Integer.valueOf(service_code);
		portion_id = line.charAt(49);
		power_type = line.substring(50, 53).trim();
		timing_load = line.substring(53, 57).trim();
		speed = line.substring(57, 60).trim();
		String operating_characteristics = line.substring(60, 66).trim();
		train_class = line.charAt(66);
		sleepers = line.charAt(67);
		reservations = line.charAt(68);
		// connection indicator isn't used
		catering_code = line.substring(70, 74).trim();
		service_branding = line.substring(74, 78).trim();
		//spare
		stp_indicator = line.charAt(79);
		
		// pull stuff out of cif
		runs_mo = runs.charAt(0) == '1';
		runs_tu = runs.charAt(1) == '1';
		runs_we = runs.charAt(2) == '1';
		runs_th = runs.charAt(3) == '1';
		runs_fr = runs.charAt(4) == '1';
		runs_sa = runs.charAt(5) == '1';
		runs_su = runs.charAt(6) == '1';
		
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
		
		locations = new ArrayList<CIFLocation>();
	}

	public void parseBXRecord(String line) {
		// parse the BX record
		// traction class not used
		uic_code = line.substring(6, 11).trim();
		atoc_code = line.substring(11, 13).trim();
		ats = line.charAt(13);
		rsid = line.substring(14, 22).trim();
	}
	
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
	
	public int getDatabaseId() {
		return databaseId;
	}
	
	public boolean isBus() {
		return category.equals("BR") || category.equals("BS");
	}
	
	public boolean isShip() {
		return status == 'S' || status == '4';
	}
	
	public boolean isTrain() {
		return !(isBus() || isShip());
	}
	
	public boolean isPassenger() {
		return isBus() || isShip() || (!isOc_q() && (category.equals("OL") || category.equals("OO") || category.equals("XC")
				|| category.equals("XX") || category.equals("XZ")));
	}
	
	@Override
	public String toString() {
		return "CIFSchedule [uid=" + uid + ", unique_id=" + unique_id
				+ ", date_from=" + date_from + ", date_to=" + date_to
				+ ", category=" + category + ", train_identity="
				+ train_identity + ", headcode=" + headcode + ", service_code="
				+ service_code + ", power_type=" + power_type
				+ ", timing_load=" + timing_load + ", speed=" + speed
				+ ", catering_code=" + catering_code + ", service_branding="
				+ service_branding + ", uic_code=" + uic_code + ", atoc_code="
				+ atoc_code + ", rsid=" + rsid + ", bank_holiday="
				+ bank_holiday + ", status=" + status + ", portion_id="
				+ portion_id + ", train_class=" + train_class + ", sleepers="
				+ sleepers + ", reservations=" + reservations + ", stp=" + stp_indicator
				+ ", ats=" + ats + ", source=" + source + ", runs_mo="
				+ runs_mo + ", runs_tu=" + runs_tu + ", runs_we=" + runs_we
				+ ", runs_th=" + runs_th + ", runs_fr=" + runs_fr
				+ ", runs_sa=" + runs_sa + ", runs_su=" + runs_su + ", oc_b="
				+ oc_b + ", oc_c=" + oc_c + ", oc_d=" + oc_d + ", oc_e=" + oc_e
				+ ", oc_g=" + oc_g + ", oc_m=" + oc_m + ", oc_p=" + oc_p
				+ ", oc_q=" + oc_q + ", oc_r=" + oc_r + ", oc_s=" + oc_s
				+ ", oc_y=" + oc_y + ", oc_z=" + oc_z + ", locations="
				+ Arrays.toString(locations.toArray()) + "]";
	}
	
	public void addLocation(CIFLocation location) {
		locations.add(location);
	}

	public String getUid() {
		return uid;
	}

	public String getUnique_id() {
		return unique_id;
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

	public String getTrain_identity() {
		return train_identity;
	}

	public String getHeadcode() {
		return headcode;
	}

	public Integer getService_code() {
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

	public String getUic_code() {
		return uic_code;
	}

	public String getAtoc_code() {
		return atoc_code;
	}

	public String getRsid() {
		return rsid;
	}

	public char getBank_holiday() {
		return bank_holiday;
	}

	public char getStatus() {
		return status;
	}

	public char getPortion_id() {
		return portion_id;
	}

	public char getTrain_class() {
		return train_class;
	}

	public char getSleepers() {
		return sleepers;
	}

	public char getReservations() {
		return reservations;
	}

	public char getStp_indicator() {
		return stp_indicator;
	}

	public char getAts() {
		return ats;
	}

	public char getSource() {
		return source;
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

	public ArrayList<CIFLocation> getLocations() {
		return locations;
	}
	
	
	
}
