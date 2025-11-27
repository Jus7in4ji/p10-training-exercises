package com.justinaji.chatapp_messages.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurtityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter; 

    @Bean
    public SecurityFilterChain SFC (HttpSecurity http) throws Exception{

        return http
            .csrf(customizer -> customizer.disable())
            .cors(customizer -> customizer.configure(http)) 
            .authorizeHttpRequests(request-> request
                .requestMatchers("/SignUp","/login", "/subscribe-room", "/gethistory", "/getusername",
                    //swagger documentation
                    "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                    // websocket related requests
                    "/chat.html", "/chat/**", "/chat", "/chat/info",  "/topic/**", "/app/**",
                    // Static resources
                    "/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**")
                .permitAll()
                .anyRequest().authenticated()) //makes sure all requests passed must be authenticated
            .httpBasic(customizer -> customizer.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
