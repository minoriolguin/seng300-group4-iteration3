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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

//Screen 3 Payment Panel (Coin Coin)

/*
 * 3-part Method that causes a Numberpad
 */
class TransparentNumpadPanel extends JPanel {
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	    private GUILogic guiLogicInstance;

	    public TransparentNumpadPanel(GUILogic guiLogicInstance) {
	    	this.guiLogicInstance = guiLogicInstance;
	    	System.out.println("jsdfjsdfbsjbf");
            setOpaque(true); // Make the panel transparent
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(400, 400)); // Set the preferred size to 400x400
        }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        // Draw a semi-transparent background
	        Graphics2D g2d = (Graphics2D) g.create();
	        g2d.setColor(new Color(128, 128, 128, 128)); // 128, 128, 128 is grey, 128 is the alpha value
	        g2d.fillRect(0, 0, getWidth(), getHeight());
	        g2d.dispose();
	    }

        public void addNumPadButtons() {
            // Create buttons and add them to the center of the panel
            JButton button1 = new JButton("1");
            JButton button2 = new JButton("2");
            JButton button3 = new JButton("3");
            JButton button4 = new JButton("1");
            JButton button5 = new JButton("2");
            JButton button6 = new JButton("3");
            JButton button7 = new JButton("1");
            JButton button8 = new JButton("2");
            JButton button9 = new JButton("3");
            JButton buttonCLEAR = new JButton("CLEAR");
            JButton button0 = new JButton("0");
            JButton buttonENTER = new JButton("ENTER");

            GridBagConstraints gbc = new GridBagConstraints();
            //Placements of Button (indentation for readability)
            gbc.gridy = 0;
                gbc.gridx = 0;
                add(button1, gbc);
                gbc.gridx = 1;
                add(button2, gbc);
                gbc.gridx = 2;
                add(button3, gbc);
            gbc.gridx = 1;    
                gbc.gridx = 0;
                add(button4, gbc);
                gbc.gridx = 1;
                add(button5, gbc);
                gbc.gridx = 2;
                add(button6, gbc);
            
            
            

            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	guiLogicInstance.addItemPopUp_button1_CustomersAddsToBaggingArea();
                	setVisible(false);                }
            });
            button2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	guiLogicInstance.addItemPopUp_button2_CustomersDOESNOTAddsToBaggingArea();
                	setVisible(false);                }
            });
            button3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	guiLogicInstance.addItemPopUp_button3_BLANK();
                	setVisible(false);                }
            });
	}
        
       
}