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

public abstract class CIFLocation {
	protected String tiploc, platform;
	protected char tiploc_instance;
	
	protected boolean ac_a, ac_ae, ac_bl, ac_c, ac_d, ac__d, ac_e, ac_g, ac_h, ac_hh, ac_k, ac_kc, ac_ke, ac_kf, ac_ks, ac_l, ac_n, ac_op, ac_or, ac_pr, ac_r, ac_rm, ac_rr, ac_s, ac_t, ac__t, ac_tb, ac_tf, ac_ts, ac_tw, ac_u, ac__u, ac_w, ac_x;
	protected boolean public_call, actual_call;
	
	public CIFLocation(String line){
		tiploc = line.substring(2, 9).trim();
		tiploc_instance = line.charAt(9);
		
		ac_a = false;
		ac_ae = false;
		ac_bl = false;
		ac_c = false;
		ac_d = false;
		ac__d = false;
		ac_e = false;
		ac_g = false;
		ac_h = false;
		ac_hh = false;
		ac_k = false;
		ac_kc = false;
		ac_ke = false;
		ac_kf = false;
		ac_ks = false;
		ac_l = false;
		ac_n = false;
		ac_op = false;
		ac_or = false;
		ac_pr = false;
		ac_r = false;
		ac_rm = false;
		ac_rr = false;
		ac_s = false;
		ac_t = false;
		ac__t = false;
		ac_tb = false;
		ac_tf = false;
		ac_ts = false;
		ac_tw = false;
		ac_u = false;
		ac__u = false;
		ac_w = false;
		ac_x = false;
	}
	
	public void computeActivity(String activity) {
		// iterate through, as it's 6x2
		for(int i = 0; i < 12; i+=2) {
			String act = activity.substring(i, i+2);
			
			if(act.equals("A ")) ac_a = true;
			if(act.equals("AE")) ac_ae = true;
			if(act.equals("BL")) ac_bl = true;
			if(act.equals("C ")) ac_c = true;
			if(act.equals("D ")) ac_d = true;
			if(act.equals("-D")) ac__d = true;
			if(act.equals("E ")) ac_e = true;
			if(act.equals("G ")) ac_g = true;
			if(act.equals("H ")) ac_h = true;
			if(act.equals("HH")) ac_hh = true;
			if(act.equals("K ")) ac_k = true;
			if(act.equals("KC")) ac_kc = true;
			if(act.equals("KE")) ac_ke = true;
			if(act.equals("KF")) ac_kf = true;
			if(act.equals("KS")) ac_ks = true;
			if(act.equals("L ")) ac_l = true;
			if(act.equals("N ")) ac_n = true;
			if(act.equals("OP")) ac_op = true;
			if(act.equals("OR")) ac_or = true;
			if(act.equals("PR")) ac_pr = true;
			if(act.equals("R ")) ac_r = true;
			if(act.equals("RM")) ac_rm = true;
			if(act.equals("RR")) ac_rr = true;
			if(act.equals("S ")) ac_s = true;
			if(act.equals("T ")) ac_t = true;
			if(act.equals("-T")) ac__t = true;
			if(act.equals("TB")) ac_tb = true;
			if(act.equals("TF")) ac_tf = true;
			if(act.equals("TS")) ac_ts = true;
			if(act.equals("TW")) ac_tw = true;
			if(act.equals("U ")) ac_u = true;
			if(act.equals("-U")) ac__u = true;
			if(act.equals("W ")) ac_w = true;
			if(act.equals("X ")) ac_x = true;
		}
	}
	
	public abstract String getLocationType();

	/**
	 * @return the tiploc
	 */
	public String getTiploc() {
		return tiploc;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * @return the tiploc_instance
	 */
	public char getTiploc_instance() {
		return tiploc_instance;
	}

	/**
	 * @return the ac_a
	 */
	public boolean isAc_a() {
		return ac_a;
	}

	/**
	 * @return the ac_ae
	 */
	public boolean isAc_ae() {
		return ac_ae;
	}

	/**
	 * @return the ac_bl
	 */
	public boolean isAc_bl() {
		return ac_bl;
	}

	/**
	 * @return the ac_c
	 */
	public boolean isAc_c() {
		return ac_c;
	}

	/**
	 * @return the ac_d
	 */
	public boolean isAc_d() {
		return ac_d;
	}

	/**
	 * @return the ac__d
	 */
	public boolean isAc__d() {
		return ac__d;
	}

	/**
	 * @return the ac_e
	 */
	public boolean isAc_e() {
		return ac_e;
	}

	/**
	 * @return the ac_g
	 */
	public boolean isAc_g() {
		return ac_g;
	}

	/**
	 * @return the ac_h
	 */
	public boolean isAc_h() {
		return ac_h;
	}

	/**
	 * @return the ac_hh
	 */
	public boolean isAc_hh() {
		return ac_hh;
	}

	/**
	 * @return the ac_k
	 */
	public boolean isAc_k() {
		return ac_k;
	}

	/**
	 * @return the ac_kc
	 */
	public boolean isAc_kc() {
		return ac_kc;
	}

	/**
	 * @return the ac_ke
	 */
	public boolean isAc_ke() {
		return ac_ke;
	}

	/**
	 * @return the ac_kf
	 */
	public boolean isAc_kf() {
		return ac_kf;
	}

	/**
	 * @return the ac_ks
	 */
	public boolean isAc_ks() {
		return ac_ks;
	}

	/**
	 * @return the ac_l
	 */
	public boolean isAc_l() {
		return ac_l;
	}

	/**
	 * @return the ac_n
	 */
	public boolean isAc_n() {
		return ac_n;
	}

	/**
	 * @return the ac_op
	 */
	public boolean isAc_op() {
		return ac_op;
	}

	/**
	 * @return the ac_or
	 */
	public boolean isAc_or() {
		return ac_or;
	}

	/**
	 * @return the ac_pr
	 */
	public boolean isAc_pr() {
		return ac_pr;
	}

	/**
	 * @return the ac_r
	 */
	public boolean isAc_r() {
		return ac_r;
	}

	/**
	 * @return the ac_rm
	 */
	public boolean isAc_rm() {
		return ac_rm;
	}

	/**
	 * @return the ac_rr
	 */
	public boolean isAc_rr() {
		return ac_rr;
	}

	/**
	 * @return the ac_s
	 */
	public boolean isAc_s() {
		return ac_s;
	}

	/**
	 * @return the ac_t
	 */
	public boolean isAc_t() {
		return ac_t;
	}

	/**
	 * @return the ac__t
	 */
	public boolean isAc__t() {
		return ac__t;
	}

	/**
	 * @return the ac_tb
	 */
	public boolean isAc_tb() {
		return ac_tb;
	}

	/**
	 * @return the ac_tf
	 */
	public boolean isAc_tf() {
		return ac_tf;
	}

	/**
	 * @return the ac_ts
	 */
	public boolean isAc_ts() {
		return ac_ts;
	}

	/**
	 * @return the ac_tw
	 */
	public boolean isAc_tw() {
		return ac_tw;
	}

	/**
	 * @return the ac_u
	 */
	public boolean isAc_u() {
		return ac_u;
	}

	/**
	 * @return the ac__u
	 */
	public boolean isAc__u() {
		return ac__u;
	}

	/**
	 * @return the ac_w
	 */
	public boolean isAc_w() {
		return ac_w;
	}

	/**
	 * @return the ac_x
	 */
	public boolean isAc_x() {
		return ac_x;
	}

	/**
	 * @return the public_call
	 */
	public boolean isPublic_call() {
		return public_call;
	}

	/**
	 * @return the actual_call
	 */
	public boolean isActual_call() {
		return actual_call;
	}
	
	protected static int getAllowanceInSeconds(String allowance) {
		if(allowance == null) return 0;
		
		int a = 0;
		
		allowance = allowance.trim();
		
		if(allowance.length() > 0) {
			if(allowance.charAt(allowance.length() - 1) == 'H') {
				a += 30;
				
				if(allowance.length() > 1) {
					a += Long.valueOf(allowance.substring(0, allowance.length() - 1)) * 60;
				}
			}
			else {
				if(allowance.length() > 0) {
					a += Long.valueOf(allowance.substring(0, allowance.length())) * 60;
				}
			}
		}
		
		return a;
	}
}
