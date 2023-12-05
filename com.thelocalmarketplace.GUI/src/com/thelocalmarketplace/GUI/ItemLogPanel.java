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


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;

public class ItemLogPanel extends JPanel
{
	private JPanel itemDisplayPanel;
	private JPanel displayTotalPanel;
	private JLabel billTotalValue;
	private ArrayList<LogItem> logItems = new ArrayList<>();

	class LogItem extends JPanel
	{
		GridBagLayout gbl_logItem;
		JLabel itemDescriptionLabel;
		JLabel itemPriceLabel;
		
		LogItem(String description, BigDecimal price)
		{
			setBackground(new Color(248, 248, 255));
			setBorder(new EmptyBorder(10, 10, 10, 10));
			
			gbl_logItem = new GridBagLayout();
			gbl_logItem.columnWidths = new int[]{0, 0, 0};
			gbl_logItem.rowHeights = new int[]{0, 0};
			gbl_logItem.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
			gbl_logItem.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			setLayout(gbl_logItem);
			
			itemDescriptionLabel = new JLabel(description);
			itemDescriptionLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
			GridBagConstraints gbc_itemDescLbl = new GridBagConstraints();
			gbc_itemDescLbl.anchor = GridBagConstraints.WEST;
			gbc_itemDescLbl.insets = new Insets(0, 0, 0, 5);
			gbc_itemDescLbl.gridx = 0;
			gbc_itemDescLbl.gridy = 0;
			add(itemDescriptionLabel, gbc_itemDescLbl);
			
			itemPriceLabel = new JLabel("$ " + price.setScale(2, RoundingMode.HALF_EVEN).toPlainString());
			itemPriceLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_1.gridx = 1;
			gbc_lblNewLabel_1.gridy = 0;
			add(itemPriceLabel, gbc_lblNewLabel_1);
		}
	}
	/**
	 * Create the panel.
	 */
	public ItemLogPanel()
	{
		setBackground(new Color(255, 255, 255));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{816, 0};
		gridBagLayout.rowHeights = new int[]{0, -38, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		itemDisplayPanel = new JPanel();
		itemDisplayPanel.setBackground(new Color(248, 248, 255));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(itemDisplayPanel, gbc_panel);
		itemDisplayPanel.setLayout(new GridLayout(0, 1, 0, 0));

		displayTotalPanel = new JPanel();
		displayTotalPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		displayTotalPanel.setBackground(Color.WHITE);
		displayTotalPanel.setForeground(new Color(255, 255, 255));
		GridBagConstraints gbc_displayTotalPanel = new GridBagConstraints();
		gbc_displayTotalPanel.fill = GridBagConstraints.BOTH;
		gbc_displayTotalPanel.gridx = 0;
		gbc_displayTotalPanel.gridy = 1;
		add(displayTotalPanel, gbc_displayTotalPanel);
		GridBagLayout gbl_displayTotalPanel = new GridBagLayout();
		gbl_displayTotalPanel.columnWidths = new int[]{403, 106, 0};
		gbl_displayTotalPanel.rowHeights = new int[]{0, 29, 0, 0};
		gbl_displayTotalPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_displayTotalPanel.rowWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		displayTotalPanel.setLayout(gbl_displayTotalPanel);
		
		JLabel billTotalLabel = new JLabel("Bill Total");
		billTotalLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_billTotalLabel = new GridBagConstraints();
		gbc_billTotalLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_billTotalLabel.insets = new Insets(0, 0, 5, 5);
		gbc_billTotalLabel.gridx = 0;
		gbc_billTotalLabel.gridy = 1;
		displayTotalPanel.add(billTotalLabel, gbc_billTotalLabel);
		
		billTotalValue = new JLabel("$ 000.00");
		billTotalValue.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_billTotalValue = new GridBagConstraints();
		gbc_billTotalValue.insets = new Insets(0, 0, 5, 0);
		gbc_billTotalValue.anchor = GridBagConstraints.NORTHEAST;
		gbc_billTotalValue.gridx = 1;
		gbc_billTotalValue.gridy = 1;
		displayTotalPanel.add(billTotalValue, gbc_billTotalValue);
		
	}
	
	public void reset()
	{
		itemDisplayPanel.removeAll();
		itemDisplayPanel.validate();
		logItems = new ArrayList<>();
		setBillTotalValue(BigDecimal.ZERO);
	}
	
	public void addItem(String description, BigDecimal price)
	{
		LogItem item = new LogItem(description, price);
		itemDisplayPanel.add(item);
		itemDisplayPanel.validate();
		logItems.add(item);
	}
	
	public void removeLogItem(int i)
	{
		itemDisplayPanel.remove(logItems.get(i));;
		itemDisplayPanel.validate();
		logItems.remove(i);
	}
	
	public void setBillTotalValue(BigDecimal billTotal)
	{
		billTotalValue.setText("$ " + billTotal.setScale(2, RoundingMode.HALF_EVEN).toPlainString());
		itemDisplayPanel.validate();
	}
}
