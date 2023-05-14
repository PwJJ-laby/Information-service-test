package com.editorial.controller;

import com.editorial.model.dto.LoginDto;
import com.editorial.service.LoginService;
import com.editorial.service.LoginServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/editorial")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> showLogoutMessage(@RequestParam(name = "logout", required = false) String logout) {
        if (logout != null) {
            return ResponseEntity.ok("You have been successfully logged out.");
        }
        return ResponseEntity.ok("Login page.");
    }

    @PostMapping("/login/v2")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            loginService.setUserSession(request, response, loginDto);
            return ResponseEntity.ok("Login successful.");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password!");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testMessageAfterLogin() {
        return ResponseEntity.ok("WELCOME");
    }
}
