package com.thelocalmarketplace.software.test.controllers;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;

import com.jjjwelectronics.scanner.Barcode;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;

import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

import com.thelocalmarketplace.software.controllers.ReceiptPrintingController;
import com.thelocalmarketplace.software.logic.AttendantLogic;
import com.thelocalmarketplace.software.logic.CentralStationLogic;
import com.thelocalmarketplace.software.logic.StateLogic.States;

import powerutility.PowerGrid;

/**
 * @author Phuong Le (30175125)
 * ----------------------------
 * @author Angelina Rochon (30087177)
 * @author Connell Reffo (10186960)
 * @author Tara Strickland (10105877)
 * @author Julian Fan (30235289)
 * @author Braden Beler (30084941)
 * @author Samyog Dahal (30194624)
 * @author Maheen Nizmani (30172615)
 * @author Daniel Yakimenka (10185055)
 * @author Merick Parkinson (30196225)
 * @author Farida Elogueil (30171114)
 */
public class ReceiptPrintingTests {

    private CentralStationLogic session;
    private SelfCheckoutStationBronze station;
    private ReceiptPrintingController controller;
    

    @Before
    public void setUp() {
        PowerGrid.engageUninterruptiblePowerSource();
        PowerGrid.instance().forcePowerRestore();
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        station = new SelfCheckoutStationBronze();
        station.plugIn(PowerGrid.instance());
        station.turnOn();
        session = new CentralStationLogic(station);
        session.startSession();
        controller = new ReceiptPrintingController(session);
              
    }

    @After
    public void tearDown() {
        PowerGrid.engageUninterruptiblePowerSource();
    }
    
    @Test
    public void testHandlePrintReceiptWithoutInk() throws OverloadedDevice {
    	Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "TestProduct", 1, 100.0);      
        session.cartLogic.addProductToCart(product1);      	
        station.printer.addPaper(1000);
        controller.handlePrintReceipt(new BigDecimal(0));
        assertTrue(this.session.stateLogic.inState(States.SUSPENDED));
    }
    
    @Test
    public void testHandlePrintReceiptWithoutPaper() throws OverloadedDevice {
    	Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "TestProduct", 1, 100.0);      
        session.cartLogic.addProductToCart(product1);  
        station.printer.addInk(1000);
        controller.handlePrintReceipt(new BigDecimal(0));
        
        assertTrue(this.session.stateLogic.inState(States.SUSPENDED));
    }
    
    @Test
    public void testPrintReceiptWithPaperandInk() throws OverloadedDevice {
    	Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "TestProduct", 1, 100.0);      
        session.cartLogic.addProductToCart(product1);  
        station.printer.addInk(1000);
        station.printer.addPaper(1000);
        controller.handlePrintReceipt(new BigDecimal(0));
        
        assertNotEquals(this.session.stateLogic.getState(), States.SUSPENDED);
    }
    
    @Test
    public void testNotifyOutofInk() throws OverloadedDevice {
    	Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "TestProduct", 1, 100.0);      
        session.cartLogic.addProductToCart(product1);  
        station.printer.addInk(5);
        station.printer.addPaper(5);
        controller.handlePrintReceipt(new BigDecimal(0));
        
        assertEquals(this.session.stateLogic.getState(), States.SUSPENDED);
    }
    
    @Test
    public void testNotifyOutofPaper() throws OverloadedDevice {
    	Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "TestProduct", 1, 100.0);      
        session.cartLogic.addProductToCart(product1);  
        station.printer.addInk(1000);
        station.printer.addPaper(1);
        controller.handlePrintReceipt(new BigDecimal(0));
        
        assertEquals(this.session.stateLogic.getState(), States.SUSPENDED);
    }
    
    @Test
    public void testAttendantResolvingError() throws OverloadedDevice {
    	Barcode barcode1 = new Barcode(new Numeral[] {Numeral.one});
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "TestProduct", 1, 100.0);      
        session.cartLogic.addProductToCart(product1);  
        station.printer.addInk(1000);
        station.printer.addPaper(1);
        controller.handlePrintReceipt(new BigDecimal(0));
        
        assertEquals(this.session.stateLogic.getState(), States.SUSPENDED);
        
        station.printer.addPaper(100);
        AttendantLogic attendant = new AttendantLogic(session);
        attendant.printDuplicateReceipt();
        assertEquals(this.session.stateLogic.getState(), States.NORMAL);
    }
}
