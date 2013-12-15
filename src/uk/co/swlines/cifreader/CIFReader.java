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

package uk.co.swlines.cifreader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.co.swlines.cifreader.cif.CIFFileException;
import uk.co.swlines.cifreader.cif.CIFProcessor;
import uk.co.swlines.cifreader.cif.data.CIFSchedule;

public class CIFReader {
	
	public static void main(String[] args) {
		//CIFSchedule schedule = new CIFSchedule("BSNY363901302101302100000001 PXX1Y48    121865000 DMUE   090      S            O");
		//schedule.parseBXRecord("BX         NTY                                                                  ");
		
		CIFProcessor processor = new CIFProcessor();
		try {
			processor.readFile("src/resources/CFTCRSY.CIF");
		} catch (CIFFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	
	
	}
}
