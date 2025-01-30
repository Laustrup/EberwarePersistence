package eberware.api.core.systems.services;

import eberware.api.core.systems.libaries.SecurityLibrary;
import eberware.api.core.systems.models.Password;
import eberware.api.core.systems.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.naming.SizeLimitExceededException;

public class PasswordService {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(SecurityLibrary.get_bCryptFactor());

    public static Password encode(String plainTextPassword) {
        try {
            return enGibberise(encoder.encode(plainTextPassword));
        } catch (SizeLimitExceededException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean matches(String plainTextPassword, String gibberish) {
        try {
            return encoder.matches(plainTextPassword, deGibberise(gibberish));
        } catch (SizeLimitExceededException e) {
            throw new RuntimeException(e);
        }
    }

    private static String deGibberise(String gibberish) throws SizeLimitExceededException {
        String[] encodings = gibberish.split(String.valueOf(SecurityLibrary.passwordEncodingCharacter));
        StringBuilder password = new StringBuilder(encodings[0]);
        StringBuilder stash = new StringBuilder(encodings[1]);

        for (int i = 0; i < stash.length(); i++) {
            int offset = calculateOffset(SecurityLibrary.get_gibberish().charAt(i));
            password.replace(offset, offset + 1, String.valueOf(stash.toString().charAt(i)));
        }
        return password.toString();
    }

    private static Password enGibberise(String bcrypt)
            throws SizeLimitExceededException, IllegalArgumentException {
        StringBuilder password = new StringBuilder(bcrypt);
        StringBuilder stash = new StringBuilder();

        StringService.Configuration stringConfiguration = new StringService.Configuration(false, true);
        for (char letter : SecurityLibrary.get_gibberish().toCharArray()) {
            int offset = calculateOffset(letter);
            stash.append(password.charAt(offset));
            password.replace(
                    offset,
                    offset + 1,
                    StringService.generateRandom(1, stringConfiguration)
            );
        }

        return new Password(password.toString(), stash.toString());
    }

    private static int calculateOffset(char letter) throws SizeLimitExceededException {
        int offset = convertHexToInt(letter) + SecurityLibrary.bCryptOffsetLowerBound;

        if (!SecurityLibrary.bCryptOffsetIsPermitted(offset))
            throw new SizeLimitExceededException(
                    String.format("""
                            The bcrypt offset "%s" is not within %s and %s, therefore it is not allowed!
                            """,
                            offset,
                            SecurityLibrary.bCryptOffsetLowerBound,
                            SecurityLibrary.bCryptOffsetUpperBound
                    )
            );

        return offset;
    }

    public static int convertHexToInt(char hex) throws IllegalArgumentException {
        return switch (hex) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Integer.parseInt(String.valueOf(hex));
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

    public static void check(String password, User user) throws IllegalAccessException {
        if (user != null && !PasswordService.matches(
                password,
                user.get_password()
        ))
            throw new IllegalAccessException(String.format("""
                    Passwords "%s" and "%s" does not match for user with email "%s"
                    """,
                    password,
                    user.get_password(),
                    user.get_contactInfo().get_email()
            ));
    }
}
