package ui;

import util.ColorPalette;

import javax.swing.*;
import java.awt.*;

/** Static information about the project, for the resume/portfolio story. */
public class AboutPanel extends JPanel {

    public AboutPanel() {
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(ColorPalette.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        card.add(line("AlgoVision", Font.BOLD, 30, ColorPalette.TEXT_PRIMARY));
        card.add(Box.createVerticalStrut(6));
        card.add(line("Visualize \u2022 Learn \u2022 Understand", Font.PLAIN, 15, ColorPalette.ACCENT_HOVER));
        card.add(Box.createVerticalStrut(20));
        card.add(line("A modern Java Swing application that animates how core", Font.PLAIN, 14, ColorPalette.TEXT_SECONDARY));
        card.add(line("data structures and algorithms work, step by step.", Font.PLAIN, 14, ColorPalette.TEXT_SECONDARY));
        card.add(Box.createVerticalStrut(20));
        card.add(line("Version   1.0", Font.PLAIN, 14, ColorPalette.TEXT_PRIMARY));
        card.add(line("Developer  Your Name", Font.PLAIN, 14, ColorPalette.TEXT_PRIMARY));
        card.add(line("GitHub     github.com/yourusername/AlgoVision", Font.PLAIN, 14, ColorPalette.TEXT_PRIMARY));
        card.add(line("Portfolio  yourportfolio.com", Font.PLAIN, 14, ColorPalette.TEXT_PRIMARY));
        card.add(Box.createVerticalStrut(20));
        card.add(line("Built with Java 17, Swing & Java2D \u2014 no external libraries.", Font.ITALIC, 13, ColorPalette.TEXT_SECONDARY));

        add(card);
    }

    private JLabel line(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", style, size));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}
