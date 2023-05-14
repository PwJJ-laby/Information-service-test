package com.editorial.config;

import com.editorial.repository.UserRepository;
import com.editorial.service.BasicServiceImpl;
import com.editorial.service.RegisterService;
import com.editorial.service.RegisterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestDbConfig {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private BasicServiceImpl basicService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisterService registerService() {
        return new RegisterServiceImpl(passwordEncoder(), userRepository, basicService);
    }
}
