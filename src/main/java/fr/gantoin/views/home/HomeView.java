package fr.gantoin.views.home;

import javax.servlet.http.HttpSession;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import fr.gantoin.data.entity.User;
import fr.gantoin.data.entity.UserSession;
import fr.gantoin.security.AuthenticatedUser;
import fr.gantoin.views.MainLayout;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    public HomeView(HttpSession httpSession) {
        Div div = new Div();
        if (httpSession.getAttribute("user") != null) {
            UserSession userSession = (UserSession) httpSession.getAttribute("user");
            User user = userSession.getUser();
//            div.setText("Welcome " + user.getUsername());
        } else {
            div.setText("Welcome");
        }
    }
}
