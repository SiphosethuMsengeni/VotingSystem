package za.ac.cput.votingserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGui extends JFrame {
    private JTextArea textArea;
    private JButton btnClearLog; // Button to clear the log

    public ServerGui() {
        setTitle("Voting Server Log");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the text area for logging
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));  // Set font for better readability
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Create the button to clear the log
        btnClearLog = new JButton("Clear Log");
        btnClearLog.setFont(new Font("Arial", Font.BOLD, 16));  // Larger font for button
        btnClearLog.setBackground(new Color(255, 0, 0));  // Red background
        btnClearLog.setForeground(Color.WHITE);  // White text
        btnClearLog.setFocusPainted(false);  // Remove focus border on the button
        btnClearLog.addActionListener(new ActionListener() {
            
            
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLog(); // Call method to clear the log
            }
        });

        // Create a panel for the button and align it at the bottom
        JPanel pnlSouth = new JPanel();
        pnlSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnlSouth.add(btnClearLog);

        // Add button panel to the bottom
        add(pnlSouth, BorderLayout.SOUTH);

        // Set visibility to true
        setVisible(true);
    }

    // Method to append messages to the log
    public void logMessage(String message) {
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
    }

    // Method to clear the log
    private void clearLog() {
        textArea.setText(""); // Clear the text area
    }
}
