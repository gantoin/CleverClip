package fr.gantoin.data.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gantoin.data.entity.Clip;
import fr.gantoin.data.entity.SamplePerson;

@Service
public class TwitchService {

    String username; // TODO: only for testing
    private final ClipMapper clipMapper;
    private final String clientId;
    private final String clientSecret;
    private final String twitchUserId;
    private final String bearerToken;

    public TwitchService(ClipMapper clipMapper, @Autowired Environment env) {
        username = "BenZaie"; // TODO: only for testing
        this.clipMapper = clipMapper;
        clientId = env.getProperty("spring.security.oauth2.client.registration.twitch.client-id");
        clientSecret = env.getProperty("spring.security.oauth2.client.registration.twitch.client-secret");
        try {
            this.bearerToken = getTwitchBearer();
        } catch (IOException e) {
            throw new RuntimeException("Unable to get bearer token from Twitch", e);
        }
        twitchUserId = getTwitchUserId();
    }

    public List<Clip> get3PopularClips() throws IOException {
        URL url = new URL(String.format("https://api.twitch.tv/helix/clips?broadcaster_id=%s&first=3", twitchUserId));
        JsonNode data = requestTwitchApi(url).get("data");
        List<Clip> clips = new ArrayList<>();
        data.forEach(clip -> clips.add(clipMapper.map(clip)));
        return clips;
    }

    private String getTwitchBearer() throws IOException {
        URL url = new URL(String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials", clientId, clientSecret));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(connection.getInputStream());
        return rootNode.get("access_token").asText();
    }

    private String getTwitchUserId() {
        try {
            URL obj = new URL(String.format("https://api.twitch.tv/helix/users?login=%s", username));
            JsonNode data = requestTwitchApi(obj).get("data");
            return data.get(0).get("id").asText();
        } catch (IOException e) {
            throw new RuntimeException("Error getting user id", e);
        }
    }

    private JsonNode requestTwitchApi(URL obj) throws IOException {
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Client-ID", clientId);
        con.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
        con.setRequestProperty("Authorization", "Bearer " + bearerToken);
        con.setDoOutput(true);
        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Error getting streams");
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(con.getInputStream());
        return root;
    }

    private Page<Clip> importClips(Specification<Clip> filter, Pageable pageable) throws IOException {
        URL url;
        if (pageable.getPageNumber() == 0) {
            url = new URL(String.format("https://api.twitch.tv/helix/clips?broadcaster_id=%s&first=100", twitchUserId));
        } else {
            url = new URL(String.format("https://api.twitch.tv/helix/clips?broadcaster_id=%s&first=100&after=%s", twitchUserId, pageable.getPageNumber()));
        }
        JsonNode data = requestTwitchApi(url).get("data");
        List<Clip> clips = new ArrayList<>();
        data.forEach(clip -> clips.add(clipMapper.map(clip)));
        return new PageImpl<>(clips.subList(0, 10));
    }

    public List<Clip> list(String lastClipId) {
        URL url;
        try {
            if (lastClipId == null) {
                url = new URL(String.format("https://api.twitch.tv/helix/clips?broadcaster_id=%s&first=100", twitchUserId));
            } else {
                url = new URL(String.format("https://api.twitch.tv/helix/clips?broadcaster_id=%s&first=100&after=%s", twitchUserId, lastClipId));
            }
            JsonNode jsonNode = requestTwitchApi(url);
            JsonNode data = jsonNode.get("data");
            List<Clip> clips = new ArrayList<>();
            data.forEach(clip -> {
                Clip map = clipMapper.map(clip);
                map.setCursor(jsonNode.get("pagination").get("cursor").asText());
                clips.add(map);
            });
            return clips;
        } catch (IOException e) {
            throw new RuntimeException("Error getting clips", e);
        }
    }
}
