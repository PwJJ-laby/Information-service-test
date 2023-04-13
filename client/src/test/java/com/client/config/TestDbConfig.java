package com.client.config;

import com.client.repository.ArticleRepository;
import com.client.service.ArticleService;
import com.client.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestDbConfig {

    @Autowired
    private ArticleRepository articleRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ArticleService articleService(){
        return new ArticleServiceImpl(articleRepository);
    }
}