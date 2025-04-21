package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.dto.request.UserUpdateRequestDto;
import org.example.backend.user.dto.response.UserResponseDto;
import org.example.backend.user.entity.User;
import org.example.backend.common.exception.user.UserNotFoundException;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto updateUser(String email, UserUpdateRequestDto dto) {
        User user  = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일을 사용하는 유저를 찾을 수 없습니다." + email));

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
