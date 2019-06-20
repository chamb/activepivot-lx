/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.cfg;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.activeviam.lx.kivi.KiviTuplizer;
import com.leanxcale.kivi.database.Database;
import com.leanxcale.kivi.database.Table;
import com.leanxcale.kivi.query.aggregation.Aggregations;
import com.leanxcale.kivi.session.Connection;
import com.leanxcale.kivi.session.ConnectionFactory;
import com.leanxcale.kivi.session.Credentials;
import com.leanxcale.kivi.session.Settings;
import com.leanxcale.kivi.tuple.TupleKey;
import com.qfs.store.IDatastore;
import com.qfs.store.IStoreMetadata;
import com.qfs.store.impl.SchemaPrinter;
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
    
    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;

    @Bean
    public Settings connectionSettings() {
		Credentials credentials = new Credentials();
		credentials.setUser(USER);
		credentials.setPass(PASSWORD);
		credentials.setDatabase(DATABASE);

		Settings settings = new Settings();
		settings.credentials(credentials);
		return settings;
    }
    
    @Bean
    @DependsOn(value = "startManager")
    public Void loadData() throws Exception {

    	Integer poolSize = env.getProperty("kivi.poolSize", Integer.class, 4);
    	Long chunkSize = env.getProperty("kivi.chunkSize", Long.class, 10000000L);
    	
    	
    	Map<String, Long> tableSizes = getTableSizes();
    	tableSizes.entrySet().forEach(e -> {
    		LOGGER.info("Table " + e.getKey() + ": " + e.getValue() + " records.");
    	});

    	final long before = System.nanoTime();

    	datastore.getTransactionManager().startTransaction();

		// Define range loads
		final java.util.List<Load> loads = new ArrayList<>();
    	tableSizes.forEach((tableName, size) -> {
    		String id = "RISKS".equalsIgnoreCase(tableName) ? "TRADEID" : "ID";
    		for(long from = 0L; from < size; from += chunkSize) {
    			long to = Math.min(size, from + chunkSize);
    			loads.add(new Load(tableName, id, from, to));
    		}
    		
    	});

    	// Execute the partial loads in parallel
    	ForkJoinPool customThreadPool = new ForkJoinPool(poolSize);
        customThreadPool.submit(
        		() -> loads.parallelStream().forEach(Load::load)
        ).get();

		datastore.getTransactionManager().commitTransaction();
		
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


	
	
	/**
	 * @return number of records per table
	 */
	Map<String, Long> getTableSizes() {
		Map<String, Long> sizes = new LinkedHashMap<String, Long>();
		
		try (Connection connection = ConnectionFactory.connect(KIVI_URL, connectionSettings())) {
			
			connection.beginTransaction();

			Stream.of("Products", "Trades", "Risks").forEach(storeName -> {
				String tableName = storeName.toUpperCase();
				Database database = connection.database();
				Table table = database.getTable(tableName);
				table.find().aggregate(null, Aggregations.count("COUNT")).forEach(t -> {
					Long count = t.getInteger("COUNT").longValue();
					sizes.put(storeName, count);
				});
			});

			connection.commit();
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "An error occurred while measuring table sizes.", e);
		}
		
		return sizes;
	}
	
	
	/** Range load operation */
	protected class Load {
		public String storeName;
		public String keyField;
		public Long from;
		public Long to;
		public Load(String storeName, String keyField, Long from, Long to) {
			this.storeName = storeName;
			this.keyField = keyField;
			this.from = from;
			this.to = to;
		}
		
		public void load() {
			
			System.out.println("Loading " + storeName + " from " + from + " to " + to);
			
			try (Connection connection = ConnectionFactory.connect(KIVI_URL, connectionSettings())) {
				
				connection.beginTransaction();
				
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
			    
			    TupleKey min = table.createTupleKey();
			    min.putLong(keyField, from);
			    TupleKey max = table.createTupleKey();
			    max.putLong(keyField, to);
		

				table.find().min(min).max(max).forEach(t -> {
					count[0]++;
					Object[] javaTuple = tuplizer.toJavaTuple(t);
					datastore.getTransactionManager().add(storeId, javaTuple);
				});

				
				connection.commit();
				
				final long elapsed = System.nanoTime() - start;
				LOGGER.info("Fetched " + count[0] + " " + storeName + " records in " + elapsed / 1000000L + "ms");
				
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "An error occurred during loading of " + storeName, e);
			}
			
		}
		
	}
	
}
