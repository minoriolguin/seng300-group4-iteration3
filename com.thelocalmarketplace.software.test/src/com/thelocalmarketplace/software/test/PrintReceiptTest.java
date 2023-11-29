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
    ArrayList<BarcodedProduct> cart;
    BarcodedProduct product;
    PrintReceiptTestAttendantStub attendantStub;

    // barcode product details
    Barcode barcode;
    String description;
    long price;
    double weight;

    // plu product details


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
        description = "product";
        price = 100L;
        weight = 1;

        product = new BarcodedProduct(barcode,description,price,weight);
        cart = new ArrayList<>();
        cart.add(product);
        
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

        cart = new ArrayList<>();

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.print();
        assertEquals(1, attendantStub.outOfPaperCounter);
    }

    @Test
    public void testOutOfInkCallsAttendantStub() throws OverloadedDevice {
        PrintReceipt.setStartString(".\n");
        PrintReceipt.setEndString("");
        cart = new ArrayList<>();

        printerBronze = new ReceiptPrinterBronze();
        printerBronze.plugIn(power);
        printerBronze.turnOn();
        printerBronze.addInk(1);
        printerBronze.addPaper(500);
        attendantStub = new PrintReceiptTestAttendantStub(printerBronze);
        printerBronze.register(attendantStub);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.print();
        assertEquals(1, attendantStub.outOfInkCounter);
    }

    @Test
    public void testPrintOnlyDefaultTemplate(){
        cart = new ArrayList<>();
        PrintReceipt r = new PrintReceipt(printerBronze);
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
        cart = new ArrayList<>();
        cart.add(product);
        PrintReceipt r = new PrintReceipt(printerBronze);
        r.print();
        String expected = """
                    Hello
                         product                                      $1.00
                                                               Total: $1.00
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
        cart = new ArrayList<>();
        cart.add(product);
        PrintReceipt r = new PrintReceipt(printerBronze);
        r.print();
        String expected = """
                     product                                      $1.00
                                                           Total: $1.00
                """;
        assertEquals(expected, printerBronze.removeReceipt());
    }

    @Test
    public void testPrintSimpleSingleLineSilver(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        cart = new ArrayList<>();
        cart.add(product);
        PrintReceipt r = new PrintReceipt(printerSilver);
        r.print();
        String expected = """
                     product                                      $1.00
                                                           Total: $1.00
                """;
        assertEquals(expected, printerSilver.removeReceipt());
    }

    @Test
    public void testPrintSimpleSingleLineGold(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");
        cart = new ArrayList<>();
        cart.add(product);
        PrintReceipt r = new PrintReceipt(printerGold);
        r.print();
        String expected = """
                     product                                      $1.00
                                                           Total: $1.00
                """;
        assertEquals(expected, printerGold.removeReceipt());
    }

    @Test
    public void testPrintSingleLineWithDecimals(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        price = 251L;
        product = new BarcodedProduct(barcode, description, price, weight);

        cart = new ArrayList<>();
        cart.add(product);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.print();
        String expected = """
                     product                                      $2.51
                                                           Total: $2.51
                """;
        assertEquals(expected, printerBronze.removeReceipt());
    }

    @Test
    public void testPrintSingleLineWhereTheDescriptionIsTooLong(){
        PrintReceipt.setStartString("");
        PrintReceipt.setEndString("");

        description = "------------------------------------------------------------";
        product = new BarcodedProduct(barcode, description, price, weight);

        cart = new ArrayList<>();
        cart.add(product);

        PrintReceipt r = new PrintReceipt(printerBronze);
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

        cart = new ArrayList<>();
        cart.add(product);
        cart.add(product);
        cart.add(product);
        cart.add(product);

        PrintReceipt r = new PrintReceipt(printerBronze);
        r.print();
        String expected = """
                     product                                      $1.00
                     product                                      $1.00
                     product                                      $1.00
                     product                                      $1.00
                                                           Total: $4.00
                """; // one new line included automatically
        assertEquals(expected, printerBronze.removeReceipt());
    }
}