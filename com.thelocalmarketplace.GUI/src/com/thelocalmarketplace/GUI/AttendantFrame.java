 /**
 *Project, Iteration 3, Group 4
 *  Group Members:
 * - Arvin Bolbolanardestani / 30165484
 * - Anthony Chan / 30174703
 * - Marvellous Chukwukelu / 30197270
 * - Farida Elogueil / 30171114
 * - Ahmed Elshabasi / 30188386
 * - Shawn Hanlon / 10021510
 * - Steven Huang / 30145866
 * - Nada Mohamed / 30183972
 * - Jon Mulyk / 30093143
 * - Althea Non / 30172442
 * - Minori Olguin / 30035923
 * - Kelly Osena / 30074352
 * - Muhib Qureshi / 30076351
 * - Sofia Rubio / 30113733
 * - Muzammil Saleem / 30180889
 * - Steven Susorov / 30197973
 * - Lydia Swiegers / 30174059
 * - Elizabeth Szentmiklossy / 30165216
 * - Anthony Tolentino / 30081427
 * - Johnny Tran / 30140472
 * - Kaylee Xiao / 30173778 
 **/

package com.thelocalmarketplace.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Attendant;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

public class AttendantFrame {
	 
	JLabel totalLabel;
	
    // Attendant Frame --------------------------------------BEGIN
	// It assumes that there is only one SelfCheckoutStation right now 
	private JFrame attend_frame;
	private Attendant attendant;
	public Product product;
	public Software software;
	
	JPanel middlePanel = new JPanel();
    public AttendantFrame(Software software) {
    	
    	this.software = software;
    	
    	// For Testing Purpose
//    	PriceLookUpCode testcode = new PriceLookUpCode("1234");
//    	PLUCodedProduct testProduct = new PLUCodedProduct(testcode,"Apple",5);
//    	screen.getSoftware().updateCart.addPLUProduct(testProduct);
    	
    	
    	attendant = software.attendant;
        attend_frame = new JFrame("Attendant Screen");
        attend_frame.setSize(450, 800);
        attend_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        attend_frame.setLocation(1000, 0); // Adjust the coordinates as needed

        // Top Panel (Text: Meow)
        JPanel topPanel = createLabelPanel("Attendant", 450, 150); 
        attend_frame.add(topPanel, BorderLayout.NORTH);

        // Middle Panel (DIsplay Area)
       
        attend_frame.add(middlePanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(5, 2));
        // Array of button titles
        String[] buttonTitles = {"Lookup Product",
        						"Remove Product",
        						"Refill Coins", 
        						 "Empty Coins", 
        						 "Refill Banknotes", 
        						 "Empty Banknotes", 
        						 "Add Reciept Paper", 
                                 "Add Reciept Ink",  
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
            	
                System.out.println("Lookup Product");
                //insert logic
                // Init new keyboard frame
                VirtualKeyboard keyboard = new VirtualKeyboard();
                // Run keyboard frame
                keyboard.run(software);
                break;
            case 2:
            	// Call removeItemCall function
            	removeItemCall();
            	
           
                break;
            case 3:
                System.out.println("Refill Coins");
                attendant.setAttendedToFalse();
                attendant.disableCustomerStation();
                attendant.refillBankNotes();
                break;
            case 4:
                System.out.println("Empty Coins");
                //insert Logic
                break;
            case 5:
                System.out.println("Refill Banknotes");
                attendant.refillBankNotes();
                break;
            case 6:
                System.out.println("Empty Banknotes");
                attendant.emptyBankNotes();
                break;
            case 7:
                System.out.println("Add Receipt Paper");
                //System.out.println(attendant.software.maintenance.getPaperRemaining());
                attendant.refillPaper();
                //System.out.println(attendant.software.maintenance.getPaperRemaining());
                break;
            case 8:
                System.out.println("Add Receipt Ink");
                //System.out.println(attendant.software.maintenance.getPaperRemaining());
                attendant.refillInk();
                //System.out.println(attendant.software.maintenance.getPaperRemaining());
                break;
                
                //do we wanna do a button that block/unblock customer?
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
    
    public void show() {
    	attend_frame.setVisible(true);
    }
    private void removeItemCall() {
    	// Creates a title called "Remove Items"
    	JLabel title = new JLabel("Remove Items:");
    	// Change middle panel display to box layout so items stack above each other
    	middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
    	// Add title to panel
    	middlePanel.add(title);
    	// Iterate through PLUCodedProduct in order list to get all items and make them a label with
    	// proper mouse click event handling and add to panel
    	for(PLUCodedProduct item: software.getPluCodedProductsInOrder()) {
    		JLabel itemLabel = new JLabel(item.getDescription());
    		itemLabel.setBorder(BorderFactory.createLineBorder(Color.black));
    		itemLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// Remove item from product list
					software.touchScreen.removeProduct(item);
					middlePanel.remove(itemLabel);
					middlePanel.revalidate();
					middlePanel.repaint();
					
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
    			
    		});
    		middlePanel.add(itemLabel);
    	}
    	// Iterate through BarCoded Product in order list to get all items and make them a label with
    	// proper mouse click event handling and add to panel
    	for(BarcodedProduct item: software.getBarcodedProductsInOrder()) {
    		JLabel itemLabel = new JLabel(item.getDescription());
    		itemLabel.setBorder(BorderFactory.createLineBorder(Color.black));
    		itemLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// Remove item from product list
					software.touchScreen.removeProduct(item);
					middlePanel.remove(itemLabel);
					middlePanel.revalidate();
					middlePanel.repaint();
					
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
    			
    		});
    		middlePanel.add(itemLabel);
    	}
    	// Make a finished button that is appended but clear all of middlePanel content when clicked
    	JButton finished = new JButton("Done");
    	finished.addActionListener(e->{
    		middlePanel.removeAll();
    		middlePanel.revalidate();
        	middlePanel.repaint();
        	RunGUI.setOrderTotal(software.getOrderTotal().intValue());
        	RunGUI.setWeight(software.getExpectedTotalWeight().inGrams());
        	RunGUI.updateOrderList();
    		
    	});
    	middlePanel.add(finished);
    	middlePanel.revalidate();
    	middlePanel.repaint();
    }
}
