package com.thelocalmarketplace.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class EnterPLU extends JPanel{	
    JPanel labelPanel = new JPanel();
    JPanel textPanel = new JPanel();
    
	JLabel text = new JLabel("Enter PLU Code");
    JTextField textField = new JTextField();
    
    RunGUI instance;
    public static String textPLUcode;
    
	public EnterPLU(RunGUI instance) {
		this.instance = instance;
		
		textField.setPreferredSize(new Dimension(100, 40));
		textField.setAlignmentX(CENTER_ALIGNMENT);
		textPanel.add(textField);
		
		labelPanel.add(text);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
		add(labelPanel);
		add(textPanel);
		add(createNumberPadPanel());
	
	
	}
	
	private JPanel createNumberPadPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 5, 5));

        for (int i = 1; i <= 9; i++) {
            addButton(panel, String.valueOf(i));
        }

        addButton(panel, "0");
        addButton(panel, "Clear");
        addButton(panel, "Enter");     

        return panel;
    }
	
	private void addButton(JPanel panel, String label) {
        JButton button = new JButton(label);
        button.addActionListener(new NumberPadButtonListener());
        panel.add(button);
    }

    public class NumberPadButtonListener implements ActionListener {
    		@Override
        public void actionPerformed(ActionEvent e) {
            JButton sourceButton = (JButton) e.getSource();
            String buttonText = sourceButton.getText();

            if (buttonText.equals("Clear")) {
                textField.setText("");
            } else if (buttonText.equals("Enter")){
            		textPLUcode = textField.getText();
            		//guiLogicInstance.buttonR7_CustomerAddsItem_PLUCode(code);
				instance.switchPanels("itemAddedFrame"); 
				textPLUcode = "";
				textField.setText("");	
            } else {
            		textField.setText(textField.getText() + buttonText);
            		textPLUcode = textField.getText();
            	
            }
        }
    }

    
}
