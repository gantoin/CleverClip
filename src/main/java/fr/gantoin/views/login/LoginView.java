package fr.gantoin.views.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import fr.gantoin.views.MainLayout;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
public class LoginView extends HorizontalLayout {

    private final OAuth2AuthorizedClientService clientService;

    public LoginView(OAuth2AuthorizedClientService clientService, @Autowired Environment env) {
        this.clientService = clientService;
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        String clientKey = env.getProperty("spring.security.oauth2.client.registration.twitch.client-id");
        if (clientKey == null) {
            Paragraph text = new Paragraph("⚠️ Could not find OAuth client key in application.properties.");
            text.getStyle().set("padding-top", "100px");
            add(text);
        } else {
            navbar();
        }
    }

    private void navbar() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            Button loginButton = getTwitchSignInButton();
            Anchor anchor = new Anchor("/oauth2/authorization/twitch", loginButton);
            anchor.getElement().setAttribute("router-ignore", true);
            add(anchor);
        } else {
            Notification.show("✅ Logged In");
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = this.clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
            String accessToken = client.getAccessToken().getTokenValue();
            Notification.show("🔌 Logged in with token: " + accessToken);
        }
    }

    public static Button getTwitchSignInButton() {
        Button loginButton = new Button();
        Span icon = new Span();
        icon.setClassName("lab la-twitch");
        loginButton.setIcon(icon);
        loginButton.setText("Login with Twitch");
        loginButton.addClassName("twitch-login");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClassName("toolbar");
        return loginButton;
    }
}
