package org.example.backend.service.login;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.login.request.LoginRequestDto;
import org.example.backend.dto.login.response.LoginResponseDto;
import org.example.backend.entity.User;
import org.example.backend.exception.user.InvalidPasswordException;
import org.example.backend.exception.user.UserNotFoundException;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.token.JwtTokenProvider;
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


