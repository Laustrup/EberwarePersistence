package eberware.api.web.controllers;

import eberware.api.core.systems.managers.UserManager;
import eberware.api.core.systems.models.Login;
import eberware.api.core.systems.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static eberware.api.core.systems.services.WebService.respond;

@RestController
@RequestMapping("user/")
public class UserController {

    @GetMapping("login")
    public ResponseEntity<Login> login(
            @RequestBody Login login
    ) {
        return respond(() -> UserManager.login(login));
    }

    @PostMapping("create")
    public ResponseEntity<User.DTO> create(
            @RequestBody Login login
    ) {
        return login.getUser().getId() == null
                ? respond(() -> UserManager.upsert(login))
                : null;
    }

    @PostMapping("upsert")
    public ResponseEntity<User.DTO> upsert(
            @AuthenticationPrincipal Login login
    ) {
        return respond(() -> UserManager.upsert(new User(login.getUser())));
    }
}
