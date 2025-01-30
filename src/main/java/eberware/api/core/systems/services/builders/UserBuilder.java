package eberware.api.core.systems.services.builders;

import eberware.api.ProgramInitializer;
import eberware.api.core.systems.models.*;
import eberware.api.core.systems.persistence.DatabaseTable;
import eberware.api.core.systems.services.JDBCService;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static eberware.api.core.systems.services.JDBCService.*;

public class UserBuilder {

    public static Logger _logger = Logger.getLogger(ProgramInitializer.class.getSimpleName());

    public static Stream<Login> buildLogins(ResultSet resultSet) {
        List<Login> logins = new ArrayList<>();

        JDBCService.build(
                resultSet,
                () -> logins.add(new Login(
                        get(
                                column -> getString(resultSet, column),
                                DatabaseTable.USER.get_title(),
                                User.DatabaseColumns.password.name()
                        ),
                        null,
                        new User.DTO(build(resultSet))
                ))
        );

        return logins.stream();
    }

    public static User build(ResultSet resultSet) {
        AtomicReference<UUID> userId = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        AtomicReference<User.ContactInfo> contactInfo = new AtomicReference<>();
        AtomicReference<Instant> timestamp = new AtomicReference<>();
        AtomicReference<ZoneId> zoneId = new AtomicReference<>();
        Set<User.Authority> authorities = new HashSet<>();
        Map<UUID, User.ContactInfo.Address> addresses = new HashMap<>();
        Map<UUID, History.Story> stories = new HashMap<>();

        try {
            JDBCService.build(
                    resultSet,
                    () -> {
                        zoneId.set(ZoneId.of(get(
                                column -> getString(resultSet, column),
                                User.DatabaseColumns.zone_id.name()
                        )));
                        userId.set(get(
                                column -> getUUID(resultSet, column),
                                Model.DatabaseColumn.id.name()
                        ));
                        password.set(get(
                                column -> getString(resultSet, column),
                                User.DatabaseColumns.password.name()
                        ));
                        authorities.add(User.Authority.valueOf(get(
                                column -> getString(resultSet, column),
                                User.DatabaseColumns.authority.name()
                        )));

                        putIfAbsent(
                                stories,
                                get(
                                        column -> getUUID(resultSet, column),
                                        History.Story.DatabaseColumn.story_id.name()
                                ), HistoryBuilder.buildStory(resultSet)
                        );

                        putIfAbsent(
                                addresses,
                                get(
                                        column -> getUUID(resultSet, column),
                                        User.ContactInfo.Address.DatabaseColumn.id.name()
                                ),
                                buildAddress(resultSet, stories)
                        );

                        contactInfo.set(new User.ContactInfo(
                                get(
                                        column -> getString(resultSet, column),
                                        User.ContactInfo.DatabaseColumn.name.name()
                                ),
                                get(
                                        column -> getString(resultSet, column),
                                        User.ContactInfo.DatabaseColumn.email.name()
                                ),
                                addresses.values().stream().toList()
                        ));
                        timestamp.set(get(
                                column -> getTimestamp(resultSet, column, Timestamp::toInstant),
                                Model.DatabaseColumn.timestamp.name()
                        ));
                    },
                    primary -> !get(
                            column -> getUUID(resultSet, column),
                            Model.DatabaseColumn.id.name()
                    ).equals(userId.get()),
                    userId.get()
            );

            return new User(
                    userId.get(),
                    password.get(),
                    contactInfo.get(),
                    zoneId.get(),
                    authorities,
//                    new History(new ArrayList<>(HistoryBuilder.getStoriesOfOwner(stories, userId.get()).toList())),
                    null,
                    timestamp.get()
            );
        } catch (Exception e) {
            _logger.log(
                    Level.WARNING,
                    String.format(
                            "Couldn't build user with id \"%s\"",
                            userId.get() != null
                                    ? userId.toString()
                                    : "UNKNOWN"
                    ),
                    e
            );

            return null;
        }
    }

    public static User.ContactInfo.Address buildAddress(ResultSet resultSet, Map<UUID, History.Story> stories) {
        UUID id = get(
                column -> getUUID(resultSet, column),
                DatabaseTable.ADDRESS.get_title(),
                User.ContactInfo.Address.DatabaseColumn.id.name()
        );

        return new User.ContactInfo.Address(
                id,
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.street.name()
                ),
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.number.name()
                ),
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.floor.name()
                ),
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.postal_code.name()
                ),
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.city.name()
                ),
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.country.name()
                ),
                new History(new ArrayList<>(HistoryBuilder.getStoriesOfOwner(stories, id).toList())),
                get(

                        column -> getTimestamp(resultSet, column, Timestamp::toInstant),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.timestamp.name()
                )
        );
    }
}
