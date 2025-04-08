package org.example.backend.service.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.response.ResponseDto;
import org.example.backend.entity.User;
import org.example.backend.exception.requestError.UserListEmptyException;
import org.example.backend.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllService {

    private final UserRepository userRepository;

    public List<ResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (users.isEmpty()) {
            throw new UserListEmptyException("등록된 사용자가 없습니다.");
        }
        return users.stream()
                .map(user -> ResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());

    }
}
