package eberware.api.core.systems.persistence.queries;

import eberware.api.core.systems.models.Query;
import lombok.Getter;

import java.util.List;

public class UserQueries {

    public static final String doesUserExistColumnName = "existance";

    public static final Query loginQuery = new Query(/*language=mysql*/ """
            select
                *
            from
                users
                    inner join contact_infos
                        on users.contact_info_id = contact_infos.id
            where
                contact_infos.email = %s
                    and
                users.password = %s
            """,
            List.of(
                    new Query.Parameter(Parameter.CONTACT_INFO_EMAIL.get_key()),
                    new Query.Parameter(Parameter.USER_PASSWORD.get_key())
            )
    );

    public static final Query doesUserExist = new Query(
            String.format(/*language=mysql*/ """
                    select
                        count(*) > 0 as %s
                    from
                        contact_infos
                    where
                        contact_infos.email = %s
                    """,
                    doesUserExistColumnName,
                    "%s"
            ),
            new Query.Parameter(Parameter.CONTACT_INFO_EMAIL.get_key())
    );

    public static final Query upsertContactInfoQuery = new Query(/*language=mysql*/ """
            insert into contact_infos(
                id,
                name,
                email
            ) values (
                (select id from users where id = %s),
                %s,
                %s
            ) on duplicate key update
                name = %s,
                email = %s
            ;
            
            """,
            List.of(
                    new Query.Parameter(Parameter.USER_ID.get_key()),
                    new Query.Parameter(Parameter.CONTACT_INFO_NAME.get_key()),
                    new Query.Parameter(Parameter.CONTACT_INFO_EMAIL.get_key()),
                    new Query.Parameter(Parameter.CONTACT_INFO_NAME.get_key()),
                    new Query.Parameter(Parameter.CONTACT_INFO_EMAIL.get_key())
            )
            );

    public static Query upsertAddressQuery(int index) {
        return new Query(/*language=mysql*/ """
            insert into addresses(
                id,
                contact_info_id,
                street,
                number,
                postal_code,
                city,
                country
            ) values (
                ifnull(%s, uuid()),
                %s,
                %s,
                %s,
                %s,
                %s,
                %s
            ) on duplicate key update
                street = %s,
                number = %s,
                postalNumber = %s,
                city = %s,
                country = %s
            ;
            
            """,
            List.of(
                    new Query.Parameter(index, Parameter.ADDRESS_ID.get_key()),
                    new Query.Parameter(index, Parameter.USER_ID.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_STREET.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_NUMBER.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_POSTAL_CODE.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_CITY.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_COUNTRY.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_STREET.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_NUMBER.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_POSTAL_CODE.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_CITY.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_COUNTRY.get_key())
            )
        );
    }

    public static final Query upsertUserQuery = new Query(/*language=mysql*/ """
            insert into users(
                id,
                password,
                zone_id
            ) values (
                %s,
                %s,
                %s
            );
            
            """,
            List.of(
                    new Query.Parameter(Parameter.USER_ID.get_key()),
                    new Query.Parameter(Parameter.USER_PASSWORD.get_key()),
                    new Query.Parameter(Parameter.ZONE_ID.get_key())
            )
    );

    @Getter
    public enum Parameter {
        CONTACT_INFO_EMAIL("contact_info_email"),
        CONTACT_INFO_NAME("contact_info_name"),
        USER_PASSWORD("user_password"),
        USER_ID("user_id"),
        ZONE_ID("zone_id"),
        ADDRESS_ID("address_id"),
        ADDRESS_STREET("address_street"),
        ADDRESS_NUMBER("address_number"),
        ADDRESS_POSTAL_CODE("address_postal_code"),
        ADDRESS_CITY("address_city"),
        ADDRESS_COUNTRY("address_country");

        private final String _key;

        Parameter(String key) {
            _key = Query.formatKey(key);
        }
    }
}
