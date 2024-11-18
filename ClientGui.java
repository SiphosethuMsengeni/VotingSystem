package ac.za.cput.votingclients;


import static java.awt.AWTEventMulticaster.add;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import za.ac.cput.model.Vehicle;

public class ClientGui extends JFrame implements ActionListener {

    // GUI Components
    private JPanel pnlNorth = new JPanel();
    private JPanel pnlCenter = new JPanel();
    private JPanel pnlSouth = new JPanel();
    private JPanel pnlVehicleSelection = new JPanel();
    private JPanel pnlAddVehicle = new JPanel();  // Panel to hold the Add Vehicle text field
    private JPanel pnlResults = new JPanel();
    
    private JLabel lblHeading = new JLabel("Vote for Your Car of the Year");
    private JLabel lblVehicles = new JLabel("Select Vehicle:");
    private JLabel lblNewVehicle = new JLabel("Add New Vehicle:");
    
    private JComboBox comboBoxVehicles = new JComboBox();
    private JTextField txtNewVehicle = new JTextField(20);
    private DefaultTableModel tblModel = new DefaultTableModel();  // Create table model
    private JTable tblResults = new JTable(tblModel);  // Create JTable with the model
    private JScrollPane scrollPane = new JScrollPane(tblResults);  // Wrap table in a scroll pane
    
    private JButton btnVote = new JButton("Vote");
    private JButton btnAdd = new JButton("Add");
    private JButton btnViewResults = new JButton("View Results");
    private JButton btnDelete= new JButton("Delete");
    private JButton btnExit = new JButton("Exit");
    
    private Font fontTitle = new Font("Arial", Font.BOLD, 32);
    private Font fontButtons = new Font("Arial", Font.PLAIN, 22);
    
    private ClientSocketHandler socketHandler;  // ClientSocketHandler instance to handle client-server communication

    // Constructor for ClientGui
    public ClientGui() {
        super("Car of the Year Voting App");
        socketHandler = new ClientSocketHandler();  // Ensure the correct initialization of the socket handler
        socketHandler.connectToServer();  // Connect to the server


        // Set up North Panel
        lblHeading.setFont(fontTitle);
        lblHeading.setForeground(Color.YELLOW);
        pnlNorth.setBackground(new Color(0, 106, 255));
        pnlNorth.add(lblHeading);

        // Vehicle Selection Section
        lblVehicles.setFont(fontButtons);
        comboBoxVehicles.setFont(fontButtons);
        pnlVehicleSelection.add(lblVehicles);
        pnlVehicleSelection.add(comboBoxVehicles);

        // Add New Vehicle Section (Initially Hidden)
        lblNewVehicle.setFont(fontButtons);
        txtNewVehicle.setFont(fontButtons);
        pnlAddVehicle.add(lblNewVehicle);
        pnlAddVehicle.add(txtNewVehicle);
        pnlAddVehicle.setVisible(false);  // Initially hide this panel

        // Results Section with JTable
        String[] columns = {"Vehicle Name", "Vote Count"};  // Table columns
        tblModel.setColumnIdentifiers(columns);  // Set columns for the table model
     
        
        scrollPane.setPreferredSize(new Dimension(400, 350));  // Set preferred size for scroll pane
        
        pnlResults.setLayout(new BorderLayout(0,50));
        pnlResults.add(scrollPane, BorderLayout.CENTER);  // Add scroll pane with the table to the results panel

        // Add components to Center Panel
        pnlCenter.setLayout(new BorderLayout(0,10));
        pnlCenter.add(pnlVehicleSelection, BorderLayout.NORTH);
        pnlCenter.add(pnlAddVehicle, BorderLayout.CENTER);  // Initially hidden
        pnlCenter.add(pnlResults, BorderLayout.SOUTH);

        // Set Font and Colors for Buttons
        btnVote.setFont(fontButtons);
        btnVote.setBackground(new Color(0, 128, 0));  // Green for voting
        btnVote.setForeground(Color.WHITE);

        btnAdd.setFont(fontButtons);
        btnAdd.setBackground(new Color(0, 0, 255));  // Blue for adding a vehicle
        btnAdd.setForeground(Color.WHITE);

        btnViewResults.setFont(fontButtons);
        btnViewResults.setBackground(new Color(255, 165, 0));  // Orange for viewing results
        btnViewResults.setForeground(Color.WHITE);
        
        btnDelete.setFont(fontButtons);
        btnDelete.setBackground(new Color(0,0,200));
        btnDelete.setBackground(Color.MAGENTA);
        

        btnExit.setFont(fontButtons);
        btnExit.setBackground(new Color(255, 0, 0));  // Red for exiting
        btnExit.setForeground(Color.WHITE);

        pnlSouth.setLayout(new GridLayout(1, 4,0,0));
        pnlSouth.add(btnAdd);
        pnlSouth.add(btnVote);
        pnlSouth.add(btnViewResults);
        pnlSouth.add(btnDelete);
        pnlSouth.add(btnExit);

        // Add Panels to Frame
        add(pnlNorth, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);

        // Action Listeners
        btnVote.addActionListener(this);
        btnAdd.addActionListener(this);
        btnViewResults.addActionListener(this);
        btnDelete.addActionListener(this);
        btnExit.addActionListener(this);

        // Load initial vehicle list from server
         updateVehicleList();
    }

     @Override
public void actionPerformed(ActionEvent e) {
      if (e.getSource() == btnVote) {
            String selectedVehicle = (String) comboBoxVehicles.getSelectedItem();
            castVote(selectedVehicle);
    } else if (e.getSource() == btnAdd) {
        if (!pnlAddVehicle.isVisible()) {
            pnlAddVehicle.setVisible(true);
            this.revalidate();  // Refresh the GUI to show the panel
        } else {
            String newVehicle = txtNewVehicle.getText().trim();
            if (!newVehicle.isEmpty()) {
                addVehicle(newVehicle);  // Request the server to add the new vehicle
                updateVehicleList();  // Refresh the ComboBox with the updated vehicle list
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtNewVehicle.setText("");  // Clear the text field
                pnlAddVehicle.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a vehicle name!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } else if (e.getSource() == btnDelete) {
    // Get the selected row index from the JTable
    int selectedRow = tblResults.getSelectedRow();
    
    if (selectedRow != -1) {  // Check if a row is actually selected
        String vehicleName = (String) tblModel.getValueAt(selectedRow, 0);  // Get vehicle name from the selected row
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + vehicleName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteVehicle(vehicleName);  // Send the delete request to the server
            tblModel.removeRow(selectedRow);  // Remove the row from the JTable model
            
            // Now, remove the specific vehicle from the ComboBox
            comboBoxVehicles.removeItem(vehicleName);  // Only remove the deleted vehicle from the combo box
            
            JOptionPane.showMessageDialog(this, vehicleName + " has been deleted.");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a vehicle from the table to delete.");
    
}

    } else if (e.getSource() == btnViewResults) {
        viewResults();  // Request server for vote results
    } else if (e.getSource() == btnExit) {
        socketHandler.closeConnection();  // Close connection to server
        System.exit(0);
    }
}

    // Sends the vote request to the server
    private void castVote(String vehicle) {
        socketHandler.sendVote(vehicle);
        JOptionPane.showMessageDialog(this, "Voted for: " + vehicle);
    }

    // Sends a request to the server to add a new vehicle
    private void addVehicle(String vehicleName) {
        socketHandler.addVehicle(vehicleName);
    }

    // Requests the updated vehicle list from the server and populates the JComboBox
    private void updateVehicleList() {
        comboBoxVehicles.removeAllItems();  // Clear current items
        ArrayList<String> vehicleList = socketHandler.getVehicleList();  // Request list from server
        for (String vehicle : vehicleList) {
            comboBoxVehicles.addItem(vehicle);  // Add each vehicle to the ComboBox
        }
    }

    private void viewResults() {
        System.out.println("Requesting vote results from server...");

        // Request vote results from server
        ArrayList<Vehicle> votes = socketHandler.getVoteResults();

        // Check if results are correctly received
        if (votes != null && !votes.isEmpty()) {
            tblModel.setRowCount(0);  // Clear existing rows
            System.out.println("Received vote results from server.");
            for (Vehicle vehicle : votes) {
                System.out.println("Vehicle: " + vehicle.getVehicleName() + ", Votes: " + vehicle.getVotesNum());
                tblModel.addRow(new Object[]{vehicle.getVehicleName(), vehicle.getVotesNum()});
            }
        } else {
            System.out.println("No vote results available.");
            JOptionPane.showMessageDialog(this, "No vote results available", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteVehicle(String vehicleName) {
    try {
        socketHandler.deleteVehicle(vehicleName);
        JOptionPane.showMessageDialog(this, vehicleName + " has been deleted.");
        //refreshVehicleList();  // Method to refresh the vehicle list after deletion
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error deleting vehicle: " + ex.getMessage());
    }
}

}
