package Services;

import java.sql.*;
import java.time.LocalTime;
import java.util.Scanner;

public class ReservationService {
    public ReservationService() {}

    public static int createReservation (Connection conn, String userIdNumResult) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        int currReservationId = 0;
        int changedRowsNumber = 0;

        int userId = UserService.getCurrUserId(conn, userIdNumResult);

        LocalTime currentTime = LocalTime.now();
        String addNewResQuery = "INSERT INTO reservations (user_id, totalPrice, dateOfReservation) VALUES (?, ?, ?)";
        prStatement = conn.prepareStatement(addNewResQuery);
        try {
            prStatement.setInt(1, userId);
            prStatement.setDouble(2, 0.00);
            prStatement.setObject(3, currentTime);
            changedRowsNumber = prStatement.executeUpdate();

            if (changedRowsNumber == 1) {
                System.out.println("Reservation created successfully!");
            } else {
                System.out.println("Failed to create reservation!");
            }

            String getReservationIdQuery = "SELECT id FROM reservations WHERE reservations.dateOfReservation = ?";
            prStatement = conn.prepareStatement(getReservationIdQuery);

            prStatement.setObject(1, currentTime);
            rs = prStatement.executeQuery();
            while (rs.next()) {
                currReservationId = rs.getInt("id");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
        currReservationId = checkForLatestReservation(conn, userIdNumResult);

        return currReservationId;
    }

    public static void deleteReservation(Connection conn, String userIdResult) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        PreparedStatement prStatement = null;
        int changedRowsNumber = 0;

        int currUserId = UserService.getCurrUserId(conn, userIdResult);
        System.out.println("Enter the id of the reservation you want to remove: ");
        int inputReservationId = sc.nextInt();


        String deleteReservationQuery = "DELETE FROM reservations WHERE id = ? AND user_id = ?";
        prStatement = conn.prepareStatement(deleteReservationQuery);
        try {
            prStatement.setInt(1, inputReservationId);
            prStatement.setInt(2, currUserId);
            changedRowsNumber = prStatement.executeUpdate();

            if (changedRowsNumber == 1) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Failed to remove reservation!");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

    public static void displayAllReservations(Connection conn) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayAllTickets = "SELECT * FROM reservations";
            prStatement = conn.prepareStatement(displayAllTickets);
            rs = prStatement.executeQuery();

            System.out.println("--------------------------------------------------------");
            System.out.println("| id | User id | Total price | Date Of Reservation");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                int reservationId = rs.getInt("id");
                int user_id = rs.getInt("user_id");
                double totalPrice = rs.getDouble("totalPrice");
                Time dateOfReservation = rs.getTime("dateOfReservation");

                System.out.println("| " + reservationId + " | " + user_id + " | " + totalPrice + " | " + dateOfReservation);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

    public static void displayUserReservations(Connection conn, String userIdResult) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayUsersTickets = "SELECT * FROM reservations JOIN users ON reservations.user_id = users.id" +
                    " WHERE users.userIdNumber = ?";
            prStatement = conn.prepareStatement(displayUsersTickets);

            prStatement.setString(1, userIdResult);
            rs = prStatement.executeQuery();

            System.out.println("--------------------------------------------------------");
            System.out.println("| id | User id | Total price | Date of Reservation");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                int reservationId = rs.getInt("id");
                int user_id = rs.getInt("user_id");
                double totalPrice = rs.getDouble("totalPrice");
                Time dateOfReservation = rs.getTime("dateOfReservation");

                System.out.println("| " + reservationId + " | " + user_id + " | " + totalPrice + " | " + dateOfReservation);
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

    public static int checkForLatestReservation(Connection conn, String currUserId) throws SQLException, Exception {
        int latestReservationId = 0;
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        int userId = UserService.getCurrUserId(conn, currUserId);

        String getLatestReservationId = "SELECT id FROM reservations WHERE user_id = ? ORDER BY TIME (dateOfReservation)" +
                                        " DESC LIMIT 1 ";
        prStatement = conn.prepareStatement(getLatestReservationId);

        try {
            prStatement.setInt(1, userId);
            rs = prStatement.executeQuery();
            boolean hasRow = rs.next();
            if(!hasRow){
                 latestReservationId = ReservationService.createReservation(conn, currUserId);
             }else {
                 if (hasRow) {
                     latestReservationId = rs.getInt("id");
                 }
             }
        } finally {
            if(rs != null){
                rs.close();
            }
            if (prStatement != null) {
                prStatement.close();
            }
        }
        return latestReservationId;
    }

    public static double getReservationTotalPrice(Connection conn, String currUserId, int currReservationId) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        double totalPrice = 0;

        int userId = UserService.getCurrUserId(conn, currUserId);

        String getReservationTotalPrice = "SELECT totalPrice FROM reservations WHERE user_id = ? AND id = ?";
        prStatement = conn.prepareStatement(getReservationTotalPrice);
        try {
            prStatement.setInt(1, userId);
            prStatement.setInt(2, currReservationId);
            rs = prStatement.executeQuery();

            if (rs.next()) {
                totalPrice = rs.getDouble("totalPrice");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (prStatement != null) {
                prStatement.close();
            }
        }

        return totalPrice;
    }
}
