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

package com.thelocalmarketplace.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AttendantFrame {
	 
	JLabel totalLabel;
	
    // Attendant Frame --------------------------------------BEGIN
	// It assumes that there is only one SelfCheckoutStation right now 
	
    public void AttendantFrame() {
        JFrame attend_frame = new JFrame("Attendant Screen"); 
        attend_frame.setSize(450, 800);
        attend_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        attend_frame.setLocation(1000, 0); // Adjust the coordinates as needed

        // Top Panel (Text: Meow)
        JPanel topPanel = createLabelPanel("Meow", 450, 150); 
        attend_frame.add(topPanel, BorderLayout.NORTH);

        // Middle Panel (Single Button)
        JPanel middlePanel = new JPanel();
        JButton topButton = new JButton("Top Button");
        topButton.addActionListener(e -> handleButtonClick(1)); // Assuming 1 corresponds to "Meow"
        middlePanel.add(topButton);
        attend_frame.add(middlePanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(5, 2));
        // Array of button titles
        String[] buttonTitles = {"Button 1", 
        						 "Button 2", 
        						 "Button 3", 
        						 "Button 4", 
        						 "Button 5", 
                                 "Button 6", 
                                 "Button 7", 
                                 "Button 8", 
                                 "Button 9", 
                                 "Button 10"};

        // Array of button listeners
        ActionListener[] buttonListeners = {
            e -> handleButtonClick(1),
            e -> handleButtonClick(2),
            e -> handleButtonClick(3),
            e -> handleButtonClick(4),
            e -> handleButtonClick(5),
            e -> handleButtonClick(6),
            e -> handleButtonClick(7),
            e -> handleButtonClick(8),
            e -> handleButtonClick(9),
            e -> handleButtonClick(10)
        };

        // Add buttons to the bottom panel with titles and listeners
        for (int i = 0; i < buttonTitles.length; i++) {
            JButton button = new JButton(buttonTitles[i]);
            button.addActionListener(buttonListeners[i]);
            bottomPanel.add(button);
        }
        // Add the components to the main frame
        attend_frame.setLayout(new BorderLayout());
        attend_frame.add(topPanel, BorderLayout.NORTH);
        attend_frame.add(middlePanel, BorderLayout.CENTER);
        attend_frame.add(bottomPanel, BorderLayout.SOUTH);
        
        attend_frame.setVisible(true);
    }

    
    
    private void handleButtonClick(int buttonNumber) {
        switch (buttonNumber) {
            case 1:
                System.out.println("Meow");
                //insert logic
                break;
            case 2:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 3:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 4:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 5:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 6:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 7:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 8:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 9:
                System.out.println("Button Clicked");
                //insert Logic
                break;
            case 10:
                System.out.println("Button Clicked");
                //insert Logic
                break;    
        }
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
