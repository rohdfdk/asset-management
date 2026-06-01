package org.example.assetmanagement.config;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ThymeleafフォームはCSRF有効。
                // REST APIのcurl確認を続けたい場合は /api/** のみCSRF除外します。
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/health/**"
                        ).permitAll()

                        // ユーザー管理は管理者のみ
                        .requestMatchers("/users-view", "/users-view/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // 資産登録・編集・削除は管理者のみ
                        .requestMatchers("/assets-view/new").hasRole("ADMIN")
                        .requestMatchers("/assets-view/*/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/assets-view/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/assets-view/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/assets-view/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assets/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/assets/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/assets/**").hasRole("ADMIN")

                        // 資産一覧はログイン済みなら可
                        .requestMatchers(HttpMethod.GET, "/assets-view", "/api/assets/**").authenticated()

                        // 貸出画面・貸出APIはログイン済みなら可
                        // ただし「自分の貸出のみ」制御はController/Service側で行う
                        .requestMatchers("/loans-view", "/loans-view/**").authenticated()
                        .requestMatchers("/api/loans/**").authenticated()

                        // 管理画面系
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // それ以外はログイン必須
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}