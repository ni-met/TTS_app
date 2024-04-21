import Services.ReservationService;
import Services.TicketService;
import Services.TrainService;
import Services.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    public Menu() {}

    public String homeMenu(Connection conn, Scanner sc) throws SQLException, Exception{
        int answer = 0;
        String currUserId = null;

        do {
            System.out.println("HOME\n*-*-*\n1.Login\n2.SignUp\n3.Exit");
            answer = sc.nextInt();

            switch (answer) {
                case 1:
                    currUserId = UserService.userLogin(conn);
                    mainMenu(conn, sc, currUserId);
                case 2: UserService.createUser(conn);
                    homeMenu(conn, sc);
                case 3: System.exit(0);
            }
        }while(answer != 1 && answer != 2 && answer != 3);
        return currUserId;
    }

    public void mainMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception{
        int answer = 0;

        do {
            boolean hasAccess = UserService.checkCurrentUsersAccess(conn, currUserId);
            if(hasAccess){
                System.out.println("---------\nMAIN MENU\n---------\n1.Trains Menu\n2.Tickets Menu\n3.Reservations Menu" +
                        "\n4.Account\n5.Users Menu\n6.Exit");
            }else {
                System.out.println("----------------------------------------------------------------------------");
                System.out.println("---------\nMAIN MENU\n---------\n1.Trains Menu\n2.Tickets Menu\n3.Reservations Menu" +
                        "\n4.Account\n5.Exit");
            }
            answer = sc.nextInt();

            switch (answer) {
                case 1: trainsMenu(conn, sc, currUserId);
                case 2: if(hasAccess){
                            ticketsAdministratorMenu(conn, sc, currUserId);
                        }else{
                            ticketsUserMenu(conn, sc, currUserId);
                        }
                case 3: if(hasAccess){
                            reservationsAdministratorMenu(conn, sc, currUserId);
                        }else{
                            reservationsUsersMenu(conn, sc, currUserId);
                        }
                case 4: accountMenu(conn, sc, currUserId);
                case 5: if(hasAccess){
                            usersAdministratorMenu(conn, sc, currUserId);
                        }else{
                            UserService.userLogOut();
                        }
                case 6: if(hasAccess){
                            UserService.userLogOut();
                        }else{
                            mainMenu(conn, sc, currUserId);
                        }
            }
        }while(answer != 1 && answer != 2 && answer != 3 && answer != 4 && answer != 5 && answer != 6);
    }

    public void trainsMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception{
        int answer = 0;

        do {
            boolean hasAccess = UserService.checkCurrentUsersAccess(conn, currUserId);
            if(hasAccess){
                System.out.println("----------------------------------------------------------------------------");
                System.out.println("TRAINS MENU\n-------------\n1.Show All Trains\n2.Search A Train By " +
                        "Destination/DepartureTime\n3.Add A New Train\n4.Back");
            }else {
                System.out.println("----------------------------------------------------------------------------");
                System.out.println("TRAINS MENU\n-------------\n1.Show All Trains\n2.Search A Train By " +
                        "Destination/Departure Time\n3.Back");
            }
            answer = sc.nextInt();

            switch (answer) {
                case 1: TrainService.displayAllTrains(conn);
                        trainsMenu(conn, sc, currUserId);
                case 2: TrainService.searchTrain(conn);
                        trainsMenu(conn, sc, currUserId);
                case 3: if(hasAccess){
                            TrainService.addTrain(conn, hasAccess);
                            trainsMenu(conn, sc, currUserId);
                        }else{
                            mainMenu(conn, sc, currUserId);
                        }
                case 4: if(hasAccess){
                            mainMenu(conn, sc, currUserId);
                        }else{
                            trainsMenu(conn, sc, currUserId);
                        }
            }
        }while(answer != 1 && answer != 2 && answer != 3 && answer != 4);
    }

    public void ticketsUserMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception {
        int answer = 0;

        do {
            System.out.println("----------------------------------------------------------------------------");
            System.out.println("TICKETS MENU\n--------------\n1.Add A New Ticket\n2.Remove Ticket From " +
                    "Reservation\n3.Show My Tickets\n4.Back");
            answer = sc.nextInt();

            switch (answer) {
                case 1: TicketService.createTicketAndCalcByDistance(conn, currUserId);
                        ticketsUserMenu(conn, sc, currUserId);
                case 2: TicketService.deleteTicket(conn, currUserId);
                        ticketsUserMenu(conn, sc, currUserId);
                case 3: TicketService.displayUserTickets(conn, currUserId);
                        ticketsUserMenu(conn, sc, currUserId);
                case 4: mainMenu(conn, sc, currUserId);
            }
        }while (answer != 1 && answer != 2 && answer != 3 && answer != 4);
    }

    public void ticketsAdministratorMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception  {
        int answer = 0;

        do {
            System.out.println("----------------------------------------------------------------------------");
            System.out.println("TICKETS MENU\n--------------\n1.Show All Tickets\n2.Back");
            answer = sc.nextInt();

            switch (answer) {
                case 1: TicketService.displayAllTickets(conn);
                        ticketsAdministratorMenu(conn, sc, currUserId);
                case 2: mainMenu(conn, sc, currUserId);
            }
        }while (answer != 1 && answer != 2) ;
    }

    public void reservationsAdministratorMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception {
        int answer = 0;

        do {
            boolean hasAccess = UserService.checkCurrentUsersAccess(conn, currUserId);

            System.out.println("----------------------------------------------------------------------------");
            System.out.println("RESERVATIONS MENU\n--------------\n1.Show All Reservations\n2.Back");
            answer = sc.nextInt();

            switch (answer) {
                case 1: ReservationService.displayAllReservations(conn);
                        reservationsAdministratorMenu(conn, sc, currUserId);
                case 2: mainMenu(conn, sc, currUserId);
            }
        }while (answer != 1 && answer != 2);
    }

    public void reservationsUsersMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception {
        int answer = 0;

        do {
            boolean hasAccess = UserService.checkCurrentUsersAccess(conn, currUserId);

            System.out.println("----------------------------------------------------------------------------");
            System.out.println("RESERVATIONS MENU\n-------------\n1.Add A New Reservation\n2.Delete A Reservation\n" +
                                "3.Show My Reservations\n4.Back");
            answer = sc.nextInt();

            switch (answer) {
                case 1: ReservationService.createReservation(conn, currUserId);
                        reservationsUsersMenu(conn, sc, currUserId);
                case 2: ReservationService.deleteReservation(conn, currUserId);
                        reservationsUsersMenu(conn, sc, currUserId);
                case 3: ReservationService.displayUserReservations(conn, currUserId);
                        reservationsUsersMenu(conn, sc, currUserId);
                case 4: mainMenu(conn, sc, currUserId);
            }
        }while (answer != 1 && answer != 2 && answer != 3 && answer != 4);
    }

    public void usersAdministratorMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception {
        Scanner scAdditional = new Scanner(System.in);
        int answer = 0;

        do {
            System.out.println("----------------------------------------------------------------------------");
            System.out.println("USERS MENU\n-------------\n1.Show All Users\n2.Create A Profile\n" +
                                "3.Delete A Profile\n4.Change User's Card Type\n5.Back");
            answer = sc.nextInt();

            switch (answer) {
                case 1: UserService.displayAllUsers(conn);
                        usersAdministratorMenu(conn, sc, currUserId);
                case 2: UserService.createUser(conn);
                        usersAdministratorMenu(conn, sc, currUserId);
                case 3: System.out.println("Enter the userIdNumber of the user you want to delete the profile of: ");
                        String inputUserIdNum = scAdditional.nextLine();

                        UserService.deleteUserAccount(conn, inputUserIdNum);
                        usersAdministratorMenu(conn, sc, currUserId);
                case 4: System.out.println("Enter the userIdNumber of the user you want to change the card of: ");
                        String inputUserIdNumber = scAdditional.nextLine();

                        UserService.addUserCard(conn, inputUserIdNumber);
                        usersAdministratorMenu(conn, sc, currUserId);
                case 5: mainMenu(conn, sc, currUserId);
            }
        }while (answer != 1 && answer != 2 && answer != 3 && answer != 4 && answer != 5);
    }

    public void accountMenu(Connection conn, Scanner sc, String currUserId) throws SQLException, Exception {
        int answer = 0;

        do {
            System.out.println("----------------------------------------------------------------------------");
            System.out.println("ACCOUNT MENU\n-------------\n1.Show Account Info\n2.Change Your Card Type\n3.Back");
            answer = sc.nextInt();

            switch (answer) {
                case 1: UserService.showAccountInfo(conn, currUserId);
                        accountMenu(conn, sc, currUserId);
                case 2: UserService.addUserCard(conn, currUserId);
                        accountMenu(conn, sc, currUserId);
                case 3: mainMenu(conn, sc, currUserId);
            }
        }while (answer != 1 && answer != 2 && answer != 3);
    }
}
