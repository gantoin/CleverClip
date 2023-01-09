package fr.gantoin.data.service;

import java.util.List;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.opencv.core.Rect;

class VerticalVideoServiceIT {
    @Test
    @Ignore("This test is ignored because it requires a video file")
    void captureFace() {
        VerticalVideoService.captureFace("src/test/resources/video-test.mp4",
                String.format("src/test/resources/video-test-output-%s.mp4", System.currentTimeMillis()));
    }
}
