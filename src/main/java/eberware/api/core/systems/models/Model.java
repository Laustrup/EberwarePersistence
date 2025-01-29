package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter @AllArgsConstructor
public abstract class Model {

    private UUID _id;

    private History _history;

    private Instant _timestamp;

    public Model(DTO model) {
        _id = model.getId();
        _timestamp = model.getTimestamp();
        _history = model.getHistory();
    }

    @Getter @Setter
    @FieldNameConstants
    @AllArgsConstructor
    public static class DTO {

        private UUID id;

        private Instant timestamp;

        private History history;

        public DTO(Model model) {
            id = model.get_id();
            timestamp = model.get_timestamp();
            history = model.get_history();
        }
    }

    public enum DatabaseColumn {
        id,
        timestamp
    }
}
