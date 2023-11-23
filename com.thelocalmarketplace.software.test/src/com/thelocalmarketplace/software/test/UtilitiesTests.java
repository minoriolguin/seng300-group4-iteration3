package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.Utilities;

/**
 * @author Connell Reffo (10186960)
 * ----------------------------------
 * @author Angelina Rochon (30087177)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class UtilitiesTests {
	
	/**
	 * Map that will track the count of any object
	 */
	private Map<Object, Integer> testMap;
	
	private Object o1;
	private Object o2;
	
	
	@Before
	public void init() {
		this.testMap = new HashMap<>();
		
		// Initialize some arbitrary objects
		this.o1 = "str1";
		this.o2 = "str2";
		
		// Initialize their counts arbitrarily in the test map
		this.testMap.put(o1, 1);
		this.testMap.put(o2, 3);
	}
	
	@Test
	public void testAddAlreadyExistingInModifyCountMapping() {
		Utilities.modifyCountMapping(this.testMap, o1, 2);
		
		assertEquals(3, testMap.get(o1).intValue());
	}
	
	@Test
	public void testAddNotExistingInModifyCountMapping() {
		Object o = new Object();
		
		Utilities.modifyCountMapping(this.testMap, o, 2);
		
		assertEquals(2, testMap.get(o).intValue());
	}
	
	@Test
	public void testRemoveNotExistingInModifyCountMapping() {
		Object o = new Object();
		
		Utilities.modifyCountMapping(this.testMap, o, -3);
		
		assertFalse(this.testMap.containsKey(o));
	}
	
	@Test
	public void testRemoveAlreadyExistingInModifyCountMapping() {
		Utilities.modifyCountMapping(this.testMap, o2, -1);
		
		assertEquals(2, testMap.get(o2).intValue());
	}
	
	@Test
	public void testRemoveCompletelyAlreadyExistingInModifyCountMapping() {
		Utilities.modifyCountMapping(this.testMap, o1, -1);
		
		assertFalse(this.testMap.containsKey(o1));
	}
}
