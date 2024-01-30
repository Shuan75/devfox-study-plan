package com.devfox.devfoxstudy.config;

import com.devfox.devfoxstudy.domain.UserAccount;
import com.devfox.devfoxstudy.dto.UserAccountDto;
import com.devfox.devfoxstudy.repository.UserAccountRepository;
import com.devfox.devfoxstudy.security.BoardPrincipal;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,"/","/articles","/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()) // 要求タイプに応じてURLパターンを指定し、その要求タイプに対するセキュリティ設定を行う
                // authenticatedを使ってRootPage,articles,search-hashtagを除外したPageは認証が必要に作る
                .formLogin().and()
                .logout()
                    .logoutSuccessUrl("/")
                    .and()
                .csrf().disable()
                .build();

    }

    // 使わない利用はCSRFのような保安攻撃に弱くなる（securityが全部無視）
    // .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()に変更
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // spring securiy検査から除外static resource(css,js,images,etc)
//        // atCommonLocationsの中StaticResourceLocationの中の定義せれてます
//        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }

    // User情報を持つ部分を具現(interface)
    // 認証Dataを具現
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) { // DBの貯蔵する具現のため
        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from) // mappingをしてUserNameの内容をDtoに変換
                .map(BoardPrincipal::from) // userdetailを受ける
                .orElseThrow(() -> new UsernameNotFoundException("userを探せません" + username)); // .mapのOptional処理
    }

    // springの認証機能を使う時は必ずpasswordEncoderも登録するべき
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // passwordEncoder設定をfactoryから委員して持ってくる意味
    }

}
