package Models;

public class Ticket {
    private int ticketId;
    private double price;
    private boolean isRoundTrip;

    public Ticket(int ticketId, double price, boolean isRoundTrip) {
        this.ticketId = ticketId;
        this.price = price;
        this.isRoundTrip = isRoundTrip;
    }

    public int getTicketId() {
        return ticketId;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRoundTrip() {
        return isRoundTrip;
    }
}
