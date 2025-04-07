package org.example.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.RegisterRequestDto;
import org.example.backend.dto.user.response.ResponseDto;
import org.example.backend.entity.User;
import org.example.backend.exception.requestError.DuplicateUserException;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    public ResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .balance(0.0)
                .build();

        User savedUser = userRepository.save(user);
        return new ResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
}
