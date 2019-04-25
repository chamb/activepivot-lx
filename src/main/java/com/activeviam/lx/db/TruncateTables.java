/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Truncate all database tables
 * 
 * @author ActiveViam
 *
 */
public class TruncateTables {
	
	/** Logger */
	static final Logger LOG = Logger.getLogger(TruncateTables.class.getName());
	
	public static void main(String[] args) throws Exception {

		DatabaseConnection connection = new DatabaseConnection();

	    try(Connection conn = connection.getConnection();
	    	Statement stmt = conn.createStatement()) {

		    stmt.executeUpdate("TRUNCATE TABLE Products");
			LOG.info("Table 'Products' truncated successfully.");
		    stmt.executeUpdate("TRUNCATE TABLE Trades");
			LOG.info("Table 'Trades' truncated successfully.");
		    stmt.executeUpdate("TRUNCATE TABLE Risks");
			LOG.info("Table 'Risks' truncated successfully.");
		    
	     } catch(SQLException se) { 
	        //Handle errors for JDBC 
	        se.printStackTrace(); 
	     }

	}

}
