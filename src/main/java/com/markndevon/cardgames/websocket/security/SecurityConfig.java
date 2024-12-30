package com.markndevon.cardgames.websocket.security;

import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.websocket.security.filters.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CardsUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Set up CORS TODO: Finish configuration, only one domain so shouldnt need to be wide open
                .authorizeHttpRequests(request -> request
                        .requestMatchers("login", "register", "/ws/**").permitAll() // Allow login endpoint
                        .anyRequest().authenticated()            // Secure everything
                ).httpBasic(Customizer.withDefaults())         // Basic Auth (JWT is recommended for production)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
    @Bean
    public UserDetailsService userDetailsService() {
        // In memory user authentication, really we want this to be backed by a database of users
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("PLAYER")
                .build();

        UserDetails devon = User.builder()
                .username("devon")
                .password(passwordEncoder().encode("markIzCool"))
                .roles("PLAYER")
                .build();

        UserDetails tom = User.builder()
                .username("tom")
                .password(passwordEncoder().encode("markIzCooler"))
                .roles("PLAYER")
                .build();


        UserDetails kyle = User.builder()
                .username("kyle")
                .password(passwordEncoder().encode("markIzCoolest"))
                .roles("PLAYER")
                .build();

        return new InMemoryUserDetailsManager(user, devon, tom, kyle);
    }
     */
}