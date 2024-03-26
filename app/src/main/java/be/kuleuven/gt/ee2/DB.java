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
    private int carLocation;
    @SerializedName("Time")
    private String time;
    @SerializedName("date_only")
    private String date;

    public int getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getPlateStatus() {
        return plateStatus;
    }

    public int getCarLocation() {
        return carLocation;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}




