package fr.gantoin.data.entity;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.ToString;

@Component
@SessionScope
@ToString
public class UserSession implements Serializable {

    private User user;
    public UserSession(User user) {
        this.user = user;
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
            this.user = new User();
            user.setName(principal.getAttribute("given_name"));
            user.setSub(principal.getAttribute("sub"));
            user.setProfilePicture(principal.getAttribute("picture"));
            return user;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !isAnonymous(authentication);
    }

    private boolean isAnonymous(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ANONYMOUS")) {
                return true;
            }
        }
        return false;
    }
}
