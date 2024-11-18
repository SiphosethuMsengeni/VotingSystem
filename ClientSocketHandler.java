package ac.za.cput.votingclients;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import za.ac.cput.model.Vehicle;

public class ClientSocketHandler {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Connect to the server
    public void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 12345);  // Replace with your server's IP and port
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Close the connection
    public void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a vote to the server
    public void sendVote(String vehicle) {
        try {
            out.writeObject("VOTE");
            out.flush();
            out.writeObject(vehicle);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a new vehicle to the server
    public void addVehicle(String vehicleName) {
        try {
            out.writeObject("ADD_VEHICLE");
            out.flush();
            out.writeObject(vehicleName);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve vehicle list from server
    public ArrayList<String> getVehicleList() {
        ArrayList<String> vehicleList = new ArrayList<>();
        try {
            out.writeObject("GET_VEHICLES");
            out.flush();
            vehicleList = (ArrayList<String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return vehicleList;
    }
    // Method to send a delete request to the server
public void deleteVehicle(String vehicleName) {
    try {
        out.writeObject("DELETE_VEHICLE");  // Send a command to delete the vehicle
        out.writeObject(vehicleName);  // Send the name of the vehicle to delete
        out.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


// Method to delete a vehicle from the database




    // Retrieve vote results from the server
    // In ClientSocketHandler.java
    public ArrayList<Vehicle> getVoteResults() {
        ArrayList<Vehicle> results = new ArrayList<>();
        try {
            // Send the GET_RESULTS request to the server
            System.out.println("Requesting vote results from the server...");
            out.writeObject("GET_RESULTS");
            out.flush();

            // Read the response from the server (ArrayList of Vehicle objects)
            results = (ArrayList<Vehicle>) in.readObject();

            // Debugging print statements to verify the data
            System.out.println("Received vote results from the server.");
            for (Vehicle vehicle : results) {
                System.out.println("Vehicle: " + vehicle.getVehicleName() + " - Votes: " + vehicle.getVotesNum());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Optional: Add user-friendly error message in the GUI
        }

        return results;  // Return the vehicle results to the client GUI
    }

}
