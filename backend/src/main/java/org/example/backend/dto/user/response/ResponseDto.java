package org.example.backend.dto.user.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private Long id;
    private String username;
    private String email;

}


