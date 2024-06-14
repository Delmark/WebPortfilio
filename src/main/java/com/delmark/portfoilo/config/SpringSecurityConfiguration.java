package com.delmark.portfoilo.config;


import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.utils.RSAKeyProperties;
import com.delmark.portfoilo.views.LoginView;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SpringSecurityConfiguration extends VaadinWebSecurity {

    private RSAKeyProperties rsaKeyProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**")).permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                // Документация API
                                .requestMatchers("/api-docs/**").permitAll()
                                .requestMatchers("/delm-api-info.html").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                // Общедоступные запросы
                                .requestMatchers("/api/v1/projects**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/v1/portfolio**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/v1/workPlaces**").hasAnyRole("ADMIN", "USER")
                                // Общедоступные запросы на чаты
                                .requestMatchers(HttpMethod.GET, "/api/v1/chat/{id}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/api/v1/chat/{id}/messages").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/v1/chat/{chatId}/user/{userId}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST,"/api/v1/chat/**").hasAnyRole("ADMIN", "USER")
                                // Запросы, требующие роли администратора
                                .requestMatchers(HttpMethod.GET, "/api/v1/chat").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/chat/user/{username}").hasRole("ADMIN")
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/v1/tech/**").hasRole("ADMIN")
                                .requestMatchers("/test/**").permitAll()
                ).oauth2ResourceServer(auth -> auth.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .formLogin((loginPage) -> {
                    loginPage.successForwardUrl("/");
                })
                .rememberMe(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

//        http.csrf(AbstractHttpConfigurer::disable);
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        super.configure(http);

        this.setLoginView(http, LoginView.class);
    }

    @Override
    protected void configure(WebSecurity webSecurity) throws Exception {
        super.configure(webSecurity);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(UserService userService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setPasswordEncoder(passwordEncoder());
        daoProvider.setUserDetailsService((UserDetailsService) userService);
        return new ProviderManager(daoProvider);
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.getPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.getPublicKey()).privateKey(rsaKeyProperties.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
