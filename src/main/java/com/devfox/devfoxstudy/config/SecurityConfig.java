package com.devfox.devfoxstudy.config;

import com.devfox.devfoxstudy.domain.UserAccount;
import com.devfox.devfoxstudy.dto.UserAccountDto;
import com.devfox.devfoxstudy.repository.UserAccountRepository;
import com.devfox.devfoxstudy.security.BoardPrincipal;
import com.devfox.devfoxstudy.security.KakaoOAuth2Response;
import com.devfox.devfoxstudy.service.UserAccountService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,"/","/articles","/articles/search-hashtag","/user/signup"
                        ).permitAll()
                        .anyRequest().authenticated()) // 要求タイプに応じてURLパターンを指定し、その要求タイプに対するセキュリティ設定を行う
                // authenticatedを使ってRootPage,articles,search-hashtagを除外したPageは認証が必要に作る
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
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


    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService(); // 一回使うだからnew生成

        // 基本認証方式は具現体が提供してくれるのを使う
        // ここでkakaoResponseをCapture後parsingして有意味な情報を抽出してUserをDatabaseでもう一回照会
        // Userがあるとそのまま進行ないと貯蔵して会員介入を誘導
        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());
            String registrationId = userRequest.getClientRegistration().getRegistrationId();  // yamlFileのkakao
            String providerId = String.valueOf(kakaoResponse.id());
            String username = registrationId + "_" + providerId; // kakao認証値 +　固有値
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());
            // 認証責任はKakaoApiが持つ、だが会員Table設計時Passwordが必須だから作る
            // bcrypt => DBに入れる時Encoding

            //  上のMethodを実行してsearchUserを検索　ない時この名でIdを作る
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from) // dataがある時mapping
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    // springの認証機能を使う時は必ずpasswordEncoderも登録するべき
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // passwordEncoder設定をfactoryから委員して持ってくる意味
    }

}
