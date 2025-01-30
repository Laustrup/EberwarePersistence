package eberware.api.core.systems.persistence.queries;

import eberware.api.core.systems.persistence.Query;
import lombok.Getter;

import java.util.Arrays;

public class ScriptorianQueries {

    public static final Query createTableIfNotExists = new Query(/*language=MySQL*/ """
    create table if not exists scriptories(
        title varchar(64) not null,
        file_name varchar(128) not null,
        error_message longtext,
        content longtext not null,
        versionstamp datetime not null,
        successtamp datetime,
        timestamp datetime not null default now(),

        primary key (file_name)
    );
    """);

    public static final Query findScriptoriesWithoutSuccess = new Query(/*language=MySQL*/ """
    select * from scriptories where successtamp is null;
    """);

    public static final Query findAllScriptories = new Query(/*language=MySQL*/ """
    select * from scriptories;
    """);

    public static final Query insertIntoScriptories = new Query(
            String.format(/*language=MySQL*/ """
                    insert ignore into scriptories(
                        title,
                        file_name,
                        error_message,
                        content,
                        versionstamp,
                        successtamp
                    ) values %s;
                    """,
                    Query.valuesInsertCollection(Parameter.values().length)
            ),
            Arrays.stream(Parameter.values())
                    .map(parameter -> new Query.Parameter(parameter.get_key()))
                    .toList()
    );

    @Getter
    public enum Parameter {
        TITLE("scriptorian_title"),
        FILE_NAME("scriptorian_file_name"),
        ERROR_MESSAGE("scriptorian_error_message"),
        CONTENT("scriptorian_content"),
        VERSIONSTAMP("scriptorian_versionstamp"),
        SUCCESSTAMP("scriptorian_successtamp");

        private final String _key;

        Parameter(String key) {
            _key = Query.formatKey(key);
        }

        @Override
        public String toString() {
            return _key;
        }
    }
}
