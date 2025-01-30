package eberware.api.core.systems.persistence.queries;

import eberware.api.core.systems.persistence.Query;

public class DatabaseLibraryQueries {

    public static Query createSchemaIfNotExists(String schema) {
        return new Query(
                String.format(
                        /*language=MySQL*/ "create database if not exists %s;",
                        schema
                )
        );
    }
}
