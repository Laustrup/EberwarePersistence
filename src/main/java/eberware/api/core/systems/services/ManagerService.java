package eberware.api.core.systems.services;

import eberware.api.core.systems.persistence.DatabaseGate;

import java.util.function.Supplier;

public class ManagerService {

    public static void databaseInteraction(Runnable action) {
        action.run();
        DatabaseGate.closeConnection();
    }

    public static <T> T databaseInteraction(Supplier<T> action) {
        T actual = action.get();
        DatabaseGate.closeConnection();
        return actual;
    }
}
