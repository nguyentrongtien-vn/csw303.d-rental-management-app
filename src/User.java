public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;

    public User(int userId, String firstName,
                String lastName, String email) {

        validateUserId(userId);

        firstName = validateName(firstName, "First name");
        lastName = validateName(lastName, "Last name");
        email = validateEmail(email);

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                "UserID must be a positive integer."
            );
        }
    }

    private String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " cannot be empty."
            );
        }

        String trimmedName = name.trim();

        if (!trimmedName.matches("[\\p{L}]+(?:[ '\\u002D][\\p{L}]+)*")) {
            throw new IllegalArgumentException(
                fieldName + " contains invalid characters."
            );
        }

        return trimmedName;
    }

    private String validateEmail(String email) {
        if (email == null ||
            !email.trim().matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            )) {
            throw new IllegalArgumentException(
                "Invalid email format."
            );
        }

        return email.trim();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updateProfile(String firstName,
                              String lastName, String email) {

        // Validate everything before changing the profile
        String newFirstName = validateName(firstName, "First name");
        String newLastName = validateName(lastName, "Last name");
        String newEmail = validateEmail(email);

        // Update only after all validations succeed
        this.firstName = newFirstName;
        this.lastName = newLastName;
        this.email = newEmail;
    }

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
}