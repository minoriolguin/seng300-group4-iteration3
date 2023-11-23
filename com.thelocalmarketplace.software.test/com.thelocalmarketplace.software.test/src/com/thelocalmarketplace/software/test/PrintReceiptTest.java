/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */

package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.hardware.Product; // Use the external Product class
import com.thelocalmarketplace.software.PrintReceipt;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * This class contains JUnit test cases to verify the functionality of the PrintReceipt class.
 */
public class PrintReceiptTest {

    /**
     * Test case to verify the receipt formatting with an empty list of products.
     */
    @Test
    public void testReceiptFormattingWithEmptyProductList() {
        PrintReceipt printReceipt = new PrintReceipt(null);
        List<Product> products = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<String> formattedReceipt = printReceipt.formatReceiptContent(products, totalAmount);
        assertFalse("Receipt should indicate no items purchased", formattedReceipt.stream().anyMatch(s -> s.contains("Items Purchased:")));
        assertTrue("Receipt should show a total amount of $0.00", formattedReceipt.stream().anyMatch(s -> s.contains("Total: $0.00")));
    }

    /**
     * Test case to verify receipt formatting with null product list.
     */
    @Test(expected = NullPointerException.class)
    public void testReceiptFormattingWithNullProductList() {
        PrintReceipt printReceipt = new PrintReceipt(null);
        printReceipt.formatReceiptContent(null, BigDecimal.ZERO);
    }

    public class myProduct extends Product {
        public float price;
        public boolean isPerUnit;

        protected myProduct(long price, boolean isPerUnit) {
            super(price, isPerUnit);
            this.price = price;
            this.isPerUnit = isPerUnit;
        }
    }

    /**
     * Test normal purchase receipt
     */
    @Test
    public void testReceiptformatting() {
        PrintReceipt printReceipt = new PrintReceipt(null);
        List<Product> products = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.TEN;
        products.add(new myProduct(5, false));
        products.add(new myProduct(2, true));
        products.add(new myProduct(3, false));

        List<String> formattedReceipt = printReceipt.formatReceiptContent(products, totalAmount);

        assertTrue("Receipt should indicate each price", formattedReceipt.stream().anyMatch(s -> s.contains("$5")));
        assertTrue("Receipt should indicate each price", formattedReceipt.stream().anyMatch(s -> s.contains("$3")));
        assertTrue("Receipt should indicate each price", formattedReceipt.stream().anyMatch(s -> s.contains("$2")));
        assertTrue("Receipt should show a total amount of $10.00", formattedReceipt.stream().anyMatch(s -> s.contains("Total: $10.00")));
    }


}
