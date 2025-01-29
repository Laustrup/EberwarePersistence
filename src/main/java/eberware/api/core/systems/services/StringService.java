package eberware.api.core.systems.services;

import lombok.Getter;

import java.util.Random;

public class StringService {

    private static final String _numerals = "0123456789";

    private static final String _letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final Random _random = new Random();

    public static String randomString() {
        return generateRandom(_random.nextInt(_numerals.length() + _letters.length()) + 1);
    }

    public static String generateRandom(int length) {
        return generateRandom(length, null);
    }

    public static String generateRandom(int length, Configuration configuration) {
        String options = generateOptions(configuration);
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < length; i++)
            string.append(options.charAt(_random.nextInt(options.length())));

        return string.toString();
    }

    private static String generateOptions(Configuration configuration) {
        if (configuration == null)
            return _numerals + _letters;

        StringBuilder options = new StringBuilder();

        if (configuration.is_letters()) {
            options.append(_letters);

            if (configuration.is_uppercase() != configuration.is_lowercase()) {
                char[] chars = options.toString().toCharArray();

                for (int i = 0; i < chars.length; i++)
                    chars[i] = configuration.is_uppercase()
                            ? Character.toUpperCase(chars[i])
                            : Character.toLowerCase(chars[i]);

                options = new StringBuilder(String.valueOf(chars));
            }
        }

        if (configuration.is_numerals())
            options.append(_numerals);


        return options.toString();
    }

    @Getter
    public static class Configuration {

        private final boolean _numerals;

        public boolean _letters;

        public boolean _uppercase = true;

        public boolean _lowercase = true;

        public Configuration(boolean numerals, boolean letters) {
            _numerals = numerals;
            _letters = letters;
        }

        public Configuration(boolean numerals, boolean letters, boolean uppercase, boolean lowercase) {
            _numerals = numerals;
            _letters = letters;
            _uppercase = uppercase;
            _lowercase = lowercase;
        }
    }
}
