package tda.darkarmy.acharwala.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String streetAddress;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String landmark;

    private String contactNumber;

    private String recipientName;
}
