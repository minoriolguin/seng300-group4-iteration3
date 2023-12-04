 /**
 *Project 3 Iteration Group 4
 *  Group Members:
 * - Julie Kim 10123567
 * - Aryaman Sandhu 30017164
 * - Arcleah Pascual 30056034
 * - Aoi Ueki 30179305
 * - Ernest Shukla 30156303
 * - Shawn Hanlon 10021510
 * - Jaimie Marchuk 30112841
 * - Sofia Rubio 30113733
 * - Maria Munoz 30175339
 * - Anne Lumumba 30171346
 * - Nathaniel Dafoe 30181948
 * - Arvin Bolbolanardestani 30165484
 * - Anthony Chan 30174703
 * - Marvellous Chukwukelu 30197270
 * - Farida Elogueil 30171114
 * - Ahmed Elshabasi 30188386
 * - Shawn Hanlon 10021510
 * - Steven Huang 30145866
 * - Nada Mohamed 30183972
 * - Jon Mulyk 30093143
 * - Althea Non 30172442
 * - Minori Olguin 30035923
 * - Kelly Osena 30074352
 * - Muhib Qureshi 30076351
 * - Sofia Rubio 30113733
 * - Muzammil Saleem 30180889
 * - Steven Susorov 30197973
 * - Lydia Swiegers 30174059
 * - Elizabeth Szentmiklossy 30165216
 * - Anthony Tolentino 30081427
 * - Johnny Tran 30140472
 * - Kaylee Xiao 30173778
 */

package com.thelocalmarketplace.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SelectLanguage extends JPanel { 
	private JButton engButton = new JButton("ENGLISH");
	private JButton cancel = new JButton("CANCEL");
	
	public SelectLanguage() {
		getCancel().setBackground(Color.RED);
		getCancel().setPreferredSize(new Dimension(100, 50));
		getEngButton().setPreferredSize(new Dimension(250, 130));
		add(getEngButton());
		add(getCancel());
		
		getCancel().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				
			}
			
		});
		getEngButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ENGLISH LANGUAGE SELECTED");
				setVisible(false);
				
			}
			
		});
	}

	public JButton getCancel() {
		return cancel;
	}

	public void setCancel(JButton cancel) {
		this.cancel = cancel;
	}

	public JButton getEngButton() {
		return engButton;
	}

	public void setEngButton(JButton engButton) {
		this.engButton = engButton;
	}
}
