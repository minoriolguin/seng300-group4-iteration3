// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948
package com.thelocalmarketplace.software.test;

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
        software.updateCart.addPLUProduct(pluProduct);
        
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
        assertTrue(software.getProductsInOrder().isEmpty());
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
        software.updateCart.addProduct(pluProduct);

        software.printReceipt.print();
        String expected = """
                     Barcoded product                             $1.00
                     Barcoded product                             $1.00
                     PLU product                                  $1.50
                     PLU product                                  $1.50
                                                           Total: $5.00
                """; // one new line included automatically
        assertEquals(expected, hardware.getPrinter().removeReceipt());
    }
}