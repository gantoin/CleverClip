package fr.gantoin.security;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.security.AuthenticationContext;

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
