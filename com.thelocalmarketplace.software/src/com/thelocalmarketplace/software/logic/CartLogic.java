package com.thelocalmarketplace.software.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Utilities;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Handles all logical operations on the customer's cart
 * 
 * Combined from Tara's and Angelina's seperate projects
 * 
 * @author Connell Reffo (10186960)
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
public class CartLogic {
	
	/**
	 * Tracks all of the products that are in the customer's cart
	 * Includes products without barcodes
	 * Maps a product to its count
	 */
	private Map<Product, Integer> cart;
	
	/**
	 * Tracks how much money the customer owes
	 */
	private BigDecimal balanceOwed;
	
	/**
	 * Constructor for a new CartLogic instance
	 */
	public CartLogic() {
		
		// Initialization
		this.cart = new HashMap<Product, Integer>();
		
		this.balanceOwed = BigDecimal.ZERO;
	}
	
	
	public void addProductToCart(BarcodedProduct product) {
		Utilities.modifyCountMapping(cart, product, 1);
		
		// Update balance owed
		//if (product.isPerUnit()) {
		BigDecimal newPrice = this.balanceOwed.add(new BigDecimal(product.getPrice()));
		this.updateBalance(newPrice);
		//} else {
			
		//}
	}
	
	/**
	 * Removes a product from customer's cart
	 * @param product The product to remove
	 * @throws SimulationException If the product is not in the cart
	 */
	public void removeProductFromCart(BarcodedProduct product) throws SimulationException {
		if (!this.getCart().containsKey(product)) {
			throw new InvalidStateSimulationException("Product not in cart");
		}
		
		Utilities.modifyCountMapping(cart, product, -1);
		
		// Update balance owed
		//if (product.isPerUnit()) {
		BigDecimal newPrice = this.balanceOwed.subtract(new BigDecimal(product.getPrice()));
		this.updateBalance(newPrice);
		//} else {
			
		//}
	}
	
	/**
	 * Takes a barcode, looks it up in product database, then adds it to customer cart
	 * @param barcode The barcode to use
	 * @throws SimulationException If barcode is not registered to product database
	 * @throws SimulationException If barcode is not registered in available inventory
	 */
	public void addBarcodedProductToCart(Barcode barcode) throws SimulationException {
		BarcodedProduct toadd = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		
		if (!ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			throw new InvalidStateSimulationException("Barcode not registered to product database");
		}
		else if (!ProductDatabases.INVENTORY.containsKey(toadd) || ProductDatabases.INVENTORY.get(toadd) < 1) {
			throw new InvalidStateSimulationException("No items of this type are in inventory");
		}
		
		this.addProductToCart(toadd);
	}
	
	/**
	 * Gets the customer's cart
	 * @return A list of products that represent the cart
	 */
	public Map<Product, Integer> getCart() {
		return this.cart;
	}
	
	/**
	 * Calculates the balance owed based on the products added to customer's cart
	 * @return The balance owed
	 */
	public BigDecimal calculateTotalCost() {
		long balance = 0;
		
		for (Entry<Product, Integer> productAndCount : this.getCart().entrySet()) {
			Product product = productAndCount.getKey();
			int count = productAndCount.getValue();
			
			balance += product.getPrice() * count;
		}
		
		return new BigDecimal(balance);
	}
	
	/**
	 * Gets the balance owed by the customer
	 * @return The balance owed
	 */
	public BigDecimal getBalanceOwed() {
		return this.balanceOwed;
	}
	
	/**
   * Increments/Decrements the customer's balance
   * @param amount Is the amount to increment/decrement by
   */
  public void modifyBalance(BigDecimal amount) {
    this.balanceOwed = this.balanceOwed.add(amount);

    if (this.balanceOwed.compareTo(BigDecimal.ZERO) < 0) {
      this.balanceOwed = BigDecimal.ZERO;
    }
  }
	
	/**
	 * Sets the customer's balance
	 * @param balance The new balance value
	 */
	public void updateBalance(BigDecimal balance) {
		this.balanceOwed = balance;
	}
}