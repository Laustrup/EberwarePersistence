package eberware.api.core.systems.libaries;

import eberware.api.core.systems.services.PasswordService;
import lombok.Getter;

public class SecurityLibrary {

    @Getter
    public static final int _bCryptFactor = 5;

    public static final int bCryptOffsetLowerBound = 7;

    public static final int bCryptOffsetUpperBound = 72;

    @Getter
    private static String _gibberish;

    public static final char passwordEncodingCharacter = '€';

    public static final String gibberishRules = """
            1. Each index must be between a -> f either in upper or lowercase
            """;

    public static void setup(
            String gibberish
    ) {
        if (!SecurityLibrary.gibberishIsPermitted(gibberish))
            throw new IllegalArgumentException(String.format("""
                        
                        The gibberish "%s" is not allowed!
                        
                        Rules that should be followed:
                        
                        %s
                        """,
                    gibberish,
                    SecurityLibrary.gibberishRules
            ));

        _gibberish = gibberish;
    }

    public static boolean bCryptOffsetIsPermitted(int offset) {
        return offset > bCryptOffsetLowerBound && offset < bCryptOffsetUpperBound;
    }

    public static boolean gibberishIsPermitted(String gibberish) {
        for (char letter : gibberish.toCharArray())
            if (!gibberishIsValid(letter))
                return false;

        return true;
    }

    private static boolean gibberishIsValid(char letter) {
        int hex = PasswordService.convertHexToInt(letter);

        return true;
    }

    @Getter
    public enum CommandOption {
        GIBBERISH("gibberish");

        private final String _title;

        CommandOption(String title) {
            _title = title;
        }
    }
}
