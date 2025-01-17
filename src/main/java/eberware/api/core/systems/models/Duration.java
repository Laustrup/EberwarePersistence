package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@AllArgsConstructor
@ToString
public class Duration {

    public Instant _start;

    public Instant _end;

    public Duration(DTO duration, ZoneId zoneId) {
        _start = duration.getStart().atZone(zoneId).toInstant();
        _end = duration.getEnd().atZone(zoneId).toInstant();
    }

    @Getter @Setter
    public static class DTO {

        public LocalDateTime start;

        public LocalDateTime end;

        public DTO(Duration duration, ZoneId zoneId) {
            start = LocalDateTime.ofInstant(duration.get_start(), zoneId);
            end = LocalDateTime.ofInstant(duration.get_end(), zoneId);
        }
    }
}
