package Models;

public class User {
    private String userId;
    private String username;
    private String userPassword;
    private String firstName;
    private String lastName;
    private int userAge;
    private boolean hasAccess;
    private enum CardType{
        SeniorPass,
        FamilyPass,
        NormalPass
    }

    public User(String userId, String username, String userPassword, String firstName, String lastName, int userAge, boolean hasAccess) {
        this.userId = userId;
        this.username = username;
        this.userPassword = userPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userAge = userAge;
        this.hasAccess = hasAccess;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getUserAge() {
        return userAge;
    }

    public boolean isHasAccess() {
        return hasAccess;
    }
}
