package fr.gantoin.views;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.gantoin.components.appnav.AppNav;
import fr.gantoin.components.appnav.AppNavItem;
import fr.gantoin.data.entity.User;
import fr.gantoin.data.service.UserRepository;
import fr.gantoin.security.AuthenticatedUser;
import fr.gantoin.views.generated.GeneratedView;
import fr.gantoin.views.home.HomeView;
import fr.gantoin.views.settings.SettingsView;
import fr.gantoin.views.templates.TemplatesView;
import fr.gantoin.views.twitchclips.TwitchClipsView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    private UserRepository userRepository;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, UserRepository userRepository) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.userRepository = userRepository;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("CleverClip");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        if (accessChecker.hasAccess(HomeView.class)) {
            nav.addItem(new AppNavItem("Home", HomeView.class, "la la-home"));

        }
        if (accessChecker.hasAccess(TwitchClipsView.class)) {
            nav.addItem(new AppNavItem("Twitch Clips", TwitchClipsView.class, "lab la-twitch"));

        }
        if (accessChecker.hasAccess(GeneratedView.class)) {
            nav.addItem(new AppNavItem("Generated", GeneratedView.class, "la la-film"));

        }
        if (accessChecker.hasAccess(TemplatesView.class)) {
            nav.addItem(new AppNavItem("Templates", TemplatesView.class, "la la-paint-roller"));

        }
        if (accessChecker.hasAccess(SettingsView.class)) {
            nav.addItem(new AppNavItem("Settings", SettingsView.class, "lab la-whmcs"));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = Optional.empty();
        Optional<String> principalName = authenticatedUser.getPrincipalName();
        if (principalName.isPresent()) {
             maybeUser = userRepository.findBySub(principalName.get());
        }

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-iÌtems", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
