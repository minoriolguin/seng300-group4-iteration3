/**
 * @author Alan Yong: 30105707
 * @author Atique Muhammad: 30038650
 * @author Ayman Momin: 30192494
 * @author Christopher Lo: 30113400
 * @author Ellen Bowie: 30191922
 * @author Emil Huseynov: 30171501
 * @author Eric George: 30173268
 * @author Kian Sieppert: 30134666
 * @author Muzammil Saleem: 30180889
 * @author Ryan Korsrud: 30173204
 * @author Sukhnaaz Sidhu: 30161587
 */
package com.thelocalmarketplace.software;
import java.math.BigDecimal;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

/**
 * Represents various types of products that are added to the order list.
 * Mass refers to the expected weight of the product if available, 
 * otherwise the recorded weight in the case of priced-by-weight items such as PLUProducts.
 */
public class SessionItem extends Item {
	BigDecimal price;
	String name;
	protected SessionItem(String n, Mass m, BigDecimal p) {
		super(m);
		this.price = p;
		this.name = n;
	}
	public BigDecimal getPrice() {
		return price;
	}
	
	public String getName() {
		return name;
	}
}
