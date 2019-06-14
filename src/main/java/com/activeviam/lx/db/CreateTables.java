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
 * Create and populate a database.
 * 
 * @author ActiveViam
 *
 */
public class CreateTables {

	/** Logger */
	static final Logger LOG = Logger.getLogger(CreateTables.class.getName());
	
	public static void main(String[] args) throws Exception {

		DatabaseConnection connection = new DatabaseConnection();
	    try(Connection conn = connection.getConnection()) {
	    	try(Statement stmt = conn.createStatement()) {

				stmt.executeUpdate("CREATE TABLE PRODUCTS (\n" + 
						"  Id BIGINT not NULL,\n" + 
						"  ProductName VARCHAR(255),\n" + 
						"  ProductType VARCHAR(255), \n" + 
						"  UnderlierCode VARCHAR(255),\n" + 
						"  UnderlierCurrency VARCHAR(255),\n" + 
						"  UnderlierType VARCHAR(255),\n" + 
						"  UnderlierValue DOUBLE,\n" + 
						"  ProductBaseMtm DOUBLE,\n" + 
						"  BumpedMtmUp DOUBLE,\n" + 
						"  BumpedMtmDown DOUBLE,\n" + 
						"  Theta DOUBLE,\n" + 
						"  Rho DOUBLE,\n" + 
						"  PRIMARY KEY ( Id )\n" + 
						")"
				);
				LOG.info("Table 'PRODUCTS' created successfully.");
				
				stmt.executeUpdate("CREATE TABLE TRADES (\n" + 
						"  Id BIGINT not NULL,\n" + 
						"  ProductId BIGINT,\n" + 
						"  ProductQtyMultiplier DOUBLE,\n" + 
						"  Desk VARCHAR(255),\n" + 
						"  Book INTEGER,\n" + 
						"  Trader VARCHAR(255),\n" + 
						"  Counterparty VARCHAR(255),\n" + 
						"  Date DATE,\n" + 
						"  Status VARCHAR(255),\n" + 
						"  IsSimulated VARCHAR(255),\n" + 
						"  PRIMARY KEY ( Id )\n" + 
						")"
				);
				LOG.info("Table 'TRADES' created successfully.");
				
				String createRiskTable = "CREATE TABLE RISKS (" + 
						"TradeId BIGINT not NULL, " + 
						"Pnl DOUBLE, " + 
						"DeltaX DOUBLE, " + 
						"PnlDelta DOUBLE, " + 
						"Gamma DOUBLE, " + 
						"Vega DOUBLE, " + 
						"PnlVega DOUBLE, " +
						"PRIMARY KEY ( TradeId )" + 
						")";
				stmt.executeUpdate(createRiskTable);
				LOG.info("Table 'RISKS' created successfully.");
				
				conn.commit();
			
	    	} catch(SQLException se) { 
	    		//Handle errors for JDBC
	    		conn.rollback();
	    		se.printStackTrace(); 
	    	}
	    }

	}

}
