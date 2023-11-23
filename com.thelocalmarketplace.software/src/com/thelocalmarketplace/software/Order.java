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
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.Barcode;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Contains information and actions which can be performed to the current order.
 */
public class Order {
	private CustomerStationControl customerStationControl;
	private ArrayList<SessionItem> items;
	private ArrayList<SessionItem> bulkyItems; // this will be sublist of items, containing the "bulky items"
	private Mass customerBagWeight;
	private BigDecimal totalPrice; //total price of order
	private BigDecimal totalUnpaid; //total UNPAID price of order

	public Order(CustomerStationControl customerStationControl) {
		this.customerStationControl = customerStationControl;
		items = new ArrayList<SessionItem>();
		bulkyItems = new ArrayList<SessionItem>();
		totalPrice = new BigDecimal(0);
		totalUnpaid = new BigDecimal(0);
		customerBagWeight = new Mass(0);
	}
	
	public ArrayList<SessionItem> getBulkyItems(){
		return bulkyItems;
	}
	
	/**
	 * Gets the expected mass of the order
	 * @return the expected mass of the order
	 **/
	public Mass getExpectedMass() {
		Mass sum = new Mass(0);
		for (SessionItem i : items) {
			Mass iMass = i.getMass();
			sum = sum.sum(iMass);	
		}
		for	(SessionItem i : bulkyItems) {
			Mass iMass = i.getMass();	
			sum = sum.difference(iMass).abs();
		}
		sum = sum.sum(customerBagWeight);
		return (sum);
	}

	/**
	 * Adds an product with a barcode to the order list as a new item with 
	 * it's mass assigned to the expected weight of the product.
	 * @param barcode the barcode of the added item
	 */
	public void add(Barcode barcode) {
		if(preconditionsMet(barcode)) {

			//Fetching item from database based on scanned barcode
			BarcodedProduct itemProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			Mass expectedWeight = new Mass(itemProduct.getExpectedWeight());
			BigDecimal price = new BigDecimal(itemProduct.getPrice());
			String name = itemProduct.getDescription();
			SessionItem item = new SessionItem(name, expectedWeight, price);
			
			//weight and expected weight is done through querying the items in the order list
						
			//Adding item to session order
			items.add(item);
			totalPrice = totalPrice.add(price);
			totalUnpaid = totalUnpaid.add(price);
			if(customerStationControl.getAddingBulkyItem()) {
				bulkyItems.add(item);
				customerStationControl.setAddingBulkyItem(false);
			} else {
				 customerStationControl.block();
			}
			customerStationControl.notifyCustomer("Place the scanned item in the bagging area", 
					customerStationControl.notifyPlaceItemInBaggingAreaCode);
			
			//the customer station is unblocked following a non-discrepancy creating weight change from the scale listener
		}
	}
	
	/**
	 * Adds an item to the order list by PLU code.
	 * Only used to show code structure in overloading the add method 
	 * to support the various ways of adding an item to the order list.
	 * @param plu the plu of the item adding to order
	 */
	public void add(PriceLookUpCode plu) {}
	
	
	/**
	 * Removes an item from the order list and removes it's cost from the total price of the order.
	 * See documentation for parameter n.
	 * @param n will be selected through UI. This is placeholder as UI will not be implemented this iteration.
	 */
	public void remove(int n) {//see javadoc for explanation of int n
		if(preconditionsMetRemove(n)) {
			customerStationControl.block();
            totalPrice = totalPrice.subtract(items.get(n).getPrice());
            totalUnpaid = totalUnpaid.subtract(items.get(n).getPrice());
            items.remove(0);
            
            customerStationControl.notifyCustomer("Item removed, Remove scanned item from bagging area", 
                    customerStationControl.notifyOtherCode);
            
            //the station will be unblocked as a result of the next weight change in the bagging area  
            //(which will not cause a weight discrepancy if the correct item is removed),
            //occuring as a result of the customer removing the item from the bagging area. (see ScaleListener)
		}
	}
		
	/**
	 * Subtracts the amount paid by the customer from the total unpaid amount for the order
	 * @param value the value of the coins paid by the customer
	 */
	public void addAmountPaid(BigDecimal value) {
		totalUnpaid = totalUnpaid.subtract(value);
	}
	
	/**Checks if the preconditions are met for removing an item from the order list.
	 * Specifically: station is not blocked, and n is a valid index of the items list.
	 * @param n
	 * @return boolean that tracks whether adding items and payment is blocked at the station
	 */
	public boolean preconditionsMetRemove(int n) {
		return (n >= 0 && n < items.size() && !customerStationControl.isBlocked());
	}
	
	/**Checks if the preconditions are met for adding a barcode product to the order list.
	 * @param barcode

	 */
	public boolean preconditionsMet(Barcode barcode) {
		return !customerStationControl.isBlocked();
	}
	
	/**Checks if the preconditions are met for adding a PLU product to the order list.
	 * Only used to show code structure in overloading the preconditionsMet method
	 * to support differing preconditions for the various ways of adding an item to the order list.
	 * @param plu the plus of the item adding to oder
	 * @return boolean 
	 */
	public boolean preconditionsMet(PriceLookUpCode plu) {return false;}
	

	//Various self-explanatory setters and getters
	public ArrayList<SessionItem> getItems() {
		return items;
	}
	
	public BigDecimal getTotal() {
		return totalPrice;
	}
	
	public BigDecimal getTotalUnpaid() {
		return totalUnpaid;
	}
	
	public CustomerStationControl getCustomerStationControl() {
		return customerStationControl;
	}
	
	public void setTotalUnpaid(BigDecimal n) {
		totalUnpaid = n;
	}
	
	public Mass getCustomerBagWeight() {
		return customerBagWeight;
	}

	public void setCustomerBagWeight(Mass customerBagWeight) {
		this.customerBagWeight = customerBagWeight;
	}
}
