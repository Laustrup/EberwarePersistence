package eberware.api.core.systems.models;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class History {

    private List<Story> _stories;

    public History() {
        _stories = new ArrayList<>();
    }

    public History(List<Story> stories) {
        _stories = stories;
    }

    public static class Story extends Model {

        private String _title;

        private List<String> _details;

        public Story(
                UUID id,
                String title,
                List<String> details,
                History history,
                Instant timestamp
        ) {
            super(id, history, timestamp);
            _title = title;
            _details = details;
        }

        public Story(
                String title,
                List<String> details
        ) {
            this(
                null,
                title,
                details,
                null,
                null
            );
        }
    }
}
