/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ac.za.cput.votingclients;

/**
 *
 * @author msengenisiphosethu
 */
public class runClients {

    public static void main(String[] args) {
       ClientSocketHandler socketHandler = new ClientSocketHandler();

        // Create and launch the Client GUI, passing the socket handler
        ClientGui gui = new ClientGui();
        gui.setSize(900, 600);  // Set window size
        gui.setLocationRelativeTo(null);  // Center the window
        gui.setVisible(true); 
    }
}
