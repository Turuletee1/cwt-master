package com.ibm.cwt.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

import com.ibm.cwt.dto.ReportRowDTO;
import com.ibm.cwt.exceptions.CWTDAOException;
import com.ibm.cwt.exceptions.CWTInvalidCredentialsException;
import com.ibm.cwt.utils.DBConnector;
	
public class ILCDAO {
	private final static Logger LOGGER = Logger.getLogger(ILCDAO.class.getName());
	private final static String file = "query.properties"; 
	private static Properties props;
	private Connection conn;

	static {
		try {
			InputStream fis = ILCDAO.class.getClassLoader().getResourceAsStream(file);
			props = new Properties();
			props.load(fis);
			LOGGER.config(props.toString());
		} catch (IOException e) {
			LOGGER.severe("ERROR: '"+file + "' cannot be opened to get query. Reports are not available");
			LOGGER.severe(e.getMessage());
		} 
	}
	
	public Collection<ReportRowDTO> getMissingPersons(String user, char[] pwd) throws CWTDAOException, CWTInvalidCredentialsException {
		assert props!=null;
		Collection<ReportRowDTO> missingPersons = new ArrayList<ReportRowDTO>();
		
		try {
			conn = DBConnector.getInstance().connect(user, pwd);
		} catch (SQLException e) {
			throw new CWTInvalidCredentialsException();
		}
		
		try {
			PreparedStatement ps = conn.prepareStatement(props.getProperty("query"));
					
			LOGGER.fine("About to execute query");
			ResultSet rs = ps.executeQuery();
			LOGGER.fine("Processing data returned form DB");
			while (rs.next()) {
				ReportRowDTO rowData = new ReportRowDTO();
				
				rowData.setCc(rs.getString(1).trim());
				rowData.setDept(rs.getString(2).trim());
				rowData.setSerial(rs.getString(3).trim());
				rowData.setStatus(rs.getString(4).trim());
				rowData.setName(rs.getString(5).trim());
				rowData.setWeek(rs.getString(6).trim());
				rowData.setEmail(rs.getString(7).trim());
				LOGGER.finest(rowData.toString());
				missingPersons.add(rowData);
			}
		} catch (SQLException e) {
			LOGGER.severe("ERROR while executing report query: "+ e.getMessage());
			throw new CWTDAOException();
		} finally {
			LOGGER.finer("Closing JDBC connection");
			DBConnector.getInstance().Disconnect(conn);
		}
		
		return missingPersons;
	}
	

}
