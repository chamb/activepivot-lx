/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.cfg;


import static com.activeviam.lx.db.DatabaseConnection.DB_URL;
import static com.activeviam.lx.db.DatabaseConnection.JDBC_DRIVER;
import static com.activeviam.lx.db.DatabaseConnection.PASS;
import static com.activeviam.lx.db.DatabaseConnection.USER;

import java.util.ArrayList;
import java.util.List;
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
public class JDBCDataLoadingConfig {

    private static final Logger LOGGER = Logger.getLogger(JDBCDataLoadingConfig.class.getSimpleName());

    
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
				8, // pool size
				20000, // append queue size
				5000  // append batch size
		);

		// Register topics
		jdbcSource.addTopic(new JDBCTopic("Products", "SELECT * from PRODUCTS"));

		jdbcSource.addTopic(new JDBCTopic("Trades-0", "SELECT * from TRADES WHERE MOD(ID, 2) = 0"));
		jdbcSource.addTopic(new JDBCTopic("Trades-1", "SELECT * from TRADES WHERE MOD(ID, 2) = 1"));

		jdbcSource.addTopic(new JDBCTopic("Risks-0", "SELECT * from RISKS WHERE MOD(TRADEID, 2) = 0"));
		jdbcSource.addTopic(new JDBCTopic("Risks-1", "SELECT * from RISKS WHERE MOD(TRADEID, 2) = 1"));

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
		
		List<IMessageChannel<String, QfsResultSetRow>> channels = new ArrayList<>();
		channels.add(jdbcChannelFactory.createChannel("Products"));
		channels.add(jdbcChannelFactory.createChannel("Trades-0", "Trades"));
		channels.add(jdbcChannelFactory.createChannel("Trades-1", "Trades"));
		channels.add(jdbcChannelFactory.createChannel("Risks-0", "Risks"));
		channels.add(jdbcChannelFactory.createChannel("Risks-1", "Risks"));
		
		jdbcSource.fetch(channels);
		
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
