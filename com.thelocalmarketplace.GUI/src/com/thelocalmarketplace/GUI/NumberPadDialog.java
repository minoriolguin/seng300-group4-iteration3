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
