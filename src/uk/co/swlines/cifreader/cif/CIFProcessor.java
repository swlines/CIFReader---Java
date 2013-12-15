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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;

import uk.co.swlines.cifreader.cif.data.*;
import uk.co.swlines.cifreader.exceptions.LogicException;

public class CIFProcessor {
	
	private CIFSchedule schedule = null;
	private CIFDatabase database = null;
	
	private ArrayList<CIFSchedule> schedulesInsert = null;
	private ArrayList<CIFSchedule> schedulesDelete = null;
	
	private ArrayList<CIFAssociation> associationsInsert = null;
	private ArrayList<CIFAssociation> associationsDelete = null;
	
	public CIFProcessor() {
		try {
			database = new CIFDatabase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			database.createTemporaryTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			database.disableKeys();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readFile(String filepath) throws CIFFileException {
		BufferedReader in = null;
		
		File file = new File(filepath);
		int fileSize = (int) (file.length() / (1024 * 1000));
		
		try {
			in = new BufferedReader(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			// do something
			e.printStackTrace();
			return;
		}
		
		String line = null;
		CIFHeader header = null;
		String recordType = null;
		
		schedulesInsert = new ArrayList<CIFSchedule>();
		schedulesDelete = new ArrayList<CIFSchedule>();
		
		associationsInsert = new ArrayList<CIFAssociation>();
		associationsDelete = new ArrayList<CIFAssociation>();
		
		try {
			// get the header
			line = in.readLine();
			if(line == null || !line.substring(0, 2).equals("HD")) throw new CIFFileException();
			else header = new CIFHeader(line);
			
			System.out.println("===========================================================\n" +
					"Network Rail CIF: "+ filepath + "\n" +
					"Importing " + header.getFile_ref() + " created for " + header.getUser_identity() +"\n" +
					"Generated on "+ header.getExtract_date() + " at "+ header.getExtract_time() +"\n" +
					"Data window is "+ header.getUser_extract_start_date() + " to " + header.getUser_extract_end_date() +"\n" +
					"File is "+ ((header.isFullExtract()) ? "FULL EXTRACT" : "UPDATE") +", size: " + fileSize + "MiB\n" +
							"===========================================================\n");
			
			CIFLocationEnRouteChange location_change = null;
			
			// ok, we're good to continue.
			// just create some stuff in case we need it later
			schedule = null; 
			
			while((line = in.readLine()) != null) {
				recordType = line.substring(0, 2);
				
				if(recordType.equals("TI") || recordType.equals("TA")) {
					if(recordType.equals("TA")) {
						database.deleteTiploc(line.substring(2, 9));
					}
					
					database.insertTiploc(new CIFTiploc(line));
				}
				else if(recordType.equals("TD")) {
					database.deleteTiploc(line.substring(2, 9));
				}
				else if(recordType.equals("AA")) {
					char transactionType = line.charAt(2);
					CIFAssociation association = new CIFAssociation(line);
					
					// perform correct behaviour
					if(transactionType == 'D') {
						// delete the schedule
						queueForDeletion(association);
						
						continue;
					}
					else if(transactionType == 'R') {
						// delete the old schedule
						queueForDeletion(association);
					}
					
					queueForInsertion(association);
				}
				else if(recordType.equals("BS")) {					
					char transactionType = line.charAt(2);
					
					schedule = new CIFSchedule(line);
					
					// perform correct behaviour
					if(transactionType == 'D') {
						// delete the schedule
						queueForDeletion(schedule);
						
						schedule = null;
						continue;
					}
					else if(transactionType == 'R') {
						// delete the old schedule
						queueForDeletion(schedule);
					}
					
					if(schedule.getStp_indicator() != 'C') {
						// move forward and grab the BX
						line = in.readLine();
						
						schedule.parseBXRecord(line);
					}
					
					queueForInsertion(schedule);
				}
				else if(recordType.equals("LO")) {
					schedule.addLocation(new CIFLocationOrigin(line));
				}
				else if(recordType.equals("LI")) {
					if(location_change == null) {
						schedule.addLocation(new CIFLocationIntermediate(line));
					} else {
						schedule.addLocation(new CIFLocationIntermediate(line, location_change));
						location_change = null;
					}
				}
				else if(recordType.equals("LT")) {
					schedule.addLocation(new CIFLocationTerminus(line));
					schedule = null;
				}
				else if(recordType.equals("CR")) {
					location_change = new CIFLocationEnRouteChange(line);
				}
				else if(recordType.equals("ZZ")) break; // complete
				else throw new CIFFileException();
				
				if(schedule == null) {
					if(schedulesInsert.size() > 300)
						processSchedules();
						
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processAssociations() {
		for(CIFAssociation a : associationsDelete) {
			if(a.getStp_indicator() == 'C') {
				database.deleteAssociationSTPCancellation(a);
			}
			else {
				database.deleteAssociation(a);
			}
		}
		
		associationsDelete.clear();
		
		ArrayList<CIFAssociation> associationInsertCancellations = new ArrayList<CIFAssociation>();
		ArrayList<CIFAssociation> associationInsertNormal = new ArrayList<CIFAssociation>();
		
		for(CIFAssociation a : associationsInsert) {
			if(a.getStp_indicator() == 'C') {
				associationInsertCancellations.add(a);
			}
			else {
				associationInsertNormal.add(a);
			}
		}
		
		database.insertAssociationSTPCancellationsBulk(associationInsertCancellations);
		database.insertAssociationBulk(associationInsertNormal);
		
		associationsInsert.clear();
	}
	
	private void processSchedules() {
		for(CIFSchedule s : schedulesDelete) {
			if(s.getStp_indicator() == 'C') {
				database.deleteScheduleSTPCancellation(s);
			}
			else {
				database.deleteSchedule(s);
			}
		}
		
		schedulesDelete.clear();
		
		ArrayList<CIFSchedule> scheduleInsertNormal = new ArrayList<CIFSchedule>();
		
		for(CIFSchedule s : schedulesInsert) {
			if(s.getStp_indicator() == 'C') {
				database.scheduleInsertSTPCancellation(s);
			}
			else {
				scheduleInsertNormal.add(s);
			}
		}
		
		try {
			database.insertScheduleBulk(scheduleInsertNormal);
		} catch (LogicException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		schedulesInsert.clear();
	}
			
	private void queueForDeletion(CIFSchedule schedule) {
		schedulesDelete.add(schedule);
	}
	
	private void queueForInsertion(CIFSchedule schedule) {
		schedulesInsert.add(schedule);
	}
	
	private void queueForDeletion(CIFAssociation association) {
		associationsDelete.add(association);
	}
	
	private void queueForInsertion(CIFAssociation association) {
		associationsInsert.add(association);
	}
	
	
}
