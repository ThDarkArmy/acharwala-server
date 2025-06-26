package tda.darkarmy.acharwala.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tda.darkarmy.acharwala.enums.Role;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String email;
    private String password;
    private String newPassword;
    private Role role;
    private String phoneNumber;
    private String dob;
    private String profilePic;
    private boolean emailVerified;
    private boolean active;
    private Long otp;

}
