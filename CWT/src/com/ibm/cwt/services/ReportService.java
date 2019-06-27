package com.ibm.cwt.services;

import java.util.Collection;
import java.util.logging.Logger;

import com.ibm.cwt.dao.ILCDAO;
import com.ibm.cwt.dto.ReportRowDTO;
import com.ibm.cwt.exceptions.CWTDAOException;
import com.ibm.cwt.exceptions.CWTEmailException;
import com.ibm.cwt.exceptions.CWTInvalidCredentialsException;
import com.ibm.cwt.exceptions.CWTReportSaveException;

public class ReportService {
	private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
	
	public void runReport(String user, char[] pwd) throws CWTDAOException, CWTReportSaveException, CWTEmailException, CWTInvalidCredentialsException {
		LOGGER.fine("Retrieving missing persons");
		Collection<ReportRowDTO> missingPersons = new ILCDAO().getMissingPersons(user, pwd);
		
		LOGGER.fine("Retrieving manager information from EnterpriseDirectory");
		new BluePagesService().getManagerInformation(missingPersons);
		
		LOGGER.fine("Sending notifications");
		new CWTMailer().sendMissingNotifications(missingPersons);
		
		LOGGER.fine("Saving report to disk");
		new CWTReportPersister().saveToFile(missingPersons);
	}

}
