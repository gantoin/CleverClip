package fr.gantoin.views.home;

import java.util.Optional;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import fr.gantoin.data.entity.User;
import fr.gantoin.data.service.UserRepository;
import fr.gantoin.security.AuthenticatedUser;
import fr.gantoin.views.MainLayout;
import fr.gantoin.views.login.LoginView;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final UserRepository userRepository;
    private final AuthenticatedUser authenticatedUser;
    public HomeView(AuthenticatedUser authenticatedUser, UserRepository userRepository) {
        this.authenticatedUser = authenticatedUser;
        this.userRepository = userRepository;
        addContent();
    }

    private void addContent() {
        Div div = new Div();
        Optional<User> maybeUser = Optional.empty();
        Optional<String> principalName = authenticatedUser.getPrincipalName();
        if (principalName.isPresent()) {
            maybeUser = userRepository.findBySub(principalName.get());
        }
        if (maybeUser.isPresent()) {
            div.setText("Hello " + maybeUser.get().getName());
            add(div);
        } else {
            div.setText("Welcome");
            add(div);
            Button button = LoginView.getTwitchSignInButton();
            button.addClickListener(e -> {
                UI.getCurrent().getPage().setLocation("/login");
            });
            add(button);
        }
    }
}
