package com.thelocalmarketplace.GUI;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Software;

public class AddToScaleGUI extends JFrame {
	private JFrame addToScaleFrame;
	private JButton clickMe;
	private JButton createWeightDiscrep;
	private Product item;
	private Software software;

	public AddToScaleGUI(Product i,Software s) {
		item = i;
		software = s;
		addToScaleFrame = new JFrame("Customer Choice");
		addToScaleFrame.setSize(300,300);
		addToScaleFrame.setLayout(new GridLayout(2,2));
		addToScaleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		clickMe = new JButton("Add to scale");
		createWeightDiscrep = new JButton("Create Weight Discrepency");
	
		clickMe.addActionListener(e->clicked());
		createWeightDiscrep.addActionListener(e->skip());
		addToScaleFrame.add(clickMe);
		addToScaleFrame.add(createWeightDiscrep);
	
		
		addToScaleFrame.setVisible(true);
	}
	
	
	public void clicked() {
		// PLUCODED PRODUCT
		if (item instanceof PLUCodedProduct) {
			
			PLUCodedProduct covItem = (PLUCodedProduct)item;
			Mass weight = new Mass(200);
			PLUCodedItem asItem = new PLUCodedItem(covItem.getPLUCode(),weight);
			software.getHardware().getScanningArea().addAnItem(asItem);
			software.updateCart.addPLUProduct(covItem);
			software.getHardware().getScanningArea().removeAnItem(asItem);
			software.getHardware().getBaggingArea().addAnItem(asItem);
			RunGUI.setOrderTotal(software.getOrderTotal().intValue());
        	RunGUI.setWeight(software.getExpectedTotalWeight().inGrams());
        	RunGUI.updateOrderList();
			addToScaleFrame.dispose();
			
			
		}
		//BARCODED PRODUCT
		else {
			
			BarcodedProduct covItem = (BarcodedProduct)item;
			Mass barMass = new Mass(covItem.getExpectedWeight());
			BarcodedItem barcodedItem = new BarcodedItem(covItem.getBarcode(),barMass);
			software.getHardware().getBaggingArea().addAnItem(barcodedItem);
			software.updateCart.addScannedProduct(covItem.getBarcode());
			RunGUI.setOrderTotal(software.getOrderTotal().intValue());
        	RunGUI.setWeight(software.getExpectedTotalWeight().inGrams());
        	RunGUI.updateOrderList();
			addToScaleFrame.dispose();
		}
	}
	public void skip() {
if (item instanceof PLUCodedProduct) {
			
			PLUCodedProduct covItem = (PLUCodedProduct)item;
			Mass weight = new Mass(200);
			PLUCodedItem asItem = new PLUCodedItem(covItem.getPLUCode(),weight);
			software.getHardware().getScanningArea().addAnItem(asItem);
			software.updateCart.addPLUProduct(covItem);
			software.getHardware().getScanningArea().removeAnItem(asItem);
			bagItemGUI newScreen = new bagItemGUI(asItem,software);
						
		}
		//BARCODED PRODUCT
		else {
			
			BarcodedProduct covItem = (BarcodedProduct)item;
			Mass barMass = new Mass(covItem.getExpectedWeight());
			BarcodedItem barcodedItem = new BarcodedItem(covItem.getBarcode(),barMass);
			software.updateCart.addScannedProduct(covItem.getBarcode());
			bagItemGUI newScreen = new bagItemGUI(barcodedItem,software);
			
		}
	}

}
