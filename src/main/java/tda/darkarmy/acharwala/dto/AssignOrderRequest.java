package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignOrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
}