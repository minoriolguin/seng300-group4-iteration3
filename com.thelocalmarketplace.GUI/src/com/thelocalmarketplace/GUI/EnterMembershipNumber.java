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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EnterMembershipNumber extends JPanel {
	JLabel text = new JLabel("Please enter your membership number here");
    JTextField textField = new JTextField();
    JLabel text2 = new JLabel("Membership number successfully entered");
    JButton close = new JButton("Continue");
    JPanel panel2 = new JPanel();
    
    JPanel labelPanel = new JPanel();
    JPanel textPanel = new JPanel();
    

	RunGUI instance;
	
	public EnterMembershipNumber(RunGUI instance) {
		this.instance = instance;
		textField.setPreferredSize(new Dimension(150, 30));
		textField.setAlignmentX(CENTER_ALIGNMENT);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		labelPanel.add(text);
		textPanel.add(textField);
		add(labelPanel);
		add(textPanel);
		add(createNumberPadPanel());
		add(panel2);

		panel2.add(text2);
    	panel2.add(close);
    	panel2.setPreferredSize(new Dimension(300, 300));
    	text2.setVisible(false);
    	close.setVisible(false);
		
		//close button after membership number is entered
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				instance.switchPanels("AddItemsPanel");
				
				reset();
				
			}
			
		});
	}
	
	//called after a number is entered
	private void confirm() {
    	text2.setVisible(true);
    	close.setVisible(true);
    	//maybe needs to send the membership number entered to another class in the software to be stored?
		//unclear what the system must do with the membership number
	}
	
	private void reset() {
    	text2.setVisible(false);
    	close.setVisible(false);
    	textField.setText("");
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

    private class NumberPadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton sourceButton = (JButton) e.getSource();
            String buttonText = sourceButton.getText();

            if (buttonText.equals("Clear")) {
                textField.setText("");
            } else if (buttonText.equals("Enter")) {
            	confirm();
            } else {
            	textField.setText(textField.getText() + buttonText);
            }
        }
    }


}
