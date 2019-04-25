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
public class LoadSampleData {

	/** Logger */
	static final Logger LOG = Logger.getLogger(LoadSampleData.class.getName());
	
	public static void main(String[] args) throws Exception {

		DatabaseConnection connection = new DatabaseConnection();
	    try(Connection conn = connection.getConnection();
	    	Statement stmt = conn.createStatement()) {
			
			stmt.executeUpdate("INSERT INTO Products VALUES(0,'EQUITY SWAP','OTC','TUIGn.DE','USD','SingleStock',3283.19,864.61,0.5,0.67,7.1063835616438356,-5.7640666666666664)");
			stmt.executeUpdate("INSERT INTO Products VALUES(1,'BARRIER OPTION','OTC','IBEX','ZAR','EquityIndex',571.0,754.84,0.29,0.86,6.204164383561643,-5.032266666666667)");
			stmt.executeUpdate("INSERT INTO Products VALUES(2,'BARRIER OPTION','OTC','FTEPRA','GBP','EquityIndex',4264.43,955.14,0.89,0.73,7.850465753424658,-6.3675999999999995)");
			stmt.executeUpdate("INSERT INTO Products VALUES(3,'OPTION','LISTED','CECEE','EUR','EquityIndex',6111.65,668.06,0.07,0.68,5.4909041095890405,-4.453733333333333)");
			stmt.executeUpdate("INSERT INTO Products VALUES(4,'EQUITY SWAP','OTC','SRG.MI','JPY','SingleStock',5522.48,958.34,0.94,0.54,7.876767123287671,-6.388933333333333)");
			
			LOG.info("Products loaded successfully.");
			
			stmt.executeUpdate("INSERT INTO Trades VALUES(0,0,297,'DeskB',8,'Will','Unilever ord','2025-04-21','SIMULATION','SIMULATION')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(1,1,5055,'DeskB',6,'Sam','Asahi Chemical Ind.','2028-02-04','SIMULATION','SIMULATION')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(2,2,5860,'DeskB',7,'Luke','Sumitomo Trust and Banking','2026-06-24','DONE','LIVE')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(3,3,2777,'DeskA',8,'Sam','Mitsubishi Estate','2025-05-12','DONE','LIVE')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(4,4,1584,'DeskB',4,'John','Cosco Co.','2023-05-13','DONE','LIVE')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(5,0,8740,'DeskA',2,'Luke','Rockwell Automation  Inc.','2028-04-27','MATCHED','LIVE')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(6,1,5630,'DeskA',3,'Sam','Formosa Plastics Corporation','2027-12-19','SIMULATION','SIMULATION')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(7,2,4546,'DeskB',9,'Eric','Sumitomo Electric','2021-04-23','MATCHED','LIVE')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(8,3,7195,'DeskA',8,'Stan','China Life Insurance Company Limited','2019-11-09','DONE','LIVE')");
			stmt.executeUpdate("INSERT INTO Trades VALUES(9,4,4497,'DeskB',6,'Eric','HSBC Holdings plc','2021-03-20','DONE','LIVE')");
			
			LOG.info("Trades loaded successfully.");
			
			stmt.executeUpdate("INSERT INTO Risks VALUES(0,196.0199999999999,-3.323023539542407,40.03453457080451,20.002345345708036,19.60199999999999,0.4003453457080451)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(1,3942.9,-322.46386505787564,-1433.0914184859978,379.9590858151408,394.29000000000076,-14.330914184859978)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(2,-351.6000000000003,-25.85788574668883,253.63298098329295,-32.62367019016712,-35.160000000000046,2.5363298098329294)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(3,-6998.04,397.16010266367806,-2026.564413174685,-720.0696441317475,-699.8040000000007,-20.26564413174685)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(4,95.03999999999999,6.29685149639524,64.90437216123401,10.153043721612354,9.504000000000014,0.6490437216123401)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(5,9439.2,616.896758698101,-6254.122920586452,881.3787707941366,943.9200000000011,-62.54122920586452)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(6,-7544.199999999999,130.40931899813114,391.66519318597835,-750.5033480681404,-754.4200000000002,3.9166519318597834)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(7,4818.76,-392.10852411359673,-1648.9806042686305,465.386193957314,481.8760000000003,-16.489806042686304)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(8,2302.4000000000005,-212.9402588673823,582.6238540244588,236.06623854024505,230.24000000000046,5.826238540244588)");
			stmt.executeUpdate("INSERT INTO Risks VALUES(9,-3417.7200000000003,157.10086887079854,3292.1394117217933,-308.85060588278253,-341.77200000000045,32.921394117217936)");
			
			conn.commit();
			
			LOG.info("Risks loaded successfully.");
			
			
	     } catch(SQLException se) { 
	        //Handle errors for JDBC 
	        se.printStackTrace(); 
	     }

	}

}
