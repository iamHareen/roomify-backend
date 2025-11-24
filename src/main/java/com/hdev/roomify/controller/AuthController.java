package com.hdev.roomify.controller;


import com.hdev.roomify.dto.LoginRequestDTO;
import com.hdev.roomify.dto.ResponseDTO;
import com.hdev.roomify.entity.User;
import com.hdev.roomify.service.interfac.IUserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
@RequestMapping("/auth")
public class AuthController {

    private IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody User user) {
        ResponseDTO response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        ResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
