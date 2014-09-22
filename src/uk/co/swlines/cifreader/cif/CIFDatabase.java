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

package uk.co.swlines.cifreader.cif;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uk.co.swlines.cifreader.CIFReader;
import uk.co.swlines.cifreader.DatabaseMySQL;
import uk.co.swlines.cifreader.cif.data.CIFAssociation;
import uk.co.swlines.cifreader.cif.data.CIFLocation;
import uk.co.swlines.cifreader.cif.data.CIFLocationArrival;
import uk.co.swlines.cifreader.cif.data.CIFLocationDepart;
import uk.co.swlines.cifreader.cif.data.CIFLocationEnRouteChange;
import uk.co.swlines.cifreader.cif.data.CIFLocationIntermediate;
import uk.co.swlines.cifreader.cif.data.CIFLocationOrigin;
import uk.co.swlines.cifreader.cif.data.CIFSchedule;
import uk.co.swlines.cifreader.cif.data.CIFTiploc;
import uk.co.swlines.cifreader.exceptions.LogicException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class CIFDatabase extends DatabaseMySQL {
	
	public CIFDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, ConfigurationException {
		XMLConfiguration configuration = null;
		
		configuration = new XMLConfiguration(!CIFReader.inDevelopmentEnviroment() ? "cifreader_config.xml" : "src/resources/configuration.xml");		
		
		createConnection(String.format("jdbc:mysql://%s/%s", configuration.getString("database.url"), 
				configuration.getString("database.database")), 
				configuration.getString("database.username"), configuration.getString("database.password"));
	}
	
	public void createTemporaryTables(boolean truncate) throws SQLException {
		Statement query = getConnection().createStatement();

		query.execute("DROP TABLE IF EXISTS associations_old, associations_stpcancel_old, associations_stpcancel_core_old, "
				+ "locations_old, locations_change_old, schedules_old, schedules_stpcancel_old, schedules_stpcancel_core_old, "
				+ "tiplocs_old, schedules_cache_old, tiplocs_cache_old, associations_t, associations_stpcancel_t, "
				+ "associations_stpcancel_core_t, locations_t, locations_change_t, schedules_t, schedules_stpcancel_t, "
				+ "schedules_stpcancel_core_t, tiplocs_t, schedules_cache_t, tiplocs_cache_t");
		
		query.execute("CREATE TABLE associations_t LIKE associations");
		query.execute("CREATE TABLE associations_stpcancel_t LIKE associations_stpcancel");
		query.execute("CREATE TABLE associations_stpcancel_core_t LIKE associations_stpcancel_core");
		query.execute("CREATE TABLE locations_change_t LIKE locations_change");
		query.execute("CREATE TABLE schedules_t LIKE schedules");
		query.execute("CREATE TABLE schedules_stpcancel_t LIKE schedules_stpcancel");
		query.execute("CREATE TABLE schedules_stpcancel_core_t LIKE schedules_stpcancel_core");
		query.execute("CREATE TABLE tiplocs_t LIKE tiplocs");
		query.execute("CREATE TABLE tiplocs_cache_t LIKE tiplocs_cache");
		query.execute("CREATE TABLE schedules_cache_t LIKE schedules_cache");
		
		query.execute("CREATE TABLE locations_t LIKE locations");
		query.execute("ALTER TABLE locations_t DROP INDEX `location_type`, DROP INDEX `tiploc_code`, DROP INDEX `order_time`,DROP INDEX `public_call`, DROP INDEX `actual_call`");
		
		if (!truncate) {			
			query.execute("INSERT INTO locations_t SELECT * FROM locations");
            query.execute("INSERT INTO associations_t SELECT * FROM associations");
            query.execute("INSERT INTO associations_stpcancel_core_t SELECT * FROM associations_stpcancel_core");
            query.execute("INSERT INTO locations_change_t SELECT * FROM locations_change");
            query.execute("INSERT INTO schedules_t SELECT * FROM schedules");
            query.execute("INSERT INTO schedules_stpcancel_core_t SELECT * FROM schedules_stpcancel_core");
            query.execute("INSERT INTO tiplocs_t SELECT * FROM tiplocs");
		}
	}
	
	public void disableKeys() throws SQLException {
		Statement query = getConnection().createStatement();

		query.execute("ALTER TABLE associations_t DISABLE KEYS");
		query.execute("ALTER TABLE associations_stpcancel_core_t DISABLE KEYS");
		query.execute("ALTER TABLE locations_t DISABLE KEYS");
		query.execute("ALTER TABLE locations_change_t DISABLE KEYS");
		query.execute("ALTER TABLE schedules_t DISABLE KEYS");
		query.execute("ALTER TABLE schedules_stpcancel_core_t DISABLE KEYS");
		query.execute("ALTER TABLE tiplocs_t DISABLE KEYS");
	}
	
	public void enableKeys() throws SQLException {
		Statement query = getConnection().createStatement();

		query.execute("ALTER TABLE associations_t ENABLE KEYS");
		query.execute("ALTER TABLE associations_stpcancel_core_t ENABLE KEYS");
		query.execute("ALTER TABLE locations_t ENABLE KEYS");
		query.execute("ALTER TABLE locations_change_t ENABLE KEYS");
		query.execute("ALTER TABLE schedules_t ENABLE KEYS");
		query.execute("ALTER TABLE schedules_stpcancel_core_t ENABLE KEYS");
		query.execute("ALTER TABLE tiplocs_t ENABLE KEYS");
	}
	
	public void finalise() throws SQLException {
		Statement query = getConnection().createStatement();
		
		query.execute("ALTER TABLE locations_t ADD INDEX (`location_type`), ADD INDEX (`tiploc_code`), ADD INDEX (`order_time`), ADD INDEX  (`public_call`), ADD INDEX (`actual_call`)");
		
		query.execute("insert into schedules_stpcancel_t select s.id as id, ssc.cancel_from, ssc.cancel_to, ssc.cancel_mo, ssc.cancel_tu, ssc.cancel_we, ssc.cancel_th, ssc.cancel_fr, ssc.cancel_sa, ssc.cancel_su from schedules_stpcancel_core_t ssc left join schedules_t s on s.train_uid = ssc.train_uid and s.stp_indicator = 'P' and ssc.cancel_to >= s.date_from");
        query.execute("insert into associations_stpcancel_t select a.id as id, asc.cancel_from, asc.cancel_to, asc.cancel_mo, asc.cancel_tu, asc.cancel_we, asc.cancel_th, asc.cancel_fr, asc.cancel_sa, asc.cancel_su from associations_stpcancel_core_t as `asc` left join associations_t a on a.main_train_uid = asc.main_train_uid and a.assoc_train_uid = asc.assoc_train_uid and a.location = asc.location and a.stp_indicator = 'P' and asc.cancel_to >= a.date_from");
        query.execute("INSERT INTO schedules_cache_t SELECT l1.id, l1.tiploc_code as origin, l1.departure as departure, l1.public_departure as public_origin, l2.tiploc_code as destination, l2.arrival as destination_time, l2.public_arrival as public_destination FROM locations_t l1 LEFT JOIN locations_t l2 ON l1.id = l2.id AND l2.location_type = 'LT' WHERE l1.location_type = 'LO'");
        query.execute("INSERT INTO tiplocs_cache_t SELECT s.tiploc, t.nalco, s.description, t.stanox, s.crs, t.description FROM stations s LEFT JOIN tiplocs_t t ON s.tiploc = t.tiploc UNION SELECT la.additional_tiploc, t.nalco, s.description, t.stanox, s.crs, t.description FROM stations s INNER JOIN locations_alternatives la ON s.tiploc = la.tiploc LEFT JOIN tiplocs_t t ON s.tiploc = t.tiploc UNION SELECT t.* FROM tiplocs_t t LEFT JOIN stations s ON t.tiploc = s.tiploc LEFT JOIN locations_alternatives la ON t.tiploc = la.additional_tiploc WHERE la.tiploc IS NULL AND s.tiploc IS NULL ORDER BY tiploc ASC");

        query.execute("RENAME TABLE associations TO associations_old, associations_t TO associations, associations_stpcancel TO associations_stpcancel_old, associations_stpcancel_t TO associations_stpcancel, associations_stpcancel_core TO associations_stpcancel_core_old, associations_stpcancel_core_t TO associations_stpcancel_core, locations_change TO locations_change_old, locations_change_t TO locations_change, schedules TO schedules_old, schedules_t TO schedules, schedules_stpcancel TO schedules_stpcancel_old, schedules_stpcancel_t TO schedules_stpcancel, schedules_stpcancel_core TO schedules_stpcancel_core_old, schedules_stpcancel_core_t TO schedules_stpcancel_core, tiplocs TO tiplocs_old, tiplocs_t TO tiplocs, locations TO locations_old, locations_t TO locations, schedules_cache TO schedules_cache_old, schedules_cache_t TO schedules_cache, tiplocs_cache TO tiplocs_cache_old, tiplocs_cache_t TO tiplocs_cache");
	}
	
	public void deleteTiploc(String tiploc) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM tiplocs_t WHERE tiploc = ?");
			stmt.setString(1, tiploc);
			stmt.execute();
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTiploc(CIFTiploc r) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO tiplocs_t (tiploc, nalco, " +
					"tps_description, stanox, crs, description) VALUES (?, ?, ?, ?, ?, ?);");
						
			stmt.setString(1, r.getTiploc());
			stmt.setString(2, r.getNlc());
			stmt.setString(3, r.getTps_description());
			stmt.setString(4, r.getStanox());
			stmt.setString(5, r.getCrs());
			stmt.setString(6, r.getDescription());
			
			stmt.execute();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAssociationSTPCancellation(CIFAssociation r) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM associations_stpcancel_core_t WHERE " +
					"main_train_uid = ? AND assoc_train_uid = ? AND location = ? AND cancel_from = ?");
			stmt.setString(1, r.getMain_train_uid());
			stmt.setString(2, r.getAssociated_train_uid());
			stmt.setString(3, r.getLocation());
			stmt.setString(4, r.getDate_from());
			
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAssociation(CIFAssociation r) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM associations_t WHERE " +
					"main_train_uid = ? AND assoc_train_uid = ? AND location = ? AND date_from = ? AND stp_indicator = ?");
			
			stmt.setString(1, r.getMain_train_uid());
			stmt.setString(2, r.getAssociated_train_uid());
			stmt.setString(3, r.getLocation());
			stmt.setString(4, r.getDate_from());
			stmt.setString(5, String.valueOf(r.getStp_indicator()));
			
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertAssociationSTPCancellationsBulk(ArrayList<CIFAssociation> associationInsertCancellations) {
		if(associationInsertCancellations.size() == 0) return;
		
		try {
			PreparedStatement stmt = getConnection().prepareStatement(new StringBuilder("INSERT INTO associations_stpcancel_core_t (main_train_uid, " +
					"assoc_train_uid, location, base_location_suffix, assoc_location_suffix, cancel_from, cancel_to, cancel_mo," +
					"cancel_tu, cancel_we, cancel_th, cancel_fr, cancel_sa, cancel_su) VALUES ").append(createInsertPlaceholdersList(associationInsertCancellations.size(), 14)).toString());
			
			int parameterIndex = 1;
			for(CIFAssociation a : associationInsertCancellations) {
				stmt.setString(parameterIndex++, a.getMain_train_uid());
				stmt.setString(parameterIndex++, a.getAssociated_train_uid());
				stmt.setString(parameterIndex++, a.getLocation());
				stmt.setString(parameterIndex++, String.valueOf(a.getLocation_main_suffix()));
				stmt.setString(parameterIndex++, String.valueOf(a.getLocation_associated_suffix()));
				stmt.setString(parameterIndex++, a.getDate_from());
				stmt.setString(parameterIndex++, a.getDate_to());
				
				stmt.setBoolean(parameterIndex++, a.isRuns_mo());
				stmt.setBoolean(parameterIndex++, a.isRuns_tu());
				stmt.setBoolean(parameterIndex++, a.isRuns_we());
				stmt.setBoolean(parameterIndex++, a.isRuns_th());
				stmt.setBoolean(parameterIndex++, a.isRuns_fr());
				stmt.setBoolean(parameterIndex++, a.isRuns_sa());
				stmt.setBoolean(parameterIndex++, a.isRuns_su());
			}
			
			stmt.execute();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertAssociationBulk(ArrayList<CIFAssociation> associationInsert) {
		if(associationInsert.size() == 0) return; 
		
		try {			
			PreparedStatement stmt = getConnection().prepareStatement(new StringBuilder("INSERT INTO associations_t (main_train_uid, assoc_train_uid, " +
					"date_from, date_to, runs_mo, runs_tu, runs_we, runs_th, runs_fr, runs_sa, runs_su, category, date_indicator, " +
					"location, base_location_suffix, assoc_location_suffix, assoc_type, stp_indicator) VALUES ").append(createInsertPlaceholdersList(associationInsert.size(), 18)).toString());
			
			int parameterIndex = 1;
			for(CIFAssociation a : associationInsert) {
				stmt.setString(parameterIndex++, a.getMain_train_uid());
				stmt.setString(parameterIndex++, a.getAssociated_train_uid());
				
				stmt.setString(parameterIndex++, a.getDate_from());
				stmt.setString(parameterIndex++, a.getDate_to());
				stmt.setBoolean(parameterIndex++, a.isRuns_mo());
				stmt.setBoolean(parameterIndex++, a.isRuns_tu());
				stmt.setBoolean(parameterIndex++, a.isRuns_we());
				stmt.setBoolean(parameterIndex++, a.isRuns_th());
				stmt.setBoolean(parameterIndex++, a.isRuns_fr());
				stmt.setBoolean(parameterIndex++, a.isRuns_sa());
				stmt.setBoolean(parameterIndex++, a.isRuns_su());
				
				stmt.setString(parameterIndex++, a.getCategory());
				stmt.setString(parameterIndex++, String.valueOf(a.getDate_indicator()));
				
				stmt.setString(parameterIndex++, a.getLocation());
				stmt.setString(parameterIndex++, String.valueOf(a.getLocation_main_suffix()));
				stmt.setString(parameterIndex++, String.valueOf(a.getLocation_associated_suffix()));
				
				stmt.setString(parameterIndex++, String.valueOf(a.getAssociation_type()));
				stmt.setString(parameterIndex++, String.valueOf(a.getStp_indicator()));
			}
			
			stmt.execute();
			stmt.close();
			
		} catch(SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void deleteScheduleSTPCancellation(CIFSchedule r) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM schedules_stpcancel_core_t WHERE " +
					"train_uid = ? AND cancel_from = ?");
			stmt.setString(1, r.getUid());
			stmt.setString(2, r.getDate_from());
			
			stmt.execute();
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteSchedule(CIFSchedule r) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("DELETE locations_t, schedules_t, locations_change_t "
					+ "FROM schedules_t INNER JOIN locations_t ON schedules_t.id = locations_t.id LEFT JOIN locations_change_t "
					+ "ON schedules_t.id = locations_change_t.schedule_id WHERE train_uid = ? AND date_from = ? AND stp_indicator = ?");
			stmt.setString(1, r.getUid());
			stmt.setString(2, r.getDate_from());
			stmt.setString(3, String.valueOf(r.getStp_indicator()));
			
			stmt.execute();
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void scheduleInsertSTPCancellation(CIFSchedule r) {
		try {
			PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO schedules_stpcancel_core_t (train_uid, cancel_from, " +
					"cancel_to, cancel_mo, cancel_tu, cancel_we, cancel_th, cancel_fr, cancel_sa, cancel_su) VALUES " +
					"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			stmt.setString(1, r.getUid());
			stmt.setString(2, r.getDate_from());
			stmt.setString(3, r.getDate_to());
			stmt.setBoolean(4, r.isRuns_mo());
			stmt.setBoolean(5, r.isRuns_tu());
			stmt.setBoolean(6, r.isRuns_we());
			stmt.setBoolean(7, r.isRuns_th());
			stmt.setBoolean(8, r.isRuns_fr());
			stmt.setBoolean(9, r.isRuns_sa());
			stmt.setBoolean(10, r.isRuns_su());
			
			stmt.execute();
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertScheduleBulk(ArrayList<CIFSchedule> r) throws LogicException {
		if(r.size() == 0) return;
		
		try {			
			final StringBuilder schedulesStatement = new StringBuilder("INSERT INTO schedules_t (train_uid, date_from, date_to," +
					"runs_mo, runs_tu, runs_we, runs_th, runs_fr, runs_sa, runs_su, bank_hol, status, category, train_identity, headcode, " +
					"service_code, portion_id, power_type, timing_load, speed, train_class, sleepers, reservations, catering_code, " +
					"service_branding, stp_indicator, uic_code, atoc_code, ats_code, rsid, bus, train, ship, passenger, " +
					"oc_b, oc_c, oc_d, oc_e, oc_g, oc_m, oc_p, oc_q, oc_r, oc_s, oc_y, oc_z) VALUES ");
			
			final StringBuilder locationsStatement = new StringBuilder("INSERT INTO locations_t (id, location_order, location_type, " +
					"tiploc_code, tiploc_instance, arrival, arrival_aftermidnight, public_arrival, public_arrival_aftermidnight, " +
					"pass, pass_aftermidnight, departure, departure_aftermidnight, public_departure, public_departure_aftermidnight, " +
					"platform, line, path, engineering_allowance, pathing_allowance, performance_allowance, public_call, " +
					"actual_call, order_time, public_time, act_a, act_ae, act_bl, act_c, act_d, act_minusd, act_e, act_g, act_h, act_hh, act_k, act_kc, " +
					"act_ke, act_kf, act_ks, act_l, act_n, act_op, act_or, act_pr, act_r, act_rm, act_rr, act_s, act_t, act_minust, act_tb, " +
					"act_tf, act_ts, act_tw, act_u, act_minusu, act_w, act_x) VALUES ");
			
			final StringBuilder locationsChange = new StringBuilder("INSERT INTO locations_change_t (schedule_id, location_id, category, "
					+ "train_identity, headcode, service_code, portion_id, power_type, timing_load, speed, train_class, sleepers, "
					+ "reservations, catering_code, service_branding, uic_code, rsid, oc_b, oc_c, oc_d, oc_e, oc_g, oc_m, oc_p, "
					+ "oc_q, oc_r, oc_s, oc_y, oc_z) VALUES "+ createInsertPlaceholders(29));
			
			PreparedStatement stmtSchedules = getConnection().prepareStatement(schedulesStatement.append(createInsertPlaceholders(46)).
					toString(), Statement.RETURN_GENERATED_KEYS);
			
			PreparedStatement stmtLocChange = getConnection().prepareStatement(locationsChange.toString());
			
			int locationTotal = 0;
			
			for(CIFSchedule schedule : r) {
				int parameterIndex = 1;
						
				stmtSchedules.clearParameters();
				
				stmtSchedules.setString(parameterIndex++, schedule.getUid());
				
				stmtSchedules.setString(parameterIndex++, schedule.getDate_from());
				stmtSchedules.setString(parameterIndex++, schedule.getDate_to());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_mo());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_tu());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_we());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_th());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_fr());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_sa());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isRuns_su());
				
				if(schedule.getBank_holiday() != ' ')
					stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getBank_holiday()));
				else
					stmtSchedules.setNull(parameterIndex++, java.sql.Types.CHAR);
				
				stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getStatus()).trim());
				stmtSchedules.setString(parameterIndex++, schedule.getCategory());
				stmtSchedules.setString(parameterIndex++, schedule.getTrain_identity());
				stmtSchedules.setString(parameterIndex++, schedule.getHeadcode());
				
				if (schedule.getService_code() != null) 
					stmtSchedules.setInt(parameterIndex++, schedule.getService_code());
				else
					stmtSchedules.setNull(parameterIndex++, java.sql.Types.INTEGER);
				
				if(schedule.getPortion_id() != ' ')
					stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getPortion_id()));
				else
					stmtSchedules.setNull(parameterIndex++, java.sql.Types.CHAR);
				
				stmtSchedules.setString(parameterIndex++, schedule.getPower_type());
				stmtSchedules.setString(parameterIndex++, schedule.getTiming_load());
				stmtSchedules.setString(parameterIndex++, schedule.getSpeed());
				
				if(schedule.getTrain_class() != ' ')
					stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getTrain_class()));
				else
					stmtSchedules.setNull(parameterIndex++, java.sql.Types.CHAR);
				
				if(schedule.getSleepers() != ' ')
					stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getSleepers()));
				else
					stmtSchedules.setNull(parameterIndex++, java.sql.Types.CHAR);
				
				if(schedule.getReservations() != ' ')
					stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getReservations()));
				else
					stmtSchedules.setNull(parameterIndex++, java.sql.Types.CHAR);
				
				stmtSchedules.setString(parameterIndex++, schedule.getCatering_code());
				stmtSchedules.setString(parameterIndex++, schedule.getService_branding());
				stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getStp_indicator()).trim());
				stmtSchedules.setString(parameterIndex++, schedule.getUic_code());
				stmtSchedules.setString(parameterIndex++, schedule.getAtoc_code());
				stmtSchedules.setString(parameterIndex++, String.valueOf(schedule.getAts()).trim());
				stmtSchedules.setString(parameterIndex++, schedule.getRsid());
				
				stmtSchedules.setBoolean(parameterIndex++, schedule.isBus());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isTrain());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isShip());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isPassenger());
				
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_b());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_c());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_d());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_e());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_g());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_m());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_p());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_q());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_r());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_s());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_y());
				stmtSchedules.setBoolean(parameterIndex++, schedule.isOc_z());
				
				stmtSchedules.execute();
				
				locationTotal += schedule.getLocations().size();
				
				ResultSet rs = stmtSchedules.getGeneratedKeys();
				
				while(rs.next()) {
					schedule.setDatabaseId(rs.getInt(1));
				}
				
				rs.close();
			}			
			
			stmtSchedules.close();

			
			int parameterIndex = 1;
			PreparedStatement stmtLocations = getConnection().prepareStatement(locationsStatement.append(createInsertPlaceholdersList(locationTotal, 59)).
					toString());
			
			for(CIFSchedule schedule : r) {
				int locationOrder = -1;
				Integer originDeparture = null, originPublicDeparture = null;
				
				CIFLocation origin = schedule.getLocations().get(0);
				if(origin instanceof CIFLocationOrigin) {
					originDeparture = ((CIFLocationOrigin) origin).getDeparture();
					originPublicDeparture = ((CIFLocationOrigin) origin).getPublic_departure();
				}
				
				if(originDeparture == null) throw new LogicException(schedule);
				
				for(CIFLocation location : schedule.getLocations()) {
					stmtLocations.setInt(parameterIndex++, schedule.getDatabaseId());
					stmtLocations.setInt(parameterIndex++, ++locationOrder);
					stmtLocations.setString(parameterIndex++, location.getLocationType());
					stmtLocations.setString(parameterIndex++, location.getTiploc());
					stmtLocations.setString(parameterIndex++, String.valueOf(location.getTiploc_instance()).trim());
					
					Integer orderTime = null;
					Integer publicTime = null;
					
					if(location instanceof CIFLocationArrival && ((CIFLocationArrival) location).getArrival() != null) {
						CIFLocationArrival locationArrival = (CIFLocationArrival) location;
						
						stmtLocations.setInt(parameterIndex++, locationArrival.getArrival());
						stmtLocations.setBoolean(parameterIndex++, locationArrival.getArrival() < originDeparture);
						
						orderTime = locationArrival.getArrival();
						
						if(locationArrival.getPublic_arrival() != null) {
							stmtLocations.setInt(parameterIndex++, locationArrival.getPublic_arrival());
							stmtLocations.setBoolean(parameterIndex++, originPublicDeparture != null ? locationArrival.getPublic_arrival() < originPublicDeparture : null);
							
							publicTime = locationArrival.getPublic_arrival();
						}
						else {
							stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
							stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						}
					}
					else {
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
					}
					
					if(location instanceof CIFLocationIntermediate && ((CIFLocationIntermediate) location).getPass() != null) {
						CIFLocationIntermediate locationIntermediate = (CIFLocationIntermediate) location;
						
						stmtLocations.setInt(parameterIndex++, locationIntermediate.getPass());
						stmtLocations.setBoolean(parameterIndex++, locationIntermediate.getPass() < originDeparture);
						
						orderTime = locationIntermediate.getPass();
					}
					else {
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
					}
					
					if(location instanceof CIFLocationDepart && ((CIFLocationDepart) location).getDeparture() != null) {
						CIFLocationDepart locationDeparture = (CIFLocationDepart) location;
						
						stmtLocations.setInt(parameterIndex++, locationDeparture.getDeparture());
						stmtLocations.setBoolean(parameterIndex++, locationDeparture.getDeparture() < originDeparture);
						
						orderTime = locationDeparture.getDeparture();
						
						if(locationDeparture.getPublic_departure() != null) {
							stmtLocations.setInt(parameterIndex++, locationDeparture.getPublic_departure());
							stmtLocations.setBoolean(parameterIndex++, originPublicDeparture != null ? locationDeparture.getPublic_departure() < originPublicDeparture : null);
							
							publicTime = locationDeparture.getPublic_departure();
						}
						else {
							stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
							stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						}
					}
					else {
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
					}
					
					if(location.getPlatform().length() != 0)
						stmtLocations.setString(parameterIndex++, location.getPlatform());
					else 
						stmtLocations.setNull(parameterIndex++, java.sql.Types.VARCHAR);
					
					if(location instanceof CIFLocationDepart && ((CIFLocationDepart) location).getLine().length() != 0) 
						stmtLocations.setString(parameterIndex++, ((CIFLocationDepart) location).getLine());
					else
						stmtLocations.setNull(parameterIndex++, java.sql.Types.VARCHAR);

					if(location instanceof CIFLocationArrival && ((CIFLocationArrival) location).getPath().length() != 0) 
						stmtLocations.setString(parameterIndex++, ((CIFLocationArrival) location).getPath());
					else
						stmtLocations.setNull(parameterIndex++, java.sql.Types.VARCHAR);
					
					if(location instanceof CIFLocationDepart) {
						CIFLocationDepart locationDeparture = (CIFLocationDepart) location;
						
						stmtLocations.setInt(parameterIndex++, locationDeparture.getAllowance_engineering());
						stmtLocations.setInt(parameterIndex++, locationDeparture.getAllowance_pathing());
						stmtLocations.setInt(parameterIndex++, locationDeparture.getAllowance_performance());
					}
					else {
						stmtLocations.setInt(parameterIndex++, 0);
						stmtLocations.setInt(parameterIndex++, 0);
						stmtLocations.setInt(parameterIndex++, 0);
					}
					
					stmtLocations.setBoolean(parameterIndex++, location.isPublic_call());
					stmtLocations.setBoolean(parameterIndex++, location.isActual_call());
					
//					stmtLocations.setInt(parameterIndex++, location.isActual_call() ? ( ? : ) : ((CIFLocationIntermediate) location).getPass());
					stmtLocations.setInt(parameterIndex++, orderTime);
					if(publicTime != null)
						stmtLocations.setInt(parameterIndex++, publicTime);
					else
						stmtLocations.setNull(parameterIndex++, java.sql.Types.INTEGER);
					
					stmtLocations.setBoolean(parameterIndex++, location.isAc_a());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_ae());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_bl());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_c());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_d());
					stmtLocations.setBoolean(parameterIndex++, location.isAc__d());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_e());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_g());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_h());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_hh());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_k());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_kc());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_ke());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_kf());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_ks());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_l());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_n());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_op());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_or());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_pr());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_r());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_rm());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_rr());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_s());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_t());
					stmtLocations.setBoolean(parameterIndex++, location.isAc__t());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_tb());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_tf());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_ts());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_tw());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_u());
					stmtLocations.setBoolean(parameterIndex++, location.isAc__u());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_w());
					stmtLocations.setBoolean(parameterIndex++, location.isAc_x());
					
					if(location instanceof CIFLocationIntermediate) {
						CIFLocationEnRouteChange change = ((CIFLocationIntermediate) location).getChangeRecord();
						
						if(change != null) {
							stmtLocChange.setInt(1, schedule.getDatabaseId());
							stmtLocChange.setInt(2, locationOrder);
							stmtLocChange.setString(3, change.getCategory());
							stmtLocChange.setString(4, change.getTrain_identity());
							stmtLocChange.setString(5, change.getHeadcode());
							stmtLocChange.setString(6, change.getService_code());
							
							if(change.getPortion_id() != ' ')
								stmtLocChange.setString(7, String.valueOf(change.getPortion_id()));
							else
								stmtLocChange.setNull(7, java.sql.Types.CHAR);
							
							stmtLocChange.setString(8, change.getPower_type());
							stmtLocChange.setString(9, change.getTiming_load());
							stmtLocChange.setString(10, change.getSpeed());
							
							if(change.getTrain_class() != ' ')
								stmtLocChange.setString(11, String.valueOf(change.getTrain_class()));
							else
								stmtLocChange.setNull(11, java.sql.Types.CHAR);
							
							if(change.getSleeper() != ' ')
								stmtLocChange.setString(12, String.valueOf(change.getSleeper()));
							else
								stmtLocChange.setNull(12, java.sql.Types.CHAR);
							
							if(change.getReservations() != ' ')
								stmtLocChange.setString(13, String.valueOf(change.getReservations()));
							else
								stmtLocChange.setNull(13, java.sql.Types.CHAR);
							
							stmtLocChange.setString(14, change.getCatering_code());
							stmtLocChange.setString(15, change.getService_branding());
							stmtLocChange.setString(16, change.getUic());
							stmtLocChange.setString(17, change.getRsid());
							
							stmtLocChange.setBoolean(18, change.isOc_b());
							stmtLocChange.setBoolean(19, change.isOc_c());
							stmtLocChange.setBoolean(20, change.isOc_d());
							stmtLocChange.setBoolean(21, change.isOc_e());
							stmtLocChange.setBoolean(22, change.isOc_g());
							stmtLocChange.setBoolean(23, change.isOc_m());
							stmtLocChange.setBoolean(24, change.isOc_p());
							stmtLocChange.setBoolean(25, change.isOc_q());
							stmtLocChange.setBoolean(26, change.isOc_r());
							stmtLocChange.setBoolean(27, change.isOc_s());
							stmtLocChange.setBoolean(28, change.isOc_y());
							stmtLocChange.setBoolean(29, change.isOc_z());
							
							stmtLocChange.addBatch();
						}
					}
				}
			}
			
			stmtLocChange.executeBatch();
			stmtLocations.execute();
			stmtLocations.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void removeOld(String date) {
		try {
			PreparedStatement stmtSchedules = getConnection().prepareStatement("DELETE schedules_t, locations_t, locations_change_t "
					+ "FROM schedules_t INNER JOIN locations_t ON schedules_t.id = locations_t.id LEFT JOIN locations_change_t ON "
					+ "schedules_t.id = locations_change_t.schedule_id WHERE schedules_t.date_to < ?");
			
			PreparedStatement stmtAssociations = getConnection().prepareStatement("DELETE FROM associations_t WHERE date_to < ?");
			PreparedStatement stmtAssocStpCan = getConnection().prepareStatement("DELETE FROM associations_stpcancel_core_t WHERE cancel_to < ?");
			PreparedStatement stmtSchedStpCan = getConnection().prepareStatement("DELETE FROM schedules_stpcancel_core_t WHERE cancel_to < ?");
			
			stmtSchedules.setString(1, date);
			stmtAssociations.setString(1, date);
			stmtAssocStpCan.setString(1, date);
			stmtSchedStpCan.setString(1, date);
			
			stmtSchedules.execute();
			stmtAssociations.execute();
			stmtAssocStpCan.execute();
			stmtSchedStpCan.execute();
			
			stmtSchedules.close();
			stmtAssociations.close();
			stmtAssocStpCan.close();
			stmtSchedStpCan.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private static String createInsertPlaceholders(int placeholders) {
		final StringBuilder builder = new StringBuilder("(");
		
		for(int i = 0; i < placeholders; i++) {
			if(i != 0) {
				builder.append(",");
			}
			
			builder.append("?");
		}
		
		return builder.append(")").toString();
	}
	
	private static String createInsertPlaceholdersList(final int number, final int placeholders) {
		final String placeholdersString = createInsertPlaceholders(placeholders);
		
		final StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < number; i++) {
			if(i != 0) stringBuilder.append(",");
			stringBuilder.append(placeholdersString);
		}
		
		return stringBuilder.toString();
	}
	
}
