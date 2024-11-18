package za.ac.cput.votingserver.dao;

import java.sql.*;
import java.util.ArrayList;
import za.ac.cput.model.Vehicle;
import za.ac.cput.votingserver.VotingServer;  // Import VotingServer to access serverGui

public class DatabaseHandler {
    private Connection connection;

    public DatabaseHandler() {
        try {
            String dbURL = "jdbc:derby://localhost:1527/VoteDB";
            String username = "administrator";
            String password = "Admin";
            connection = DriverManager.getConnection(dbURL, username, password);
            createTableIfNotExists();
        } catch (SQLException e) {
            VotingServer.serverGui.logMessage("Database connection error: " + e.getMessage());  // Log error
        }
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE vehicles (" +
                    "vehicle_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "vehicle_name VARCHAR(100), " +
                    "vote_count INT DEFAULT 0)";
            stmt.executeUpdate(sql);
            VotingServer.serverGui.logMessage("Created vehicles table.");
        } catch (SQLException e) {
            VotingServer.serverGui.logMessage("Table creation error or table exists: " + e.getMessage());
        }
    }

    public void voteForVehicle(String vehicleName) {
        String query = "UPDATE vehicles SET vote_count = vote_count + 1 WHERE vehicle_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicleName);
            pstmt.executeUpdate();
            VotingServer.serverGui.logMessage("Vote count updated for vehicle: " + vehicleName);
        } catch (SQLException e) {
            VotingServer.serverGui.logMessage("Error updating vote count: " + e.getMessage());
        }
    }

    public void addVehicle(String vehicleName) {
        String query = "INSERT INTO vehicles (vehicle_name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicleName);
            pstmt.executeUpdate();
            VotingServer.serverGui.logMessage("New vehicle added: " + vehicleName);
        } catch (SQLException e) {
            VotingServer.serverGui.logMessage("Error adding new vehicle: " + e.getMessage());
        }
    }

    public ArrayList<String> getVehicleList() {
        ArrayList<String> vehicles = new ArrayList<>();
        String query = "SELECT vehicle_name FROM vehicles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vehicles.add(rs.getString("vehicle_name"));
            }
            VotingServer.serverGui.logMessage("Fetched vehicle list from database.");
        } catch (SQLException e) {
            VotingServer.serverGui.logMessage("Error fetching vehicle list: " + e.getMessage());
        }
        return vehicles;
    }

    public ArrayList<Vehicle> getVoteResults() {
        ArrayList<Vehicle> results = new ArrayList<>();
        String query = "SELECT vehicle_name, vote_count FROM vehicles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String vehicleName = rs.getString("vehicle_name");
                int votesNum = rs.getInt("vote_count");
                results.add(new Vehicle(vehicleName, votesNum));
                VotingServer.serverGui.logMessage("Fetched vehicle: " + vehicleName + " with votes: " + votesNum);
            }
        } catch (SQLException e) {
            VotingServer.serverGui.logMessage("Error fetching vote results: " + e.getMessage());
        }
        return results;
    }

    public boolean deleteVehicle(String vehicleToDelete) {
    // Log the vehicle list before attempting deletion (for debugging)
    ArrayList<String> vehicleList = getVehicleList();
    VotingServer.serverGui.logMessage("Current vehicle list: " + vehicleList.toString());

    String query = "DELETE FROM vehicles WHERE LOWER(vehicle_name) = LOWER(?)";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, vehicleToDelete);  // Set the vehicle name in the query
        VotingServer.serverGui.logMessage("Executing delete for vehicle: " + vehicleToDelete);
        
        int rowsAffected = pstmt.executeUpdate();  // Execute the deletion query

        if (rowsAffected > 0) {
            VotingServer.serverGui.logMessage("Successfully deleted vehicle: " + vehicleToDelete);
            return true;  // Return true if the deletion was successful
        } else {
            VotingServer.serverGui.logMessage("Vehicle not found: " + vehicleToDelete);
            return false;  // Return false if no rows were affected (vehicle not found)
        }
    } catch (SQLException e) {
        VotingServer.serverGui.logMessage("Error deleting vehicle: " + e.getMessage());
        return false;  // Return false in case of a database error
    }
}


}
