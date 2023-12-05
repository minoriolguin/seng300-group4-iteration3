package com.thelocalmarketplace.GUI;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.jjjwelectronics.Item;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.software.Software;

public class bagItemGUI {
	private JFrame guiFrame;
	private Software software;
	public bagItemGUI(Item i,Software s) {
		software = s;
		guiFrame = new JFrame("Please Add Item To Bagging Area");
		guiFrame.setSize(300, 300);
		guiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JButton button = new JButton("Add to bagging Area");
		button.addActionListener(e->addToBag(i));
		guiFrame.add(button);
		guiFrame.setVisible(true);
		
	}
	public void addToBag(Item i) {
		software.getHardware().getBaggingArea().addAnItem(i);
		RunGUI.setOrderTotal(software.getOrderTotal().intValue());
    	RunGUI.setWeight(software.getExpectedTotalWeight().inGrams());
    	RunGUI.updateOrderList();
		guiFrame.dispose();
	}
}
