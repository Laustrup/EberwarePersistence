package eberware.api.core.systems.services.builders;

import eberware.api.core.systems.models.History;
import eberware.api.core.systems.models.Model;
import eberware.api.core.systems.persistence.DatabaseTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static eberware.api.core.systems.services.JDBCService.*;

public class HistoryBuilder {

    public static History.Story buildStory(ResultSet resultSet) throws SQLException {
        return new History.Story(
                get(
                        column -> getUUID(resultSet, column),
                        DatabaseTable.STORY.get_title(),
                        Model.DatabaseColumn.id.name()
                ),
                get(
                        column -> getString(resultSet, column),
                        DatabaseTable.STORY.get_title(),
                        History.Story.DatabaseColumn.title.name()
                ),
                getCollection(
                        resultSet,
                        set -> get(
                                column -> getString(resultSet, column),
                                DatabaseTable.STORY_DETAIL.get_title(),
                                History.Story.DatabaseColumn.content.name()
                        )
                ),
                get(
                        column -> getTimestamp(resultSet, column, Timestamp::toInstant)
                )
        );
    }

    public static Stream<History.Story> getStoriesOfOwner(Map<UUID, History.Story> collection, UUID ownerId) {
        return collection.values().stream()
                .filter(story -> story.get_ownerId().equals(ownerId));
    }
}
