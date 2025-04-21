package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.dto.response.UserResponseDto;
import org.example.backend.user.entity.User;
import org.example.backend.common.exception.user.UserListEmptyException;
import org.example.backend.user.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGetAllService {

    private final UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (users.isEmpty()) {
            throw new UserListEmptyException();
        }
        return users.stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .balance(user.getBalance())
                        .build())
                .collect(Collectors.toList());

    }
}
