package eberware.api.core.systems.booking.models;

import eberware.api.core.systems.models.ContactInfo;
import eberware.api.core.systems.models.Duration;
import eberware.api.core.systems.models.History;
import eberware.api.core.systems.models.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString(callSuper = true)
public class Booking extends Model {

    private UUID _ownerId;

    @Setter
    private LocalDateTime _booked;

    private Duration _duration;

    private ContactInfo _contactInfo;

    public Booking(DTO booking) {
        super(booking);
        _ownerId = booking.getOwnerId();
        _booked = booking.getBooked();
        _duration = booking.getDuration();
        _contactInfo = booking.getContactInfo();
    }

    public Booking(
            UUID id,
            UUID ownerId,
            LocalDateTime booked,
            Duration duration,
            ContactInfo contactInfo,
            History history,
            Instant timestamp
    ) {
        super(id, history, timestamp);
        _ownerId = ownerId;
        _booked = booked;
        _duration = duration;
        _contactInfo = contactInfo;
    }

    public Booking(
            UUID ownerId,
            Duration duration
    ) {
        this(
                null,
                ownerId,
                null,
                duration,
                null,
                null,
                null
        );
    }

    public boolean isBooked() {
        return _booked != null;
    }

    @Getter @Setter
    public static class DTO extends Model.DTO{

        private UUID ownerId;

        @Setter
        private LocalDateTime booked;

        private Duration duration;

        private ContactInfo contactInfo;

        public DTO(Booking booking) {
            super(booking);
            ownerId = booking.get_ownerId();
            booked = booking.get_booked();
            duration = booking.get_duration();
            contactInfo = booking.get_contactInfo();
        }
    }
}
