package flight.ui.components;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A collection of reusable styled components that all share the Theme.
 *
 * Having them in one place means you only change Theme.java to restyle
 * the whole app — this is the "single source of truth" pattern.
 */
public final class Components {

    private Components() {}

    // ── StyledButton ─────────────────────────────────────────────────────────

    /**
     * A custom-painted rounded button with hover and press animations.
     * Extends JButton and overrides paintComponent() to draw our own look.
     */
    public static class StyledButton extends JButton {

        private final Color baseColor;
        private final Color textColor;
        private float       hoverAlpha = 0f;   // 0 = normal, 1 = fully hovered

        public StyledButton(String text, Color base, Color txt) {
            super(text);
            this.baseColor = base;
            this.textColor = txt;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(Theme.FONT_BODY_BOLD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(180, 38));

            // Hover animation using a Timer (fires every 16ms ≈ 60fps)
            addMouseListener(new MouseAdapter() {
                Timer t;
                @Override public void mouseEntered(MouseEvent e) {
                    startAnim(true);
                }
                @Override public void mouseExited(MouseEvent e) {
                    startAnim(false);
                }
                void startAnim(boolean in) {
                    if (t != null) t.stop();
                    t = new Timer(16, ev -> {
                        hoverAlpha += in ? 0.1f : -0.1f;
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

            // Background — blend between base and a lighter hover color
            Color bg = Theme.blend(baseColor,
                       Theme.blend(baseColor, Theme.ACCENT_GLOW, 0.3f), hoverAlpha);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.CORNER_RADIUS, Theme.CORNER_RADIUS);

            // Border glow on hover
            if (hoverAlpha > 0.1f) {
                g2.setColor(new Color(Theme.ACCENT_GLOW.getRed(),
                        Theme.ACCENT_GLOW.getGreen(),
                        Theme.ACCENT_GLOW.getBlue(),
                        (int)(hoverAlpha * 120)));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2,
                                 Theme.CORNER_RADIUS, Theme.CORNER_RADIUS);
            }

            // Text
            g2.setFont(getFont());
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }
    }

    /** Primary amber button */
    public static StyledButton primaryButton(String text) {
        return new StyledButton(text, Theme.ACCENT, Theme.BG_DEEP);
    }

    /** Secondary ghost button */
    public static StyledButton secondaryButton(String text) {
        return new StyledButton(text, Theme.BG_CARD, Theme.TEXT_PRIMARY);
    }

    // ── CardPanel ────────────────────────────────────────────────────────────

    /**
     * A JPanel with a rounded rectangle background, optional title,
     * and optional accent border on the top edge.
     */
    public static class CardPanel extends JPanel {
        private final String title;
        private final boolean accentTop;

        public CardPanel(String title, boolean accentTop) {
            this.title     = title;
            this.accentTop = accentTop;
            setOpaque(false);
            setLayout(new BorderLayout());
        }

        public CardPanel(String title) { this(title, true); }
        public CardPanel()             { this("", false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // Card background
            g2.setColor(Theme.BG_CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                             Theme.CORNER_RADIUS * 2, Theme.CORNER_RADIUS * 2);

            // Subtle border
            g2.setColor(Theme.BORDER);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1,
                             Theme.CORNER_RADIUS * 2, Theme.CORNER_RADIUS * 2);

            // Amber accent line at top
            if (accentTop) {
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(Theme.CORNER_RADIUS * 2, 1,
                            getWidth() - Theme.CORNER_RADIUS * 2, 1);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Section Header ───────────────────────────────────────────────────────

    public static JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.ACCENT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    public static JLabel bodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        return lbl;
    }

    public static JLabel codeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_MONO_MED);
        lbl.setForeground(Theme.ACCENT);
        return lbl;
    }

    // ── StyledComboBox ───────────────────────────────────────────────────────

    /**
     * A dark-themed JComboBox that matches our palette.
     * We override the UI renderer to paint our own colors.
     */
    public static <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setBackground(Theme.BG_CARD);
        combo.setForeground(Theme.TEXT_PRIMARY);
        combo.setFont(Theme.FONT_MONO_MED);
        combo.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        combo.setPreferredSize(new Dimension(160, 36));

        // Custom renderer so each item has proper dark background
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                setBackground(selected ? Theme.ACCENT_DIM : Theme.BG_CARD);
                setForeground(selected ? Theme.BG_DEEP : Theme.TEXT_PRIMARY);
                setFont(Theme.FONT_MONO_MED);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return combo;
    }

    // ── StyledSpinner ────────────────────────────────────────────────────────

    public static JSpinner styledSpinner(int min, int max, int initial) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(initial, min, max, 1));
        spinner.setBackground(Theme.BG_CARD);
        spinner.setForeground(Theme.TEXT_PRIMARY);
        spinner.setFont(Theme.FONT_MONO_MED);
        spinner.setPreferredSize(new Dimension(80, 36));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            de.getTextField().setBackground(Theme.BG_CARD);
            de.getTextField().setForeground(Theme.TEXT_PRIMARY);
            de.getTextField().setFont(Theme.FONT_MONO_MED);
            de.getTextField().setCaretColor(Theme.ACCENT);
            de.getTextField().setBorder(new EmptyBorder(2, 8, 2, 4));
        }
        return spinner;
    }

    // ── StyledTextField ──────────────────────────────────────────────────────

    public static JTextField styledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(Theme.BG_CARD);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setFont(Theme.FONT_MONO_MED);
        tf.setCaretColor(Theme.ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    // ── ResultArea ───────────────────────────────────────────────────────────

    /**
     * A JTextPane that always reports a tall minimum viewport size.
     *
     * WHY THIS IS NEEDED:
     *   JTextPane with HTML content returns a very small getPreferredSize()
     *   until the content is actually rendered. This causes JScrollPane to
     *   collapse to roughly 1 row even when placed in BorderLayout.CENTER.
     *
     *   Overriding getPreferredScrollableViewportSize() tells the JScrollPane
     *   "always reserve at least this much vertical space" — so the result
     *   area is always tall and fully visible regardless of content length.
     */
    public static JTextPane styledResultArea() {
        JTextPane tp = new JTextPane() {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                // Always reserve at least 320 px of height in the scroll pane
                return new Dimension(
                        super.getPreferredScrollableViewportSize().width,
                        320
                );
            }
            @Override
            public boolean getScrollableTracksViewportWidth() {
                // Wrap text to viewport width — no horizontal scroll bar
                return true;
            }
        };
        tp.setBackground(Theme.BG_DEEP);
        tp.setForeground(Theme.TEXT_PRIMARY);
        tp.setFont(Theme.FONT_MONO_SMALL);
        tp.setEditable(false);
        tp.setBorder(new EmptyBorder(Theme.PAD_MD, Theme.PAD_MD, Theme.PAD_MD, Theme.PAD_MD));
        return tp;
    }

    // ── Divider ──────────────────────────────────────────────────────────────

    public static JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        sep.setBackground(Theme.BG_PANEL);
        return sep;
    }
}
