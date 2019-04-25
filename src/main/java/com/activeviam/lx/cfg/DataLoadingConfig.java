/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.cfg;


import static com.activeviam.lx.db.DatabaseConnection.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.qfs.msg.IMessageChannel;
import com.qfs.msg.impl.QfsResultSetRow;
import com.qfs.msg.jdbc.IJDBCSource;
import com.qfs.msg.jdbc.impl.JDBCTopic;
import com.qfs.msg.jdbc.impl.NativeJDBCSource;
import com.qfs.source.impl.JDBCMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.store.transaction.ITransactionManager;
import com.qfs.util.timing.impl.StopWatch;

/**
 * Spring configuration for data sources
 * 
 * @author ActiveViam
 *
 */
public class DataLoadingConfig {

    private static final Logger LOGGER = Logger.getLogger(DataLoadingConfig.class.getSimpleName());

    
    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;
    
    
	/*
	 * **************************** Data loading *********************************
	 */
    
    /**
     * @return JDBC Source
     */
    @Bean
    public IJDBCSource<QfsResultSetRow> jdbcSource() {

		/* Initialize the database */
		Properties properties = new Properties();
		properties.setProperty("username", USER);
		properties.setProperty("password", PASS);
		
		// Create the JDBC Source
		NativeJDBCSource jdbcSource = new NativeJDBCSource(
				DB_URL, // URL
				JDBC_DRIVER, // DRIVER
				properties,
				"LeanXcale JDBC Source",
				2, // pool size
				20000, // append queue size
				5000  // append batch size
		);
		 
		// Register topics
		jdbcSource.addTopic(new JDBCTopic("Products", "SELECT * from PRODUCTS"));
		jdbcSource.addTopic(new JDBCTopic("Trades", "SELECT * from TRADES"));
		jdbcSource.addTopic(new JDBCTopic("Risks", "SELECT * from RISKS"));
		
		return jdbcSource;
    }

    @Bean
    @DependsOn(value = "startManager")
    public Void loadData(IJDBCSource<QfsResultSetRow> jdbcSource) throws Exception {
		
    	final ITransactionManager tm = datastore.getTransactionManager();
    	
    	// Load data into ActivePivot
    	final long before = System.nanoTime();
    	
    	// Transaction for TV data
	    tm.startTransaction();
		
		JDBCMessageChannelFactory jdbcChannelFactory = new JDBCMessageChannelFactory(jdbcSource, datastore);
		IMessageChannel<String, QfsResultSetRow> productChannel = jdbcChannelFactory.createChannel("Products");
		IMessageChannel<String, QfsResultSetRow> tradeChannel = jdbcChannelFactory.createChannel("Trades");
		IMessageChannel<String, QfsResultSetRow> riskChannel = jdbcChannelFactory.createChannel("Risks");
		
		jdbcSource.fetch(Arrays.asList(productChannel, tradeChannel, riskChannel));
		
		tm.commitTransaction();
		
    	final long elapsed = System.nanoTime() - before;
    	LOGGER.info("Data load completed in " + elapsed / 1000000L + "ms");
    	
    	printStoreSizes();
    	
    	return null;
    }


	private void printStoreSizes() {

		// Print stop watch profiling
		StopWatch.get().printTimings();
		StopWatch.get().printTimingLegend();

		// print sizes
		SchemaPrinter.printStoresSizes(datastore.getHead().getSchema());
	}

}
