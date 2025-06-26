package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String role;
    private String address;
    private Boolean isVerified = false;
}
