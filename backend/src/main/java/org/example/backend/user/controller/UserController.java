package org.example.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.dto.request.UserCreateRequestDto;
import org.example.backend.user.dto.request.UserUpdateRequestDto;
import org.example.backend.user.dto.response.UserResponseDto;
import org.example.backend.user.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCreateService createService;
    private final UserGetByEmailService getByEmailService;
    private final UserGetByUsernameService getByUsernameService;
    private final UserGetAllService getAllService;
    private final UserUpdateService updateService;
    private final UserDeleteService deleteService;

    @PostMapping()
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateRequestDto dto) {
        return ResponseEntity.ok(createService.createUser(dto));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> readByEmail(@PathVariable String email) {
        return ResponseEntity.ok(getByEmailService.getByEmail(email));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(getByUsernameService.getByUsername(username));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = getAllService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable String email,
            @RequestBody UserUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(updateService.updateUser(email, dto));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        deleteService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

}
