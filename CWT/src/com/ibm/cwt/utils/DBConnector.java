/*
 *IBM Confidential
 *
 *© Copyright IBM Corp. 2009  All Rights Reserved.
 *
 *The source code for this program is not published or otherwise divested of
 *its trade secrets, irrespective of what has been deposited with the U.S. Copyright office.
 *
 */
package com.ibm.cwt.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * @author kuperman@ar.ibm.com
 *
 */
public class DBConnector {
	private static final Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
	private static final String file = "connection.properties";
	private static final Properties props = new Properties();
	private static DBConnector dbc;

	private DBConnector(){
		try {
			props.load(DBConnector.class.getClassLoader().getResourceAsStream(file));
			LOGGER.config(props.toString());
		} catch (IOException e) {
			LOGGER.severe("ERROR reading '" + file + "' file to get connection information.");
		} 
	}

	public static DBConnector getInstance(){
		if(dbc==null){
			dbc=new DBConnector();
		}
		return dbc;
	}


	public Connection connect(String user, char[] pwd) throws SQLException {
		try {
			LOGGER.finer("Opening JDBC connection as " + user);
			Connection con = DriverManager.getConnection(props.getProperty("url"), user, String.valueOf(pwd)); //TODO security risk leaving pwd in a string in JVM's memory
			Arrays.fill(pwd, '\u0000');
			return con;
		} catch (SQLException e1) {
			LOGGER.severe("ERROR opening connection: " + e1.getMessage());
			throw e1;
		}
	}

	

	/**
	 * Used to free the connection logs any problem disconecting to the database
	 * @param con
	 */
	public void Disconnect(Connection con) {
		try {
			if (con!= null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			LOGGER.warning("ERROR closing connection: " + e.getMessage());
		}
	}


}
