package Models;

import java.time.LocalTime;

public class Reservation {
    private double totalPrice;
    private LocalTime dateOfReservation;

    public Reservation(double totalPrice, LocalTime dateOfReservation) {
        this.totalPrice = totalPrice;
        this.dateOfReservation = dateOfReservation;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalTime getDateOfReservation() {
        return dateOfReservation;
    }
}
