package com.editorial.service;

import com.editorial.model.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    private LoginDto loginDto;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BasicServiceImpl basicService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto("username", "password");
    }

    @Test
    public void should_set_user_session() {
        // given
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(request.getSession()).thenReturn(session);

        // when
        loginService.setUserSession(request, response, loginDto);

        // then
        verify(authenticationManager).authenticate(any());
        verify(basicService).roleCookieCreation(eq(request), eq(response));
        verify(session).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), eq(SecurityContextHolder.getContext()));
    }

    @Test
    public void should_throw_authentication_exception() {
        // given
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        // when & then
        assertThrows(BadCredentialsException.class, () -> loginService.setUserSession(request, response, loginDto));
    }
}
