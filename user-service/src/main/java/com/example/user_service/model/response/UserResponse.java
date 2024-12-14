package com.example.user_service.model.response;

import com.example.user_service.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response model containing user details")
public class UserResponse {

    @Schema(description = "Unique identifier of the user", example = "1001")
    private long id;

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Encrypted password of the user", example = "$2a$10$...")
    private String password;

    @Schema(description = "Indicates whether the user's account is expired", example = "true")
    private boolean isAccountNonExpired;

    @Schema(description = "Indicates whether the user's account is locked", example = "false")
    private boolean isAccountNonLocked;

    @Schema(description = "Indicates whether the user's credentials are expired", example = "true")
    private boolean isCredentialsNonExpired;

    @Schema(description = "Indicates whether the user's account is enabled", example = "true")
    private boolean isEnabled;

    @Schema(description = "Role associated with the user", example = "ADMIN")
    private Role role;
}