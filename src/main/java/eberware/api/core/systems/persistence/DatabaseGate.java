package eberware.api.core.systems.persistence;

import eberware.api.core.systems.libaries.DatabaseLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseGate {

    private static Connection _connection;

    private static Logger _logger = Logger.getLogger(DatabaseGate.class.getSimpleName());

    public static Connection getConnection(String url) {
        try {
            return _connection == null || _connection.isClosed()
                    ? createConnection(url)
                    : _connection;
        } catch (Exception e) {
            _logger.log(
                    Level.WARNING,
                    "Couldn't get connection to database!",
                    e
            );
            return null;
        }
    }

    private static Connection createConnection(String url) throws SQLException {
        _connection = DriverManager.getConnection(
                url,
                DatabaseLibrary.get_user(),
                DatabaseLibrary.get_password()
        );

        return _connection;
    }

    public static void closeConnection() {
        if (_connection != null) {
            try {
                _connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Boolean connectionIsClosed() {
        try {
            return _connection == null
                    ? null
                    : _connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
