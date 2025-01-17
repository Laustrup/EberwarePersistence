package eberware.api.web.controllers;

import eberware.api.core.systems.models.Login;
import eberware.api.core.systems.models.User;
import eberware.api.web.services.UserWebService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("user")
public class UserController {

    @GetMapping("/login")
    public ResponseEntity<User> login(
            @RequestBody Login login
    ) {
        return UserWebService.login(login);
    }
}
