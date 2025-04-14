package org.example.backend.service.login;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.login.request.LoginRequestDto;
import org.example.backend.entity.User;
import org.example.backend.exception.requestError.InvalidPasswordException;
import org.example.backend.exception.requestError.UserNotFoundException;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public User login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
