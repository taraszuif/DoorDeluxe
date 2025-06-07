package me.zuif.doordeluxe.service.impl;

import lombok.RequiredArgsConstructor;
import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String name = defaultIfNull(oAuth2User.getAttribute("name"), "Unknown");
        String firstName = defaultIfNull(oAuth2User.getAttribute("given_name"), "Unknown");
        String lastName = defaultIfNull(oAuth2User.getAttribute("family_name"), "Unknown");
        String picture = defaultIfNull(oAuth2User.getAttribute("picture"), "");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserName(name);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setImageUrl(picture);
            newUser.setPassword("N/A");
            newUser.setAddTime(LocalDateTime.now());
            newUser.setAge(0);
            newUser.setCity("Unknown");
            newUser.setRole(Role.USER);
            return userRepository.save(newUser);
        });

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "name"
        );
    }

    private String defaultIfNull(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}