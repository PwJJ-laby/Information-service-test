package com.client.model.dto;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRegistrationDtoTest {

    @Test
    public void should_convert_json_to_dto() throws JSONException {
        // given
        JSONObject googleUserData = new JSONObject();
        googleUserData.put("email", "test@example.com");
        googleUserData.put("given_name", "Nathan");
        googleUserData.put("family_name", "Gates");

        // when
        UserRegistrationDto userDto = UserRegistrationDto.jsonToDto(googleUserData);

        // then
        assertNotNull(userDto);
        assertEquals("test@example.com", userDto.getUsername());
        assertNotNull(userDto.getPassword());
        assertEquals("Nathan", userDto.getName());
        assertEquals("Gates", userDto.getSurname());
        assertEquals("test@example.com", userDto.getEmail());
        assertEquals("GOOGLE", userDto.getSupplier());
        assertEquals("USER", userDto.getAuthorityName());
    }
}
