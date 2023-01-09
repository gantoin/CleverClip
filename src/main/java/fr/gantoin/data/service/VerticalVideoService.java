package fr.gantoin.data.service;


import java.util.HashMap;
import java.util.Map;

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
    /**
     * Capture the face of a video and save it in a new video file.
     * NO FACE TRACKING ⚠️
     * @param inputVideoPath the path of the video to process
     * @param outputVideoPath the path of the output video
     * @return the path of the output video
     */
    public static String captureFace(String inputVideoPath, String outputVideoPath) {
        // Chargement de la bibliothèque OpenCV
        System.load("/usr/local/Cellar/opencv/4.7.0/share/java/opencv4/libopencv_java470.dylib");

        // Ouverture de la vidéo en lecture
        VideoCapture capture = new VideoCapture(inputVideoPath);
        capture.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('m', 'p', '4', 'v'));
        if (!capture.isOpened()) {
            throw new RuntimeException("Failed to open video file: " + inputVideoPath);
        }

        // Initialisation des variables
        int width = 300;
        int height = 300;
        Mat frameCapture = new Mat();
        MatOfRect faceDetections = new MatOfRect();
        CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_alt.xml");
        VideoWriter writer = new VideoWriter(outputVideoPath, VideoWriter.fourcc('M', 'J', 'P', 'G'), capture.get(Videoio.CAP_PROP_FPS), new Size(width, height));
        Map<Rect, Integer> faces = new HashMap<>();

        // Détection des visages dans la vidéo
        while (capture.read(frameCapture)) {
            faceDetector.detectMultiScale(frameCapture, faceDetections);
            if (!faceDetections.empty()) {
                // Pour chaque visage détecté, on incrémente son compteur dans le dictionnaire "faces"
                for (Rect rect : faceDetections.toArray()) {
                    if (faces.containsKey(rect)) {
                        faces.put(rect, faces.get(rect) + 1);
                    } else {
                        faces.put(rect, 1);
                    }
                }
            }
        }

        // Recherche du visage le plus fréquent
        Rect finalFace = null;
        int maxCount = 0;
        for (Map.Entry<Rect, Integer> entry : faces.entrySet()) {
            // Si le visage a été détecté assez souvent et que sa taille est suffisamment grande, on le considère comme étant le visage le plus fréquent
            if (entry.getValue() > maxCount && entry.getKey().width > 100 && entry.getKey().height > 100) {
                finalFace = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        // Si aucun visage n'a été détecté, on arrête le traitement
        if (finalFace == null) {
            return null;
        }

        // Recadrage de la vidéo sur le visage le plus fréquent
        capture.set(Videoio.CAP_PROP_POS_FRAMES, 0);
        while (capture.read(frameCapture)) {
            Mat face = new Mat(frameCapture, finalFace);
            Imgproc.resize(face, frameCapture, new Size(width, height));
            writer.write(frameCapture);
        }

        // Fermeture des objets VideoCapture et VideoWriter
        capture.release();
        writer.release();

        // Retour du chemin vers la vidéo recadrée
        return outputVideoPath;
    }
}
