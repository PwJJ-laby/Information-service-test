package com.client.service;

import com.client.model.dto.UserRegistrationDto;
import com.client.model.entity.Authority;
import com.client.model.entity.User;
import com.client.model.entity.UserDetails;
import com.client.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.client.util.UrlConstants.EDITORIAL_REGISTRATION_URL;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BasicServiceImpl basicService;

    public RegisterServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, BasicServiceImpl basicService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.basicService = basicService;
    }

    @Override
    public boolean checkIfUserExistsByUsername(String username) {
        return userRepository.existsUsersByUsername(username);
    }

    @Override
    public void registerUser(UserRegistrationDto userRegistrationDto) {
        User user = dtoToUser(userRegistrationDto);
        UserDetails userDetails = dtoToUserDetails(userRegistrationDto);
        Authority authority = new Authority(userRegistrationDto.getAuthorityName());
        user.connectUserDetails(userDetails);
        userDetails.connectUser(user);
        user.connectAuthority(authority);
        authority.setUser(user);
        userRepository.save(user);
    }

    @Override
    public UserDetails dtoToUserDetails(UserRegistrationDto userRegistrationDto) {
        return UserDetails.builder()
                .name(userRegistrationDto.getName())
                .surname(userRegistrationDto.getSurname())
                .email(userRegistrationDto.getEmail())
                .supplier(userRegistrationDto.getSupplier())
                .build();
    }

    @Override
    public User dtoToUser(UserRegistrationDto userRegistrationDto) {
        return User.builder()
                .username(userRegistrationDto.getUsername())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .enabled(true)
                .build();
    }

    @Override
    public ResponseEntity<String> registerUserClientToEditorial(UserRegistrationDto userRegistrationDto, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = basicService.copyHeadersFromRequest(request);
        headers.set("X-Caller", "REGISTRATION_FROM_CLIENT");
        return restTemplate.exchange(EDITORIAL_REGISTRATION_URL, HttpMethod.POST, new HttpEntity<>(userRegistrationDto, headers), String.class);
    }
}
