/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.lx.kivi;

import java.util.function.Function;

import com.leanxcale.kivi.database.TableModel;
import com.leanxcale.kivi.database.Type;
import com.leanxcale.kivi.tuple.Tuple;
import com.qfs.store.IStoreMetadata;

/**
 * 
 * Logic for converting KiVi tuples into java tuples that can
 * be loaded into an ActivePivot store.
 * 
 * @author ActiveViam
 *
 */
public class KiviTuplizer {

	protected final Function<Tuple, Object>[] readers;
	
	public KiviTuplizer(Function<Tuple, Object>[] readers) {
		this.readers = readers;
	}
	
	public Object[] toJavaTuple(Tuple tuple) {
		
		Object[] javaTuple = new Object[readers.length];
		for(int i = 0; i < readers.length; i++) {
			javaTuple[i] = readers[i].apply(tuple);
		}
		return javaTuple;
	}
	
	
	public static KiviTuplizer create(TableModel table, IStoreMetadata metadata) {
		final int fieldCount = metadata.getFieldNames().size();
		Function<Tuple, Object>[] readers = (Function<Tuple, Object>[]) new Function[fieldCount];
		
		for(int i = 0; i < fieldCount; i++) {
			final String fieldName = metadata.getFieldNames().get(i);
			final String tableFieldName = fieldName.toUpperCase();
			readers[i] = new Function<Tuple, Object>() {
				@Override public Object apply(Tuple t) { return t.get(tableFieldName); }
			};
		}
		
		return new KiviTuplizer(readers);
	}
	
	
	public static KiviTuplizer fromStoreZob(IStoreMetadata metadata) {
		
		Function<Tuple, Object>[] readers = (Function<Tuple, Object>[]) new Function[] {
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getInteger("ID"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getString("PRODUCTNAME"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getString("PRODUCTTYPE"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getString("UNDERLIERCODE"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getString("UNDERLIERCURRENCY"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getString("UNDERLIERTYPE"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getDouble("UNDERLIERVALUE"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getDouble("PRODUCTBASEMTM"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getDouble("BUMPEDMTMUP"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getDouble("BUMPEDMTMDOWN"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getDouble("THETA"); }
				},
				new Function<Tuple, Object>() {
					@Override public Object apply(Tuple t) { return t.getDouble("RHO"); }
				}
		};
				
		return new KiviTuplizer(readers);
	}
	
}
