package com.agile.jewelryshop.config;

import com.agile.jewelryshop.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean staff = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
            if (admin) response.sendRedirect("/admin");
            else if (staff) response.sendRedirect("/admin/orders");
            else response.sendRedirect("/");
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/products", "/products/**", "/product/**", "/about", "/register",
                        "/login", "/cart/**", "/css/**", "/js/**", "/images/**", "/fonts/**",
                        "/error/**", "/h2-console/**").permitAll()
                .requestMatchers("/admin/orders/**", "/admin/products/**", "/admin/support/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/checkout/**", "/orders/**", "/profile/**", "/payment/**",
                        "/support/tickets/**").authenticated()
                .anyRequest().permitAll())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/?logout").permitAll())
            .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, exception) -> {
                boolean csrfFailure = exception instanceof MissingCsrfTokenException
                        || exception instanceof InvalidCsrfTokenException;
                String target = request.getContextPath() + "/error/403"
                        + (csrfFailure ? "?reason=csrf" : "");
                response.sendRedirect(target);
            }))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        return http.build();
    }
}
