package eberware.api.core.systems.services;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
}
