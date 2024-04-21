package Models;

import java.time.LocalTime;

public class Train {
    private String leavingFrom;
    private String arrivingAt;
    private double distance;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public Train(String leavingFrom, String arrivingAt, double distance, LocalTime departureTime, LocalTime arrivalTime) {
        this.leavingFrom = leavingFrom;
        this.arrivingAt = arrivingAt;
        this.distance = distance;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getLeavingFrom() {
        return leavingFrom;
    }

    public String getArrivingAt() {
        return arrivingAt;
    }

    public double getDistance() {
        return distance;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
}
