package com.client.service;

import com.client.model.dto.UserRegistrationDto;
import com.client.model.entity.User;
import com.client.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static com.client.util.UrlConstants.EDITORIAL_DELETE_USER_URL;
import static com.client.util.UrlConstants.EDITORIAL_EDIT_USER_URL;

@Service
public class UserActionServiceImpl implements UserActionService {

    private final UserRepository userRepository;
    private final BasicServiceImpl basicService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserActionServiceImpl(UserRepository userRepository, BasicServiceImpl basicService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.basicService = basicService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(userRepository.findUserByName(authentication.getName()));
    }

    @Override
    public ResponseEntity<String> deleteUserClientToEditorial(Long userId, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = basicService.copyHeadersFromRequest(request);
        headers.set("X-Caller", "DELETE_FROM_CLIENT");
        URI endpointUri = UriComponentsBuilder.fromUriString(EDITORIAL_DELETE_USER_URL)
                .queryParam("id", userId).build().toUri();
        return restTemplate.exchange(endpointUri, HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);
    }

    @Override
    public void updateUser(User user, UserRegistrationDto userRegistrationDto) {
        editUserByDto(user, userRegistrationDto);
        userRepository.save(user);
    }
    @Override
    public ResponseEntity<String> updateUserClientToEditorial(Long userId, UserRegistrationDto userRegistrationDto, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = basicService.copyHeadersFromRequest(request);
        headers.set("X-Caller", "EDIT_FROM_CLIENT");
        URI endpointUri = UriComponentsBuilder.fromUriString(EDITORIAL_EDIT_USER_URL)
                .queryParam("id", userId).build().toUri();
        return restTemplate.exchange(endpointUri, HttpMethod.PUT, new HttpEntity<>(userRegistrationDto, headers), String.class);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    private void editUserByDto(User user, UserRegistrationDto userRegistrationDto) {
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.getUserDetails().setName(userRegistrationDto.getName());
        user.getUserDetails().setSurname(userRegistrationDto.getSurname());
        user.getUserDetails().setEmail(userRegistrationDto.getEmail());
    }


}
