package za.ac.cput.votingserver;

import za.ac.cput.votingserver.dao.DatabaseHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import za.ac.cput.model.Vehicle;

public class VotingServer {
    public static ServerGui serverGui;  // Make serverGui accessible to other classes
    private static DatabaseHandler dbHandler;

    public static void main(String[] args) {
        int port = 12345;  // Port number to listen on
        serverGui = new ServerGui();  // Initialize the server GUI first
        dbHandler = new DatabaseHandler();  // Now initialize DatabaseHandler after serverGui

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverGui.logMessage("Voting server is running on port " + port);  // Log server start
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Accept new client connections
                serverGui.logMessage("Client connected: " + clientSocket.getInetAddress());  // Log new client
                handleClient(clientSocket);  // Handle client communication
            }
        } catch (IOException e) {
            serverGui.logMessage("Server error: " + e.getMessage());  // Log server error
        }
    }

    // Method to handle client communication
    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String request;
            while ((request = (String) in.readObject()) != null) {
                serverGui.logMessage("Received request: " + request);  // Log incoming request to GUI

                switch (request) {
                    case "VOTE":
                        String vehicleToVote = (String) in.readObject();
                        dbHandler.voteForVehicle(vehicleToVote);
                        serverGui.logMessage("Voted for: " + vehicleToVote);  // Log vote to GUI
                        break;

                    case "ADD_VEHICLE":
                        String newVehicle = (String) in.readObject();
                        dbHandler.addVehicle(newVehicle);
                        serverGui.logMessage("Added vehicle: " + newVehicle);  // Log added vehicle to GUI
                        break;

                    case "GET_VEHICLES":
                        ArrayList<String> vehicleList = dbHandler.getVehicleList();
                        out.writeObject(vehicleList);
                        out.flush();
                        serverGui.logMessage("Sent vehicle list to client.");  // Log vehicle list sent to GUI
                        break;

                    case "GET_RESULTS":
                        ArrayList<Vehicle> voteResults = dbHandler.getVoteResults();
                        out.writeObject(voteResults);
                        out.flush();
                        serverGui.logMessage("Sent vote results to client.");  // Log results sent to GUI
                        break;
                        
                        case "DELETE_VEHICLE":
                        String vehicleToDelete = (String) in.readObject();
                        boolean deleted = dbHandler.deleteVehicle(vehicleToDelete);
                        if (deleted) {
                            serverGui.logMessage("Deleted vehicle: " + vehicleToDelete);  // Log successful deletion
                            out.writeObject("Vehicle " + vehicleToDelete + " deleted successfully.");
                        } else {
                            serverGui.logMessage("Vehicle not found: " + vehicleToDelete);  // Log failure to delete
                            out.writeObject("Vehicle " + vehicleToDelete + " not found.");
                        }
                        out.flush();
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            serverGui.logMessage("Client handling error: " + e.getMessage());  // Log error to GUI
        } finally {
            try {
                clientSocket.close();  // Close the client socket
                serverGui.logMessage("Client disconnected.");  // Log disconnection to GUI
            } catch (IOException e) {
                serverGui.logMessage("Error closing client socket: " + e.getMessage());  // Log error to GUI
            }
        }
    }
}
