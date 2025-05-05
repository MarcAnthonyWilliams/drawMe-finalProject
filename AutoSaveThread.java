import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AutoSaveThread extends Thread {
    private final DrawingPanel drawingPanel;

    public AutoSaveThread(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void run() {
        System.out.println("AutoSaveThread started."); 
        while (true) {
            try {
                Thread.sleep(3000); // Save every 30 seconds

                synchronized (drawingPanel.getLock()) {
                    BufferedImage canvas = drawingPanel.getCanvas();
                    File file = new File("saved/autosave.png");
                    ImageIO.write(canvas, "png", file);
                    System.out.println("Autosaved drawing.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
