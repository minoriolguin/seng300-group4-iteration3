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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.software.WeightDiscrepancy;

public class AddOwnBagGUI extends JPanel {
	// want a button to add bags
	// want a button to remove bags
	// bag counter
	// back to og screen
	// message that tells customer that bags have been added
	
	// successful bag addition (software)
	// failure bag addition (software)
	private WeightDiscrepancy weightDiscrepancy;
	
	private JButton addOwnBagButton = new JButton("Add Own Bag");
	private JButton removeBagButton = new JButton("Remove Bag");
	private JButton backButton = new JButton("Back to checkout");
	private JLabel bagCounter = new JLabel();
	
	public AddOwnBagGUI(){
		this.weightDiscrepancy = weightDiscrepancy;
		
		
		// Initialize bag counter
	    bagCounter = new JLabel("0");
	    bagCounter.setPreferredSize(new Dimension(50, 50));
	    add(bagCounter);
		
		getAddOwnBagButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Increment the bag count and update the display
	            int count = Integer.parseInt(bagCounter.getText());
	            bagCounter.setText(Integer.toString(count + 1));
	            
	            // how to implement bags too heavy
	            // if not too heavy, then bag addition runs fine
			}
		});
		
		getAddOwnBagButton().setOpaque(true);
		getAddOwnBagButton().setPreferredSize(new Dimension(100, 100));
		add(getAddOwnBagButton());

		
		getRemoveBagButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int count = Integer.parseInt(bagCounter.getText());
	            if (count > 0) {
	                bagCounter.setText(Integer.toString(count - 1));
	            }
	        }
		});
		getRemoveBagButton().setOpaque(true);
		getRemoveBagButton().setPreferredSize(new Dimension(100, 100));
		add(getRemoveBagButton());
	
		
		getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
		getBackButton().setOpaque(true);
		getBackButton().setPreferredSize(new Dimension(100, 100));
		add(getBackButton());
	}
	
	
	public JButton getAddOwnBagButton() {
		return addOwnBagButton;
	}
	
	public void setAddOwnBagButton() {
		this.addOwnBagButton = addOwnBagButton;
	}
	
	public JButton getRemoveBagButton() {
		return removeBagButton;
	}
	
	public void setRemoveBagButton() {
		this.removeBagButton = removeBagButton;
	}
	
	public JButton getBackButton() {
		return backButton;
	}
	
	public void setBackButton() {
		this.backButton = backButton;
	}
}

