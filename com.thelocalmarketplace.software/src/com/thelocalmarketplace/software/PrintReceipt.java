 /**
 *Project, Iteration 3, Group 4
 *  Group Members:
 * - Arvin Bolbolanardestani / 30165484
 * - Anthony Chan / 30174703
 * - Marvellous Chukwukelu / 30197270
 * - Farida Elogueil / 30171114
 * - Ahmed Elshabasi / 30188386
 * - Shawn Hanlon / 10021510
 * - Steven Huang / 30145866
 * - Nada Mohamed / 30183972
 * - Jon Mulyk / 30093143
 * - Althea Non / 30172442
 * - Minori Olguin / 30035923
 * - Kelly Osena / 30074352
 * - Muhib Qureshi / 30076351
 * - Sofia Rubio / 30113733
 * - Muzammil Saleem / 30180889
 * - Steven Susorov / 30197973
 * - Lydia Swiegers / 30174059
 * - Elizabeth Szentmiklossy / 30165216
 * - Anthony Tolentino / 30081427
 * - Johnny Tran / 30140472
 * - Kaylee Xiao / 30173778 
 **/

package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;

public class PrintReceipt {

    private static String startString="";
    private static String endString="";
    private final static int MAX_LINE_LENGTH = 60;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA);
    private ArrayList<PLUCodedProduct> pluInCart;
    private ArrayList<BarcodedProduct> barcodedProductsInCart;
    private Software software;

    private int printedChars;
    private int averagePrintedChars;
    private int paperUsed;
    private int averagePaperUsed;

    public int getAveragePrintedChars() {
		return averagePrintedChars;
	}

	public int getAveragePaperUsed() {
		return averagePaperUsed;
	}

	private IReceiptPrinter printer;

    /**
     * A constructor that takes a copy of the software.
     * @param software the software object that has a printer
     */
    public PrintReceipt(Software software){
        this.software = software;
        this.pluInCart = software.getPluCodedProductsInOrder();
        this.barcodedProductsInCart = software.getBarcodedProductsInOrder();
        printer = software.printer;
        averagePaperUsed = 0;
        averagePrintedChars = 0;

    }
    public void setPluInCart(ArrayList<PLUCodedProduct> products){
        this.pluInCart = products;
    }
    public void setBarcodedProductsInCart(ArrayList<BarcodedProduct> products){
        this.barcodedProductsInCart = products;
    }

    /**
     * A constructor that takes a printer, meant for testing.
     * @param p the printer
     */
    public PrintReceipt(IReceiptPrinter p){
        printer = p;
    }

    /**
     * Prints the start string, followed by a line for each item in the inputCart, then the total
     * followed by the end string.
     */
    public void print(){

        pluInCart = software.getPluCodedProductsInOrder();
        barcodedProductsInCart = software.getBarcodedProductsInOrder();
        // print the start template
        printLine(startString);
        // print a line for each item in the cart
        for (BarcodedProduct product : barcodedProductsInCart ){
            printLine(makeLineFromCartItem(product));
        }
        for (PLUCodedProduct product : pluInCart ){
            printLine(makeLineFromCartItem(product));
        }
        // print the total
        printTotal();
        // print the end template
        printLine(endString);
        printer.cutPaper();
        averagePrintedChars = calculateAveragePrintedChars();
        averagePaperUsed = calculateAveragePaperUsed();
        // reset
        printedChars = 0;
        paperUsed = 0;
        totalPrice = BigDecimal.ZERO;
    }

    /**
     * This method takes a string and sends each character in that string to the printer.
     * Exception if the string is too long for the printer.
     * Exception if the string does not end in a new line character.
     * If the printer is out of ink or paper, the execution stops until the problem is
     * resolved by the attendant and retries printing the character it failed on.
     *
     * @param s The input string that will be sent to the printer
     */
    private void printLine(String s){
        if (!s.isEmpty() && s.charAt(s.length() - 1) != '\n') {
            throw new RuntimeException("printLine string must end in new line character.");
        }
        boolean emptyPrinter = false;
        // send each individual character in the string to the printer using an index counter 'i'
        for (int i = 0; i < s.length(); i++) {
            do {
                try {
                    printer.print(s.charAt(i));
                    emptyPrinter = false;

                    if (s.charAt(i) != ' ' && !Character.isWhitespace(s.charAt(i))) {
                    	printedChars += 0;
                    }
                }
                catch (EmptyDevice e) {
                    // pause the system with a message
                    // alert the attendant with specific information
                    // currently the stub refills automatically when out of ink or paper
                    // this character will need to be reprinted because it was not printed successfully
                    // if the attendant did not fill the printer after request, something is wrong
                    if (emptyPrinter)
                        throw new RuntimeException("The attendant did not fill the printer.");
                    emptyPrinter = true;
                }
                catch (OverloadedDevice e) {
                    // the line had too many characters
                    // this should be caught in other methods and should not happen.
                    throw new RuntimeException("The line that was printing is too long.");
                }
            } while(emptyPrinter);
        }// end of for each character loop
        this.paperUsed += 1;
    }

    /**
     * Prints the value in totalPrice "Total: " + totalPrice padded with spaces to MAX_LINE_LENGTH - 5
     * (right aligned)
     */
    private void printTotal(){
        // if nothing is in the order do not include a total.
        // error for empty cart happens elsewhere, this will be used for testing.
        if (totalPrice.equals(BigDecimal.ZERO))
            return;
        String total = "Total: " + currencyFormat.format(totalPrice);
        int padLength = (MAX_LINE_LENGTH - 5) - total.length();
        total = (" ".repeat(padLength)) + total;
        total += "\n";
        printLine(total);
    }

    /**
     * Takes a barcoded product and returns a line that is ready to print on the receipt.
     * If the product description is too long, it will be truncated and appended with an ellipsis.
     * A line will be in the form:
     * [5 blank spaces][Description, up to 38 chars]
     * 			[price, up to 12 chars but more can overflow 5 additional spaces]['/n']
     * where blank spaces are filled with a ' ' space.
     * @param product The product that will be on this line of the receipt.
     * @return a line that is ready to print on the receipt.
     */
    private String makeLineFromCartItem(BarcodedProduct product) {
        String retStr = "     "; // the cumulative string that will end up being the whole line of the order and returned.
        StringBuilder ph; // placeholder string for intermediate steps value
        // Product description
        ph = new StringBuilder(product.getDescription());
        // If the description is too long, cut it off and place a "..."
        if (ph.length() > 38)
            ph = new StringBuilder(ph.substring(0, 35) + "...");
        // fill the length up to 38.
        while (ph.length() < 38)
            ph.append(" ");
        retStr += ph;

        // product price
        BigDecimal price = BigDecimal.valueOf(product.getPrice()).divide(BigDecimal.valueOf(100));
        totalPrice = totalPrice.add(price);
        ph = new StringBuilder(currencyFormat.format(price));
        // fill the left space up to 12.
        while (ph.length() < 12)
            ph.insert(0, " ");
        retStr += ph;

        // end with a new line character
        retStr += '\n';

        return retStr;
    }

    /**
     * Takes a PLU Coded product and returns a line that is ready to print on the receipt.
     * If the product description is too long, it will be truncated and appended with an ellipsis.
     * A line will be in the form:
     * [5 blank spaces][Description, up to 38 chars]
     * 			[price, up to 12 chars but more can overflow 5 additional spaces]['/n']
     * where blank spaces are filled with a ' ' space.
     * @param product The product that will be on this line of the receipt.
     * @return a line that is ready to print on the receipt.
     */
    private String makeLineFromCartItem(PLUCodedProduct product) {
        String retStr = "     "; // the cumulative string that will end up being the whole line of the order and returned.
        StringBuilder ph; // placeholder string for intermediate steps value
        // Product description
        ph = new StringBuilder(product.getDescription());
        // If the description is too long, cut it off and place a "..."
        if (ph.length() > 38)
            ph = new StringBuilder(ph.substring(0, 35) + "...");
        // fill the length up to 38.
        while (ph.length() < 38)
            ph.append(" ");
        retStr += ph;

        // product price
        long tempPrice = ((software.getProductsInOrder().get(product).inGrams().longValue())/1000) * product.getPrice();
        // Convert price to type 'BigDecimal'
        BigDecimal price = BigDecimal.valueOf(tempPrice).divide(BigDecimal.valueOf(100));
        totalPrice = totalPrice.add(price);
        ph = new StringBuilder(currencyFormat.format(price));
        // fill the left space up to 12.
        while (ph.length() < 12)
            ph.insert(0, " ");
        retStr += ph;

        // end with a new line character
        retStr += '\n';

        return retStr;
    }

    public static void defaultStartString() {
        startString = """
                
                Thelocalmarketplace
                ------------------------------------------------------------
                """;
    }

    public static void defaultEndString() {
        endString = """
                ------------------------------------------------------------
                Thank you!
                Please come again!
                """;
    }

    /**
     * Checks that a new line character is at the end of the template, if not throws an exception.
     * Checks that no line is too long for the printer, if a line is too long then throws an exception.
     * If the string is good, set the start template to the input string.
     * @param s the string to se the start template to.
     */
    public static void setStartString(String s) {
        // check that the new template will not break the printer
        if (!s.isEmpty() && s.charAt(s.length() - 1) != '\n')
            throw new RuntimeException("The message must end in a new line character.");
        int charCount = 0;
        for (int i = 0; i < s.length(); i++){
            switch (s.charAt(i)){
                case '\n':
                    charCount = 0;
                default:
                    charCount++;
            }
            if (charCount >= MAX_LINE_LENGTH)
                throw new RuntimeException("The start template has a line that is too long for the printer.");
        }
        startString = s;
    }

    /**
     * If the string is good, set the end template to the input string.
     * Exception if a  line in the template is too long.
     * Exception if the template does not end in a new line character.
     * @param s the string to se the start template to.
     */
    public static void setEndString(String s) {
        // check that the new template will not break the printer
        if (!s.isEmpty() && s.charAt(s.length() - 1) != '\n')
            throw new RuntimeException("The message must end in a new line character.");
        int charCount = 0;
        for (int i = 0; i < s.length(); i++){
            switch (s.charAt(i)){
                case '\n':
                    charCount = 0;
                default:
                    charCount++;
            }
            if (charCount >= MAX_LINE_LENGTH)
                throw new RuntimeException("The end template has a line that is too long for the printer.");
        }
        endString = s;
    }
    
    public int calculateAveragePrintedChars() {
    	return (printedChars + averagePrintedChars) / 2;
    }
    
    public int calculateAveragePaperUsed() {
    	return (paperUsed + averagePaperUsed) / 2;
    }
}