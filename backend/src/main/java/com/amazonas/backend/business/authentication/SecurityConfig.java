package com.amazonas.backend.business.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationController authenticationController;

    public SecurityConfig(AuthenticationController authenticationController) {
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
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationController)
                .userDetailsService(authenticationController);
        return http.build();
    }

    public static class JWTAuthentication extends UsernamePasswordAuthenticationToken {
        public JWTAuthentication() {
            super(null, null, null);
        }
    }

    private class JWTFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
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
            filterChain.doFilter(request, response);
        }
    }
}
