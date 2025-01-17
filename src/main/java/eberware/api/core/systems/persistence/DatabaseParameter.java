package eberware.api.core.systems.persistence;

import lombok.Getter;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DatabaseParameter {

    private final String _key;

    private final Object _value;

    private final List<Integer> _indexes;

    private final JDBCType _type;

    public DatabaseParameter(String key, Object value) {
        this(key, value, null);
    }

    public DatabaseParameter(String key, Object value, JDBCType type) {
        _key = key;
        _value = value;
        _type = type;
        _indexes = new ArrayList<>();
    }
}
