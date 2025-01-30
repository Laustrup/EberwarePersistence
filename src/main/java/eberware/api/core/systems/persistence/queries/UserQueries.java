package eberware.api.core.systems.persistence.queries;

import eberware.api.core.systems.persistence.Query;
import lombok.Getter;

import java.util.List;

public class UserQueries {

    public static final Query getAllUsers = new Query(/*language=mysql*/ """
            select
                *
            from
                users
                    inner join contact_infos
                               on contact_infos.id = users.id
                    left join addresses
                               on addresses.contact_info_id = contact_infos.id
                    left join stories
                               on users.id = stories.owner_id || contact_infos.id = stories.owner_id
                    left join story_details
                               on stories.id = story_details.story_id
            
            """
    );

    public static final Query getByEmail = new Query(getAllUsers.get_script() + /*language=mysql*/ """
            where contact_infos.email = %s
            """,
            new Query.Parameter(Parameter.CONTACT_INFO_EMAIL.get_key())
    );

    public static final Query getById = new Query(getAllUsers.get_script() + /*language=mysql*/ """
            where users.id = %s
            """,
            new Query.Parameter(Parameter.USER_ID.get_key())
    );

    public static final Query loginQuery = new Query(getAllUsers.get_script() + /*language=mysql*/ """
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

    public static final Query upsertContactInfoQuery = new Query(/*language=mysql*/ """
            insert into contact_infos(
                id,
                name,
                email
            ) values (
                @user_id,
                %s,
                %s
            ) on duplicate key update
                name = %s,
                email = %s
            ;
            
            """,
            List.of(
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
                floor,
                postal_code,
                city,
                country
            ) values (
                ifnull(%s, uuid_to_bin(uuid())),
                @user_id,
                %s,
                %s,
                %s,
                %s,
                %s,
                %s
            ) on duplicate key update
                street = %s,
                number = %s,
                floor = %s,
                postal_code = %s,
                city = %s,
                country = %s
            ;
            
            """,
            List.of(
                    new Query.Parameter(index, Parameter.ADDRESS_ID.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_STREET.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_NUMBER.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_FLOOR.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_POSTAL_CODE.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_CITY.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_COUNTRY.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_STREET.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_NUMBER.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_FLOOR.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_POSTAL_CODE.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_CITY.get_key()),
                    new Query.Parameter(index, Parameter.ADDRESS_COUNTRY.get_key())
            )
        );
    }

    public static final Query upsertUserQuery = new Query(/*language=mysql*/ """
            set @user_id = ifnull(%s, uuid_to_bin(uuid()));
            
            insert into users(
                id,
                password,
                zone_id
            ) values (
                @user_id,
                %s,
                %s
            ) on duplicate key update
                password = %s,
                zone_id = %s
            ;
            """,
            List.of(
                    new Query.Parameter(Parameter.USER_ID.get_key()),
                    new Query.Parameter(Parameter.USER_PASSWORD.get_key()),
                    new Query.Parameter(Parameter.ZONE_ID.get_key()),
                    new Query.Parameter(Parameter.USER_PASSWORD.get_key()),
                    new Query.Parameter(Parameter.ZONE_ID.get_key())
            )
    );

    public static Query insertIgnoreAuthorityQuery(int index) {
        return new Query(/*language=mysql*/ """
                insert ignore into user_authorities(
                    user_id,
                    authority
                ) values (
                    %s,
                    %s
                );
                """,
                List.of(
                        new Query.Parameter(index, Parameter.USER_ID.get_key()),
                        new Query.Parameter(index, Parameter.AUTHORITY.get_key())
                )
        );
    }

    @Getter
    public enum Parameter {
        CONTACT_INFO_EMAIL("contact_info_email"),
        CONTACT_INFO_NAME("contact_info_name"),
        USER_PASSWORD("user_password"),
        USER_ID("user_id"),
        ZONE_ID("zone_id"),
        AUTHORITY("authority"),
        ADDRESS_ID("address_id"),
        ADDRESS_STREET("address_street"),
        ADDRESS_NUMBER("address_number"),
        ADDRESS_FLOOR("address_floor"),
        ADDRESS_POSTAL_CODE("address_postal_code"),
        ADDRESS_CITY("address_city"),
        ADDRESS_COUNTRY("address_country");

        private final String _key;

        Parameter(String key) {
            _key = Query.formatKey(key);
        }
    }
}
