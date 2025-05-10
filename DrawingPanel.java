import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalTime;
//import java.util.Scanner;
import java.util.Stack;

import javax.imageio.ImageIO;

public class DrawingPanel extends JPanel {
    private BufferedImage canvas;
    private JLabel statusLabel;
    private Graphics2D g2;
    private final Object lock = new Object();
    private Color currentColor = Color.BLACK;
    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 600;
    private int saveCounter = 1;
    private volatile boolean isDrawing = false;
    //added for undo
    private final Stack<BufferedImage> undoStack = new Stack<>();


    public DrawingPanel(JLabel statusLabel) {
        this();
        this.statusLabel = statusLabel;
    }

    public DrawingPanel() {
        setBackground(Color.WHITE);
        // Create the drawing canvas
        canvas = new BufferedImage(1600, 600, BufferedImage.TYPE_INT_ARGB);
        g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE); // Start with white background
        g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2.setColor(currentColor); // Set drawing color
        g2.setStroke(new BasicStroke(2));

        // Mouse listeners
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                isDrawing = true;
                synchronized (lock) {
                    drawDot(e.getX(), e.getY());
                }
            }
        
            public void mouseReleased(MouseEvent e) {
                isDrawing = false;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                synchronized (lock) {
                    drawDot(e.getX(), e.getY());
                }
            }
        });
              
    }

    private BufferedImage copyCanvas(BufferedImage original) {
        BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return copy;
    }


    private void drawDot(int x, int y) {

        synchronized (lock){
            undoStack.push(copyCanvas(canvas)); // push current state
            System.out.println("Drawing...");
            g2.fillOval(x, y, 4, 4); // Small circle to simulate drawing
            repaint();
            //Uses same lock as autosave thread
            //System.out.println("Drawing lock: " + System.identityHashCode(lock)); // in draw
        }
    }
    
    private void resizeCanvas(int newWidth, int newHeight) {
        synchronized (lock) {
            // Don't shrink smaller than original size (Avoids content deletion)
            if (newWidth < MIN_WIDTH || newHeight < MIN_HEIGHT)
                return;
    
            if (canvas.getWidth() == newWidth && canvas.getHeight() == newHeight)
                return;
    
            BufferedImage newCanvas = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gNew = newCanvas.createGraphics();
            gNew.setColor(Color.WHITE);
            gNew.fillRect(0, 0, newWidth, newHeight);
            gNew.drawImage(canvas, 0, 0, null);
    
            canvas = newCanvas;
            g2 = canvas.createGraphics();
            g2.setColor(currentColor);
            g2.setStroke(new BasicStroke(2));
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (lock) {
            g.drawImage(canvas, 0, 0, null);
        }
    }

    public void updateStatus(String message) {
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() ->
            statusLabel.setText(message + " at " + LocalTime.now().withNano(0))
        );
        }   
    }

    public boolean isCurrentlyDrawing() {
        return isDrawing;
    }

    // Public access for autosave thread
    public BufferedImage getCanvas() {
        return canvas;
    }

    public Object getLock() {
        return lock;
    }

    // Button actions
    public void saveDrawing() {
        synchronized (lock) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Drawing");
    
                fileChooser.setSelectedFile(new File("manual_save" + saveCounter + ".png"));
    
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
    
                    // Ensure the file has .png extension
                    if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                    }
    
                    ImageIO.write(canvas, "png", fileToSave);
                    System.out.println("Drawing saved to: " + fileToSave.getAbsolutePath());
    
                    if (statusLabel != null) {
                        statusLabel.setText("Saved to: " + fileToSave.getName());
                    }
    
                    saveCounter++;
                } else {
                    System.out.println("Save canceled.");
                    if (statusLabel != null) {
                        statusLabel.setText("Save canceled.");
                    }
                }
    
            } catch (IOException e) {
                e.printStackTrace();
                if (statusLabel != null) {
                    statusLabel.setText("Failed to save drawing.");
                }
            }
        }
    }
    public void undo() {
        synchronized (lock) {
            if (!undoStack.isEmpty()) {
                canvas = undoStack.pop();
                g2 = canvas.createGraphics();
                g2.setColor(currentColor);
                g2.setStroke(new BasicStroke(2));
                repaint();
    
                if (statusLabel != null) {
                    statusLabel.setText("Undo applied.");
                }
            } else {
                if (statusLabel != null) {
                    statusLabel.setText("Nothing to undo.");
                }
            }
        }
    }
    // public void loadDrawing() {
    //     synchronized (lock) {
    //         try {
    //             try (Scanner filename = new Scanner(System.in)) {
    //                 System.out.print("Enter the filename to load: ");

    //                 BufferedImage loaded = ImageIO.read(new File("saved/" + filename.nextLine()));
    //             if (loaded != null) {
    //                 canvas = loaded;
    //                 g2 = canvas.createGraphics();
    //                 g2.setColor(currentColor);
    //                 g2.setStroke(new BasicStroke(2));
    //                 repaint();
    //                 System.out.println("Drawing loaded.");
    //                 }
    //             }
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }
    public void loadDrawing() {
        synchronized (lock) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a drawing to load");
    
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage loaded = ImageIO.read(file);
                    if (loaded != null) {
                        canvas = loaded;
                        g2 = canvas.createGraphics();
                        g2.setColor(currentColor);
                        g2.setStroke(new BasicStroke(2));
                        repaint();
                        System.out.println("Drawing loaded.");
                    } else {
                        System.out.println("Failed to load image.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Load button pressed");
            }
        }
    }    
    public void clearCanvas() {
        synchronized (lock) {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            g2.setColor(currentColor);
            repaint();
            System.out.println("Canvas cleared.");
        }
    }

    public void pickColor() {
        Color chosen = JColorChooser.showDialog(this, "Pick a Color", currentColor);
        if (chosen != null) {
            currentColor = chosen;
            g2.setColor(currentColor);
            System.out.println("Color changed.");
        }
    }

    public Color getCurrentColor() {
        return currentColor;
    }
 
}
