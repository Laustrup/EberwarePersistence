package eberware.api.web.services;

import eberware.api.core.systems.managers.LoginManager;
import eberware.api.core.systems.models.Login;
import eberware.api.core.systems.models.Response;
import eberware.api.core.systems.models.User;
import org.springframework.http.ResponseEntity;

public class UserWebService {

    public static ResponseEntity<User> login(Login login) {
        Response<User> response = LoginManager.login(login);

        return new ResponseEntity<>(
                response.get_object(),
                response.get_httpStatus()
        );
    }
}
