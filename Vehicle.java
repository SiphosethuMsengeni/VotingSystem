package za.ac.cput.model;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    
    private String vehicleName;
    private int votesNum;

    public Vehicle(String vehicleName, int votesNum) {
        this.vehicleName = vehicleName;
        this.votesNum = votesNum;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public int getVotesNum() {
        return votesNum;
    }
}
