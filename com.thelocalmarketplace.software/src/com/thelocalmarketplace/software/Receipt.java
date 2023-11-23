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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Receipt class used to construct a receipt and the format to be printed.
 */
class Receipt {
    private String date;
    private String time;
    private ArrayList<SessionItem> itemList;
    private double subtotal;
    private double tax;
    private double total;
    private String cardNumber;

    public Receipt(CustomerStationControl customerStationControl, String cardNumber) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
	
	    this.date = dateFormat.format(date);
	    this.time = timeFormat.format(date);
	    this.itemList = customerStationControl.getOrder().getItems();
	    this.subtotal = customerStationControl.getOrder().getTotal().doubleValue();
	    this.tax = 0; //this is left for future iterations
	    this.total = subtotal+subtotal*tax;
	    this.cardNumber = cardNumber;
    }

    @Override
    public String toString() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Receipt =====\n");
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("Time: ").append(time).append("\n");
        receipt.append("===================\n");

        // Itemized list
        for (SessionItem item : itemList) {
            receipt.append(item.getName()).append(String.format(": $%.2f", item.getPrice())).append("\n");
        }

        receipt.append("===================\n");
        receipt.append(String.format("Subtotal: $%.2f\n", subtotal));
        receipt.append(String.format("Tax (%.2f%%): $%.2f\n", tax, total-subtotal));
        receipt.append("===================\n");
        receipt.append(String.format("Total: $%.2f\n", total));
        receipt.append("===================\n");

        // Card details (if available)
        if (cardNumber != null) {
            receipt.append("Paid by Card: **** **** **" + cardNumber.substring(cardNumber.length()-2, cardNumber.length()));
            receipt.append("\n===================\n");
        }
        
        //System.out.println(receipt.toString());
        return receipt.toString();
    }
}
