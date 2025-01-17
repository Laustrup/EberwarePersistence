package eberware.api.core.systems.managers;

import eberware.api.core.systems.models.Login;
import eberware.api.core.systems.models.Response;
import eberware.api.core.systems.models.User;
import eberware.api.core.systems.persistence.DatabaseManager;
import eberware.api.core.systems.persistence.DatabaseParameter;
import eberware.api.core.systems.persistence.queries.UserQueries;
import eberware.api.core.systems.services.ManagerService;
import eberware.api.core.systems.services.PasswordService;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eberware.api.core.systems.persistence.queries.UserQueries.Parameter;

public class LoginManager {

    private static Logger _logger = Logger.getLogger(LoginManager.class.getSimpleName());

    public static Response<User> login(Login login) {
        return ManagerService.databaseInteraction(() -> {
                try {
                    String encodedPassword = PasswordService.encode(login.get_password());

                    User user = UserManager.build(DatabaseManager.read(
                            UserQueries.loginQuery,
                            generateParameters(
                                    login.get_username(),
                                    encodedPassword
                            )
                    ));

                    return new Response<>(
                            user,
                            user == null
                                    ? (
                                            DatabaseManager.read(
                                                    UserQueries.doesUserExist,
                                                    new DatabaseParameter(
                                                            Parameter.CONTACT_INFO_EMAIL.get_key(),
                                                            login.get_username()
                                                    )
                                            ).getBoolean("existance")
                                                    ? Response.Situation.WRONG_PASSWORD
                                                    : Response.Situation.WRONG_USERNAME
                                    ) : Response.Situation.LOGIN_ACCEPTED,
                            user == null
                                    ? HttpStatus.UNAUTHORIZED
                                    : HttpStatus.OK
                    );
                } catch (Exception e) {
                    _logger.log(
                            Level.INFO,
                            String.format("Issue when logging in with user \"%s\"", login.get_username()),
                            e
                    );
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private static List<DatabaseParameter> generateParameters(String username, String password) {
        return List.of(
                new DatabaseParameter(Parameter.CONTACT_INFO_EMAIL.get_key(), username),
                new DatabaseParameter(Parameter.USER_PASSWORD.get_key(), password)
        );
    }
}
