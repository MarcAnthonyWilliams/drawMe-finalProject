package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.PLAIN, 16));
        setBackground(new Color(34, 134, 34)); // Sage green
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = 30;

        Color baseColor = getBackground();
        Color hoverColor = baseColor.brighter(); // hover highlight
        Color pressColor = baseColor.darker();

        Color fill = baseColor;

        if (isHovered) {
            fill = hoverColor;
        }

        if (isPressed) {
            fill = pressColor;
        }

        // Scale effect when pressed
        double scale = isPressed ? 0.9 : 1.0;
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;

        g2.translate(x, y);

        // Draw optional glow if hovered
        if (isHovered && !isPressed) {
            g2.setColor(new Color(0, 0, 0, 30)); // subtle outer glow
            g2.fillRoundRect(-2, -2, scaledWidth + 4, scaledHeight + 4, arc + 6, arc + 6);
        }

        // Fill button background
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, scaledWidth, scaledHeight, arc, arc);

        g2.dispose();

        // Now draw the text
        super.paintComponent(g);
    }

    @Override
    public void paintBorder(Graphics g) {
        // Optional: faint border on hover
        if (isHovered) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            g2.dispose();
        }
    }
}
