package com.ibm.cwt.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.cwt.dao.EDDAO;
import com.ibm.cwt.dto.ReportRowDTO;

public class BluePagesService {
	private static final Logger LOGGER = Logger.getLogger(BluePagesService.class.getName());

	public void getManagerInformation(Collection<ReportRowDTO> missingPersons) {
		Set<String> uids = new HashSet<String>();
		for(ReportRowDTO dto : missingPersons){
			uids.add(dto.getUID());
		}
		LOGGER.finer("Searching managers...");
		Map<String, String> managersInfo = new EDDAO().searchManagerEmails(uids);
		for(ReportRowDTO dto : missingPersons){
			dto.setManagerEmail(managersInfo.get(dto.getUID()));
		}
		LOGGER.finer("Done searching managers.");
	}

}
