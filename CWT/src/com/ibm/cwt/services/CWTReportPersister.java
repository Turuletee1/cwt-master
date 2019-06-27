package com.ibm.cwt.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.ibm.cwt.dto.ReportRowDTO;
import com.ibm.cwt.exceptions.CWTReportSaveException;

public class CWTReportPersister {
	private static final Logger LOGGER = Logger.getLogger(CWTReportPersister.class.getName());

	public void saveToFile(Collection<ReportRowDTO> missingPersons) throws CWTReportSaveException {
		Date now = new Date();
		String rowString;
		try {
			String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(now)+".csv";
			LOGGER.finer("Saving report to: ./"+fileName);
			FileOutputStream fos = new FileOutputStream(fileName);
			PrintWriter pw = new PrintWriter(fos);
			pw.println("sep=,");
			for(ReportRowDTO row : missingPersons) {
				rowString = row.getCc()+","+row.getDept()+","+row.getSerial()+","+row.getStatus()+","+row.getName()+","+row.getWeek()+","+row.getEmail(); 
				LOGGER.finest(rowString);
				pw.println(rowString);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			LOGGER.warning("Report file cannot be created due to: "+ e.getMessage());
			throw new CWTReportSaveException();
		}		
	}
}
