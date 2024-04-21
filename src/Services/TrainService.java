package Services;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TrainService {
    public TrainService() {}

    public static void addTrain(Connection conn, boolean currentUserHasAccess) throws SQLException, Exception {
        if(currentUserHasAccess){
            Scanner sc = new Scanner(System.in);

            System.out.println("Enter where the train leaves from: ");
            String inputLeavingFrom = sc.nextLine();
            System.out.println("Enter where the train arrives at: ");
            String inputArrivingAt = sc.nextLine();
            System.out.println("Enter the time of departure: ");
            String inputDepartureTime = sc.nextLine();
            System.out.println("Enter the time of arrival: ");
            String inputArrivalTime = sc.nextLine();
            System.out.println("Enter the distance between the two destinations: ");
            double inputDistance = sc.nextDouble();

            LocalTime inputTimeOfDeparture = LocalTime.parse(inputDepartureTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime inputTimeOfArrival = LocalTime.parse(inputArrivalTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

            String addNewUserQuery = "INSERT INTO trains(leavingFrom, arrivingAt, distance, departureTime, arrivalTime) " +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement prStatement = conn.prepareStatement(addNewUserQuery);

            try {
                //prStatement.setString(1, inputTrainNumber);
                prStatement.setString(1, inputLeavingFrom);
                prStatement.setString(2, inputArrivingAt);
                prStatement.setDouble(3, inputDistance);
                prStatement.setObject(4, inputTimeOfDeparture);
                prStatement.setObject(5, inputTimeOfArrival);

                prStatement.executeUpdate();
            } finally {
                if (prStatement != null) {
                    prStatement.close();
                }
            }
            System.out.println("Successfully added new train!");
        }else{
            System.out.println("No Access Granted!");
        }
    }

    public static void displayAllTrains(Connection conn) throws SQLException, Exception {
        PreparedStatement prStatement = null;
        ResultSet rs = null;

        try {
            String displayAllTrains = "SELECT * FROM trains";
            prStatement = conn.prepareStatement(displayAllTrains);
            rs = prStatement.executeQuery();

            System.out.println("----------------------------------------------------------------------------");
            System.out.println("| id | Leaving From | Arriving At | Distance | Departure Time | Arrival Time");
            System.out.println("----------------------------------------------------------------------------");
            while (rs.next()) {
                int trainId = rs.getInt("id");
                String leavingFrom = rs.getString("leavingFrom");
                String arrivingAt = rs.getString("arrivingAt");
                double distance = rs.getDouble("distance");
                Time departureTime = rs.getTime("departureTime");
                Time arrivalTime = rs.getTime("arrivalTime");

                System.out.println("| " + trainId + " | " + leavingFrom + " | " + arrivingAt +
                        " | " + distance + " | " + departureTime + " | " + arrivalTime);
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

    public static void toPrintTrainInfoFromDb(Connection conn, String query, String inputInfo) throws SQLException, Exception{
        PreparedStatement prStatement = null;
        ResultSet rs = null;
        try{
            prStatement = conn.prepareStatement(query);

            prStatement.setString(1, inputInfo);
            rs = prStatement.executeQuery();

            System.out.println("| id | Leaving From | Arriving At | Distance | Departure Time | Arrival Time");
            while (rs.next()) {
                int trainId = rs.getInt("id");
                String leavingFrom = rs.getString("leavingFrom");
                String arrivingAt = rs.getString("arrivingAt");
                double distance = rs.getDouble("distance");
                Time departureTime = rs.getTime("departureTime");
                Time arrivalTime = rs.getTime("arrivalTime");

                System.out.println("| " + trainId + " | " + leavingFrom + " | " + arrivingAt +
                        " | " + distance + " | " + departureTime + " | " + arrivalTime);
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

    public static void searchTrain(Connection conn) throws SQLException, Exception {
        Scanner sc = new Scanner(System.in);
        Scanner scAdditional = new Scanner(System.in);

        int inputSearch = 0;
        String inputDestination = null;
        String inputTime = null;

        do {
            System.out.println("Search train by:\n1.Destination\n2.Departure time\nSelect [1/2]: ");
            inputSearch = sc.nextInt();
            if (inputSearch == 1) {
                System.out.println("Enter destination: ");
                inputDestination = scAdditional.nextLine();

                String findTrainDestinationQuery = "SELECT * FROM trains WHERE arrivingAt = ?";
                toPrintTrainInfoFromDb(conn, findTrainDestinationQuery, inputDestination);

            } else if (inputSearch == 2) {
                System.out.println("Enter departure time [HH:mm:ss]: ");
                inputTime = scAdditional.nextLine();

                String findTrainTimeQuery = "SELECT * FROM trains WHERE departureTime = ?";
                toPrintTrainInfoFromDb(conn, findTrainTimeQuery, inputTime);

            }else {
                System.out.println("Invalid data input!");
                System.out.println("-------------------");
            }
        }while(inputSearch != 1 && inputSearch != 2);
    }
}
