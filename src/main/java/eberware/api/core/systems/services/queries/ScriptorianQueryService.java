package eberware.api.core.systems.services.queries;

import eberware.api.core.systems.persistence.Query;
import eberware.api.core.systems.persistence.DatabaseManager;
import eberware.api.core.systems.persistence.DatabaseParameter;
import eberware.api.core.systems.persistence.queries.ScriptorianQueries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ScriptorianQueryService {

    public static void createDefaultSchemaIfNotExists() {
        DatabaseManager.create(ScriptorianQueries.createTableIfNotExists);
    }

    public static ResultSet findScriptoriesWithoutSuccess() {
        return DatabaseManager.read(ScriptorianQueries.findScriptoriesWithoutSuccess);
    }

    public static ResultSet findAllScriptories() {
        return DatabaseManager.read(ScriptorianQueries.findAllScriptories);
    }

    public static void putScriptory(List<DatabaseParameter> parameters) {
        DatabaseManager.create(ScriptorianQueries.insertIntoScriptories, parameters);
    }

    public static void executeScript(String content) {
        try {
            DatabaseManager.execute(
                    new Query(content),
                    DatabaseManager.Action.CUD
            );
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
