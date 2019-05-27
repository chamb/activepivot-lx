/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx;

import java.util.Collection;

import com.leanxcale.kivi.database.Database;
import com.leanxcale.kivi.database.Table;
import com.leanxcale.kivi.session.Connection;
import com.leanxcale.kivi.session.ConnectionFactory;
import com.leanxcale.kivi.session.Credentials;
import com.leanxcale.kivi.session.Settings;

public class KiViTest {

	public static void main(String[] args) throws Exception {
		
		/* Credentials and connection settings */
		Credentials credentials = new Credentials();
		credentials.setUser("APP");
		credentials.setPass(new char[] {'A', 'P', 'P'});
		credentials.setDatabase("db");

		Settings settings = new Settings();
		settings.credentials(credentials);
		
		try (Connection connection = ConnectionFactory.connect("kivi:zk://localhost:2181", settings)) {
		
			Database database = connection.database();
		
			// print table names
			Collection<Table> tables = database.getTables();
			tables.stream().map(t -> t.getName()).forEach(System.out::println);
			
			// print records
			Table products = database.getTable("PRODUCTS");
			products.find().forEach(t -> System.out.println(t));
		}
		
	}

}
