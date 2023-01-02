package fr.gantoin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import fr.gantoin.data.service.CustomOAuth2UserService;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfiguration(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/oauth2/authorization/twitch", "/login/oauth2/callback/**").permitAll();
        http.oauth2Login(oauth -> {
                    oauth.defaultSuccessUrl("/").userInfoEndpoint(userInfo -> {
                        userInfo.userService(customOAuth2UserService);
                    });
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/");
                });

        super.configure(http);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers(
                "/images/**"
        );
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }
}
