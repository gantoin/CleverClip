package fr.gantoin.data.service.mapper;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Set;

import fr.gantoin.data.entity.Clip;
import fr.gantoin.data.entity.ImportedClip;
import fr.gantoin.data.service.TwitchService;

public class ImportedClipMapper {

    public static Set<ImportedClip> map(Set<Clip> clips) {
        return clips.stream().map(clip -> {
            ImportedClip importedClip = new ImportedClip();
            importedClip.setBroadcasterId(clip.getBroadcasterId());
            try {
                importedClip.setVideo(TwitchService.getVideoBytes(clip.getThumbnailUrl()));
                importedClip.setThumbnail(TwitchService.getThumbnailBytes(clip.getThumbnailUrl()));
            } catch (IOException e) {
                throw new RuntimeException("Error while getting video bytes", e);
            }
            importedClip.setTitle(clip.getTitle());
            importedClip.setUrl(clip.getUrl());
            importedClip.setDuration(clip.getDuration());
            importedClip.setCreatedAt(clip.getCreatedAt());
            importedClip.setLanguage(clip.getLanguage());
            importedClip.setGameId(clip.getGameId());
            return importedClip;
        }).collect(toSet());
    }
}
