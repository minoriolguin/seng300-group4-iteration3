package com.thelocalmarketplace.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.TouchScreen;

public class ItemAddedFrame extends JPanel{

	public TouchScreen screen;
	private RunGUI instance;
	private GUILogic guiLogicInstance;
	private EnterPLU plu;
	
	public ItemAddedFrame(RunGUI instance) {
		
		guiLogicInstance = new GUILogic (screen);
		String item = guiLogicInstance.buttonR7_CustomerAddsItem_PLUCode();
		//Top Panel
		JPanel topPanel = createLabelPanel("Item Added", 400, 100); 
        
        //Middle Panel
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setSize(400, 200); 
        
        JLabel product = new JLabel(item);
        product.setHorizontalAlignment(SwingConstants.CENTER);
        product.setVerticalAlignment(SwingConstants.CENTER);
        product.setFont(new Font("Arial", Font.BOLD, 16));
        middlePanel.add(product, BorderLayout.CENTER);
        
        //Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
		JButton closeButton = new JButton("Put Item on Scale");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    
				instance.switchPanels("AddItemsPanel");
			}
		});
		
		closeButton.setFont(new Font("Arial", Font.BOLD, 10));
		bottomPanel.add(closeButton);
		
		//add panels to frame
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
	
	}
	
    private JPanel createLabelPanel(String labelText, int width, int height) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(width, height));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, gbc);
        return panel;
    }
}
