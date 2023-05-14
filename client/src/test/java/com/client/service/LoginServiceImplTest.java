package com.client.service;

import com.client.config.TestDbConfig;
import com.client.model.dto.LoginDto;
import com.client.model.dto.UserRegistrationDto;
import com.client.model.entity.Authority;
import com.client.model.entity.User;
import com.client.model.entity.UserDetails;
import com.client.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@OverrideAutoConfiguration(enabled = true)
@ActiveProfiles("testdb")
@ContextConfiguration(classes = TestDbConfig.class)
public class LoginServiceImplTest {

    private LoginDto loginDto;
    private UserRegistrationDto userRegistrationDto;
    private User user;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BasicServiceImpl basicService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        loginDto = new LoginDto("username", "password");

        userRegistrationDto = UserRegistrationDto.builder()
                .name("Ted")
                .surname("Stones")
                .email("tedStones@gmail.com")
                .supplier("APP")
                .username("tedstone")
                .password("password123")
                .authorityName("GOOGLE")
                .build();

        Authority auth = Authority.builder()
                .id(Long.parseLong("1"))
                .authorityName("USER")
                .build();

        UserDetails userDetails = UserDetails.builder()
                .id(Long.parseLong("1"))
                .name("Test")
                .surname("TestSurname")
                .email("testuser@example.com")
                .supplier("APP")
                .build();

        user = User.builder()
                .id(Long.parseLong("1"))
                .password("user")
                .username("user")
                .enabled(true)
                .authority(auth)
                .userDetails(userDetails)
                .build();

        auth.setUser(user);
        userDetails.setUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Test
    public void should_return_true_if_user_exists() {
        // given & when
        boolean result = loginService.checkIfUserExistsByEmail("testuser@example.com");

        // then
        assertTrue(result);
    }

    @Test
    public void should_return_false_if_user_does_not_exist() {
        // given & when
        boolean result = loginService.checkIfUserExistsByEmail("nonexistent@example.com");

        // then
        assertFalse(result);
    }

    @Test
    public void should_set_user_session_by_normal_login_process() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(request.getSession()).thenReturn(session);

        // when
        loginService.setUserSession(request, response, null, loginDto);

        // then
        assertEquals(loginDto.getUsername(), SecurityContextHolder.getContext().getAuthentication().getName());
        verify(authenticationManager).authenticate(any());
        verify(basicService).roleCookieCreation(eq(request), eq(response));
        verify(session).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), eq(SecurityContextHolder.getContext()));
    }

    @Test
    public void should_set_user_session_by_google_account() {
        // given
        when(request.getSession()).thenReturn(session);

        // when
        loginService.setUserSession(request, response, userRegistrationDto, null);

        // then
        verify(basicService).roleCookieCreation(eq(request), eq(response));
        verify(session).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), eq(SecurityContextHolder.getContext()));
    }

    @Test
    public void should_throw_authentication_exception() {
        // given
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        // when & then
        assertThrows(BadCredentialsException.class, () -> loginService.setUserSession(request, response, null, loginDto));
    }
}
