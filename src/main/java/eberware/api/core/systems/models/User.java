package eberware.api.core.systems.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eberware.api.core.systems.services.PasswordService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @ToString(callSuper = true)
public class User extends Model {

    private String _password;

    private ContactInfo _contactInfo;

    private ZoneId _zoneId;

    private Set<Authority> _authorities;

    private boolean _hasGibberish;

    public User(DTO user) {
        this(
                user.getId(),
                null,
                new ContactInfo(user.getContactInfo()),
                user.getZoneId(),
                user.getAuthorities(),
                user.getHistory(),
                user.getTimestamp()
        );
    }

    public User(
        String password,
        ContactInfo contactInfo,
        ZoneId zoneId,
        Set<Authority> authorities
    ) {
        this(
                null,
                password,
                contactInfo,
                zoneId,
                authorities,
                new History(),
                null
        );
    }

    public String get_username() {
        return _contactInfo.get_email();
    }

    public void gibberisePassword() {
        if (!_hasGibberish)
            _password = PasswordService.encode(_password).get_combined();

        _hasGibberish = true;
    }

    public User(
            UUID id,
            String password,
            ContactInfo contactInfo,
            ZoneId zoneId,
            Set<Authority> authorities,
            History history,
            Instant timestamp
    ) {
        super(id, history, timestamp);
        _password = password;
        _contactInfo = contactInfo;
        _zoneId = zoneId;
        _authorities = authorities;
    }

    public enum Authority {
        STANDARD,
        ADMIN
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @FieldNameConstants
    public static class DTO extends Model.DTO {

        private ContactInfo.DTO contactInfo;

        private ZoneId zoneId;

        private Set<Authority> authorities;

        public DTO(User user) {
            super(user);
            contactInfo = new ContactInfo.DTO(user.get_contactInfo());
            zoneId = user.get_zoneId();
            authorities = user.get_authorities();
        }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public DTO(
                @JsonProperty(Model.DTO.Fields.id) UUID id,
                @JsonProperty(Fields.contactInfo) ContactInfo.DTO contactInfo,
                @JsonProperty(Fields.zoneId) ZoneId zoneId,
                @JsonProperty(Fields.authorities) Set<Authority> authorities,
                @JsonProperty(Model.DTO.Fields.history) History history,
                @JsonProperty(Model.DTO.Fields.timestamp) Instant timestamp
        ) {
            super(id, timestamp, history);
            this.contactInfo = contactInfo;
            this.zoneId = zoneId;
            this.authorities = authorities;
        }
    }

    public enum DatabaseColumns {
        password,
        zone_id,
        replacements,
        authority
    }

    @Getter @ToString(callSuper = true)
    @FieldNameConstants
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
        @JsonIgnoreProperties(ignoreUnknown = true)
        @FieldNameConstants
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

            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            public DTO(
                    @JsonProperty(Fields.name) String name,
                    @JsonProperty(Fields.email) String email,
                    @JsonProperty(Fields.addresses) List<Address.DTO> addresses
            ) {
                this.name = name;
                this.email = email;
                this.addresses = addresses;
            }
        }

        public static final String databaseTable = "contact_info";

        public enum DatabaseColumn {
            name,
            email,
            addresses
        }

        @Getter
        public static class Address extends Model {

            private String _street;

            private String _number;

            private String _floor;

            private String _postalCode;

            private String _city;

            private String _country;

            public Address(ContactInfo.Address.DTO address) {
                this(
                        address.getId(),
                        address.getStreet(),
                        address.getNumber(),
                        address.getFloor(),
                        address.getPostalCode(),
                        address.getCity(),
                        address.getCountry(),
                        address.getHistory(),
                        address.getTimestamp()
                );
            }

            public Address(
                    String street,
                    String number,
                    String floor,
                    String postalCode,
                    String city,
                    String country
            ) {
                this(
                        null,
                        street,
                        number,
                        floor,
                        postalCode,
                        city,
                        country,
                        new History(),
                        null
                );
            }

            public Address(
                    UUID id,
                    String street,
                    String number,
                    String floor,
                    String postalCode,
                    String city,
                    String country,
                    History history,
                    Instant timestamp
            ) {
                super(id, history, timestamp);

                _street = street;
                _number = number;
                _floor = floor;
                _postalCode = postalCode;
                _city = city;
                _country = country;
            }

            @Getter @Setter
            @JsonIgnoreProperties(ignoreUnknown = true)
            @FieldNameConstants
            public static class DTO extends Model.DTO {

                private String street;

                private String number;

                private String floor;

                private String postalCode;

                private String city;

                private String country;

                public DTO(User.ContactInfo.Address address) {
                    super(address);
                    street = address.get_street();
                    number = address.get_number();
                    floor = address.get_floor();
                    postalCode = address.get_postalCode();
                    city = address.get_city();
                    country = address.get_country();
                }

                @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
                public DTO(
                        @JsonProperty(Model.DTO.Fields.id) UUID id,
                        @JsonProperty(Model.DTO.Fields.history) History history,
                        @JsonProperty(Fields.street) String street,
                        @JsonProperty(Fields.number) String number,
                        @JsonProperty(Fields.floor) String floor,
                        @JsonProperty(Fields.postalCode) String postalCode,
                        @JsonProperty(Fields.city) String city,
                        @JsonProperty(Fields.country) String country,
                        @JsonProperty(Model.DTO.Fields.timestamp) Instant timestamp
                ) {
                    super(id, timestamp, history);
                    this.street = street;
                    this.number = number;
                    this.floor = floor;
                    this.postalCode = postalCode;
                    this.city = city;
                    this.country = country;
                }
            }

            public enum DatabaseColumn {
                id,
                street,
                number,
                floor,
                postal_code,
                city,
                country,
                timestamp
            }
        }
    }
}
