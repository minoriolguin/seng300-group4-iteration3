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
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.software.PrintReceipt;

import powerutility.PowerGrid;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PrintReceiptTest{
    PowerGrid power = PowerGrid.instance();
    ReceiptPrinterBronze printerBronze;
    ReceiptPrinterSilver printerSilver;
    ReceiptPrinterGold printerGold;
    ArrayList<BarcodedProduct> barcodeInCart;
    ArrayList<PLUCodedProduct> pluInCart;
    BarcodedProduct barcodeProduct;
    PLUCodedProduct pluProduct;
    PrintReceiptTestAttendantStub attendantStub;

    // barcode product details
    Barcode barcode;
    String barProductDescription;
    long barProductPrice;
    double barProductWeight;

    // plu product details
    PriceLookUpCode pluCode;
    String pluProductDescription;
    long pluProductPrice;
    
    @Before
    public void setUp() throws OverloadedDevice {
        // hardware setup
        power = PowerGrid.instance();
        PowerGrid.engageUninterruptiblePowerSource();
        
        printerBronze = new ReceiptPrinterBronze();
        printerBronze.plugIn(power);
        printerBronze.turnOn();
        printerBronze.addInk(5000);
        printerBronze.addPaper(500);
        printerSilver = new ReceiptPrinterSilver();
        printerSilver.plugIn(power);
        printerSilver.turnOn();
        printerSilver.addInk(5000);
        printerSilver.addPaper(500);
        printerGold = new ReceiptPrinterGold();
        printerGold.plugIn(power);
        printerGold.turnOn();
        printerGold.addInk(5000);
        printerGold.addPaper(500);

        // Restore PrintReceipt to default
        PrintReceipt.defaultStartString();
        PrintReceipt.defaultEndString();

        // Product setup
        Numeral[] num1 = {Numeral.one};
        barcode = new Barcode(num1);
        barProductDescription = "Barcoded product";
        barProductPrice = 100L;
        barProductWeight = 1;
        
        pluCode = new PriceLookUpCode("1234");
        pluProductDescription = "PLU product";
        pluProductPrice = 150L;

        barcodeProduct = new BarcodedProduct(barcode,barProductDescription,barProductPrice,barProductWeight);
        barcodeInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);
        
        pluProduct = new PLUCodedProduct(pluCode, pluProductDescription, pluProductPrice);
        pluInCart = new ArrayList<>();
        pluInCart.add(pluProduct);
        
        // stub setup
        attendantStub = new PrintReceiptTestAttendantStub(printerBronze);
        printerBronze.register(attendantStub);
    }

   
    
    @Test
    public void testOutOfPaperCallsAttendantStub() throws OverloadedDevice {
        PrintReceipt.setStartString("Hello!\n");
        PrintReceipt.setEndString("");

        printerBronze = new ReceiptPrinterBronze();
        printerBronze.plugIn(power);
        printerBronze.turnOn();
        printerBronze.addInk(5000);
        printerBronze.addPaper(1);
        attendantStub = new PrintReceiptTestAttendantStub(printerBronze);
        printerBronze.register(attendantStub);

        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        
        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        assertEquals(1, attendantStub.outOfPaperCounter);
    }

    @Test
    public void testOutOfInkCallsAttendantStub() throws OverloadedDevice {
        PrintReceipt.setStartString(".\n");
        PrintReceipt.setEndString("");
        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();

        printerBronze = new ReceiptPrinterBronze();
        printerBronze.plugIn(power);
        printerBronze.turnOn();
        printerBronze.addInk(1);
        printerBronze.addPaper(500);
        attendantStub = new PrintReceiptTestAttendantStub(printerBronze);
        printerBronze.register(attendantStub);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        assertEquals(1, attendantStub.outOfInkCounter);
    }

    @Test
    public void testPrintOnlyDefaultTemplate(){
        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();        
        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                    
                    Thelocalmarketplace
                    ------------------------------------------------------------
                    ------------------------------------------------------------
                    Thank you!
                    Please come again!
                    """;// new line is entered automatically
        assertEquals(expected, printerBronze.removeReceipt());
    }

    @Test
    public void testSetTemplatesAndSimpleSingleLine(){
        PrintReceipt.setStartString("Hello\n");
        PrintReceipt.setEndString("world!\n");
        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);
        pluInCart.add(pluProduct);
        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                    Hello
                         Barcoded product                             $1.00
                         PLU product                                  $1.50
                                                               Total: $2.50
                    world!
                    """;
        assertEquals(expected, printerBronze.removeReceipt());
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
    public void testPrintSimpleSingleLineBronze(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);
        pluInCart.add(pluProduct);
        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                     Barcoded product                             $1.00
                     PLU product                                  $1.50
                                                           Total: $2.50
                """;
        assertEquals(expected, printerBronze.removeReceipt());
    }

    @Test
    public void testPrintSimpleSingleLineSilver(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);
        pluInCart.add(pluProduct);
        PrintReceipt r = new PrintReceipt(printerSilver);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);

        r.print();
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        String expected = """
                     Barcoded product                             $1.00
                     PLU product                                  $1.50
                                                           Total: $2.50
                """;
        assertEquals(expected, printerSilver.removeReceipt());
    }

    @Test
    public void testPrintSimpleSingleLineGold(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);
        pluInCart.add(pluProduct);
        PrintReceipt r = new PrintReceipt(printerGold);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                     Barcoded product                             $1.00
                     PLU product                                  $1.50
                                                           Total: $2.50
                """;
        assertEquals(expected, printerGold.removeReceipt());
    }

    @Test
    public void testPrintSingleLineWithDecimals(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        barProductPrice = 251L;
        barcodeProduct = new BarcodedProduct(barcode, barProductDescription, barProductPrice, barProductWeight);

        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                     Barcoded product                             $2.51
                                                           Total: $2.51
                """;
        assertEquals(expected, printerBronze.removeReceipt());
    }

    @Test
    public void testPrintSingleLineWhereTheDescriptionIsTooLong(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        barProductDescription = "------------------------------------------------------------";
        barcodeProduct = new BarcodedProduct(barcode, barProductDescription, barProductPrice, barProductWeight);
        pluProduct = new PLUCodedProduct(pluCode, pluProductDescription, pluProductPrice);

        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                     -----------------------------------...       $1.00
                                                           Total: $1.00
                """;
        assertEquals(expected, printerBronze.removeReceipt());
    }

    @Test
    public void testPrintMultiLine(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        barcodeInCart = new ArrayList<>();
        pluInCart = new ArrayList<>();
        barcodeInCart.add(barcodeProduct);
        barcodeInCart.add(barcodeProduct);
        pluInCart.add(pluProduct);
        pluInCart.add(pluProduct);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.setBarcodedProductsInCart(barcodeInCart);
        r.setPluInCart(pluInCart);
        r.print();
        String expected = """
                     Barcoded product                             $1.00
                     Barcoded product                             $1.00
                     PLU product                                  $1.50
                     PLU product                                  $1.50
                                                           Total: $5.00
                """; // one new line included automatically
        assertEquals(expected, printerBronze.removeReceipt());
    }
}