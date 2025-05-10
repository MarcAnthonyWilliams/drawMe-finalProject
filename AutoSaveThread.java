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
                if(!drawingPanel.isCurrentlyDrawing()) {
                    synchronized (drawingPanel.getLock()) {
                        BufferedImage canvas = drawingPanel.getCanvas();
                        String fileName = "saved/autosave" + saveCounter + ".png";
                        new File("saved").mkdirs(); // ensures the folder exists
                        File file = new File(fileName);
                        ImageIO.write(canvas, "png", file);
                        drawingPanel.updateStatus("Autosaved drawing to" + fileName);
                        System.out.println("Autosaved drawing to" + fileName);
                        saveCounter++;
                
                    //System.out.println("Autosave lock: " + System.identityHashCode(drawingPanel.getLock()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
