package org.example.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.UserUpdateRequestDto;
import org.example.backend.dto.user.response.UserResponseDto;
import org.example.backend.entity.User;
import org.example.backend.exception.requestError.UserNotFoundException;
import org.example.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto updateUser(String email, UserUpdateRequestDto dto) {
        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String newPassword = dto.getPassword() != null ?
                passwordEncoder.encode(dto.getPassword()) : user.getPassword();

        String newUsername = dto.getUsername() != null ?
                dto.getUsername() : user.getUsername();

        user.updateUser(newUsername, newPassword);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .build();
    }
}
