package com.client.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private HttpStatus status;
    private Map<String, String> errors;
    @Mock
    private MethodArgumentNotValidException exceptionMock;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private WebRequest request;
    @Mock
    private HttpHeaders headers;
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        status = HttpStatus.BAD_REQUEST;
    }

    @Test
    public void should_handle_method_argument_not_valid() {
        // given
        when(exceptionMock.getBindingResult()).thenReturn(bindingResult);

        FieldError error1 = new FieldError("objectName", "field1", "message1");
        FieldError error2 = new FieldError("objectName", "field2", "message2");
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(error1, error2));

        // when
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(exceptionMock, headers, status, request);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        errors = (Map<String, String>) responseEntity.getBody();
        assertEquals(2, errors.size());
        assertEquals("message1", errors.get("field1"));
        assertEquals("message2", errors.get("field2"));
    }

    @Test
    public void should_handle_method_argument_not_valid_with_no_errors() {
        // given
        when(exceptionMock.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of());

        // when
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(exceptionMock, headers, status, request);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        errors = (Map<String, String>) responseEntity.getBody();
        assertTrue(errors.isEmpty());
    }
}
