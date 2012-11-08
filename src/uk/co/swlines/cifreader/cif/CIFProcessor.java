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
