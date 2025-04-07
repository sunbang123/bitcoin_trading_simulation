package org.example.backend.dto.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@NoArgsConstructor
public class UpdateRequestDto {
    private String username;
    private String password;
}
