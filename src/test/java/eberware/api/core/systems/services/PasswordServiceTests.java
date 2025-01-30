package eberware.api.core.systems.services;

import eberware.api.core.systems.libaries.SecurityLibrary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTests {

    private final String _password = "T€5t";

    @BeforeAll
    static void setup() {
        SecurityLibrary.setup("A5C");
    }

    @Test
    void canReadGibberish() {
        assertTrue(PasswordService.matches(
                _password,
                PasswordService.encode(_password).get_combined()
        ));
    }
}