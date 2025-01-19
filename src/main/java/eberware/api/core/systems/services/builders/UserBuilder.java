package eberware.api.core.systems.services.builders;

import eberware.api.ProgramInitializer;
import eberware.api.core.systems.models.History;
import eberware.api.core.systems.models.Model;
import eberware.api.core.systems.models.User;
import eberware.api.core.systems.persistence.DatabaseTable;
import eberware.api.core.systems.services.JDBCService;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eberware.api.core.systems.services.JDBCService.*;

public class UserBuilder {

    public static Logger _logger = Logger.getLogger(ProgramInitializer.class.getSimpleName());

    public static User build(ResultSet resultSet) {
        UUID userId = null;
        Map<UUID, User.ContactInfo.Address> addresses = new HashMap<>();
        Map<UUID, History.Story> stories = new HashMap<>();

        try {
            while (resultSet.next()) {
                if (userId != null &&
                        !get(
                                column -> getUUID(resultSet, column),
                                DatabaseTable.USER.get_title(),
                                Model.DatabaseColumn.id.name()
                        ).equals(userId)
                )
                    break;
                userId = get(
                        column -> getUUID(resultSet, column),
                        DatabaseTable.USER.get_title(),
                        Model.DatabaseColumn.id.name()
                );
                stories.putIfAbsent(
                        get(
                                column -> getUUID(resultSet, column),
                                DatabaseTable.STORY.get_title(),
                                Model.DatabaseColumn.id.name()
                        ), HistoryBuilder.buildStory(resultSet)
                );
                addresses.putIfAbsent(
                        get(
                                column -> getUUID(resultSet, column),
                                DatabaseTable.ADDRESS.get_title(),
                                User.ContactInfo.Address.DatabaseColumn.id.name()
                        ), buildAddress(resultSet)
                );
            }

            return new User(
                    userId,
                    new User.ContactInfo(
                            JDBCService.specifyColumn(
                                    DatabaseTable.CONTACT_INFO.get_title(),
                                    resultSet.getString(User.ContactInfo.DatabaseColumn.name.name())
                            ),
                            resultSet.getString(User.ContactInfo.DatabaseColumn.email.name()),
                            addresses.values().stream().toList()
                    ),
                    resultSet.getObject(User.DatabaseColumns.zone_id.name(), ZoneId.class),
                    new History(HistoryBuilder.getStoriesOfOwner(stories, userId).toList()),
                    get(
                            resultSet.getTimestamp(Model.DatabaseColumn.id.name()),
                            Timestamp::toInstant
                    )
            );
        } catch (Exception e) {
            _logger.log(
                    Level.WARNING,
                    String.format(
                            "Couldn't build user with id \"%s\"",
                            userId != null
                                    ? userId.toString()
                                    : "UNKNOWN"
                    ),
                    e
            );

            return null;
        }
    }

    public static User.ContactInfo.Address buildAddress(ResultSet resultSet) {
        return new User.ContactInfo.Address(
                get(
                        column -> getUUID(resultSet, column),
                        DatabaseTable.ADDRESS.get_title(),
                        User.ContactInfo.Address.DatabaseColumn.id.name()
                ),
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
                )
        );
    }
}
