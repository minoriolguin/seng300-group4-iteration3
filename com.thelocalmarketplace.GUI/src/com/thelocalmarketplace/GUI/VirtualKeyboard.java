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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Software;

public class VirtualKeyboard {
    private JTextArea inputArea;
    private JPanel resultArea= new JPanel(new GridLayout(4,5,20,20));
    private Software software;
    
    /** 
     * Function initialize the VirtualKeyboard frame and add appropriate component
      **/

    public void run(Software s) {
        JFrame frame = new JFrame("Virtual Keyboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setLayout(new BorderLayout());
        software = s;
        inputArea = new JTextArea();
        inputArea.setEditable(false);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        frame.add(inputScrollPane, BorderLayout.NORTH);
        
       
        frame.add(resultArea,BorderLayout.CENTER);

        JPanel keyboardPanel = createKeyboardPanel();
        frame.add(keyboardPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    /** 
     * Function to instantiate the keyboard panel and fill it with buttons specified in keys.
     * Returns the JPanel
      **/
    private JPanel createKeyboardPanel() {
        JPanel keyboardPanel = new JPanel(new GridLayout(4, 13));

        String[] keys = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M",
                "Backspace", "Space", "Enter"
        };

        for (String key : keys) {
            JButton button = new JButton(key);
            button.addActionListener((ActionListener) new KeyboardButtonListener());
            keyboardPanel.add(button);
        }

        return keyboardPanel;
    }
    /** 
     * Function to handle what happens when one of keyboard button is pressed
      **/
    private class KeyboardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton sourceButton = (JButton) e.getSource();
            String buttonText = sourceButton.getText();

            switch (buttonText) {
                case "Backspace":
                    if (inputArea.getText().length() > 0) {
                        inputArea.setText(inputArea.getText().substring(0, inputArea.getText().length() - 1));
                    }
                    break;
                case "Space":
                    inputArea.append(" ");
                    
                    break;
                case "Enter":
                	resultArea.removeAll();
                	resultArea.revalidate();
                	resultArea.repaint();
                	
                	ArrayList<Product> searchResult = software.updateCart.textSearch(inputArea.getText());
                	updateResultArea(searchResult);
                    break;
                
                default:
                	if(inputArea.getText().isBlank()) {
                		inputArea.append(buttonText);
                	}
                	else{
                		inputArea.append(buttonText.toLowerCase());
                	}
                    break;
            }

        
        }
        
        
        /** 
         * Function to update the display area when enter is hit
          **/
        private void updateResultArea(ArrayList<Product> result) {
            for(Product item: result) {
            	
            	if (!item.isPerUnit()) {
            		PLUCodedProduct product = (PLUCodedProduct)item;
            		 JButton button = new JButton(product.getDescription());
            		 button.addActionListener(e->handleProductClicked(product));
            		 resultArea.add(button);
            		 resultArea.revalidate();
            		 resultArea.repaint();
            	}
            	// Barcoded Product
            	else {
            		BarcodedProduct product = (BarcodedProduct)item;
	           		 JButton button = new JButton(product.getDescription());
	           		 button.addActionListener(e->handleProductClicked(product));
	           		 resultArea.add(button);
	           		 resultArea.revalidate();
	           		 resultArea.repaint();
	            	}
            }
        }	
        /** 
         * Updates customer product shopping cart when clicked
          **/
        private void handleProductClicked(Product item){
        	software.updateCart.addProduct(item);
        }
		
    }
}