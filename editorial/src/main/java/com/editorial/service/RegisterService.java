package com.editorial.service;

import com.editorial.model.dto.UserRegistrationDto;
import com.editorial.model.entity.User;
import com.editorial.model.entity.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface RegisterService {
    boolean checkIfUserExistsByUsername(String username);
    void registerUser(UserRegistrationDto userRegistrationDto);
    boolean checkIfUserExistsByEmail(String email);
    UserDetails dtoToUserDetails(UserRegistrationDto userRegistrationDto);
    User dtoToUser(UserRegistrationDto userRegistrationDto);
    ResponseEntity<String> registerUserEditorialToClient(UserRegistrationDto userRegistrationDto, HttpServletRequest request);
}
