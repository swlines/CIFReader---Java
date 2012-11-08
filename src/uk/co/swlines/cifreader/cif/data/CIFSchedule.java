package uk.co.swlines.cifreader.cif.data;

public class CIFSchedule {
	/**
	 * Fields shown are collected from CIF, with exception of Unique ID which is generated using a combination
	 * of UID, Date From and STP.
	 */
	private String uid, unique_id, date_from, date_to, category, train_identity, headcode, service_code, 
		power_type, timing_load, speed, catering_code, service_branding, uic_code, atoc_code, rsid;
	
	private char bank_holiday, status, portion_id, train_class, sleepers, reservations, stp, ats, source;
	
	private boolean runs_mo, runs_tu, runs_we, runs_th, runs_fr, runs_sa, runs_su;
	private boolean oc_b, oc_c, oc_d, oc_e, oc_g, oc_m, oc_p, oc_q, oc_r, oc_s, oc_y, oc_z;
	
	CIFLocation[] locations = null;
	
	public CIFSchedule(String line) {
		
	}
}
