package org.example.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.UserSignupRequestDto;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    public User register(UserSignupRequestDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword()) // 나중에 암호화
                .email(dto.getEmail())
                .balance(0.0)
                .build();
        return userRepository.save(user);
    }
}
