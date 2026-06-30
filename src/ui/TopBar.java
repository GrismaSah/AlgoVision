package ui;

import model.AppState;
import util.ColorPalette;

import javax.swing.*;
import java.awt.*;

/**
 * The bar across the top of the content area. Shows the current screen name on
 * the left and a working color-scheme switcher on the right.
 * (Uses plain text instead of emoji so it renders on every system.)
 */
public class TopBar extends JPanel {

    private static final String[] SCHEMES = {"Classic", "Ocean", "Sunset"};
    private final JLabel screenLabel;
    private final JButton themeToggle;

    public TopBar() {
        setBackground(ColorPalette.SURFACE);
        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        screenLabel = new JLabel("Dashboard");
        screenLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        screenLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(screenLabel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 14));
        right.setOpaque(false);

        themeToggle = new JButton("Theme: " + SCHEMES[AppState.scheme]);
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setForeground(ColorPalette.ACCENT_HOVER);
        themeToggle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        themeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggle.setToolTipText("Switch the bar color scheme");
        themeToggle.addActionListener(e -> cycleScheme());
        right.add(themeToggle);

        add(right, BorderLayout.EAST);
    }

    /** Cycle Classic -> Ocean -> Sunset and repaint so the change shows at once. */
    private void cycleScheme() {
        AppState.scheme = (AppState.scheme + 1) % SCHEMES.length;
        themeToggle.setText("Theme: " + SCHEMES[AppState.scheme]);
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.repaint();   // BarCanvas reads AppState.scheme live on repaint
    }

    public void setScreen(String name) {
        screenLabel.setText(name);
    }
}