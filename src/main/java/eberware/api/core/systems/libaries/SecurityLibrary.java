package eberware.api.core.systems.libaries;

import eberware.api.core.systems.services.PasswordService;
import lombok.Getter;

public class SecurityLibrary {

    @Getter
    private static final int _pickingBound = 3;

    @Getter
    private static final int _bCryptOffsetLowerBound = 4;

    @Getter
    private static final int _bCryptOffsetUpperBound = 72;

    @Getter
    private static String _gibberish;

    @Getter
    private static final String _gibberishRules = String.format("""
            1. Each index must be higher than %s
            2. The picking bound is %s, therefore the total value of the gibberish must not exceed %s
            """,
            _bCryptOffsetLowerBound,
            _pickingBound,
            _bCryptOffsetUpperBound
    );

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
                    SecurityLibrary.get_gibberishRules()
            ));

        _gibberish = gibberish;
    }

    public static boolean bCryptOffsetIsPermitted(int offset) {
        return offset > _bCryptOffsetLowerBound && offset < _bCryptOffsetUpperBound;
    }

    public static boolean gibberishIsPermitted(String gibberish) {
        for (char letter : gibberish.toCharArray())
            if (!gibberishIsValid(letter, gibberish.length()))
                return false;

        return true;
    }

    private static boolean gibberishIsValid(char letter, int length) {
        int hex = PasswordService.convertHexToInt(letter);

        return hex > _bCryptOffsetLowerBound && hex * length < _bCryptOffsetUpperBound;
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
