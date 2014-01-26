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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.net.URL;
import java.util.Arrays;

import uk.co.swlines.cifreader.cif.CIFFileException;
import uk.co.swlines.cifreader.cif.CIFProcessor;
import uk.co.swlines.cifreader.cif.data.CIFSchedule;

public class CIFReader {
	
	public static void main(String[] args) {
		System.out.println("CIF Reader - Copyright 2013 Tom Cairns\n"
				+ "This program comes with ABSOLUTELY NO WARRANTY. This is free software and you are "
				+ "welcome to redistribute it under certain conditions, see LICENCE.");
		
		CIFProcessor processor = new CIFProcessor();
		
		for(String arg : args) {
			File file = new File(arg);
			
			try {
				if(file.exists()) {
					if(file.isDirectory()) {
						File[] fileList = file.listFiles();
						Arrays.sort(fileList);
						
						for(File f : fileList) {
							processor.processFile(f.getCanonicalPath());
						}
					}
					else if(file.isFile()) {
						processor.processFile(file.getCanonicalPath());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CIFFileException e) {
				e.printStackTrace();
			}
		}
		
		processor.process();
	}
	
	public static boolean inDevelopmentEnviroment() {
		String inEclipse = System.getProperty("ineclipse");
		
		return inEclipse != null && inEclipse.equals("1");
	}
}
