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

import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Software;

public class VirtualKeyboard {
    private JTextArea inputArea;
    private JPanel resultArea= new JPanel(new GridLayout(4,5));
    private Software software;

    

    public void run(Software s) {
        JFrame frame = new JFrame("Virtual Keyboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setLayout(new BorderLayout());
        software = s;
        inputArea = new JTextArea();
        inputArea.setEditable(false);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        frame.add(inputScrollPane, BorderLayout.NORTH);

//        resultArea = new JTextArea();
//        resultArea.setEditable(false);
//        JScrollPane resultScrollPane = new JScrollPane(resultArea);
//        frame.add(resultScrollPane, BorderLayout.CENTER);
       
        frame.add(resultArea,BorderLayout.CENTER);

        JPanel keyboardPanel = createKeyboardPanel();
        frame.add(keyboardPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

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
                	ArrayList<Product> searchResult = software.updateCart.textSearch(inputArea.getText());
                	updateResultArea(searchResult);
                    break;
                
                default:
                    inputArea.append(buttonText);
                    break;
            }

        
        }

        private void updateResultArea(ArrayList<Product> result) {
            for(Product item: result) {
            	//PLU Product
            	if (!item.isPerUnit()) {
            		PLUCodedProduct product = (PLUCodedProduct)item;
            		 JButton button = new JButton(product.getDescription());
            		
            		 resultArea.add(button);
            		 resultArea.revalidate();
            		 resultArea.repaint();
            	}
            }
        }

		
    }
}