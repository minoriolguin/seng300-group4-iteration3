package com.thelocalmarketplace.software;

import java.util.Map;

/**
 * @author Connell Reffo (10186960)
 * --------------------------------
 * @author Tara Strickland (10105877)
 * @author Angelina Rochon (30087177)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Phuong Le (30175125)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class Utilities {

	/**
	 * Used for incrementing/decrementing a counting map
	 * @param <T> The type that is being counted
	 * @param map Is a reference to a map object of type: Map<T, Integer>
	 * @param item Is the item to increment/decrement
	 * @param amount Is the amount to increment/decrement by
	 */
	public static <T> void modifyCountMapping(Map<T, Integer> map, T item, int amount) {
		if (map.containsKey(item)) {
			map.put(item, map.get(item) + amount);
		}
		else {
			map.put(item, amount);
		}
		
		if (map.get(item) <= 0) {
			map.remove(item);
		}
	}
}