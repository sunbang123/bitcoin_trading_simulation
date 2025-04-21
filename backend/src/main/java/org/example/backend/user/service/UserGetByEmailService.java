package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.dto.response.UserResponseDto;
import org.example.backend.user.entity.User;
import org.example.backend.common.exception.user.UserNotFoundException;
import org.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGetByEmailService {

    private final UserRepository userRepository;

    public UserResponseDto getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 이메일을 사용하는 유저를 찾을 수 없습니다." + email));
        return toDto(user);
    }

    private UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .build();
    }
}