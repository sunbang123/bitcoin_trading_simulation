package org.example.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.CreateRequestDto;
import org.example.backend.dto.user.response.ResponseDto;
import org.example.backend.entity.User;
import org.example.backend.exception.requestError.DuplicateUserException;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateService {

    private final UserRepository userRepository;

    public ResponseDto createUser(CreateRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .balance(0.0) // 초기 자금
                .build();

        User savedUser = userRepository.save(user);
        return new ResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
}
