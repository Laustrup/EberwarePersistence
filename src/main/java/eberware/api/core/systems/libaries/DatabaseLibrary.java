package eberware.api.core.systems.libaries;

import eberware.api.core.systems.persistence.SQL;
import eberware.api.core.systems.services.ManagerService;
import eberware.api.core.systems.services.queries.DatabaseLibraryQueryService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseLibrary {

    @Getter
    private static String _driver;
    
    @Getter
    private static SQL _sql;

    @Getter
    private static String _target;

    @Getter
    private static int _port;

    @Getter
    private static String _schema;

    @Getter
    private static String _user;

    @Getter
    private static String _password;

    @Getter @Setter
    private static boolean _schemaExists;

    @Getter
    public static Properties _properties;

    private static boolean _isConfigured = false;

    public static void setup(
            SQL sql,
            String target,
            Integer port,
            String schema,
            String user,
            String password,
            String[] arguments
    ) {
        setup(
                sql,
                target,
                port,
                schema,
                user,
                password,
                convertToProperties(arguments)
        );
    }

    public static void setup(
            SQL sql,
            String target,
            Integer port,
            String schema,
            String user,
            String password,
            Properties properties
    ) {
        if (_isConfigured)
            throw new IllegalStateException("Database library is already configured");
        
        _sql = sql;
        _driver = _sql == null ? null : switch (_sql) {
            case MySQL -> "com.mysql.cj.jdbc.Driver";
            case H2 -> "org.h2.Driver";
        };
        _target = Objects.requireNonNullElse(target, "localhost");
        _port = Objects.requireNonNullElse(port, 3306);
        _schema = Objects.requireNonNullElse(schema, "eberware");
        _user = user;
        _password = password;
        _properties = properties;
        ManagerService.databaseInteraction(() -> DatabaseLibraryQueryService.createSchemaIfNotExists(_schema));
        _isConfigured = true;
    }

    private static Properties convertToProperties(String[] arguments) {
        return new DatabaseLibrary.Properties(
                Stream.of(arguments)
                        .filter(argument -> argument.equals(DatabaseLibrary.CommandOption.DISALLOW_MULTIPLE_QUERIES.get_title()))
                        .toList()
                        .isEmpty()
        );
    }

    public static String get_urlPath() {
        return (
                _isConfigured
                        ? get_rootURLPath(false) + (_schema == null ? "" : _schema) + get_URLPropertyParameters()
                        : null
        );
    }

    public static String get_rootURLPath(boolean includeProperties) {
        return String.format(
                "jdbc:%s://%s:%s/",
                _sql.name().toLowerCase(),
                _target,
                _port
        ) + (includeProperties
                ? get_URLPropertyParameters()
                : ""
        );
    }

    public static String get_URLPropertyParameters() {
        return _properties.get_options().stream()
                .filter(Properties.Option::is_isEnabled)
                .map(option -> option.get_command().get_parameter())
                .collect(Collectors.joining());
    }
    
    @Getter
    public enum CommandOption {
        SQL("sql"),
        DATABASE_TARGET("databaseTarget"),
        DATABASE_PORT("databasePort"),
        DATABASE_SCHEMA("databaseSchema"),
        DATABASE_USER("databaseUser"),
        DATABASE_PASSWORD("databasePassword"),
        DISALLOW_MULTIPLE_QUERIES("disAllowMultiQueries");

        private final String _title;

        CommandOption(String title) {
            _title = title;
        }
    }

    @Getter
    public static class Properties {

        private final List<Option> _options = new ArrayList<>(List.of(
                new Option(Option.Command.ALLOW_MULTIPLE_QUERIES)
        ));

        public Properties(
                boolean allowMultipleQueries
        ) {
            _options.forEach(option -> {
                if (allowMultipleQueries)
                    option.set_isEnabled(allowMultipleQueries);
            });
        }

        @Getter @AllArgsConstructor
        public static class Option {

            @Setter
            private boolean _isEnabled;

            private final Command _command;

            public Option(Command command) {
                _command = command;
            }

            @Getter @AllArgsConstructor
            public enum Command {
                ALLOW_MULTIPLE_QUERIES("?allowMultiQueries=true");

                private final String _parameter;
            }
        }
    }
}
