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

package com.thelocalmarketplace.software.test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.PrintReceipt;

import com.thelocalmarketplace.software.Software;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrintReceiptTest{

    private SelfCheckoutStationGold hardware;
    private Software software;
    private BarcodedProduct barcodeProduct;
    private PLUCodedProduct pluProduct;
    private PLUCodedItem pluCodedItem;
    private PrintReceiptTestAttendantStub attendantStub;
    private long barProductPrice;
    private double barProductWeight;

    // plu product details
    PriceLookUpCode pluCode;
    String pluProductDescription;
    long pluProductPrice;
    
    @Before
    public void setUp() throws OverloadedDevice {

        hardware = new SelfCheckoutStationGold();
        software = Software.getInstance(hardware);
        software.turnOn();

        //hardware.getPrinter().addInk(5000);
        //hardware.getPrinter().addPaper(500);

        // Restore PrintReceipt to default
        PrintReceipt.defaultStartString();
        PrintReceipt.defaultEndString();

        // Product setup
        Numeral[] num1 = {Numeral.one};
        // barcode product details
        Barcode barcode1 = new Barcode(num1);
        String barProductDescription = "Barcoded product";
        barProductPrice = 100L;
        barProductWeight = 1;
        
        pluCode = new PriceLookUpCode("1234");
        pluProductDescription = "PLU product";
        pluProductPrice = 150L;

        barcodeProduct = new BarcodedProduct(barcode1, barProductDescription,barProductPrice,barProductWeight);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1,barcodeProduct);
        software.updateCart.addProduct(barcodeProduct);
        
        pluProduct = new PLUCodedProduct(pluCode, pluProductDescription, pluProductPrice);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode,pluProduct);
        double x = 1000;
        Mass y = new Mass(x);
        pluCodedItem = new PLUCodedItem(pluCode, y);
        hardware.getScanningArea().addAnItem(pluCodedItem);
        software.updateCart.addPLUProduct(pluProduct);
        hardware.getScanningArea().removeAnItem(pluCodedItem);
        
        // stub setup
        attendantStub = new PrintReceiptTestAttendantStub(hardware.getPrinter());
        hardware.getPrinter().register(attendantStub);
    }

   
    
    @Test
    public void testOutOfPaperCallsAttendantStub() throws OverloadedDevice {
        PrintReceipt.setStartString("Hello!\n");
        PrintReceipt.setEndString("");

        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(1);

        software.printReceipt.print();
        assertTrue(attendantStub.outOfPaper);
    }

    @Test
    public void testOutOfInkCallsAttendantStub() throws OverloadedDevice {
        PrintReceipt.setStartString(".\n");
        PrintReceipt.setEndString("");

        hardware.getPrinter().addInk(1);
        hardware.getPrinter().addPaper(500);

        software.printReceipt.print();
        assertTrue(attendantStub.outOfInk);
    }

    @Test
    public void testPrintOnlyDefaultTemplate() throws OverloadedDevice {
        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(500);
        software.updateCart.removeItem(pluProduct);
        software.updateCart.removeItem(barcodeProduct);
        software.printReceipt.print();
        String expected = """

                    Thelocalmarketplace
                    ------------------------------------------------------------
                    ------------------------------------------------------------
                    Thank you!
                    Please come again!
                    """;// new line is entered automatically
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }

    @Test
    public void testSetTemplatesAndSimpleSingleLine() throws OverloadedDevice {
        PrintReceipt.setStartString("Hello\n");
        PrintReceipt.setEndString("world!\n");
        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(500);
        software.printReceipt.print();
        String expected = """
                    Hello
                         Barcoded product                             $1.00
                         PLU product                                  $1.50
                                                               Total: $2.50
                    world!
                    """;
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }


    @Test (expected = RuntimeException.class)
    public void testSetStartStringWithoutNewLine(){
        PrintReceipt.setStartString("Hello");
    }

    @Test (expected = RuntimeException.class)
    public void testSetStartStringToBadValue(){
        PrintReceipt.setStartString("---------*---------*---------*---------*---------*---------*---------*\n");
    }

    @Test (expected = RuntimeException.class)
    public void testSetEndStringWithoutNewLine(){
        PrintReceipt.setEndString("Hello");
    }
    @Test (expected = RuntimeException.class)
    public void testSetEndStringToBadValue(){
        PrintReceipt.setEndString("---------*---------*---------*---------*---------*---------*---------*\n");
    }

    @Test
    public void testPrintSimpleSingleLine() throws OverloadedDevice {
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(500);
        software.printReceipt.print();
        String expected = """
                     Barcoded product                             $1.00
                     PLU product                                  $1.50
                                                           Total: $2.50
                """;
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }

    @Test
    public void testPrintSingleLineWithDecimals() throws OverloadedDevice {
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(500);
        software.updateCart.removeItem(pluProduct);
        software.printReceipt.print();
        String expected = """
                     Barcoded product                             $1.00
                                                           Total: $1.00
                """;
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }

    @Test
    public void testPrintSingleLineWhereTheDescriptionIsTooLong() throws OverloadedDevice {
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        software.updateCart.removeItem(pluProduct);
        software.updateCart.removeItem(barcodeProduct);

        String barProductDescription1 = "------------------------------------------------------------";
        Numeral[] num2 = {Numeral.two};
        Barcode barcode1 = new Barcode(num2);
        BarcodedProduct barcodeProduct1 = new BarcodedProduct(barcode1, barProductDescription1, barProductPrice, barProductWeight);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1,barcodeProduct1);
        //pluProduct = new PLUCodedProduct(pluCode, pluProductDescription, pluProductPrice);
        software.updateCart.addProduct(barcodeProduct1);

        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(500);


        software.printReceipt.print();
        String expected = """
                     -----------------------------------...       $1.00
                                                           Total: $1.00
                """;
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }

    @Test
    public void testPrintMultiLine() throws OverloadedDevice {
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        hardware.getPrinter().addInk(5000);
        hardware.getPrinter().addPaper(500);
        software.updateCart.addProduct(barcodeProduct);
        hardware.getScanningArea().addAnItem(pluCodedItem);
        software.updateCart.addProduct(pluProduct);

        software.printReceipt.print();
        String expected = """
                     Barcoded product                             $1.00
                     Barcoded product                             $1.00
                     PLU product                                  $3.00
                                                           Total: $5.00
                """; // one new line included automatically
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }
}