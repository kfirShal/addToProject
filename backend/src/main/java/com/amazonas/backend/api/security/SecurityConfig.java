package com.amazonas.backend.api.security;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.authentication.UserCredentials;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserManager userManager;
    private final AuthenticationController authenticationController;


    public SecurityConfig(UserManager UserManager, AuthenticationController authenticationController) {
        this.userManager = UserManager;
        this.authenticationController = authenticationController;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("userprofiles/enterasguest").permitAll()
                                .requestMatchers("/auth/guest").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(FormLoginConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(userManager)
                .addFilterBefore((servletRequest, servletResponse, filterChain) -> {
                    HttpServletRequest request = (HttpServletRequest) servletRequest;
                    String token = request.getHeader("Authorization");
                    if (token != null) {
                        if(token.startsWith("Bearer ")){
                            token = token.substring(7);
                            String userId = request.getHeader("userId");
                            if(userId != null){
                                if(authenticationController.validateToken(userId,token)){
                                    SecurityContextHolder.getContext().setAuthentication(new JWTAuthentication());
                                }
                            }
                        }
                    }
                    filterChain.doFilter(servletRequest, servletResponse);
                }, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(userManager)
                .userDetailsService(userManager);
        return http.build();
    }
}
