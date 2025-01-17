package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class Login {

    private String _username;

    private String _password;

    private int _picking;

    public Login(DTO login) {
        this(
                login.getUsername(),
                login.getPassword(),
                login.getPicking()
        );
    }

    @Getter
    private static class DTO {

        private String username;

        private String password;

        private int picking;

        public DTO(Login login) {
            this.username = login.get_username();
            this.password = login.get_password();
            this.picking = login.get_picking();
        }
    }
}
