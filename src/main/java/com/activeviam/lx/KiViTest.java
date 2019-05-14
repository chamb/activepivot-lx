/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx;

import com.leanxcale.kivi.session.ConnectionFactory;
import com.leanxcale.kivi.session.Credentials;
import com.leanxcale.kivi.session.Settings;

public class KiViTest {

	public static void main(String[] args) throws Exception {
		
		/* Credentials and connection settings */
		Credentials credentials = new Credentials();
		credentials.setUser("app");
		credentials.setPass(new char[] {'a', 'p', 'p'});
		credentials.setDatabase("db");

		Settings settings = new Settings();
		settings.credentials(credentials);
		
		ConnectionFactory.connect("kivi:zk://localhost:32771", settings);
	}

}
