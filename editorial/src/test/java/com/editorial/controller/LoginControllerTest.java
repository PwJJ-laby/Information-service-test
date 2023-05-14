package com.editorial.controller;

import com.editorial.model.dto.LoginDto;
import com.editorial.security.SecurityConfig;
import com.editorial.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@OverrideAutoConfiguration(enabled = true)
@ActiveProfiles("testmvc")
@Import(SecurityConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoginService loginService;

    @Test
    void should_show_logout_message_with_logout_param() throws Exception {
        //given & when
        mockMvc.perform(get("/editorial/login?logout=true"))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("You have been successfully logged out."));
    }

    @Test
    void should_show_logout_message_without_logout_param() throws Exception {
        //given & when
        mockMvc.perform(get("/editorial/login"))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Login page."));
    }

    @Test
    void should_login_successful() throws Exception {
        //given
        LoginDto loginDto = new LoginDto("user123", "password123");
        String loginDtoJson = new ObjectMapper().writeValueAsString(loginDto);

        //when
        mockMvc.perform(post("/editorial/login/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDtoJson))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful."));
    }

    @Test
    void should_login_failed() throws Exception {
        //given
        LoginDto loginDto = new LoginDto("user123", "wrongpassword");
        String loginDtoJson = new ObjectMapper().writeValueAsString(loginDto);
        doThrow(new BadCredentialsException("Incorrect username or password!")).when(loginService).setUserSession(any(HttpServletRequest.class), any(HttpServletResponse.class), eq(loginDto));

        //when
        mockMvc.perform(post("/editorial/login/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDtoJson))

        //then
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Incorrect username or password!"));
    }
}
