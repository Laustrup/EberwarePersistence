package eberware.api.core.systems.services;

public class PathService {

    public static String getRootPath() {
        return System.getProperty("user.dir");
    }
}
