package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.request.RegisterRequestDto;
import org.example.backend.dto.user.request.UpdateRequestDto;
import org.example.backend.dto.user.response.ResponseDto;
import org.example.backend.service.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterService registerService;
    private final ReadByEmailService readByEmailService;
    private final ReadAllService readAllService;
    private final UpdateService updateService;
    private final DeleteService deleteService;

    @PostMapping()
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(registerService.register(dto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseDto> readByEmail(@PathVariable String email) {
        return ResponseEntity.ok(readByEmailService.readByEmail(email));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResponseDto>> getAllUsers() {
        List<ResponseDto> users = readAllService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/email/{email}")
    public ResponseEntity<ResponseDto> updateUser(
            @PathVariable String email,
            @RequestBody UpdateRequestDto dto
    ) {
        return ResponseEntity.ok(updateService.updateUser(email, dto));
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        deleteService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

}
