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
	
	RunGUI instance;
	
	public SelectLanguage(RunGUI instance) {
		this.instance = instance;
		getCancel().setBackground(Color.RED);
		getCancel().setPreferredSize(new Dimension(100, 50));
		getEngButton().setPreferredSize(new Dimension(250, 130));
		add(getEngButton());
		add(getCancel());
		
		getCancel().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				instance.switchPanels("AddItemsPanel");
				
			}
			
		});
		getEngButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ENGLISH LANGUAGE SELECTED");
				instance.switchPanels("AddItemsPanel");
				
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
