/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Create and populate a database.
 * 
 * @author ActiveViam
 *
 */
public class DatabaseConnection {

	// JDBC driver
	public static final String JDBC_DRIVER = "com.leanxcale.jdbc.ElasticDriver";
	
	// Connection string
	public static final String DB_URL = "jdbc:leanxcale:direct://localhost:1529/db;create=true";
	   
	//  Database credentials 
	public static final String USER = "app"; 
	public static final String PASS = "app";
	
	public DatabaseConnection() { 
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find JDBC driver class: " + JDBC_DRIVER);
		}
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL,USER,PASS);
	}
	
	public static void main(String[] args) throws Exception {

		DatabaseConnection connection = new DatabaseConnection();

	    try(Connection conn = connection.getConnection();
	    	Statement stmt = conn.createStatement()) {
	    	
	    	//...
	    }

	}

}
