package fr.gantoin.data.service;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import fr.gantoin.data.entity.OAuthAttributes;
import fr.gantoin.data.entity.User;
import fr.gantoin.data.entity.UserSession;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    public CustomOAuth2UserService(UserRepository userRepository, HttpSession httpSession) {
        this.userRepository = userRepository;
        this.httpSession = httpSession;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attributes = OAuthAttributes.of(userNameAttributeName, oAuth2User.getAttributes());
        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new UserSession(user));
        System.out.println("attributes: " + attributes.getAttributes());
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        return userRepository.findByUsername(attributes.getName())
                .map(user -> {
                    if (!user.getSub().equals(attributes.getSub())) {
                        user.setSub(attributes.getSub());
                    }
                    if (!user.getName().equals(attributes.getName())) {
                        user.setName(attributes.getName());
                    }
                    if (!user.getUrlToProfilePicture().equals(attributes.getPicture())) {
                        try {
                            user.setProfilePicture(PictureDownloaderService.downloadPicture(attributes.getPicture()));
                        } catch (IOException e) {
                            throw new RuntimeException("Could not download picture");
                        }
                        user.setUrlToProfilePicture(attributes.getPicture());
                    }
                    System.out.println("user: " + user);
                    return userRepository.save(user);
                })
                .orElseGet(() -> userRepository.save(attributes.toEntity()));
    }
}
