package org.example.backend.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.LoginRequestDto;
import org.example.backend.dto.user.request.UserSignupRequestDto;
import org.example.backend.dto.user.response.LoginResponseDto;
import org.example.backend.dto.user.response.UserResponseDto;
import org.example.backend.entity.User;
import org.example.backend.service.user.LoginService;
import org.example.backend.service.user.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterService registerService;
    private final LoginService loginService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserSignupRequestDto dto) {
        User newUser = registerService.register(dto);
        UserResponseDto response = new UserResponseDto(newUser.getId(), newUser.getUsername(), newUser.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        User user = loginService.login(dto);
        LoginResponseDto response = new LoginResponseDto(user.getId(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(response);
    }
}
