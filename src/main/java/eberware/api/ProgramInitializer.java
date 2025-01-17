package eberware.api;

import eberware.api.core.systems.libaries.DatabaseLibrary;
import eberware.api.core.systems.libaries.SecurityLibrary;
import eberware.api.core.systems.persistence.SQL;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgramInitializer {

    private static final Logger _logger = Logger.getLogger(ProgramInitializer.class.getSimpleName());

    private static String[] _arguments;

    public static boolean startup(String[] arguments) throws IllegalArgumentException {
        _arguments = arguments;

        try {
            SecurityLibrary.setup(
                    findArgument(SecurityLibrary.CommandOption.GIBBERISH.get_title(), true)
            );

            String port = findArgument(DatabaseLibrary.CommandOption.DATABASE_PORT.get_title(), false);

            DatabaseLibrary.setup(
                SQL.valueOf(findArgument(DatabaseLibrary.CommandOption.SQL.get_title(), true)),
                findArgument(DatabaseLibrary.CommandOption.DATABASE_TARGET.get_title(), false),
                port != null
                        ? Integer.valueOf(
                                Objects.requireNonNull(
                                        findArgument(DatabaseLibrary.CommandOption.DATABASE_PORT.get_title(),
                                        false)
                                )
                        ) : null,
                findArgument(DatabaseLibrary.CommandOption.DATABASE_SCHEMA.get_title(), false),
                findArgument(DatabaseLibrary.CommandOption.DATABASE_USER.get_title(), true),
                findArgument(DatabaseLibrary.CommandOption.DATABASE_PASSWORD.get_title(), false),
                arguments
            );

            return true;
        } catch (Exception e) {
            _logger.log(
                    Level.CONFIG,
                    "Had trouble initializing!",
                    e
            );

            return false;
        }
    }

    private static String findArgument(String command, boolean isNecessary) {
        try {
            String sentence = Arrays.stream(_arguments).toList().stream().filter(argument ->
                    argument.contains("=")
                            &&
                    argument.split("=")[0].equals(command)
            )
                    .findFirst()
                    .orElseGet(() -> {
                        if (isNecessary)
                            throw new IllegalArgumentException(String.format("""
                                    Couldn't find command %s from arguments when initializing program!
                                    """, command
                            ));
                        else
                            return null;
                    });

            return sentence == null ? null : sentence.split("=")[1];
        } catch (Exception e) {
            return null;
        }
    }
}
