package org.example.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.dto.request.LoginRequestDto;
import org.example.backend.auth.dto.response.LoginResponseDto;
import org.example.backend.user.entity.User;
import org.example.backend.common.exception.user.InvalidPasswordException;
import org.example.backend.common.exception.user.UserNotFoundException;
import org.example.backend.user.repository.UserRepository;
import org.example.backend.common.security.token.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("해당 이메일을 사용하는 유저를 찾을 수 없습니다." + dto.getEmail()));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        return new LoginResponseDto(token);
    }
}


