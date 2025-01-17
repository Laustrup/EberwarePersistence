package eberware.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        if (ProgramInitializer.startup(args))
            SpringApplication.run(ApiApplication.class, args);
        else
            System.exit(-1);
    }
}
