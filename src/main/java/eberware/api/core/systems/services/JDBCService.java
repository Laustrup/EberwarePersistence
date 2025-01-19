package eberware.api.core.systems.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class JDBCService {

    public static <T> List<T> build(ResultSet resultSet, Supplier<T> supplier) {
        List<T> ts = new ArrayList<>();
        try {
            while (resultSet.next()) {
                ts.add(supplier.get());
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        return ts;
    }

    public static <T> T get(Timestamp timestamp, Function<Timestamp, T> function) {
        return timestamp == null ? null : function.apply(timestamp);
    }

    public static <T> T get(
            Function<String, T> function,
            String... columnTitle
    ) {
        for (int i = 0; i < columnTitle.length; i++)
            columnTitle[i] = specifyColumn(Arrays.stream(columnTitle).filter(Objects::nonNull).findFirst().get());

        return ifColumnExists(function, columnTitle);
    }

    public static String specifyColumn(String... columnTitle) {
        return String.join(".", columnTitle);
    }

    public static <T> List<T> getCollection(
            ResultSet resultSet,
            Function<ResultSet, T> function
    ) throws SQLException {
        return getCollection(resultSet, "id", function);
    }

    public static <T> List<T> getCollection(
            ResultSet resultSet,
            String idColumn,
            Function<ResultSet, T> function
    ) throws SQLException {
        List<T> ts = new ArrayList<>();
        UUID id = resultSet.getObject(idColumn, UUID.class);

        while (resultSet.next()) {
            if (resultSet.getObject(idColumn, UUID.class) != id)
                return ts;

            ts.add(function.apply(resultSet));
        }

        return ts;
    }

    public static <T> T ifColumnExists(
            Function<String, T> function,
            String... columns
    ) {
        for (String column : columns) {
            try {
                T t = function.apply(column);
                if (t != null)
                    return t;
            } catch (Exception ignored) {}
        }

        return null;
    }

    public static String getString(ResultSet resultSet, String column) {
        try {
            return resultSet.getString(column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static UUID getUUID(ResultSet resultSet, String column) {
        try {
            return resultSet.getObject(column, UUID.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getTimestamp(ResultSet resultSet, String column, Function<Timestamp, T> function) {
        try {
            return get(resultSet.getTimestamp(column), function);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
