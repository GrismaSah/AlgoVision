package ui;

import util.ColorPalette;

import javax.swing.*;
import java.awt.*;

/**
 * The bar across the top of the content area. Shows the current screen name
 * on the left and status placeholders + a theme button on the right.
 * (Uses plain text instead of emoji so it renders on every system.)
 */
public class TopBar extends JPanel {

    private final JLabel screenLabel;

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
        right.add(makeStatus("Time: --"));
        right.add(makeStatus("Speed: --"));

        JButton themeToggle = new JButton("Theme");
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setForeground(ColorPalette.ACCENT_HOVER);
        themeToggle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        themeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggle.setToolTipText("Theme toggle (full theming is a future step)");
        right.add(themeToggle);

        add(right, BorderLayout.EAST);
    }

    private JLabel makeStatus(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ColorPalette.TEXT_SECONDARY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    public void setScreen(String name) {
        screenLabel.setText(name);
    }
}
