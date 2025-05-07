
import javax.swing.*;

import components.RoundedButton;

import java.awt.*;
import java.awt.image.BufferedImage;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.image.BufferedImage;


public class appFrame extends JFrame{
    private DrawingPanel drawingPanel;
    private JLabel label;
    //BufferedImage image; image wont be in app frame. This is just surrounding area.

    public appFrame(){
        setLayout(new BorderLayout());
        setTitle("DrawMe");
        label = new JLabel("Welcome to DrawMe!");
        drawingPanel = new DrawingPanel();

        JLabel title = new JLabel("Welcome to DrawMe!", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        // JButton saveButton = new JButton("Save Drawing");
        // JButton clearButton = new JButton("Clear Drawing");
        // JButton loadButton = new JButton("Load Drawing");

        // JButton colorButton = new JButton();
        // colorButton.setToolTipText("Pick Color"); // Tooltip text

        RoundedButton saveButton = new RoundedButton("Save Drawing");
        RoundedButton clearButton = new RoundedButton("Clear Drawing");
        RoundedButton loadButton = new RoundedButton("Load Drawing");

        RoundedButton colorButton = new RoundedButton("Pick Color");
        colorButton.setToolTipText("Pick Color"); // Tooltip text

        // Create a dynamic icon for the color button
        int iconSize = 32; // Size of the icon
        BufferedImage colorIconImage = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = colorIconImage.createGraphics();
        g2d.setColor(Color.BLACK); // Default color
        g2d.fillRect(0, 0, iconSize, iconSize);
        g2d.dispose();
        colorButton.setIcon(new ImageIcon(colorIconImage));

        // Add an action listener to the color button
        colorButton.addActionListener(e -> {
            // Open a color picker dialog

            Color selectedColor = drawingPanel.getCurrentColor();
            if (selectedColor != null) {
                // Update the icon with the selected color
                Graphics2D g = colorIconImage.createGraphics();
                g.setColor(selectedColor);
                g.fillRect(0, 0, iconSize, iconSize);
                g.dispose();
                colorButton.repaint(); // Refresh the button to show the updated icon
            }
        });
        


        saveButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveButton.setVerticalTextPosition(SwingConstants.CENTER);
        

        ImageIcon saveIcon = new ImageIcon("icons/save.jpg");
        Image scaledSaveImage = saveIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); // Resize to 32x32
        saveButton.setIcon(new ImageIcon(scaledSaveImage));
        saveButton.setToolTipText("Save Drawing"); // Set tooltip text
        saveButton.setText(null); // Remove button text

        ImageIcon clearIcon = new ImageIcon("icons/clear.png");
        Image scaledClearImage = clearIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); // Resize to 32x32
        clearButton.setIcon(new ImageIcon(scaledClearImage));
        clearButton.setToolTipText("Clear Drawing"); // Set tooltip text
        clearButton.setText(null); // Remove button text


        //loadButton.setIcon(new ImageIcon("load.png"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(colorButton);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS)); // Stack vertically
        northPanel.add(title);
        northPanel.add(buttonPanel);

        add(northPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        
        //setSize(800, 600);
        pack(); // Automatically size the frame to fit its contents
        setLocationRelativeTo(null); // Center the frame on the screen

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Need to create functions for the buttons
        saveButton.addActionListener(e -> drawingPanel.saveDrawing());
        clearButton.addActionListener(e -> drawingPanel.clearCanvas());
        loadButton.addActionListener(e -> drawingPanel.loadDrawing());
        colorButton.addActionListener(e -> drawingPanel.pickColor());

       AutoSaveThread autoSave = new AutoSaveThread(drawingPanel);
       autoSave.start(); 
        
    }


    public static void main(String[] args){
        new appFrame();
    }
    
}
