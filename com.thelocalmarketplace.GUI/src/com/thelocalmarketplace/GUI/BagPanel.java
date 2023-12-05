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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.jjjwelectronics.EmptyDevice;
import com.thelocalmarketplace.software.Software;
import com.thelocalmarketplace.software.TouchScreen;

public class BagPanel {

   public Software software;
   private JFrame bag_panel;
   
   private JButton addOwnBagButton = new JButton("Add Own Bag");
   private JButton purchaseBagsButton = new JButton("Purchase Bag");
   private JButton removeBagButton = new JButton("Remove Bag");
   private JButton backButton = new JButton("Back");
   private JLabel bagCounter = new JLabel();
   private int bagWeight;

   public BagPanel(Software software) {
       this.software = software;
       bag_panel = new JFrame("Bagging Screen");
       bag_panel.setSize(450, 800);
       bag_panel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       bag_panel.setLocation(1000, 0); // Adjust the coordinates as needed
       bag_panel.setLayout(new GridLayout(0, 2));
       
       // Add a title above the bag counter
       JLabel title = new JLabel("Bag Counter");
       title.setAlignmentX(Component.CENTER_ALIGNMENT);
       bag_panel.add(title, BorderLayout.NORTH);

       // Initialize bag counter
       bagCounter = new JLabel("0");
       bagCounter.setAlignmentX(Component.CENTER_ALIGNMENT);
       bagCounter.setPreferredSize(new Dimension(50, 50));
       bag_panel.add(bagCounter, BorderLayout.NORTH);
       
       getAddOwnBagButton().addActionListener(new ActionListener() {
    	   @Override
    	   public void actionPerformed(ActionEvent e) {
    		   software.touchScreen.selectAddOwnBags();
	           int count = Integer.parseInt(bagCounter.getText());
	           bagCounter.setText(Integer.toString(count + 1));
	           setBagWeight(addBagSimulation());
	           software.touchScreen.selectAddOwnBags();
	           software.touchScreen.selectBagsAdded();
    	   }
    	});
       addOwnBagButton.setOpaque(true);
       addOwnBagButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       addOwnBagButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
       addOwnBagButton.setBackground(new Color(137, 221, 255));
       addOwnBagButton.setBounds(980, 495, 280, 55);
       bag_panel.add(addOwnBagButton);

    	getPurchaseBagsButton().addActionListener(new ActionListener() {
    	   @Override
    	   public void actionPerformed(ActionEvent e) {
    	       // Call the method to purchase a bag in the TouchScreen class
    	       try {
    	           software.touchScreen.purchaseBags(1);
    	           int count = Integer.parseInt(bagCounter.getText());
    	           bagCounter.setText(Integer.toString(count + 1));
    	           software.touchScreen.selectBagsAdded();
    	       } catch (EmptyDevice e1) {
    	    	   JOptionPane.showMessageDialog(null, "Bag dispenser is empty");
    	       }
    	   }
    	}); 
    	purchaseBagsButton.setOpaque(true);
        purchaseBagsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        purchaseBagsButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
        purchaseBagsButton.setBackground(new Color(137, 221, 255));
        purchaseBagsButton.setBounds(980, 495, 280, 55);
    	bag_panel.add(purchaseBagsButton);
        
    	getRemoveBagButton().addActionListener(new ActionListener() {
     	   @Override
     	   public void actionPerformed(ActionEvent e) {
 	           int count = Integer.parseInt(bagCounter.getText());
 	           bagCounter.setText(Integer.toString(count - 1));
     	   }
     	});
    	removeBagButton.setOpaque(true);
        removeBagButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBagButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
        removeBagButton.setBackground(new Color(137, 221, 255));
        removeBagButton.setBounds(980, 495, 280, 55);
    	bag_panel.add(removeBagButton);
    	
    	getBackButton().addActionListener(new ActionListener() {
      	   @Override
      	   public void actionPerformed(ActionEvent e) {
      		 JOptionPane.showMessageDialog(null, "Bags have been added");
      		 bag_panel.setVisible(false);
      	   }
      	});
    	backButton.setOpaque(true);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
        backButton.setBackground(new Color(137, 221, 255));
        backButton.setBounds(980, 495, 280, 55);
    	bag_panel.add(backButton);
	
   	}
   
   public int addBagSimulation() {
	   bagWeight = 0;
	   JFrame add_own_bag_frame = new JFrame();
	   JPanel add_own_bag_panel = new JPanel();
	   JLabel bagWeightPromptLabel = new JLabel("<html><p>Select how heavy your bag  is: "
			   									+"<br>Selecting a weight will simulate placeing "
			   									+"your bag in the bagging area!<p><html>");
	   JButton lightBagButton = new JButton();
	   JButton heavyBagButton = new JButton();
	   JLabel lightBagLabel = new JLabel("100 g");
	   JLabel heavyBagLabel = new JLabel("300 g");
	   
	   add_own_bag_frame.add(add_own_bag_panel);
	   add_own_bag_panel.setLayout(new GridLayout(3,3));
	   lightBagButton.add(lightBagLabel);
	   heavyBagButton.add(heavyBagLabel);
	   
	   add_own_bag_panel.add(bagWeightPromptLabel);
	   add_own_bag_panel.add(lightBagButton);
	   add_own_bag_panel.add(heavyBagButton);
	  
	   add_own_bag_frame.setSize(400, 400);
	   add_own_bag_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	   add_own_bag_frame.setVisible(true);
	   
	   lightBagButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e)
		    {	
		    	bagWeight = 100;
		    	add_own_bag_frame.dispose();
		    }
		});
	   
	   heavyBagButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e)
		    {
		    	bagWeight = 300;
		    	add_own_bag_frame.dispose();
		    }
		});
	   return bagWeight;
   }
   
   public int getBagWeight() {
	   return bagWeight;
   }
   
   public JButton getAddOwnBagButton() {
	   return addOwnBagButton;
   }
   
   public JButton getPurchaseBagsButton() {
		return purchaseBagsButton;
   }
   
   public JButton getRemoveBagButton() {
	   return removeBagButton;
   }
   
   public JButton getBackButton() {
	   return backButton;
   }
 
   public void setBagWeight(int bagWeight) {
	this.bagWeight = bagWeight;
   }
   
   public void setAddOwnBagButton(JButton addOwnBagButton) {
	   this.addOwnBagButton = addOwnBagButton;
   }
   
   public void setpurchaseBagsButton(JButton purchaseBagsButton) {
	   this.purchaseBagsButton = purchaseBagsButton;
   }
   
   public void setremoveBagButton(JButton removeBagButton) {
	   this.removeBagButton = removeBagButton;
   }
   
   public void setbackButton(JButton backButton) {
	   this.backButton = backButton;
   }
   
   public void show() {
	   bag_panel.setVisible(true);
   }
   
 }     
       