package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.dto.request.UserCreateRequestDto;
import org.example.backend.user.dto.response.UserResponseDto;
import org.example.backend.user.entity.User;
import org.example.backend.auth.enums.Role;
import org.example.backend.common.exception.user.DuplicateEmailException;
import org.example.backend.common.exception.user.DuplicateUsernameException;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto createUser(UserCreateRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException();
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUsernameException();
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .balance(BigDecimal.ZERO)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .build();
    }
}
