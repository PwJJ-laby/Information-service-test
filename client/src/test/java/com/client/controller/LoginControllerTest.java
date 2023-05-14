package com.client.controller;

import com.client.model.dto.LoginDto;
import com.client.model.dto.UserRegistrationDto;
import com.client.security.SecurityConfig;
import com.client.service.GoogleAuthService;
import com.client.service.LoginService;
import com.client.service.RegisterService;
import com.client.util.ExternalAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@OverrideAutoConfiguration(enabled = true)
@ActiveProfiles("testmvc")
@Import(SecurityConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoginService loginService;
    @MockBean
    private RegisterService registerService;
    @MockBean
    private GoogleAuthService googleAuthService;

    @Test
    void should_show_logout_message_with_logout_param() throws Exception {
        //given & when
        mockMvc.perform(get("/client/login?logout=true"))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("You have been successfully logged out."));
    }

    @Test
    void should_show_logout_message_without_logout_param() throws Exception {
        //given & when
        mockMvc.perform(get("/client/login"))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Login page."));
    }

    @Test
    void should_return_login_successful_when_login_is_successful() throws Exception {
        // given
        LoginDto loginDto = new LoginDto("user123", "password123");
        String loginDtoJson = new ObjectMapper().writeValueAsString(loginDto);

        // when
        mockMvc.perform(post("/client/login/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDtoJson))

        // then
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful!"));
    }

    @Test
    void should_return_unauthorized_when_authentication_exception_is_thrown() throws Exception {
        // given
        LoginDto loginDto = new LoginDto("user123", "wrongpassword");
        String loginDtoJson = new ObjectMapper().writeValueAsString(loginDto);
        ExternalAuthenticationException externalAuthenticationException = new ExternalAuthenticationException("external");

        doThrow(new BadCredentialsException("Incorrect username or password!", externalAuthenticationException))
                .when(loginService).setUserSession(any(HttpServletRequest.class), any(HttpServletResponse.class), eq(null), eq(loginDto));

        // when
        mockMvc.perform(post("/client/login/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDtoJson))

        // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("You cannot log in this way. You must log in via external"));
    }

    @Test
    void should_return_unauthorized_when_bad_credentials_exception_is_thrown() throws Exception {
        // given
        LoginDto loginDto = new LoginDto("user123", "wrongpassword");
        String loginDtoJson = new ObjectMapper().writeValueAsString(loginDto);

        doThrow(new BadCredentialsException("Incorrect username or password!")).when(loginService)
                .setUserSession(any(HttpServletRequest.class), any(HttpServletResponse.class), eq(null), eq(loginDto));

        // when
        mockMvc.perform(post("/client/login/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDtoJson))

        // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Incorrect username or password!"));
    }

    @Test
    void should_return_redirect_response_with_google_auth_url() throws Exception {
        //given
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=CLIENT_ID&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fclient%2Flogin%2Foauth2%2Fcode%2Fgoogle&scope=openid%20email%20profile";

        ///when
        mockMvc.perform(get("/client/login/google"))

        //then
                .andExpect(status().isFound())
                .andExpect(header().string("Location", googleAuthUrl));
    }

    @Test
    void should_return_redirect_response_with_google_auth_url_when_login_attempted_successfully_via_google() throws Exception {
        //given
        String accessToken = "google_access_token";
        String email = "test@example.com";
        String givenName = "John";
        String familyName = "Stones";
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail(email);
        userRegistrationDto.setName(givenName);
        userRegistrationDto.setSurname(familyName);
        userRegistrationDto.setUsername(email);
        String responseBody = "google_token_response";
        JSONObject userInfo = new JSONObject();
        userInfo.put("given_name", givenName);
        userInfo.put("email", email);
        userInfo.put("family_name", familyName);
        ResponseEntity<String> userInfoResponse = ResponseEntity.ok(userInfo.toString());

        given(googleAuthService.getTokenResponseBody(any(RestTemplate.class), anyString(), anyString(), anyString(), anyString()))
                .willReturn(responseBody);
        given(googleAuthService.getAccessToken(anyString())).willReturn(accessToken);
        given(googleAuthService.getUserInfoResponse(any(RestTemplate.class), anyString())).willReturn(userInfoResponse);
        given(loginService.checkIfUserExistsByEmail(email)).willReturn(false);
        given(registerService.registerUserClientToEditorial(any(UserRegistrationDto.class), any(HttpServletRequest.class)))
                .willReturn(ResponseEntity.ok("Registered user"));

        //when
        mockMvc.perform(get("/client/login/oauth2/code/google").param("code", "google_code"))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Successful login via google account."));
    }

    @Test
    void should_return_successful_login_response_when_user_already_exists_in_database_and_logs_in_via_google() throws Exception {
        //given
        String accessToken = "google_access_token";
        String email = "test@example.com";
        String givenName = "John";
        String familyName = "Stones";
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail(email);
        userRegistrationDto.setName(givenName);
        userRegistrationDto.setSurname(familyName);
        userRegistrationDto.setUsername(email);
        String responseBody = "google_token_response";
        JSONObject userInfo = new JSONObject();
        userInfo.put("given_name", givenName);
        userInfo.put("email", email);
        userInfo.put("family_name", familyName);
        ResponseEntity<String> userInfoResponse = ResponseEntity.ok(userInfo.toString());

        given(googleAuthService.getTokenResponseBody(any(RestTemplate.class), anyString(), anyString(), anyString(), anyString()))
                .willReturn(responseBody);
        given(googleAuthService.getAccessToken(anyString())).willReturn(accessToken);
        given(googleAuthService.getUserInfoResponse(any(RestTemplate.class), anyString())).willReturn(userInfoResponse);
        given(loginService.checkIfUserExistsByEmail(email)).willReturn(true);

        //when
        mockMvc.perform(get("/client/login/oauth2/code/google").param("code", "google_code"))

        //then
                .andExpect(status().isOk())
                .andExpect(content().string("Successful login via google account."));

        verify(registerService, never()).registerUserClientToEditorial(any(UserRegistrationDto.class), any(HttpServletRequest.class));
    }

    @Test
    void should_return_bad_request_response_when_google_authentication_fails() throws Exception {
        //given
        given(googleAuthService.getTokenResponseBody(any(RestTemplate.class), anyString(), anyString(), anyString(), anyString()))
                .willReturn(null);

        //when
        mockMvc.perform(get("/client/login/oauth2/code/google").param("code", "google_code"))

        //then
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unsuccessful authentication via google account."));

        verify(loginService, never()).setUserSession(any(HttpServletRequest.class), any(HttpServletResponse.class), any(UserRegistrationDto.class), any(LoginDto.class));
        verify(registerService, never()).registerUserClientToEditorial(any(UserRegistrationDto.class), any(HttpServletRequest.class));
    }
}
