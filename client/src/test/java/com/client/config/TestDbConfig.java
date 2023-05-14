package com.client.config;

import com.client.repository.ArticleRepository;
import com.client.repository.UserRepository;
import com.client.security.ClientDetailsService;
import com.client.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestDbConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @MockBean
    private ClientDetailsService clientDetailsService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private BasicServiceImpl basicService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ArticleService articleService() {
        return new ArticleServiceImpl(articleRepository);
    }

    @Bean
    public RegisterService registerService() {
        return new RegisterServiceImpl(passwordEncoder(), userRepository, basicService);
    }

    @Bean
    public LoginService loginService() {
        return new LoginServiceImpl(userRepository, clientDetailsService, basicService, authenticationManager);
    }
}