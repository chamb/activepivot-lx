/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.cfg;


import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.activeviam.lx.kivi.KiviTuplizer;
import com.leanxcale.kivi.database.Database;
import com.leanxcale.kivi.database.Table;
import com.leanxcale.kivi.session.Connection;
import com.leanxcale.kivi.session.ConnectionFactory;
import com.leanxcale.kivi.session.Credentials;
import com.leanxcale.kivi.session.Settings;
import com.qfs.store.IDatastore;
import com.qfs.store.IStoreMetadata;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.store.transaction.ITransactionManager;
import com.qfs.util.timing.impl.StopWatch;

/**
 * Spring configuration for data loading
 * 
 * @author ActiveViam
 *
 */
public class KiViDataLoadingConfig {

    private static final Logger LOGGER = Logger.getLogger(KiViDataLoadingConfig.class.getSimpleName());

    /** KiVi connection string */
    private static final String KIVI_URL = "kivi:zk://localhost:2181";
    
    /** User */
    private static final String USER = "APP";
    
    /** Password */
    private static final char[] PASSWORD = new char[] {'A', 'P', 'P'};
    
    /** Database */
    private static final String DATABASE = "db";
    
    /** ActivePivot stores to be loaded using KiVi API */
    private static final Collection<String> STORES = Arrays.asList("Products", "Trades", "Risks");
    
    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;

    @Bean
    @DependsOn(value = "startManager")
    public Void loadData() throws Exception {

    	final long before = System.nanoTime();

    	final ITransactionManager tm = datastore.getTransactionManager();
	    tm.startTransaction();
		
		/* Credentials and connection settings */
		Credentials credentials = new Credentials();
		credentials.setUser(USER);
		credentials.setPass(PASSWORD);
		credentials.setDatabase(DATABASE);

		Settings settings = new Settings();
		settings.credentials(credentials);
	    
		// Stores to load, processed in parallel
		STORES.stream().forEach(storeName -> {
			
			try (Connection connection = ConnectionFactory.connect(KIVI_URL, settings)) {
					
				Database database = connection.database();
				int storeId = datastore.getSchemaMetadata().getStoreId(storeName);
				IStoreMetadata metadata = datastore.getSchemaMetadata().getStoreMetadata(storeName);
				String tableName = storeName.toUpperCase();
				Table table = database.getTable(tableName);
				LOGGER.info("Table model" + table.getTableModel().getFields());
						
				KiviTuplizer tuplizer = KiviTuplizer.create(table.getTableModel(), metadata);
						
				// Fetch records from KiVi and put them into the ActivePivot store
				long[] count = new long[1];
	
		    	final long start = System.nanoTime();
				table.find().forEach(t -> {
					count[0]++;
					Object[] javaTuple = tuplizer.toJavaTuple(t);
					tm.add(storeId, javaTuple);
				});
						
				final long elapsed = System.nanoTime() - start;
				LOGGER.info("Fetched " + count[0] + " records in " + elapsed / 1000000L + "ms");

			} catch(Exception e) {
				LOGGER.log(Level.SEVERE, "Error fetching data in store " + storeName, e);
			}

		});
		
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
