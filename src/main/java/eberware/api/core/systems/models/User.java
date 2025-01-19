package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
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

    public static final String databaseTable = "users";

    public enum DatabaseColumns {
        zone_id
    }

    @Getter @ToString(callSuper = true)
    public static class ContactInfo {

        private String _name;

        private String _email;

        private List<Address> _addresses;

        public ContactInfo(ContactInfo.DTO contactInfo) {
            _name = contactInfo.getName();
            _email = contactInfo.getEmail();
            _addresses = contactInfo.getAddresses().stream()
                    .map(Address::new)
                    .toList();
        }

        public ContactInfo(
                String name,
                String email,
                List<Address> addresses
        ) {
            _name = name;
            _email = email;
            _addresses = addresses;
        }

        @Getter @Setter
        public static class DTO {

            private String name;

            private String email;

            private List<User.ContactInfo.Address.DTO> addresses;

            public DTO(ContactInfo contactInfo) {
                name = contactInfo.get_name();
                email = contactInfo.get_email();
                addresses = contactInfo.get_addresses().stream()
                        .map(Address.DTO::new)
                        .toList();
            }
        }

        public static final String databaseTable = "contact_info";

        public enum DatabaseColumn {
            name,
            email,
            addresses
        }

        @Getter @AllArgsConstructor
        public static class Address {

            private UUID _id;

            private String _street;

            private String _number;

            private String _postalCode;

            private String _city;

            private String _country;

            public Address(ContactInfo.Address.DTO address) {
                _id = address.getId();
                _street = address.getStreet();
                _number = address.getNumber();
                _postalCode = address.getPostalCode();
                _city = address.getCity();
                _country = address.getCountry();
            }

            @Getter @Setter
            public static class DTO {

                private UUID id;

                private String street;

                private String number;

                private String postalCode;

                private String city;

                private String country;

                public DTO(User.ContactInfo.Address address) {
                    street = address.get_street();
                    number = address.get_number();
                    postalCode = address.get_postalCode();
                    city = address.get_city();
                    country = address.get_country();
                }
            }

            public enum DatabaseColumn {
                id,
                street,
                number,
                postal_code,
                city,
                country
            }
        }
    }
}
