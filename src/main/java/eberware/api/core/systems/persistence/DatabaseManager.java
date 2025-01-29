package eberware.api.core.systems.persistence;

import eberware.api.ProgramInitializer;
import eberware.api.core.systems.libaries.DatabaseLibrary;
import eberware.api.core.systems.models.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static final Logger _logger = Logger.getLogger(ProgramInitializer.class.getSimpleName());

    public static ResultSet read(Query query) {
        return handle(query, Action.READ);
    }

    public static ResultSet read(Query query, DatabaseParameter databaseParameter) {
        return read(query, List.of(databaseParameter));
    }

    public static ResultSet read(Query query, List<DatabaseParameter> databaseParameters) {
        return handle(query, Action.READ, databaseParameters);
    }

    public static void upsert(Query query) {
        updateAndRead(query);
    }

    public static void upsert(Query query, List<DatabaseParameter> parameters) {
        updateAndRead(query, parameters);
    }

    public static void create(Query query) {
        handle(query, Action.CREATE);
    }

    public static void create(Query query, List<DatabaseParameter> parameters) {
        handle(query, Action.CREATE, parameters);
    }

    public static ResultSet createAndRead(Query query) {
        return handle(query, Action.CREATE);
    }

    public static ResultSet updateAndRead(Query query) {
        return handle(query, Action.UPDATE);
    }

    public static ResultSet updateAndRead(Query query, List<DatabaseParameter> parameters) {
        return handle(query, Action.UPDATE, parameters);
    }

    private static ResultSet handle(Query query, Action action) {
        try {
            return execute(query, action);
        } catch (SQLException e) {
            return null;
        }
    }

    private static ResultSet handle(
            Query query,
            Action action,
            List<DatabaseParameter> parameters
    ) {
        try {
            return execute(query, action, parameters);
        } catch (SQLException e) {
            return null;
        }
    }

    public static ResultSet execute(Query query, Action action) throws SQLException {
        return execute(query, action, Objects.requireNonNull(DatabaseLibrary.get_urlPath()));
    }

    public static ResultSet execute(Query query, Action action, String url) throws SQLException {
        return execute(query, action, new ArrayList<>(), url);
    }

    public static ResultSet execute(Query query, Action action, List<DatabaseParameter> parameters) throws SQLException {
        return execute(query, action, parameters, Objects.requireNonNull(DatabaseLibrary.get_urlPath()));
    }

    public static ResultSet execute(Query query, Action action, DatabaseParameter parameter) throws SQLException {
        return execute(query, action, parameter, Objects.requireNonNull(DatabaseLibrary.get_urlPath()));
    }

    public static ResultSet execute(
            Query query,
            Action action,
            DatabaseParameter parameter,
            String url
    ) throws SQLException {
        return execute(query, action, List.of(parameter), url);
    }

    public static ResultSet execute(
            Query query,
            Action action,
            List<DatabaseParameter> parameters,
            String url
    ) throws SQLException {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = prepareStatement(
                    query,
                    parameters,
                    url
            );

            return switch (action) {
                case Action.READ -> preparedStatement.executeQuery();
                default -> {
                    preparedStatement.executeUpdate();
                    yield null;
                }
            };
        } catch (Exception exception) {
            _logger.log(
                    Level.WARNING,
                    String.format(
                            """
                            Couldn't execute query:
                            %s
                            
                            With action: %s
                            """,
                            preparedStatement == null
                                    ? query.get_script()
                                    : preparedStatement.toString(),
                            action
                    ),
                    exception
            );

            throw exception;
        }
    }

    private static PreparedStatement prepareStatement(
            Query query,
            List<DatabaseParameter> parameters,
            String url
    ) {
        PreparedStatement preparedStatement;

        try {
            int keyIndex = 0,
                parameterIndex = 1;
            Map<String, DatabaseParameter> parametersByKey = parameters.stream()
                    .collect(Collectors.toMap(DatabaseParameter::get_key, parameter -> parameter));

            for (char character : query.get_script().toCharArray()) {
                if (character == Query.get_identifier()) {
                    String key = query.get_script().substring(
                            keyIndex,
                            query.get_script().indexOf(Query.get_endExpression(), keyIndex)
                    ) + Query.get_endExpression();
                    DatabaseParameter parameter = parametersByKey.get(key);

                    if (parameter == null) {
                        String message = "Unknown parameter: " + key;
                        _logger.log(Level.WARNING, message);
                        throw new IllegalArgumentException(message);
                    }

                    parameter.get_indexes().add(parameterIndex);
                    parameterIndex++;
                }

                keyIndex++;
            }

            Map<Integer, DatabaseParameter> databaseParametersByIndex = new HashMap<>();
            for (DatabaseParameter parameter : parametersByKey.values()) {
                query.set_script(query.get_script().replace(parameter.get_key(), "?"));
                for (Integer databaseParameterIndex : parameter.get_indexes())
                    databaseParametersByIndex.put(databaseParameterIndex, parameter);
            }

            long inputCount = query.get_script().chars().filter(character -> character == '?').count();
            if (databaseParametersByIndex.size() != inputCount) {
                throw new IllegalArgumentException(String.format("""
                        Issue when preparing query:
                        %n%s
                        
                        Amount of inputs are %s and parameters to input are %s.
                        """,
                        query.get_script(),
                        inputCount,
                        databaseParametersByIndex.size()
                ));
            }

            preparedStatement = Objects.requireNonNull(DatabaseGate.getConnection(url)).prepareStatement(query.get_script());

            for (Integer key : databaseParametersByIndex.keySet()) {
                DatabaseParameter parameter = databaseParametersByIndex.get(key);

                if (parameter.get_type() != null)
                    preparedStatement.setObject(key, parameter.get_value(), parameter.get_type());
                else
                    preparedStatement.setObject(key, parameter.get_value());
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return preparedStatement;
    }

    public enum Action {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        CUD,
        ROOT_PATH
    }
}
