package Services;

import java.sql.*;
import java.time.LocalTime;
import java.util.Scanner;

public class TicketService {
    public TicketService() {}

    public static boolean checkForRoundTrip() throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);

        String inputRoundTrip = null;
        Boolean isRoundTrip = false;

        do {
            System.out.println("Is it a round trip? [y/n]: ");
            inputRoundTrip = sc.nextLine();
        }while(!inputRoundTrip.equals("y") && !inputRoundTrip.equals("n"));

        //check if it's a round trip
        if(inputRoundTrip.equals("y")){
            return isRoundTrip = true;
        }else{
            return isRoundTrip = false;
        }
    }

    public static double discountsByHours(Connection conn, Scanner sc, String currUserId, double ticketPrice, int currTrainId) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        boolean isEcoHour = false;
        Time departureTime = null;

        String getDepartureTimeQuery = "SELECT trains.departureTime FROM trains WHERE trains.id = ?";
        prStatement = conn.prepareStatement(getDepartureTimeQuery);
        try {
            prStatement.setObject(1, currTrainId);
            rs = prStatement.executeQuery();

            boolean hasRow = rs.next();
            if (hasRow) {
                departureTime = rs.getTime("departureTime");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }

        //set timeframes
        if (departureTime.toLocalTime().isAfter(LocalTime.parse("07:29:59")) &&
                departureTime.toLocalTime().isBefore(LocalTime.parse("09:30:01")) ||
                departureTime.toLocalTime().isAfter(LocalTime.parse("15:59:59")) &&
                        departureTime.toLocalTime().isBefore(LocalTime.parse("19:30:01"))) {
            isEcoHour = false;
        }
        if (departureTime.toLocalTime().isAfter(LocalTime.parse("09:30:01")) &&
                departureTime.toLocalTime().isBefore(LocalTime.parse("15:59:59")) ||
                departureTime.toLocalTime().isAfter(LocalTime.parse("19:29:59")) ||
                departureTime.toLocalTime().isBefore(LocalTime.parse("07:29:59"))) {
            isEcoHour = true;
        }

        String currCardType = UserService.getUserCardType(conn, currUserId);
        if(currCardType.equals("NormalPass") && isEcoHour == true){
            ticketPrice = ticketPrice - (ticketPrice *= 0.05);
        }
        if(currCardType.equals("SeniorPass")){
            ticketPrice = ticketPrice - (ticketPrice *= 0.34);
        }
        if(currCardType.equals("FamilyPass")){
            String answer = null;

            do {
                System.out.println("Are any kids (below 16 years old) travelling with you? [y/n]");
                answer = sc.nextLine();
                if (answer.equals("y")) {
                    ticketPrice = ticketPrice - (ticketPrice *= 0.50);
                } else if(answer.equals("n")){
                    ticketPrice = ticketPrice - (ticketPrice *= 0.10);
                }
            }while(!answer.equals("y") && !answer.equals("n"));
        }
        return ticketPrice;
    }

    public static void createTicketAndCalcByDistance(Connection conn, String currUserId) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);

        int trainId = 0;
        double distance = 0.00;
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        boolean findTrainSuccessful = false;

        do {
            System.out.println("Enter the id of the train you want to choose: ");
            trainId = sc.nextInt();

            String getTrainIdQuery = "SELECT id, distance FROM trains WHERE id = ?";
            prStatement = conn.prepareStatement(getTrainIdQuery);

            prStatement.setInt(1, trainId);
            rs = prStatement.executeQuery();

            if (rs.next()) {
                trainId = rs.getInt("id");
                distance = rs.getDouble("distance");
                System.out.println("Selected train number: " + trainId + " | distance [km]: " + distance);
                findTrainSuccessful = true;
            } else {
                System.out.println("Incorrect train number.");
            }
        } while (findTrainSuccessful == false);

        //set ticket price
        double pricePerKilometer = 1.00;
        double ticketPrice = distance * pricePerKilometer;

        //apply discounts
        ticketPrice = discountsByHours(conn, sc, currUserId, ticketPrice, trainId);

        //check for latest reservation
        int currReservationId = ReservationService.checkForLatestReservation(conn, currUserId);

        //create ticket
        boolean isRoundTrip = checkForRoundTrip();
        if (isRoundTrip){
            ticketPrice *= 2;
        }

        int isRoundTripValue = 0;
        if(isRoundTrip){
            isRoundTripValue = 1;
        }else{
            isRoundTripValue = 0;
        }

        String createTicketQuery = "INSERT INTO tickets (ticketPrice, isRoundTrip, train_id, reservation_id) VALUES (?, ?, ?, ?)";
        prStatement = conn.prepareStatement(createTicketQuery);

        prStatement.setDouble(1, ticketPrice);
        prStatement.setInt(2, isRoundTripValue);
        prStatement.setInt(3, trainId);
        prStatement.setInt(4, currReservationId);
        prStatement.executeUpdate();

        //update reservation total price
        double reservationTotalPrice = ReservationService.getReservationTotalPrice(conn, currUserId, currReservationId);
        int userId = UserService.getCurrUserId(conn, currUserId);
        reservationTotalPrice += ticketPrice;

        String updateTotalPrice = "UPDATE reservations SET totalPrice = ? WHERE id = ?";
        prStatement = conn.prepareStatement(updateTotalPrice);

        try {
            prStatement.setDouble(1, reservationTotalPrice);
            prStatement.setInt(2, currReservationId);
            int changedRowsNumber = prStatement.executeUpdate();

            if (changedRowsNumber > 0) {
                System.out.println("Successfully updated reservation total price!");
            } else {
                System.out.println("Failed to update reservation total price!");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

    public static void displayAllTickets(Connection conn) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayAllTickets = "SELECT * FROM tickets";
            prStatement = conn.prepareStatement(displayAllTickets);
            rs = prStatement.executeQuery();

            System.out.println("--------------------------------------------------------");
            System.out.println("| id | Price | Is Round Trip | Train id | Reservation id");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                int ticketId = rs.getInt("id");
                double ticketPrice = rs.getDouble("ticketPrice");
                int isRoundTrip = rs.getInt("isRoundTrip");
                int train_id = rs.getInt("train_id");
                int reservation_id = rs.getInt("reservation_id");

                System.out.println("| " + ticketId + " | " + ticketPrice + " | " + isRoundTrip +
                        " | " + train_id + " | " + reservation_id);
            }
        }finally{
            if (rs != null) {
                rs.close();
            }
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

    public static void displayUserTickets(Connection conn, String userIdResult) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayUsersTickets = "SELECT * FROM tickets JOIN reservations ON tickets.reservation_id = reservations.id " +
                    "JOIN users ON reservations.user_id = users.id WHERE users.userIdNumber = ?";
            prStatement = conn.prepareStatement(displayUsersTickets);

            prStatement.setString(1, userIdResult);
            rs = prStatement.executeQuery();

            System.out.println("--------------------------------------------------------");
            System.out.println("| id | Price | Is Round Trip | Train id | Reservation id");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                int ticketId = rs.getInt("id");
                double ticketPrice = rs.getDouble("ticketPrice");
                boolean isRoundTrip = rs.getBoolean("isRoundTrip");
                int train_id = rs.getInt("train_id");
                int reservation_id = rs.getInt("reservation_id");

                System.out.println("| " + ticketId + " | " + ticketPrice + " | " + isRoundTrip +
                        " | " + train_id + " | " + reservation_id);
            }
        }finally{
            if (rs != null) {
                rs.close();
            }
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

    public static void deleteTicket(Connection conn, String userIdResult) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        PreparedStatement prStatement = null;
        int changedRowsNumber = 0;

        int userId = UserService.getCurrUserId(conn, userIdResult);
        int latestReservationId = ReservationService.checkForLatestReservation(conn, userIdResult);

        System.out.println("Enter the id of the ticket you want to remove: ");
        int inputTicketId = sc.nextInt();

        String deleteReservationQuery = "DELETE FROM tickets JOIN reservation ON reservations.ticket_id = tickets.id " +
                                        "WHERE tickets.id = ? AND reservations.user_id = ? AND reservations.id = ?";
        prStatement = conn.prepareStatement(deleteReservationQuery);
        try {
            prStatement.setInt(1, inputTicketId);
            prStatement.setInt(2, userId);
            prStatement.setInt(3, latestReservationId);
            changedRowsNumber = prStatement.executeUpdate();

            if (changedRowsNumber == 1) {
                System.out.println("Ticket deleted successfully!");
            } else {
                System.out.println("Failed to remove ticket!");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }
}
