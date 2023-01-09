package fr.gantoin.data.service;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Service;

@Service
public class VerticalVideoService {

    public static String captureFace(String inputVideoPath, String outputVideoPath) {
        System.load("/usr/local/Cellar/opencv/4.7.0/share/java/opencv4/libopencv_java470.dylib");
        VideoCapture capture = new VideoCapture(inputVideoPath);
        capture.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('m', 'p', '4', 'v'));
        if (!capture.isOpened()) {
            throw new RuntimeException("Failed to open video file: " + inputVideoPath);
        }
        int width = 300;
        int height = 300;
        Mat frameCapture = new Mat();
        MatOfRect faceDetections = new MatOfRect();
        CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_alt.xml");
        VideoWriter writer = new VideoWriter(outputVideoPath, VideoWriter.fourcc('M', 'J', 'P', 'G'), capture.get(Videoio.CAP_PROP_FPS), new Size(width, height));
        while (capture.read(frameCapture)) {
            faceDetector.detectMultiScale(frameCapture, faceDetections);
            if (!faceDetections.empty()) {
                Rect rect = faceDetections.toArray()[0];
                Mat face = new Mat(frameCapture, rect);
                Imgproc.resize(face, frameCapture, new Size(width, height));
            }
            writer.write(frameCapture);
        }
        writer.release();
        capture.release();
        return outputVideoPath;
    }

}
