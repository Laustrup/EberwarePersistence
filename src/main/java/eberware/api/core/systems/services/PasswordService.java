package eberware.api.core.systems.services;

import eberware.api.core.systems.libaries.SecurityLibrary;
import eberware.api.core.systems.models.Password;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.naming.SizeLimitExceededException;
import java.util.Random;

public class PasswordService {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final Random _random = new Random();

    public static String encode(String plainTextPassword) {
        return encoder.encode(plainTextPassword);
    }

    public static Password enGibberise(String plainTextPassword)
            throws SizeLimitExceededException, IllegalArgumentException {
        int picking = generatePicking();

        return new Password(
                enGibberise(plainTextPassword, picking),
                picking
        );
    }

    private static int generatePicking() {
        return _random.nextInt(SecurityLibrary.get_pickingBound());
    }

    private static String enGibberise(String plainText, int picking)
            throws SizeLimitExceededException, IllegalArgumentException {
        StringBuilder password = new StringBuilder(encoder.encode(plainText));

        for (char letter : SecurityLibrary.get_gibberish().toCharArray())
            enGibberise(password, picking, letter);

        return password.toString();
    }

    private static StringBuilder enGibberise(StringBuilder password, int picking, char letter)
            throws SizeLimitExceededException, IllegalArgumentException {
        return password.insert(
                calculateOffset(picking, letter),
                Integer.toHexString(_random.nextInt(16))
        );
    }

    private static int calculateOffset(int picking, char letter) throws SizeLimitExceededException {
        int offset = picking * convertHexToInt(letter);

        if (!SecurityLibrary.bCryptOffsetIsPermitted(offset))
            throw new SizeLimitExceededException(
                    String.format("""
                            The bcrypt offset "%s" is not within %s and %s, therefore it is not allowed!
                            """,
                            offset,
                            SecurityLibrary.get_bCryptOffsetLowerBound(),
                            SecurityLibrary.get_bCryptOffsetUpperBound()
                    )
            );

        return offset;
    }

    public static int convertHexToInt(char hex) throws IllegalArgumentException {
        return switch (hex) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> (int) hex;
            case 'a', 'A' -> 10;
            case 'b', 'B' -> 11;
            case 'c', 'C' -> 12;
            case 'd', 'D' -> 13;
            case 'e', 'E' -> 14;
            case 'f', 'F' -> 15;
            default -> throw new IllegalArgumentException(
                    String.format("""
                            The gibberish value "%s" cannot be converted into hex!
                            """,
                            hex
                    )
            );
        };
    }
}
