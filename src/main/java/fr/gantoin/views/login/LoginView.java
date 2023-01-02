package fr.gantoin.views.login;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import fr.gantoin.components.appnav.AppNavItem;
import fr.gantoin.views.MainLayout;
import fr.gantoin.views.home.HomeView;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout implements HasUrlParameter<String> {

    private final OAuth2AuthorizedClientService clientService;
    private String error;

    public LoginView(OAuth2AuthorizedClientService clientService, @Autowired Environment env) {
        this.clientService = clientService;
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        String clientKey = env.getProperty("spring.security.oauth2.client.registration.twitch.client-id");
        if (error != null) {
            add(new Paragraph("Error during login: " + error));
        }
        // Check that oauth keys are present
        if (clientKey == null) {
            Paragraph text = new Paragraph("Could not find OAuth client key in application.properties. "
                    + "Please double-check the key and refer to the README.md file for instructions.");
            text.getStyle().set("padding-top", "100px");
            add(text);
        } else {
            add(navbar());
        }
    }

    private Component navbar() {
        HorizontalLayout root = new HorizontalLayout();
        root.setWidthFull();
        root.setAlignItems(Alignment.CENTER);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            Button loginButton = new Button();
            Image twitchLogo = new Image("icons/twitch.png", "twitch_logo.png");
            Span icon = new Span();
            icon.setClassName("lab la-twitch");
            twitchLogo.setHeight("30px");
            loginButton.setIcon(icon);
            loginButton.setText("Login with Twitch");
            loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            loginButton.addClassName("toolbar");
            Anchor anchor = new Anchor("/oauth2/authorization/twitch", loginButton);
            anchor.getElement().setAttribute("router-ignore", true);
            root.add(anchor);
        } else {
            Notification.show("Logged In");
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = this.clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
            String accessToken = client.getAccessToken().getTokenValue();
            Notification.show("Logged in with token: " + accessToken);
        }
        root.addClassNames("contrast-5pct");
        return root;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            error = parameter;
        }
    }
}
