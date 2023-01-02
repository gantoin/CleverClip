package fr.gantoin.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.security.AuthenticationContext;
import fr.gantoin.data.entity.User;
import fr.gantoin.data.entity.UserSession;
import fr.gantoin.data.service.CustomOAuth2UserService;
import fr.gantoin.data.service.UserRepository;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public Optional<String> getPrincipalName() {
        return authenticationContext.getPrincipalName();
    }

    public void logout() {
        UI.getCurrent().getPage().executeJs("window.location.href = '/logout'");
    }
}
