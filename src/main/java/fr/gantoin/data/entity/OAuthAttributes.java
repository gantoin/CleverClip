package fr.gantoin.data.entity;

import java.io.IOException;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

import fr.gantoin.data.service.PictureDownloaderService;

@Data
@NoArgsConstructor
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String sub;
    private String picture;

    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String sub, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.sub = sub;
        this.picture = picture;
    }

    public static OAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes) {
        return ofTwitch(userNameAttributeName, attributes);
    }
    private static OAuthAttributes ofTwitch(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(attributes,
                userNameAttributeName,
                (String) attributes.get("preferred_username"),
                (String) attributes.get("sub"),
                (String) attributes.get("picture"));
    }

    public User toEntity() {
        User user = new User();
        user.setName(name);
        user.setSub(sub);
        user.setUsername(name);
        user.setUrlToProfilePicture(picture);
        try {
            user.setProfilePicture(PictureDownloaderService.downloadPicture(picture));
        } catch (IOException e) {
            throw new RuntimeException("Could not download picture");
        }
        return user;
    }
}
