package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Small factory helpers for consistent fonts and modern flat buttons.
 */
public final class UIHelper {

    private UIHelper() {}

    public static final String FAMILY = "Segoe UI";

    public static Font font(int style, int size) { return new Font(FAMILY, style, size); }
    public static Font mono(int size) { return new Font("Consolas", Font.PLAIN, size); }

    /**
     * Formats a duration in milliseconds for display. Algorithm compute times are
     * often sub-millisecond, so we keep extra precision for small values and fall
     * back to an em dash when there's nothing to show.
     */
    public static String fmtMs(double ms) {
        if (ms <= 0)    return "\u2014";
        if (ms < 1)     return String.format("%.3f ms", ms);
        if (ms < 100)   return String.format("%.2f ms", ms);
        return String.format("%.0f ms", ms);
    }

    public static JLabel label(String text, Color color, int style, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(font(style, size));
        return l;
    }

    /** A filled accent button with a hover effect. */
    public static JButton primaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(ColorPalette.ACCENT);
        b.setForeground(Color.WHITE);
        hover(b, ColorPalette.ACCENT, ColorPalette.ACCENT_HOVER);
        return b;
    }

    /** A subtle outlined/ghost button with a hover effect. */
    public static JButton ghostButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(ColorPalette.SURFACE_LIGHT);
        b.setForeground(ColorPalette.TEXT_PRIMARY);
        hover(b, ColorPalette.SURFACE_LIGHT, ColorPalette.BORDER);
        return b;
    }

    private static JButton baseButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setFont(font(Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void hover(JButton b, Color normal, Color over) {
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(over); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(normal); }
        });
    }
}
