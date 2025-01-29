package eberware.api.core.systems.models;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class History {

    private List<Story> _stories;

    public History(DTO history) {
        _stories = history.getStories();
    }

    public History() {
        _stories = new ArrayList<>();
    }

    public History(List<Story> stories) {
        if (stories == null)
            stories = new ArrayList<>();

        List<UUID> ownerIds = stories.stream()
                .map(Story::get_ownerId)
                .toList();

        if (!ownerIds.isEmpty() && ownerIds.stream().allMatch(id -> Collections.frequency(ownerIds, id) == ownerIds.size()))
            throw new IllegalArgumentException("Owner IDs not unique for history with stories: " + stories);

        _stories = stories;
    }

    public UUID get_ownerId() {
        return _stories.getFirst().get_ownerId();
    }

    @Getter
    public static class DTO {

        private List<Story> stories;

        public DTO(History history) {
            stories = history.get_stories();
        }
    }

    @Getter
    public static class Story {

        private UUID _id;

        private UUID _ownerId;

        private String _title;

        private List<String> _details;

        private Instant _timestamp;

        public Story(DTO story) {
            _id = story.getId();
            _ownerId = story.getOwnerId();
            _title = story.getTitle();
            _details = story.getDetails();
            _timestamp = story.getTimestamp();
        }

        public Story(
                UUID id,
                String title,
                List<String> details,
                Instant timestamp
        ) {
            _id = id;
            _title = title;
            _details = details;
            _timestamp = timestamp;
        }

        public Story(
                String title,
                List<String> details
        ) {
            this(
                null,
                title,
                details,
                null
            );
        }

        @Getter
        public static class DTO {

            private UUID id;

            private UUID ownerId;

            private String title;

            private List<String> details;

            private Instant timestamp;

            public DTO(Story story) {
                id = story.get_id();
                ownerId = story.get_ownerId();
                title = story.get_title();
                details = story.get_details();
                timestamp = story.get_timestamp();
            }
        }

        public enum DatabaseColumn {
            title,
            content
        }
    }
}
