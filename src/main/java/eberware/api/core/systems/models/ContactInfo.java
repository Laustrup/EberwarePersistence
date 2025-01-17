package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @ToString(callSuper = true)
public class ContactInfo extends Model {

    private String _name;

    private String _email;

    private List<Address> _addresses;

    public ContactInfo(DTO contactInfo) {
        super(contactInfo);
        _name = contactInfo.getName();
        _email = contactInfo.getEmail();
        _addresses = contactInfo.getAddresses().stream()
                .map(Address::new)
                .toList();
    }

    public ContactInfo(
            UUID id,
            String name,
            String email,
            List<Address> addresses,
            History history,
            Instant timestamp
    ) {
        super(id, history, timestamp);
        _name = name;
        _email = email;
        _addresses = addresses;
    }

    @Getter @Setter
    public static class DTO extends Model.DTO {

        private String name;

        private String email;

        private List<Address.DTO> addresses;

        public DTO(ContactInfo contactInfo) {
            super(contactInfo);
            name = contactInfo.get_name();
            email = contactInfo.get_email();
            addresses = contactInfo.get_addresses().stream()
                    .map(Address.DTO::new)
                    .toList();
        }
    }

    @Getter @AllArgsConstructor
    public static class Address {

        private UUID _id;

        private String _street;

        private String _number;

        private String _postalCode;

        private String _city;

        private String _country;

        public Address(DTO address) {
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

            public DTO(Address address) {
                street = address.get_street();
                number = address.get_number();
                postalCode = address.get_postalCode();
                city = address.get_city();
                country = address.get_country();
            }
        }
    }
}
