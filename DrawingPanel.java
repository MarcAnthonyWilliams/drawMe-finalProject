import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
//import java.util.Scanner;

import javax.imageio.ImageIO;

public class DrawingPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics2D g2;
    private final Object lock = new Object();
    private Color currentColor = Color.BLACK;
    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 600;
    private int saveCounter = 1;

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
                drawDot(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawDot(e.getX(), e.getY());
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                resizeCanvas(size.width, size.height);
            }
        });        
    }


    private void drawDot(int x, int y) {
        synchronized (lock){
            System.out.println("Drawing...");
            g2.fillOval(x, y, 4, 4); // Small circle to simulate drawing
            repaint();
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
                // Open a save dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Drawing");
    
                // Set default file name
                fileChooser.setSelectedFile(new File("manual_save" + saveCounter + ".png"));
    
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
    
                    // Ensure the file has a .png extension
                    if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                    }
    
                    // Save the canvas to the selected file
                    ImageIO.write(canvas, "png", fileToSave);
                    System.out.println("Drawing saved to: " + fileToSave.getAbsolutePath());
                } else {
                    // If no file is selected, save with default name in the current directory
                    String defaultFileName = "manual_save" + saveCounter + ".png";
                    File defaultFile = new File(defaultFileName);
                    ImageIO.write(canvas, "png", defaultFile);
                    System.out.println("Drawing saved to default file: " + defaultFile.getAbsolutePath());
                }
    
                saveCounter++; // Increment the counter for default naming
            } catch (IOException e) {
                e.printStackTrace();
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
