package Services;

import java.sql.*;
import java.util.Scanner;

public class UserService {
    public UserService() {}

    public static void createUser(Connection conn) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter ID: ");
        String inputUserId = sc.nextLine();
        System.out.println("Enter username: ");
        String inputUsername = sc.nextLine();
        System.out.println("Enter password: ");
        String inputPassword = sc.nextLine();
        System.out.println("Enter your first name: ");
        String inputFirstName = sc.nextLine();
        System.out.println("Enter your last name: ");
        String inputLastName = sc.nextLine();
        System.out.println("Enter your age: ");
        String inputAge = sc.nextLine();

        String addNewUserQuery = "INSERT INTO users(userIdNumber, username, userPassword, firstName, lastName, userAge, hasAccess, card_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement prStatement = conn.prepareStatement(addNewUserQuery);

        try{
            prStatement.setString(1, inputUserId);
            prStatement.setString(2, inputUsername);
            prStatement.setString(3, inputPassword);
            prStatement.setString(4, inputFirstName);
            prStatement.setString(5, inputLastName);
            prStatement.setString(6, inputAge);
            //gives normal client profile & NormalPass
            prStatement.setString(7, "0");
            prStatement.setString(8, "3");

            int changedRowsNumber = prStatement.executeUpdate();
            if (changedRowsNumber > 0) {
                System.out.println("New user created successfully!");
            } else {
                System.out.println("Failed to create user!");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

    public static String userLogin(Connection conn) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);

        String userIdResult = new String();
        String inputUsername = null;
        Boolean loginSuccessful = false;
        String inputPassword = null;
        ResultSet rs = null;
        do {
            System.out.println("Enter username: ");
            inputUsername = sc.nextLine();
            System.out.println("Enter password: ");
            inputPassword = sc.nextLine();

            String userLoginQuery = "SELECT userIdNumber FROM users WHERE username = ? AND userPassword = ?";
            PreparedStatement prStatement = conn.prepareStatement(userLoginQuery);

            prStatement.setString(1, inputUsername);
            prStatement.setString(2, inputPassword);

            rs = prStatement.executeQuery();

            if (rs.next()) {
                userIdResult = rs.getString("userIdNumber");
                System.out.println("Welcome, " + inputUsername + "!");
                loginSuccessful = true;
            } else {
                System.out.println("Incorrect username or password.");
            }
        }while (loginSuccessful == false);

        return userIdResult;
    }

    public static void userLogOut() {
        System.out.println("You've logged out!");
        System.exit(0);
    }

    public static void updateUserCard(Connection conn, String userIdResult, int newCardId) throws SQLException, Exception {
        String updateCardTypeQuery = "UPDATE users SET card_id = ? WHERE userIdNumber  = ?";
        PreparedStatement updateCardStatement = conn.prepareStatement(updateCardTypeQuery);
        try {
            updateCardStatement.setInt(1, newCardId);
            updateCardStatement.setString(2, userIdResult);
            int changedRowsNumber = updateCardStatement.executeUpdate();

            if (changedRowsNumber == 1) {
                System.out.println("Card type changed successfully!");
            } else {
                System.out.println("Failed to change card type!");
            }
        }finally{
            if (updateCardStatement != null) {
                updateCardStatement.close();
            }
        }

    }

    public static void addUserCard(Connection conn, String userIdResult) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        int inputCardType = 0;

        do {
            System.out.println("Types of cards:");
            System.out.println("1.SeniorPass\n2.FamilyPass\n3.NormalPass");
            System.out.println("Enter the type of card you want [1/2/3]: ");

            inputCardType = sc.nextInt();
        } while (inputCardType != 1 && inputCardType != 2 && inputCardType != 3);

        int userAge = 0;

        String selectUserAgeQuery = "SELECT userAge FROM users WHERE userIdNumber = ?";
        PreparedStatement prStatement = conn.prepareStatement(selectUserAgeQuery);

        prStatement.setString(1, userIdResult);
        ResultSet rs =  prStatement.executeQuery();
        if (rs.next()) {
            userAge = rs.getInt("userAge");
        }

        if(inputCardType == 3){
            updateUserCard(conn, userIdResult, 3);
        }
        if(userAge > 60 && inputCardType == 1) {
            updateUserCard(conn, userIdResult, 1);
        }
        if(inputCardType == 2) {
            updateUserCard(conn, userIdResult, 2);
        }
    }

    public static void displayAllUsers(Connection conn) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayAllTrains = "SELECT * FROM users";
            prStatement = conn.prepareStatement(displayAllTrains);
            rs = prStatement.executeQuery();

            System.out.println("----------------------------------------------------------------------------");
            System.out.println("| id | User ID Number | Username | First Name | Last Name | Age " +
                    "| Has Access | Card id");
            System.out.println("----------------------------------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                String userIdNumber = rs.getString("userIdNumber");
                String username = rs.getString("username");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                int userAge = rs.getInt("userAge");
                boolean hasAccess = rs.getBoolean("hasAccess");
                int card_id = rs.getInt("card_id");

                System.out.println("| " +  id + " | " + userIdNumber + " | " + username +
                        " | " + firstName + " | " + lastName + " | " + userAge + " | " + hasAccess + " | " + card_id);
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

    public static boolean checkCurrentUsersAccess(Connection conn, String currUserID) throws SQLException, Exception {
        boolean userStatusResult = false;

        String userStatusQuery = "SELECT hasAccess FROM users WHERE userIdNumber = ?";
        PreparedStatement prStatement = conn.prepareStatement(userStatusQuery);

        prStatement.setString(1, currUserID);
        ResultSet rs = prStatement.executeQuery();
        if (rs.next()) {
            userStatusResult = rs.getBoolean("hasAccess");
        } else {
            System.out.println("Error: Invalid data input!");
        }

        return userStatusResult;
    }

    public static int getCurrUserId(Connection conn, String userIdNumResult) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        int currUserId = 0;

        String getUserIdQuery = "SELECT id FROM users WHERE userIdNumber = ?";
        prStatement = conn.prepareStatement(getUserIdQuery);
        try {
            prStatement.setString(1, userIdNumResult);
            rs = prStatement.executeQuery();

            while (rs.next()) {
                currUserId = rs.getInt("id");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
        return currUserId;
    }

    public static String getUserCardType(Connection conn, String currUserId) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        String cardType = null;

        try {
            String displayAccountInfo = "SELECT cards.type FROM cards JOIN users ON cards.id = users.card_id " +
                                        "WHERE userIdNumber = ?";
            prStatement = conn.prepareStatement(displayAccountInfo);
            prStatement.setString(1, currUserId);
            rs = prStatement.executeQuery();

            while (rs.next()) {
                cardType = rs.getString("type");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (prStatement != null) {
                prStatement.close();
            }
        }
        return cardType;
    }

    public static void showAccountInfo(Connection conn, String currUserId) throws SQLException, Exception{
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayAccountInfo = "SELECT userIdNumber, username, firstName, lastName, userAge, card_id " +
                    "FROM users WHERE userIdNumber = ?";
            prStatement = conn.prepareStatement(displayAccountInfo);
            prStatement.setString(1, currUserId);
            rs = prStatement.executeQuery();

            System.out.println("--------------------------------------------------------");
            System.out.println("| id number | Username | First name | Last name | Age | Card id");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                String userIdNumber = rs.getString("userIdNumber");
                String username = rs.getString("username");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String userAge = rs.getString("userAge");
                int card_id = rs.getInt("card_id");

                System.out.println("| " + userIdNumber + " | " + username + " | " + firstName + " | " + lastName +
                        " | " + userAge + " | " + card_id);
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
    public static void deleteUserAccount(Connection conn, String userIdResult) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        PreparedStatement prStatement = null;
        int changedRowsNumber = 0;

        String deleteReservationQuery = "DELETE * FROM users WHERE userIdNumber = ?";
        prStatement = conn.prepareStatement(deleteReservationQuery);
        try {
            prStatement.setString(1, userIdResult);
            changedRowsNumber = prStatement.executeUpdate();

            if (changedRowsNumber == 1) {
                System.out.println("User Account deleted successfully!");
            } else {
                System.out.println("Failed to remove user account!");
            }
        } finally {
            if (prStatement != null) {
                prStatement.close();
            }
        }
    }

}
