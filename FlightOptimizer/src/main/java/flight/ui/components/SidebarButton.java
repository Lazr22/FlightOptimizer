package flight.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Navigation button for the left sidebar.
 *
 * Has two states: selected (highlighted amber) and normal.
 * We override paintComponent to draw a custom look.
 */
public class SidebarButton extends JToggleButton {

    private final String icon;
    private float hoverAlpha = 0f;

    public SidebarButton(String icon, String label) {
        super(label);
        this.icon = icon;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(Theme.FONT_BODY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(248, 44));

        addMouseListener(new MouseAdapter() {
            Timer t;
            @Override public void mouseEntered(MouseEvent e) { anim(true);  }
            @Override public void mouseExited (MouseEvent e) { anim(false); }
            void anim(boolean in) {
                if (t != null) t.stop();
                t = new Timer(16, ev -> {
                    hoverAlpha += in ? 0.12f : -0.12f;
                    hoverAlpha = Math.max(0, Math.min(1, hoverAlpha));
                    repaint();
                    if ((in && hoverAlpha >= 1) || (!in && hoverAlpha <= 0)) t.stop();
                });
                t.start();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        boolean selected = isSelected();
        int w = getWidth(), h = getHeight();

        if (selected) {
            // Selected: amber background pill
            g2.setColor(new Color(0xF0A500, false)); // solid amber
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.fillRoundRect(4, 2, w - 8, h - 4, 10, 10);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Left accent bar
            g2.setColor(Theme.ACCENT);
            g2.fillRoundRect(0, 6, 3, h - 12, 3, 3);
        } else if (hoverAlpha > 0) {
            g2.setColor(Theme.BG_HOVER);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hoverAlpha * 0.8f));
            g2.fillRoundRect(4, 2, w - 8, h - 4, 10, 10);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Icon
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        g2.setColor(selected ? Theme.ACCENT : Theme.TEXT_SECONDARY);
        g2.drawString(icon, 14, h / 2 + 6);

        // Label
        g2.setFont(selected ? Theme.FONT_BODY_BOLD : Theme.FONT_BODY);
        g2.setColor(selected ? Theme.ACCENT : Theme.TEXT_SECONDARY);
        g2.drawString(getText(), 42, h / 2 + 5);

        g2.dispose();
    }
}
