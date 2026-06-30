package util;

import java.awt.Color;

/**
 * Central color definitions for the entire AlgoVision app.
 * Also provides the swappable bar color "schemes" used by the visualizer.
 */
public final class ColorPalette {

    private ColorPalette() {}

    // ----- Background layers -----
    public static final Color BACKGROUND     = new Color(0x1E1E2E);
    public static final Color SURFACE        = new Color(0x27293D);
    public static final Color SURFACE_LIGHT  = new Color(0x313450);
    public static final Color SIDEBAR        = new Color(0x181825);
    public static final Color BORDER         = new Color(0x3A3D5C);

    // ----- Text -----
    public static final Color TEXT_PRIMARY   = new Color(0xECECEC);
    public static final Color TEXT_SECONDARY = new Color(0x9399B2);

    // ----- Brand accent -----
    public static final Color ACCENT         = new Color(0x7C5CFF);
    public static final Color ACCENT_HOVER   = new Color(0x9277FF);

    // ----- Default bar states (Classic scheme) -----
    public static final Color BAR_DEFAULT    = new Color(0x4C8BF5);
    public static final Color BAR_COMPARING  = new Color(0xF54C4C);
    public static final Color BAR_SWAPPING   = new Color(0xF5C84C);
    public static final Color BAR_SORTED     = new Color(0x4CF58A);
    public static final Color BAR_PIVOT      = new Color(0xFF8C42);

    /**
     * Returns the 5 colors for a bar scheme, in order:
     * [default, comparing, swapping, sorted, pivot].
     */
    public static Color[] schemeColors(int scheme) {
        switch (scheme) {
            case 1: // Ocean
                return new Color[]{
                    new Color(0x2EC5CE), new Color(0xFF6B6B), new Color(0xFFD93D),
                    new Color(0x4CF58A), new Color(0xA78BFA)};
            case 2: // Sunset
                return new Color[]{
                    new Color(0xFF7E5F), new Color(0xE63946), new Color(0xFFD166),
                    new Color(0x06D6A0), new Color(0x8338EC)};
            default: // Classic
                return new Color[]{
                    BAR_DEFAULT, BAR_COMPARING, BAR_SWAPPING, BAR_SORTED, BAR_PIVOT};
        }
    }
}
