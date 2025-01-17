package eberware.api.core.systems.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Getter @ToString(callSuper = true)
public class User extends Model {

    private ContactInfo _contactInfo;

    private ZoneId _zoneId;

    public User(DTO user) {
        super(user);
        _contactInfo = new ContactInfo(user.getContactInfo());
    }

    public User(
            UUID id,
            ContactInfo contactInfo,
            ZoneId zoneId,
            History history,
            Instant timestamp
    ) {
        super(id, history, timestamp);
        _contactInfo = contactInfo;
        _zoneId = zoneId;
    }

    public User(
        ContactInfo contactInfo,
        ZoneId zoneId
    ) {
        this(
                null,
                contactInfo,
                zoneId,
                new History(),
                null
        );
    }

    @Getter @Setter
    public static class DTO extends Model.DTO {

        private ContactInfo.DTO contactInfo;

        private ZoneId zoneId;

        public DTO(User user) {
            super(user);
            contactInfo = new ContactInfo.DTO(user.get_contactInfo());
            zoneId = user.get_zoneId();
        }
    }
}
