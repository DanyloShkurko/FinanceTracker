package org.example.authservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response model for login containing a token and its expiration time")
public class LoginResponse {

    @Schema(description = "JWT token generated upon successful login", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "The expiration time of the token in seconds", example = "3600")
    private long expiresIn;
}