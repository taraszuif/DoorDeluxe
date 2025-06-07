package me.zuif.doordeluxe.config;

import me.zuif.doordeluxe.repository.UserRepository;
import me.zuif.doordeluxe.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserRepository userRepository;
    private final SecretKey aesKey;

    public WebSecurityConfig(UserRepository userRepository) throws Exception {
        this.userRepository = userRepository;
        this.aesKey = generateAESKey();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/change", "/fragment/doors", "/cart/add/**", "/cart/remove/**", "/cart/clear/**", "/about", "/", "/home", "/contact", "/index", "/help", "/register", "/cart", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico", "/resources/**", "/error", "/door/about/**").permitAll()
                        .requestMatchers("/user/edit/**", "/user", "/rating/**", "/cart/checkout", "/order/list", "order/about/**").hasAnyRole("USER", "MANAGER",
                                "DIRECTOR")
                        .requestMatchers("/discount/new/**", "/discount/edit/**", "/discount/delete/**").hasAnyRole("MANAGER", "DIRECTOR")
                        .requestMatchers("/door/new/**", "/door/edit/**", "/door/delete/**",
                                "/order/delete/**", "/order/status/**").hasAnyRole("MANAGER", "DIRECTOR")
                        .requestMatchers("/analytics/**", "/admin/**", "/user/delete/**", "/user/add/**", "/user/edit/**", "user/list").hasRole("DIRECTOR")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                ).oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    public String encryptPassword(String password) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public boolean comparePasswords(String rawPassword, String hashedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, hashedPassword);
    }

    public boolean checkAlreadyEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }
}