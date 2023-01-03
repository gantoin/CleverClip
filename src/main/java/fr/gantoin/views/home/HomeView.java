package fr.gantoin.views.home;

import java.io.IOException;
import java.util.Optional;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import fr.gantoin.data.entity.ClipEmbed;
import fr.gantoin.data.entity.User;
import fr.gantoin.data.service.TwitchService;
import fr.gantoin.data.service.UserRepository;
import fr.gantoin.security.AuthenticatedUser;
import fr.gantoin.views.MainLayout;
import fr.gantoin.views.login.LoginView;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends Main implements HasComponents, HasStyle {

    private final UserRepository userRepository;
    private final AuthenticatedUser authenticatedUser;
    private OrderedList popularClips;
    private Optional<User> maybeUser;

    public HomeView(AuthenticatedUser authenticatedUser, UserRepository userRepository,  TwitchService twitchService) {
        this.authenticatedUser = authenticatedUser;
        this.userRepository = userRepository;
        constructUI();
        try {
            twitchService.get3PopularClips().forEach(clip -> popularClips.add(new ClipEmbed(clip)));
        } catch (IOException e) {
            throw new RuntimeException("Error getting clips", e);
        }
    }

    private void constructUI() {
        Optional<String> principalName = authenticatedUser.getPrincipalName();
        principalName.ifPresent(s -> maybeUser = userRepository.findBySub(s));
        if (maybeUser.isPresent()) {
            addClassNames("templates-view");
            addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);
            HorizontalLayout container = new HorizontalLayout();
            container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);
            VerticalLayout headerContainer = new VerticalLayout();
            H2 header = new H2("☀️ Welcome " + maybeUser.get().getName()+"!");
            header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
            Paragraph description = new Paragraph("Effortlessly manage your Twitch clips with our app");
            description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
            headerContainer.add(header, description);

            H3 h3 = new H3("📈 Your most popular clips");
            popularClips = new OrderedList();
            popularClips.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

            container.add(headerContainer);
            add(container, h3, popularClips, new Hr());

            // Import your clips in CleverClips
            Button importButton = new Button("Import your clips", e -> UI.getCurrent().navigate("clips"));
            importButton.addClassNames(Margin.Bottom.XLARGE, Margin.Top.XLARGE);
            importButton.getElement().setAttribute("theme", "primary");
            add(importButton);

        } else {
            Div div = new Div();
            div.setText("You are not logged in");
            add(div);
            Button button = LoginView.getTwitchSignInButton();
            button.addClickListener(e -> UI.getCurrent().getPage().setLocation("/login"));
            add(button);
        }
    }



}
