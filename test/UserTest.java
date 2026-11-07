
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        User user = new User(
            2, "Anna", "Nguyen", "anna@example.com"
        );

        assertEquals(2, user.getUserId());
        assertEquals("Anna", user.getFirstName());
        assertEquals("Nguyen", user.getLastName());
        assertEquals("anna@example.com", user.getEmail());
    }

    @Test
    void shouldRejectNonPositiveUserId() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(-1, "Anna", "Nguyen", "anna@example.com")
        );

        assertThrows(IllegalArgumentException.class, () ->
            new User(0, "Anna", "Nguyen", "anna@example.com")
        );
    }

    @Test
    void shouldRejectBlankNames() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(2, "   ", "Nguyen", "anna@example.com")
        );

        assertThrows(IllegalArgumentException.class, () ->
            new User(2, "Anna", "", "anna@example.com")
        );
    }

    @Test
    void shouldAcceptOrdinaryNames() {
        User user = new User(
            2, "Anna-Marie", "O'Connor", "anna@example.com"
        );

        assertEquals("Anna-Marie", user.getFirstName());
        assertEquals("O'Connor", user.getLastName());
    }

    @Test
    void shouldTrimNames() {
        User user = new User(
            2, " Anna ", " Nguyen ", "anna@example.com"
        );

        assertEquals("Anna", user.getFirstName());
        assertEquals("Nguyen", user.getLastName());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(2, "Anna", "Nguyen", "anna@@example.com")
        );
    }

    @Test
    void shouldUpdateProfileWithValidData() {
        User user = new User(
            2, "Anna", "Nguyen", "anna@example.com"
        );

        user.updateProfile("John", "Smith", "john@example.com");

        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void shouldKeepOldProfileWhenUpdateIsInvalid() {
        User user = new User(
            2, "Anna", "Nguyen", "anna@example.com"
        );

        assertThrows(IllegalArgumentException.class, () ->
            user.updateProfile("John", "Smith", "anna@@example.com")
        );

        assertEquals("Anna", user.getFirstName());
        assertEquals("Nguyen", user.getLastName());
        assertEquals("anna@example.com", user.getEmail());
    }
}

