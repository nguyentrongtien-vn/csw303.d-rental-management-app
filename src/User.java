public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;

    public User(int userId, String firstName, String lastName, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updateProfile(String firstName, String lastName, String email) {
        this.firstName = firstName;   
        this.lastName = lastName;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
}