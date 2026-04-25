package flight.ui.components;

import java.awt.*;

/**
 * Centralized color palette and font constants.
 * Change values here to restyle the entire app.
 *
 * Aesthetic: Dark aviation dashboard — deep navy background,
 * amber/gold accents like cockpit instruments.
 */
public final class Theme {

    private Theme() {} // utility class — no instances

    // ── Palette ──────────────────────────────────────────────────────────────

    public static final Color BG_DEEP     = new Color(0x0D1117);  // near-black background
    public static final Color BG_PANEL    = new Color(0x161B22);  // panel surface
    public static final Color BG_CARD     = new Color(0x1C2330);  // card / input surface
    public static final Color BG_HOVER    = new Color(0x21262D);  // hover state

    public static final Color ACCENT      = new Color(0xF0A500);  // amber gold — primary accent
    public static final Color ACCENT_DIM  = new Color(0x9B6B00);  // muted gold
    public static final Color ACCENT_GLOW = new Color(0xFFBF2E);  // bright highlight

    public static final Color GREEN       = new Color(0x3FB950);  // success / found
    public static final Color RED         = new Color(0xF85149);  // error / critical
    public static final Color BLUE        = new Color(0x58A6FF);  // info / links
    public static final Color PURPLE      = new Color(0xBC8CFF);  // MST edges

    public static final Color TEXT_PRIMARY   = new Color(0xE6EDF3);
    public static final Color TEXT_SECONDARY = new Color(0x8B949E);
    public static final Color TEXT_DIM       = new Color(0x484F58);

    public static final Color BORDER      = new Color(0x30363D);
    public static final Color BORDER_ACCENT = new Color(0xF0A500).darker();

    // ── Fonts ────────────────────────────────────────────────────────────────
    // We use built-in fonts that look great on all systems.
    // Monospaced for airport codes (like a departure board),
    // SansSerif for body text.

    public static final Font FONT_MONO_LARGE  = new Font(Font.MONOSPACED, Font.BOLD, 22);
    public static final Font FONT_MONO_MED    = new Font(Font.MONOSPACED, Font.BOLD, 14);
    public static final Font FONT_MONO_SMALL  = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Font FONT_BODY        = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD   = new Font(Font.SANS_SERIF, Font.BOLD,  13);
    public static final Font FONT_LABEL       = new Font(Font.SANS_SERIF, Font.BOLD,  11);
    public static final Font FONT_TITLE       = new Font(Font.SANS_SERIF, Font.BOLD,  18);
    public static final Font FONT_SMALL       = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    // ── Dimensions ───────────────────────────────────────────────────────────

    public static final int  CORNER_RADIUS = 10;
    public static final int  PAD_SM        = 8;
    public static final int  PAD_MD        = 14;
    public static final int  PAD_LG        = 22;

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Mix two colors by a 0.0–1.0 factor */
    public static Color blend(Color a, Color b, float t) {
        int r = (int)(a.getRed()   * (1-t) + b.getRed()   * t);
        int g = (int)(a.getGreen() * (1-t) + b.getGreen() * t);
        int bl= (int)(a.getBlue()  * (1-t) + b.getBlue()  * t);
        return new Color(r, g, bl);
    }
}
