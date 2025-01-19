package eberware.api.web.services;

import eberware.api.core.systems.managers.UserManager;
import eberware.api.core.systems.models.Login;
import eberware.api.core.systems.models.Response;
import eberware.api.core.systems.models.User;
import org.springframework.http.ResponseEntity;

public class UserWebService {

    public static ResponseEntity<User> login(Login login) {
        Response<User> response = UserManager.login(login);

        return new ResponseEntity<>(
                response.get_object(),
                response.get_httpStatus()
        );
    }

    public static ResponseEntity<User> upsert(Login login) {
        Response<User> response = UserManager.upsert(login.get_user(), login.get_password());

        return new ResponseEntity<>(
                response.get_object(),
                response.get_httpStatus()
        );
    }
}
