package org.example.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.UpdateRequestDto;
import org.example.backend.dto.user.response.ResponseDto;
import org.example.backend.entity.User;
import org.example.backend.exception.requestError.UserNotFoundException;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {

    private final UserRepository userRepository;

    public ResponseDto updateUser(String email, UpdateRequestDto dto) {
        User existingUser  = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        existingUser.updateUser(dto.getUsername(), dto.getPassword());

        User savedUser = userRepository.save(existingUser);
        return new ResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }
}
