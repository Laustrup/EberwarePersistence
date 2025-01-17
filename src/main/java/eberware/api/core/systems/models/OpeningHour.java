package eberware.api.core.systems.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.util.List;

@Getter @ToString
public class OpeningHour {

    private List<DayOfWeek> _days;

    private Duration _duration;

    @Setter
    private boolean _includingHolidays;

    public OpeningHour(DTO openingHour) {
        _days = openingHour.getDays();
        _duration = openingHour.getDuration();
        _includingHolidays = openingHour.isIncludingHolidays();
    }

    public OpeningHour(
            List<DayOfWeek> days,
            Duration duration,
            boolean includingHolidays
    ) {
        _days = days;
        _duration = duration;
        _includingHolidays = includingHolidays;
    }

    @Getter @Setter
    public static class DTO {

        private List<DayOfWeek> days;

        private Duration duration;

        private boolean includingHolidays;

        public DTO(OpeningHour openingHour) {
            days = openingHour.get_days();
            duration = openingHour.get_duration();
            includingHolidays = openingHour.is_includingHolidays();
        }
    }
}
