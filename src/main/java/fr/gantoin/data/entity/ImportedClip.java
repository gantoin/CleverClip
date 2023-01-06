package fr.gantoin.data.entity;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImportedClip extends AbstractEntity {
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
    private byte[] thumbnail;
    private String thumbnailContentType;
    private byte[] video;
    private String videoContentType;
}
