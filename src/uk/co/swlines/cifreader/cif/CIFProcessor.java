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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import uk.co.swlines.cifreader.cif.data.*;

public class CIFProcessor {
	
	public void readFile() throws CIFFileException {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader("TEST.CIF"));
		} catch (FileNotFoundException e) {
			// do something
			e.printStackTrace();
			return;
		}
		
		String line = null;
		CIFHeader header = null;
		String recordType = null;
		
		try {
			// get the header
			line = in.readLine();
			if(line == null || !line.substring(0, 2).equals("HD")) throw new CIFFileException();
			else header = new CIFHeader(line);
			
			// ok, we're good to continue.
			// just create some stuff in case we need it later
			CIFSchedule schedule = null; 
			
			
			while((line = in.readLine()) != null) {
				recordType = line.substring(0, 2);
				
				if(recordType.equals("TI") || recordType.equals("TA")) {
					
				}
				else if(recordType.equals("TD")) {
					
				}
				else if(recordType.equals("AA")) {
					
				}
				else if(recordType.equals("BS")) {
					schedule = new CIFSchedule(line);
				}
				else if(recordType.equals("BX")) {
					
				}
				else if(recordType.equals("LO")) {
					
				}
				else if(recordType.equals("LI")) {
					
				}
				else if(recordType.equals("LT")) {
					
				}
				else if(recordType.equals("CR")) {
					
				}
				else if(recordType.equals("TN")) {
					// not used in CIF
				}
				else if(recordType.equals("LN")) {
					// not used in CIF
				}
				else if(recordType.equals("ZZ")) break; // complete
				else throw new CIFFileException();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
