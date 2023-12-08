package com.devfox.devfoxstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration // 様々な設定
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("jong"); // TODO: Spring Securityで認証機能を追加する時修正
    }
    // TableのcreatedByに何かを貯蔵する時誰が作ったかの情報を入れるためのMethod
}
