package com.client.controller;

import com.client.model.dto.UserRegistrationDto;
import com.client.security.SecurityConfig;
import com.client.service.LoginService;
import com.client.service.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterController.class)
@OverrideAutoConfiguration(enabled = true)
@ActiveProfiles("testmvc")
@Import(SecurityConfig.class)
public class RegisterControllerTest {

    private String userRegistrationDtoJson;
    private UserRegistrationDto userRegistrationDto;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegisterService registerService;
    @MockBean
    private LoginService loginService;

    @BeforeEach
    void setUp()throws Exception {
        userRegistrationDto = UserRegistrationDto.builder()
                .name("Ted")
                .surname("Stones")
                .email("tedStones@gmail.com")
                .supplier("APP")
                .username("tedstone")
                .password("password123")
                .authorityName("USER")
                .build();

        userRegistrationDtoJson = new ObjectMapper().writeValueAsString(userRegistrationDto);
    }

    @Test
    void should_create_user_account_and_return_ok() throws Exception {
        //given
        when(loginService.checkIfUserExistsByEmail(userRegistrationDto.getEmail())).thenReturn(false);
        when(registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername())).thenReturn(false);
        when(registerService.registerUserClientToEditorial(any(UserRegistrationDto.class), any(HttpServletRequest.class))).thenReturn(ResponseEntity.ok("Successfully registered user"));

        //when
        mockMvc.perform(post("/client/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Correct registration process."));
    }

    @Test
    void should_return_conflict_when_email_is_already_taken() throws Exception {
        //given
        when(loginService.checkIfUserExistsByEmail(userRegistrationDto.getEmail())).thenReturn(true);

        //when
        mockMvc.perform(post("/client/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        //then
                .andExpect(status().isConflict())
                .andExpect(content().string("Provided e-mail is already taken!"));
    }

    @Test
    void should_return_conflict_when_username_is_already_taken() throws Exception {
        //given
        when(loginService.checkIfUserExistsByEmail(userRegistrationDto.getEmail())).thenReturn(false);
        when(registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername())).thenReturn(true);

        //when
        mockMvc.perform(post("/client/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        //then
                .andExpect(status().isConflict())
                .andExpect(content().string("Provided username is already taken!"));
    }

    @Test
    void should_return_bad_request_when_caller_header_is_invalid() throws Exception {
        //given & when
        mockMvc.perform(post("/client/registration/fe")
                        .header("X-Caller", "INVALID_CALLER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        //then
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsuccessful registration process in client microservice."));
    }

    @Test
    void should_create_user_account_by_client_and_return_ok() throws Exception {
        //given
        String caller = "REGISTRATION_FROM_EDITORIAL";
        when(loginService.checkIfUserExistsByEmail(userRegistrationDto.getEmail())).thenReturn(false);
        when(registerService.checkIfUserExistsByUsername(userRegistrationDto.getUsername())).thenReturn(false);

        //when
        mockMvc.perform(post("/client/registration/fe")
                        .header("X-Caller", caller)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Correct registration process."));
    }

    @Test
    void should_create_user_account_by_client() throws Exception {
        // given
        String caller = "REGISTRATION_FROM_EDITORIAL";
        when(loginService.checkIfUserExistsByEmail(anyString())).thenReturn(false);
        when(registerService.checkIfUserExistsByUsername(anyString())).thenReturn(false);

        // when
        mockMvc.perform(post("/client/registration/fe")
                        .header("X-Caller", caller)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        // then
                .andExpect(status().isOk())
                .andExpect(content().string("Correct registration process."));

        verify(registerService).registerUser(userRegistrationDto);
    }

    @Test
    void should_return_409_when_email_already_exists_in_create_user_account_by_editorial() throws Exception {
        // given
        String caller = "REGISTRATION_FROM_EDITORIAL";
        when(loginService.checkIfUserExistsByEmail(anyString())).thenReturn(true);

        // when
        mockMvc.perform(post("/client/registration/fe")
                        .header("X-Caller", caller)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        // then
                .andExpect(status().isConflict())
                .andExpect(content().string("Provided e-mail is already taken!"));
    }

    @Test
    void should_return_409_when_username_already_exists_in_create_user_account_by_editorial() throws Exception {
        // given
        String caller = "REGISTRATION_FROM_EDITORIAL";
        when(loginService.checkIfUserExistsByEmail(anyString())).thenReturn(false);
        when(registerService.checkIfUserExistsByUsername(anyString())).thenReturn(true);

        // when
        mockMvc.perform(post("/client/registration/fe")
                        .header("X-Caller", caller)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        // then
                .andExpect(status().isConflict())
                .andExpect(content().string("Provided username is already taken!"));
    }

    @Test
    void should_return_bad_request_when_caller_header_is_not_set_in_create_user_account_by_editorial() throws Exception {
        // given
        String caller = "INVALID_REGISTRATION_FROM_EDITORIAL";

        // when
        mockMvc.perform(post("/client/registration/fe")
                        .header("X-Caller", caller)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRegistrationDtoJson))

        // then
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsuccessful registration process in client microservice."));
    }
}
