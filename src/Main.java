import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, Exception {
        final String dbUrl = "jdbc:mysql://localhost:3306/ticket_system";
        Connection conn = DriverManager.getConnection(dbUrl, "user", "pass");
        Scanner sc = new Scanner(System.in);

        Menu menu = new Menu();
        menu.homeMenu(conn, sc);

        sc.close();
        conn.close();
    }
}