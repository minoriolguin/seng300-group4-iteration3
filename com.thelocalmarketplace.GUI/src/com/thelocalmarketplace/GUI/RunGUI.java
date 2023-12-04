//Arvin Bolbolanardestani / 30165484
//Anthony Chan / 30174703
//Marvellous Chukwukelu / 30197270
//Farida Elogueil / 30171114
//Ahmed Elshabasi / 30188386
//Shawn Hanlon / 10021510
//Steven Huang / 30145866
//Nada Mohamed / 30183972
//Jon Mulyk / 30093143
//Althea Non / 30172442
//Minori Olguin / 30035923
//Kelly Osena / 30074352
//Muhib Qureshi / 30076351
//Sofia Rubio / 30113733
//Muzammil Saleem / 30180889
//Steven Susorov / 30197973
//Lydia Swiegers / 30174059
//Elizabeth Szentmiklossy / 30165216
//Anthony Tolentino / 30081427
//Johnny Tran / 30140472
//Kaylee Xiao / 30173778

package com.thelocalmarketplace.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.thelocalmarketplace.software.PayByCard;
import com.thelocalmarketplace.software.TouchScreen;
import com.jjjwelectronics.card.Card;

public class RunGUI extends JFrame implements logicObserver {
	private PayByCard payByCard;
	private Card card;
    // for receipt building on GUI 
	private List<JLabel> labelList = new ArrayList<>();
	// Paneling on GUI
    private JPanel leftPanel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    // For logic testing - delete after all GUI is done
    private int total;
    private JLabel totalLabel;
    private EnterPLU plu;
    
    
    //This is what allows Logic to happen when I click a button
	private GUILogic guiLogicInstance;
    
    //For Testing Purposes - to run GUI 
    public RunGUI() {
        SelfCheckoutGUI();
    }

    // Constructor to initialize GUILogic
    public RunGUI(GUILogic guiLogicInstance) {
        this.guiLogicInstance = guiLogicInstance;
        SelfCheckoutGUI();
    }
    
    /**
     * Main method that creates the GUI
     */
    private void SelfCheckoutGUI() {
    	//Frame Size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocation(0,0);
        
       
        // Create and add panels to the card panel
        // When you add new panel, make sure to add one here too 
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(StartSessionPanel(), "welcomePanel");
        cardPanel.add(createAddItemsPanel(), "AddItemsPanel");
        cardPanel.add(createThankYouPanel(), "thankYouPanel");
        cardPanel.add(createPaymentPanel(), "paymentPanel");
        cardPanel.add(createCashBillPanel(), "cashBillPanel");
        cardPanel.add(createCashCoinPanel(), "cashCoinPanel");
        cardPanel.add(new SelectLanguage(this), "selectLanguage");
        cardPanel.add(new EnterMembershipNumber(this), "enterMembership");
        cardPanel.add(new EnterPLU(this), "enterPLU");
        //TODO: error happens from here
        cardPanel.add(new ItemAddedFrame(this, this.guiLogicInstance), "itemAddedFrame");
//        cardPanel.add(createNumberPad(), "numpadPanel");
        add(cardPanel);
        
        // Show the welcome panel initially
        cardLayout.show(cardPanel, "welcomePanel");
        //Or use method switchPanels("welcomePanel")
        
        setVisible(true);
    }
        
        // Open Attendant Frame beside the Self CheckOut
//        AttendantFrame attendantFrame = new AttendantFrame();
//        attendantFrame.AttendantFrame();
//    }

    // Customer Screen 1 
    private JButton StartSessionPanel() {
        JButton panel = new JButton();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to Self Checkout!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);
        
        JLabel label = new JLabel("Press Anywhere to Start");
        panel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.StartSessionButtonPressed();
                switchPanels("AddItemsPanel");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label, gbc);
        return panel;
    }
    
    /*
     * The Panel for Checkout (MAIN) 
     * There are additional parts that made this layout 
     * Part 1
     */
    private JPanel createAddItemsPanel() {
        // Create main panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Top Left Panel (To Display Total)
        JPanel topLeftPanel = createLabelPanel("", 500, 20);
        JLabel custTotalLabel = new JLabel("Total is: "+total);
        topLeftPanel.add(custTotalLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.20; 
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(topLeftPanel, gbc);
        
        // Left Panel (Product List / Checkout / Receipt)
        leftPanel = createLabelPanel("", 500, 500);
        leftPanel.add(new JLabel("RECEIPT / LIST OF CHECKOUT PRODUCT HERE"));

        // Left Panel with JScrollPane)
        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5; 
        gbc.weighty = 1.20;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(leftScrollPane, gbc);

        // Top Right Panel (Weight Display)
        JPanel topRightPanel = createLabelPanel("Top right Panel", 500, 20);
        JLabel topRightLabel = new JLabel("Weight Goes Here");
        topRightPanel.add(topRightLabel);
        gbc.gridx = 2;  // Adjust the gridx to place it to the right
        gbc.gridy = 0;
        gbc.weightx = 0.5;  // Adjust the weightx to control the width ratio
        mainPanel.add(topRightPanel, gbc);

        // Right Panel (TouchScreen Buttons)
        JPanel rightPanel = createButtonPanelRightPanel("Touch Screen", 500, 500);
        gbc.gridx = 2;  // Adjust the gridx to place it to the right
        gbc.gridy = 1;
        gbc.weightx = 0.5;  // Adjust the weightx to control the width ratio
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(rightPanel, gbc);

        // Bottom Panel (Customer Actions not displayed in TouchScreen)
        JPanel bottomPanel = createButtonPanelBottomPanel("Customer Actions (Not Displayed on Touch Screen)", 1000, 200);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(bottomPanel, gbc);

        // Add the main panel to the card panel
        cardPanel.add(mainPanel, "yourFrameClass2");
        return mainPanel;
    }
    /*
     * The Panel for Checkout (MAIN) 
     * Part 2
     * This is for Upper Left and Upper Right Panels
     */
    private JPanel createLabelPanel(String label, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(label));

        // Set the preferred size to have a fixed width and height
        panel.setPreferredSize(new Dimension(width, height));
        // Set the maximum size to enforce the desired height
        panel.setMaximumSize(new Dimension(width, height));        // Set the maximum size to have a fixed width and height
        return panel;
    }
    /*
     * The Panel for Checkout (MAIN) 
     * Part 3
     * This is for Right Panel Buttons
     */
    private JPanel createButtonPanelRightPanel(String label, int width, int height) {
        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(label));
        panel.setMaximumSize(new Dimension(width-5, height));
        
        JButton button1 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Add<br>Membership Number</div></html>");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR1_AddMemberNoButton();
              	switchPanels("enterMembership");
            }
        });
        
        JButton button2 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Sign Up for<br>Membership</div></html>");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR2_SignUpForMembershipButton();
            	//PROJ3 : Something needs to happen when customer wants to create membership
            }
        });
   
        JButton button3 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Call<br>Attendant</div></html>");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR3_CustomerCallsAttendant();
            	// PROJ3: Customer gets blocked until Attendant clears
            }
        });
    
        
        JButton button4 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Add<br>Own Bags</div></html>");
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR4_CustomerAddsOwnBag();
            	AddOwnBagGUI addOwnBagPanel = new AddOwnBagGUI();
            	addOverlayPanel(addOwnBagPanel);
            }
        });
        
        JButton button5 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Remove<br>Last Item</div></html>");
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR5_CustomerWantstoRemoveItem();
            	//GUI will remove Last Product added
                removeLastLabel();
            }
        });
        
        JButton button6 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "SELECT LANGUAGE</div></html>");
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR6_CustomerSelectsLanguage();
              	switchPanels("selectLanguage");
            }
        });
        JButton button7 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Enter Item<br>(PLU Code)</div></html>");
        button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	switchPanels("enterPLU");
//            	String addItemPLU_result = guiLogicInstance.buttonR7_CustomerAddsItem_PLUCode();
//            	addNewLabel(addItemPLU_result);
            	
            }
        });
        JButton button8 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Enter Item<br>(Visual Catalog)</div></html>");
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	String addItemVC_result = guiLogicInstance.buttonR8_CustomerAddsItem_VisualCatalogue();
            	addNewLabel(addItemVC_result);
            }
        });
        JButton button9 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Pay<br>For Order</div></html>");
        button9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	guiLogicInstance.buttonR9_CustomerWantsToPay();
                switchPanels("paymentPanel");
            }
        });
        
        //Turns Pay Button 9 (PAY) Green
        button9.setBackground(new Color(0, 255, 0));
        
        //Display the Buttons on the GUI Panel
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);
        panel.add(button5);
        panel.add(button6);
        panel.add(button7);
        panel.add(button8);
        panel.add(button9);

        // Set a smaller font size for each button
        Font smallFont = new Font(button1.getFont().getName(), Font.BOLD, 10); // Adjust the font size as needed
        button1.setFont(smallFont);
        button2.setFont(smallFont);
        button3.setFont(smallFont);
        button4.setFont(smallFont);
        button5.setFont(smallFont);
        button6.setFont(smallFont);
        button7.setFont(smallFont);
        button8.setFont(smallFont);
        button9.setFont(smallFont);
    
		// Set maximum size for each button to control the width
		Dimension buttonMaxSize = new Dimension(width / 3, height / 3);
		button1.setMaximumSize(buttonMaxSize);
		button2.setMaximumSize(buttonMaxSize);
		button3.setMaximumSize(buttonMaxSize);
		button4.setMaximumSize(buttonMaxSize);
		button5.setMaximumSize(buttonMaxSize);
		button6.setMaximumSize(buttonMaxSize);
		button7.setMaximumSize(buttonMaxSize);
		button8.setMaximumSize(buttonMaxSize);
		button9.setMaximumSize(buttonMaxSize);
		
        return panel;
    }
    
    /*
     * The Panel for Checkout (MAIN) 
     * Part 4
     * This is for Bottom Panel Buttons
     */
    private JPanel createButtonPanelBottomPanel(String label, int width, int height) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(label));
        panel.setMaximumSize(new Dimension(width-5, height));
        
        JButton bot_button1 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Add Item<br>(Barcoded Product)<br>Main Scanner</div></html>");
        bot_button1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	String addItem_result = guiLogicInstance.buttonB1_CustomerScansBarcodedProduct_MainScanner();
        	addNewLabel(addItem_result);
        	}
        });
        
       JButton bot_button2 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Add Item<br>(Barcoded Product)<br>HandheldScanner</div></html>");
        bot_button2.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	String addItem_result = guiLogicInstance.buttonB2_CustomerScansBarcodedProduct_HandheldScanner();
        	addNewLabel(addItem_result);
            }
        });
        
        JButton bot_button3 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Add Item <br>to Bagging Area</div></html>");
        bot_button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button Clicked!");
            }
        });
        
        JButton bot_button4 = new JButton("<html><div style='text-align: center; display: flex; flex-direction: column; align-items: center;'>"
        		+ "Remove Item <br>from Bagging Area</div></html>");
        bot_button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button Clicked!");
            }
        });

        // Set a smaller font size for each button
        Font smallFont = new Font(bot_button1.getFont().getName(), Font.PLAIN, 12); // Adjust the font size as needed
        bot_button1.setFont(smallFont);
        bot_button2.setFont(smallFont);
        bot_button3.setFont(smallFont);
        bot_button4.setFont(smallFont);
    
	    // Set maximum size for each button to control the width
	    Dimension buttonMaxSize = new Dimension(width / 3, height / 3);
	    bot_button1.setMaximumSize(buttonMaxSize);
	    bot_button2.setMaximumSize(buttonMaxSize);
	    bot_button3.setMaximumSize(buttonMaxSize);
	    bot_button4.setMaximumSize(buttonMaxSize);

        //Display the Buttons on the GUI Panel
        panel.add(bot_button1);
        panel.add(bot_button2);
        panel.add(bot_button3);
        panel.add(bot_button4);

        return panel;
    }
    
    /*
     * The Panel for Checkout (MAIN) 
     * Part 5
     * This is for Left Panel/Checkout (Add product to Receipt)
     */
    public void addNewLabel(String text) {
        JLabel label = new JLabel(text);
        labelList.add(label);
        leftPanel.add(label);
        leftPanel.revalidate();
        leftPanel.repaint();
    }
    /*
     * The Panel for Checkout (MAIN) 
     * Part 5
     * This is for Left Panel/Checkout (Remove product from Receipt)
     */
    public void removeLastLabel() {
        if (!labelList.isEmpty()) {
            JLabel removedLabel = labelList.remove(labelList.size() - 1);
            leftPanel.remove(removedLabel);
            leftPanel.revalidate();
            leftPanel.repaint();
        }
    }

    // Screen LAST Thank You Panel
    private JPanel createThankYouPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel TY_changeLabel = new JLabel("Your change is "+total+"!");
        TY_changeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(TY_changeLabel, gbc);

        JLabel TY_receiptLabel = new JLabel("Please Take Your Receipt.");
        TY_receiptLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 1;
        panel.add(TY_receiptLabel, gbc);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("welcomePanel");
            }
        });
        gbc.gridy = 2;
        panel.add(exitButton, gbc);

        return panel;
    }
   
    //Screen 3 Payment Panel 
    private JPanel createPaymentPanel() {
        JPanel PaymentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton payment_button1 = new JButton("DEBIT (Swipe)");
        payment_button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
                payByCard.aCardHasBeenSwiped();
            }
        });
        JButton payment_button2 = new JButton("DEBIT (Tap)");
        payment_button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel"); 
                payByCard.aCardHasBeenTapped();
            }
        });
        JButton payment_button3 = new JButton("DEBIT (Insert Card)");
        payment_button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
                payByCard.aCardHasBeenInserted();
            }
        });
        JButton payment_button4 = new JButton("CREDIT (Swipe)");
        payment_button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
                payByCard.aCardHasBeenSwiped();
            }
        });
        JButton payment_button5 = new JButton("CREDIT (Tap)");
        payment_button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
                payByCard.aCardHasBeenTapped();
            }
        });
        JButton payment_button6 = new JButton("CREDIT (Insert Card)");
        payment_button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
                payByCard.aCardHasBeenInserted();
            }
        });
        JButton payment_button7 = new JButton("Cash (Bills)");
        payment_button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("cashBillPanel");
            }
        });
        JButton payment_button8 = new JButton("Cash (Coins)");
        payment_button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("cashCoinPanel");
            }
        });
        JButton payment_button9 = new JButton("Leave Without Paying");
        payment_button9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
            }
        });
        JButton BacktoCheckoutButton = new JButton("Back to Checkout");
        BacktoCheckoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("AddItemsPanel");
            }
        });
        gbc.gridx = 1; gbc.gridy = 1;
        payment_button1.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button1, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        payment_button2.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button2, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        payment_button3.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button3, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        payment_button4.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button4, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        payment_button5.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button5, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        payment_button6.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button6, gbc);
        
        gbc.gridx = 3; gbc.gridy = 1;
        payment_button7.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button7, gbc);
        
        gbc.gridx = 3; gbc.gridy = 2;
        payment_button8.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button8, gbc);
        
        gbc.gridx = 3; gbc.gridy = 3;
        payment_button9.setPreferredSize(new Dimension(150,150));
        PaymentPanel.add(payment_button9, gbc);
        
        gbc.gridx = 2; gbc.gridy = 4;
        BacktoCheckoutButton.setPreferredSize(new Dimension(150,50));
        PaymentPanel.add(BacktoCheckoutButton, gbc);
        
        return PaymentPanel;        
    }
    

    //Screen 3.B Payment Panel (Coin Bill) 
    private JPanel createCashBillPanel() {
        JPanel CoinBillPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton payment_button1 = new JButton("$5.00");
        payment_button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("5.00");
            }
        });
        JButton payment_button2 = new JButton("$10.00");
        payment_button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("10.00");
            }
        });
        JButton payment_button3 = new JButton("$20.00");
        payment_button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("20.00");
            }
        });
        JButton payment_button4 = new JButton("$50.00");
        payment_button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("50.00");
            }
        });
        JButton payment_button5 = new JButton("100.00");
        payment_button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("10.00");
            }
        });
        JButton payment_button6 = new JButton("Pay for Order");
        payment_button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
            }
        });
        JButton payment_button7 = new JButton("Back to Checkout/Add More Items");
        payment_button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("AddItemsPanel");
            }
        });
        gbc.gridx = 0; gbc.gridy = 0;
        payment_button1.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button1, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        payment_button2.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        payment_button3.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button3, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        payment_button4.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button4, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        payment_button5.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button5, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        payment_button6.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button6, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        payment_button7.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button7, gbc);
        
        return CoinBillPanel;        
    }
    
    //Screen 3 Payment Panel (Coin Coin)
    private JPanel createCashCoinPanel() {
        JPanel CoinBillPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton payment_button1 = new JButton("$0.01");
        payment_button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("0.01");
            }
        });
        JButton payment_button2 = new JButton("$0.05");
        payment_button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("0.05");
            }
        });
        JButton payment_button3 = new JButton("$0.10");
        payment_button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("0.10");
            }
        });
        JButton payment_button4 = new JButton("$0.25");
        payment_button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("0.25");
            }
        });
        JButton payment_button5 = new JButton("$1.00");
        payment_button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("1.00");
            }
        });
        JButton payment_button6 = new JButton("Pay for Order");
        payment_button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("thankYouPanel");
            }
        });
        JButton payment_button7 = new JButton("Back to Checkout/Add More Items");
        payment_button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels("AddItemsPanel"); 
            }
        });
        gbc.gridx = 0; gbc.gridy = 0;
        payment_button1.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button1, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        payment_button2.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        payment_button3.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button3, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        payment_button4.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button4, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        payment_button5.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button5, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        payment_button6.setPreferredSize(new Dimension(150,150)); 
        CoinBillPanel.add(payment_button6, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        payment_button7.setPreferredSize(new Dimension(150,150));
        CoinBillPanel.add(payment_button7, gbc);
        
        return CoinBillPanel;        
    }
    
    /*c
     * NumberPad Pop Up Part 2/3 
     * What causes the Overlay to show up
     * 
     * If needed, copy paste this code for each Add Item  
     */
    private void openNumPadPanel() {
    	    // Create a transparent overlay panel
    	TransparentNumpadPanel numpadPanel = new TransparentNumpadPanel(guiLogicInstance);

    	    // Add buttons to the overlay panel
    	    numpadPanel.addNumPadButtons();

    	    // Add the overlay panel to the main frame
    	    setGlassPane(numpadPanel);
    	    numpadPanel.setVisible(true);
    }
    
    /*
     * NumberPad Pop Up Part 3/3 
     * What causes the Overlay to disappear
     */
    private void closeNumPadPanel() {
    	TransparentNumpadPanel numpadPanel = new TransparentNumpadPanel(guiLogicInstance);
	    numpadPanel.addNumPadButtons();
	    setGlassPane(numpadPanel);
	    numpadPanel.setVisible(false);
}

    private void addOverlayPanel(JPanel panel) {
    	setGlassPane(panel);
    	panel.setVisible(true);
	}

    	
    // Function Methods ------------------------------------BEGIN
    public void switchPanels(String string) {
    	cardLayout.show(cardPanel, string);
    }
    // Function Methods ------------------------------------END
    
    //Observer Methods --------------------------------------BEGIN
    @Override
    public void updateTotal(int total) {
    	totalLabel.setText("Total: "+ total);     
    }
    
    //Observer Methods --------------------------------------END


    
    
    
    
   

    

}
