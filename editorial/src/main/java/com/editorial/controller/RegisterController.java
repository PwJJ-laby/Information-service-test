package com.editorial.controller;

import com.editorial.model.dto.UserRegistrationDto;
import com.editorial.service.RegisterService;
import com.editorial.service.RegisterServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.editorial.util.AccountConstants.APP_SUPPLIER;

@RestController
@RequestMapping("/editorial")
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> createUserAccount(@Valid @RequestBody UserRegistrationDto userRegistrationDto, HttpServletRequest request) {
        if (registerService.checkIfUserExistsByEmail(userRegistrationDto.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided e-mail is already taken!");
        else if (registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided username is already taken!");

        userRegistrationDto.setSupplier(APP_SUPPLIER);
        registerService.registerUser(userRegistrationDto);
        ResponseEntity<String> clientResponse = registerService.registerUserEditorialToClient(userRegistrationDto, request);
        if (!clientResponse.getStatusCode().is2xxSuccessful())
            return new ResponseEntity<>(clientResponse.getBody(), clientResponse.getStatusCode());
        return ResponseEntity.ok("Correct registration process.");
    }

    @PostMapping("/registration/fc")
    public ResponseEntity<String> createUserAccountByClient(@Valid @RequestBody UserRegistrationDto userRegistrationDto, @RequestHeader("X-Caller") String caller) {
        if (!"REGISTRATION_FROM_CLIENT".equals(caller))
            return ResponseEntity.badRequest().body("Unsuccessful registration process in editorial microservice.");
        else {
            if (registerService.checkIfUserExistsByEmail(userRegistrationDto.getEmail()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided e-mail is already taken!");
            else if (registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Provided username is already taken!");

            registerService.registerUser(userRegistrationDto);
            return ResponseEntity.ok("Correct registration process.");
        }
    }
}