import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AutoSaveThread extends Thread {
    private final DrawingPanel drawingPanel;
    private int saveCounter = 1;

    public AutoSaveThread(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void run() {
        System.out.println("AutoSaveThread started."); 
        while (true) {
            try {
                Thread.sleep(20000); // Save every 20 seconds

                synchronized (drawingPanel.getLock()) {
                    BufferedImage canvas = drawingPanel.getCanvas();
                    String fileName = "autosave" + saveCounter + ".png";
                    File file = new File(fileName);
                    ImageIO.write(canvas, "png", file);
                    System.out.println("Autosaved drawing to saved/" + fileName);
                    saveCounter++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
