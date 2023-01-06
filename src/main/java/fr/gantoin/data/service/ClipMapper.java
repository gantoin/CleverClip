package fr.gantoin.data.service;

import java.sql.Date;
import java.text.DateFormat;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gantoin.data.entity.Clip;

@Service
public class ClipMapper {

    public Clip map(JsonNode jsonNode) {
        Clip clip = new Clip();
        clip.setId(jsonNode.get("id").asText());
        clip.setTitle(jsonNode.get("title").asText());
        clip.setUrl(jsonNode.get("url").asText());
        clip.setThumbnailUrl(jsonNode.get("thumbnail_url").asText());
        clip.setCreatorName(jsonNode.get("creator_name").asText());
        clip.setCreatorId(jsonNode.get("creator_id").asText());
        clip.setVideoId(jsonNode.get("video_id").asText());
        clip.setGameId(jsonNode.get("game_id").asText());
        clip.setLanguage(jsonNode.get("language").asText());
        clip.setCreatedAt(Date.valueOf(jsonNode.get("created_at").asText().substring(0, 10)));
        clip.setDuration(jsonNode.get("duration").asDouble());
        clip.setVodOffset(jsonNode.get("vod_offset").asText());
        clip.setEmbedUrl(jsonNode.get("embed_url").asText());
        clip.setBroadcasterId(jsonNode.get("broadcaster_id").asText());
        clip.setBroadcasterName(jsonNode.get("broadcaster_name").asText());
        clip.setViewCount(jsonNode.get("view_count").asInt());
        return clip;
    }
}
