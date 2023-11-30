// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948

package com.thelocalmarketplace.GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NumberPadDialog extends JDialog {
    private JTextField targetTextField;

    public NumberPadDialog(JFrame parentFrame, JTextField targetTextField) {
        super(parentFrame, "Number Pad", true); // true for modal

        this.targetTextField = targetTextField;

        // Create number pad buttons
        JPanel numberPadPanel = createNumberPadPanel();

        // Add the number pad to the dialog content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(numberPadPanel, BorderLayout.CENTER);

        // Set dialog properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parentFrame);
    }

    private JPanel createNumberPadPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 5, 5));

        for (int i = 1; i <= 9; i++) {
            addButton(panel, String.valueOf(i));
        }

        addButton(panel, "0");
        addButton(panel, "Clear");
        addButton(panel, "Close");

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
                targetTextField.setText("");
            } else if (buttonText.equals("Close")) {
                dispose(); // Close the dialog
            } else {
                targetTextField.setText(targetTextField.getText() + buttonText);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JTextField textField = new JTextField();

            JButton openButton = new JButton("Open Number Pad");
            openButton.addActionListener(e -> {
                NumberPadDialog dialog = new NumberPadDialog(frame, textField);
                dialog.setVisible(true);
            });

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(textField, BorderLayout.NORTH);
            mainPanel.add(openButton, BorderLayout.SOUTH);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}
