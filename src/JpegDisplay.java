import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.AlphaComposite;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JpegDisplay extends JpegDisplayDemo {

    public JpegDisplay() {

    }

    @Override
    BufferedImage setTransparency(BufferedImage background, BufferedImage currentImage, List<Integer> missingSegments) {

        if (missingSegments == null || missingSegments.isEmpty()) {
            return currentImage; // gibt bild wieder wenn keine Fehler da sind
        }


        // Erstelle ein neues Bild mit Transparenz
        BufferedImage resultImage = new BufferedImage(
                currentImage.getWidth(),
                currentImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        // Höhe eines einzelnen Segments berechnen
        int segmentHeight = currentImage.getHeight() / 22;

        Graphics2D graphics = resultImage.createGraphics();

        try {

            graphics.drawImage(background, 0, 0, null); //erst Hintergrund zeichnen


            renderSegments(graphics, currentImage, missingSegments, segmentHeight);// dann  transparente Segmente zeichnen

        } finally {
            graphics.dispose();

        }

        return resultImage;
    }


    private void renderSegments(Graphics2D graphics, BufferedImage image, List<Integer> missingSegments, int segmentHeight) {
        //zeichnet alle Segmente die nicht in Missing List sind
        for (int segmentIndex = 0; segmentIndex < 22; segmentIndex++) {
            if (!missingSegments.contains(segmentIndex)) {
                int yPosition = segmentIndex * segmentHeight;

                // Zeichne das Segment aus dem Originalbild
                graphics.drawImage(
                        image,
                        0, yPosition,                                 // Ziel (x, y)
                        image.getWidth(), yPosition + segmentHeight, // Ziel (x, y + Höhe)
                        0, yPosition,                                     // Quelle (x, y)
                        image.getWidth(), yPosition + segmentHeight, // Quelle (x, y + Höhe)
                        null
                );
            }
        }
    }
}