package com.client.service;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.client.util.UrlConstants.GOOGLE_TOKEN_ENDPOINT;
import static com.client.util.UrlConstants.GOOGLE_USERINFO_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthServiceImplTest {

    private final String code = "correct-code";
    private final String redirectUri = "correct-redirect-uri";
    private final String clientSecret = "correct-client-secret";
    private final String correctResponseBody = "{ \"access_token\": \"1234abc\" }";
    private final String incorrectResponseBody = "{}";
    private final String accessToken = "abc123";
    private String clientId;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private GoogleAuthServiceImpl googleAuthService;

    @Test
    public void should_return_token_response_body() {
        // given
        clientId = "correct-client-id";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", redirectUri);
        map.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = new ResponseEntity<>(correctResponseBody, HttpStatus.OK);
        when(restTemplate.postForEntity(GOOGLE_TOKEN_ENDPOINT, request, String.class)).thenReturn(response);

        // when
        String tokenResponseBody = googleAuthService.getTokenResponseBody(restTemplate, code, clientId, redirectUri, clientSecret);

        // then
        assertEquals(correctResponseBody, tokenResponseBody);
        verify(restTemplate).postForEntity(GOOGLE_TOKEN_ENDPOINT, request, String.class);
    }

    @Test
    public void should_return_bad_request() {
        // given
        clientId = "incorrect-client-id";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", redirectUri);
        map.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = new ResponseEntity<>(incorrectResponseBody, HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(GOOGLE_TOKEN_ENDPOINT, request, String.class)).thenReturn(response);

        // when
        String tokenResponseBody = googleAuthService.getTokenResponseBody(restTemplate, code, clientId, redirectUri, clientSecret);

        // then
        assertEquals(incorrectResponseBody, tokenResponseBody);
        verify(restTemplate).postForEntity(GOOGLE_TOKEN_ENDPOINT, request, String.class);
    }

    @Test
    void should_return_access_token() throws JSONException {
        // given & when
        String accessToken = googleAuthService.getAccessToken(correctResponseBody);

        // then
        assertEquals("1234abc", accessToken);
    }

    @Test
    void should_return_invalid_response_body() {
        //given & when & then
        assertThrows(JSONException.class, () -> googleAuthService.getAccessToken(incorrectResponseBody));
    }

    @Test
    public void should_return_valid_user_info_response() {
        // given
        String expectedResponse = "{\"id\": \"123456\", \"name\": \"John Doe\", \"email\": \"johndoe@gmail.com\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> accessEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> expected = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(GOOGLE_USERINFO_ENDPOINT, HttpMethod.GET, accessEntity, String.class)).thenReturn(expected);

        // when
        ResponseEntity<String> response = googleAuthService.getUserInfoResponse(restTemplate, accessToken);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void should_return_error_response_when_rest_template_throws_exception() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> accessEntity = new HttpEntity<>("", headers);
        when(restTemplate.exchange(GOOGLE_USERINFO_ENDPOINT, HttpMethod.GET, accessEntity, String.class))
                .thenThrow(new RestClientException("Could not connect to Google UserInfo Endpoint"));

        // when & then
        assertThrows(RestClientException.class, () -> googleAuthService.getUserInfoResponse(restTemplate, accessToken));
    }
}
