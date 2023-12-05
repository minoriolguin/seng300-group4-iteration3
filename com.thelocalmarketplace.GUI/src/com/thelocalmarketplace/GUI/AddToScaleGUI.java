package com.thelocalmarketplace.GUI;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

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
		addToScaleFrame.setLayout(new GridLayout(1,2));
		addToScaleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		clickMe = new JButton("Add to scale");
		createWeightDiscrep = new JButton("Create Weight Discrep");
		
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
			software.getHardware().getBaggingArea().addAnItem(asItem);
			software.updateCart.addPLUProduct(covItem);
			addToScaleFrame.dispose();
			
			
		}
		//BARCODED PRODUCT
		else {
			
			BarcodedProduct covItem = (BarcodedProduct)item;
			Mass barMass = new Mass(covItem.getExpectedWeight());
			BarcodedItem barcodedItem = new BarcodedItem(covItem.getBarcode(),barMass);
			software.getHardware().getBaggingArea().addAnItem(barcodedItem);
			software.updateCart.addScannedProduct(covItem.getBarcode());
			addToScaleFrame.dispose();
		}
	}
	public void skip() {
		
	}
}
