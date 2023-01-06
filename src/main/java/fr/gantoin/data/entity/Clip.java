package fr.gantoin.data.entity;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Clip {
    /*
    "id" -> {TextNode@15047} ""FurryAgitatedStarTinyFace""
    "url" -> {TextNode@15049} ""https://clips.twitch.tv/FurryAgitatedStarTinyFace""
    "embed_url" -> {TextNode@15051} ""https://clips.twitch.tv/embed?clip=FurryAgitatedStarTinyFace""
    "broadcaster_id" -> {TextNode@15053} ""4102418""
    "broadcaster_name" -> {TextNode@15055} ""BenZaie""
    "creator_id" -> {TextNode@15057} ""26608408""
    "creator_name" -> {TextNode@15059} ""Eankalt""
    "video_id" -> {TextNode@15061} """"
    "game_id" -> {TextNode@15061} """"
    "language" -> {TextNode@15064} ""fr""
    "title" -> {TextNode@15066} ""Nouveau Modem 4G ! - C'est le #StreamDeLinsomnie !""
    "view_count" -> {IntNode@15068} "4848"
    "created_at" -> {TextNode@15070} ""2016-08-28T23:41:09Z""
    "thumbnail_url" -> {TextNode@15072} ""https://clips-media-assets2.twitch.tv/23028971088-offset-3456-preview-480x272.jpg""
    "duration" -> {DoubleNode@15074} "31.9"
    "vod_offset" -> {NullNode@15076} "null"
     */
    private String id;
    private String url;
    private String embedUrl;
    private String broadcasterId;
    private String broadcasterName;
    private String creatorId;
    private String creatorName;
    private String videoId;
    private String gameId;
    private String language;
    private String title;
    private int viewCount;
    private Date createdAt;
    private String thumbnailUrl;
    private double duration;
    private String vodOffset;
    private String cursor;

}
