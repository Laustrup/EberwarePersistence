package eberware.api.core.systems.services.queries;

import eberware.api.core.systems.models.Query;
import eberware.api.core.systems.models.User;
import eberware.api.core.systems.persistence.DatabaseManager;
import eberware.api.core.systems.persistence.DatabaseParameter;
import eberware.api.core.systems.persistence.queries.UserQueries;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static eberware.api.core.systems.persistence.queries.UserQueries.Parameter;
import static eberware.api.core.systems.models.Query.formatIndexedKey;
import static eberware.api.core.systems.services.JDBCService.*;

public class UserQueryService {

    public static ResultSet login(String email, String password) {
        return DatabaseManager.read(
                UserQueries.loginQuery,
                List.of(
                        new DatabaseParameter(
                                Parameter.CONTACT_INFO_EMAIL.get_key(),
                                email
                        ),
                        new DatabaseParameter(
                                Parameter.USER_PASSWORD.get_key(),
                                password
                        )
                )
        );
    }

    public static ResultSet upsert(User user) {
        List<Query> queries = new ArrayList<>();

        queries.add(UserQueries.upsertUserQuery);
        queries.add(UserQueries.upsertContactInfoQuery);
        queries.addAll(prepareQueries(user.get_contactInfo().get_addresses(), UserQueries::upsertAddressQuery));
        queries.addAll(prepareQueries(user.get_authorities(), UserQueries::insertIgnoreAuthorityQuery));

        user.gibberisePassword();

        DatabaseManager.upsert(
                new Query(
                        queries.stream()
                                .map(Query::get_script)
                                .reduce(String::concat)
                                .orElseThrow()
                ),
                generateUpsertParameters(user)
        );

        return DatabaseManager.read(
                UserQueries.loginQuery,
                new ArrayList<>(List.of(
                        new DatabaseParameter(Parameter.CONTACT_INFO_EMAIL.get_key(), user.get_contactInfo().get_email()),
                        new DatabaseParameter(Parameter.USER_PASSWORD.get_key(), user.get_password())
                ))
        );
    }

    private static List<DatabaseParameter> generateUpsertParameters(User user) {
        List<DatabaseParameter> parameters = new ArrayList<>(List.of(
                new DatabaseParameter(Parameter.USER_ID.get_key(), user.get_id()),
                new DatabaseParameter(Parameter.CONTACT_INFO_NAME.get_key(), user.get_contactInfo().get_name()),
                new DatabaseParameter(Parameter.CONTACT_INFO_EMAIL.get_key(), user.get_contactInfo().get_email()),
                new DatabaseParameter(Parameter.USER_PASSWORD.get_key(), user.get_password()),
                new DatabaseParameter(Parameter.ZONE_ID.get_key(), user.get_zoneId().getId())
        ));

        for (int i = 0; i < user.get_contactInfo().get_addresses().size(); i++) {
            User.ContactInfo.Address address = user.get_contactInfo().get_addresses().get(i);
            parameters.addAll(List.of(
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_ID.get_key(), i), address.get_id()),
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_STREET.get_key(), i), address.get_street()),
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_NUMBER.get_key(), i), address.get_number()),
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_FLOOR.get_key(), i), address.get_floor()),
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_POSTAL_CODE.get_key(), i), address.get_postalCode()),
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_CITY.get_key(), i), address.get_city()),
                    new DatabaseParameter(formatIndexedKey(Parameter.ADDRESS_COUNTRY.get_key(), i), address.get_country())
            ));
        }

        List<User.Authority> authorities = user.get_authorities() == null
                ? new ArrayList<>()
                : user.get_authorities().stream().toList();

        for (int i = 0; i < (user.get_authorities() == null ? i : user.get_authorities().size()); i++)
            parameters.add(
                    new DatabaseParameter(
                            formatIndexedKey(Parameter.AUTHORITY.get_key(), i),
                            authorities.get(i).name()
                    )
            );

        return parameters;
    }

    public static ResultSet getAllLogins() {
        return DatabaseManager.read(UserQueries.getAllUsers);
    }
}
