package com.client.controller;

import com.client.model.dto.UserRegistrationDto;
import com.client.service.LoginService;
import com.client.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.client.util.AccountConstants.APP_SUPPLIER;
import static com.client.util.AccountConstants.ROLE_USER;

@RestController
@RequestMapping("/client")
public class RegisterController {

    private final LoginService loginService;
    private final RegisterService registerService;

    @Autowired
    public RegisterController(LoginService loginService, RegisterService registerService) {
        this.loginService = loginService;
        this.registerService = registerService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> createUserAccount(@Valid @RequestBody UserRegistrationDto userRegistrationDto, HttpServletRequest request) {
        if (loginService.checkIfUserExistsByEmail(userRegistrationDto.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided e-mail is already taken!");
        else if (registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided username is already taken!");

        userRegistrationDto.setSupplier(APP_SUPPLIER);
        userRegistrationDto.setAuthorityName(ROLE_USER);
        registerService.registerUser(userRegistrationDto);
        ResponseEntity<String> editorialResponse = registerService.registerUserClientToEditorial(userRegistrationDto, request);
        if (!editorialResponse.getStatusCode().is2xxSuccessful())
            return new ResponseEntity<>(editorialResponse.getBody(), editorialResponse.getStatusCode());
        return ResponseEntity.ok("Correct registration process.");
    }

    @PostMapping("/registration/fe")
    public ResponseEntity<String> createUserAccountByEditorial(@Valid @RequestBody UserRegistrationDto userRegistrationDto, @RequestHeader("X-Caller") String caller) {
        if (!"REGISTRATION_FROM_EDITORIAL".equals(caller))
            return ResponseEntity.badRequest().body("Unsuccessful registration process in client microservice.");
        else {
            if (loginService.checkIfUserExistsByEmail(userRegistrationDto.getEmail()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided e-mail is already taken!");
            else if (registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided username is already taken!");

            registerService.registerUser(userRegistrationDto);
            return ResponseEntity.ok("Correct registration process.");
        }
    }
}
