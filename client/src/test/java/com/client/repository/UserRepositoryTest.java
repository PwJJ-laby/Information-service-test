package com.client.repository;

import com.client.config.TestDbConfig;
import com.client.model.entity.Authority;
import com.client.model.entity.User;
import com.client.model.entity.UserDetails;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@OverrideAutoConfiguration(enabled = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("testdb")
@ContextConfiguration(classes = TestDbConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;
    @BeforeAll
    public void setUp() {
        Authority auth = Authority.builder()
                .id(Long.parseLong("1"))
                .authorityName("USER")
                .build();

        UserDetails userDetails = UserDetails.builder()
                .id(Long.parseLong("1"))
                .name("Test")
                .surname("TestSurname")
                .email("Test1@email.com")
                .supplier("APP")
                .build();

        user = User.builder()
                .id(Long.parseLong("1"))
                .password("user")
                .username("user")
                .enabled(true)
                .authority(auth)
                .userDetails(userDetails)
                .endorsements(new ArrayList<>())
                        .build();
        auth.setUser(user);
        userDetails.setUser(user);
    }
    @Test
    public void find_user_by_name() {
        // given
        user.setUsername("john.doe");
        userRepository.save(user);
        // when
        User foundUser = userRepository.findUserByName("john.doe");
        // then
        assertNotNull(foundUser);
        assertEquals("john.doe", foundUser.getUsername());
    }

    @Test
    public void user_exists_by_email() {
        // given
        user.getUserDetails().setEmail("john.doe@example.com");
        userRepository.save(user);
        // when
        boolean exists = userRepository.existsUsersByEmail("john.doe@example.com");
        // then
        assertTrue(exists);
    }

    @Test
    public void user_exists_by_username() {
        // given
        user.setUsername("john");
        userRepository.save(user);
        // when
        boolean exists = userRepository.existsUsersByUsername("john");
        // then
        assertTrue(exists);
    }

    @Test
    public void delete_user_by_id() {
        // given
        userRepository.save(user);
        // when & then
        assertDoesNotThrow(() -> userRepository.deleteUserById(user.getId()));
    }

    @Test
    public void find_user_by_id() {
        // given
        userRepository.save(user);
        // when
        User foundUser = userRepository.findUserById(user.getId());
        // then
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }
}
