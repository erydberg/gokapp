package se.scouttavling.gokapp.start;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test/**").
                        hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")              // use our Thymeleaf login page
                        .failureUrl("/loginfailed")       // maps to controller method above
                        .defaultSuccessUrl("/", true)     // redirect after successful login
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}
