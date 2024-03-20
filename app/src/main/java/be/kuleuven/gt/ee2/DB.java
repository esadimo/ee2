package be.kuleuven.gt.ee2;

import com.google.gson.annotations.SerializedName;

public class DB {
    @SerializedName("Id")
    private int id;
    @SerializedName("Table Number")
    private int tableNumber;
    @SerializedName("Plate Status")
    private String plateStatus;
    @SerializedName("Car Location")
    private String carLocation;
    @SerializedName("Time")
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getPlateStatus() {
        return plateStatus;
    }

    public void setPlateStatus(String plateStatus1) {
        this.plateStatus = plateStatus1;
    }

    public String getCarLocation() {
        return carLocation;
    }

    public void setCarLocation(String carLocation) {
        this.carLocation = carLocation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}




