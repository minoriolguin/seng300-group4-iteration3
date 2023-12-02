package com.thelocalmarketplace.GUI;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

public class SelfCheckoutSimulation extends JFrame {
	 public SelfCheckoutSimulation() {
	        // Set frame properties
	        setTitle("Selfcheckout Station Simulation");
	        setSize(500, 500);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        // Create a panel with a FlowLayout to center the components
	        JPanel panel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER));

	        // Create three buttons
	        String[] buttonTitles = {"Bronze Station",
					"Gold Station",
					"Silver Station"
					 };

	        // Array of button listeners
	        ActionListener[] buttonListeners = {
	            e -> handleButtonClick(1),
	            e -> handleButtonClick(2),
	            e -> handleButtonClick(3),
	            
	        };

	        // Add buttons to the bottom panel with titles and listeners
	        for (int i = 0; i < buttonTitles.length; i++) {
	            JButton button = new JButton(buttonTitles[i]);
	            button.addActionListener(buttonListeners[i]);
	            panel.add(button);
	        }
	   

	        // Add panel to the frame
	        add(panel);

	        // Set frame visibility
	        setVisible(true);
	    }
	 
	 private void handleButtonClick(int buttonNumber) {
	        switch (buttonNumber) {
	            case 1:
	                System.out.println("Meow");
	                //insert logic
	                RunGUI gui = new RunGUI(); 
	                break;
	            case 2:
	                System.out.println("Button Clicked");
	                //insert Logic
	                break;
	            case 3:
	                System.out.println("Button Clicked");
	                //insert Logic
	                break;
	            
	        }
	       }
}
