package com.thelocalmarketplace.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SelectLanguage extends JPanel { 
	JButton engButton = new JButton("ENGLISH");
	JButton cancel = new JButton("CANCEL");
	
	public SelectLanguage() {
		cancel.setBackground(Color.RED);
		cancel.setPreferredSize(new Dimension(100, 50));
		engButton.setPreferredSize(new Dimension(250, 130));
		add(engButton);
		add(cancel);
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				
			}
			
		});
		engButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ENGLISH LANGUAGE SELECTED");
				setVisible(false);
				
			}
			
		});
	}
}
