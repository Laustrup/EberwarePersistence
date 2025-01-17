package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
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

    @Getter @Setter @AllArgsConstructor
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
}
