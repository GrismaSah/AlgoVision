package ui;

import util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The left navigation rail. It is intentionally "dumb": when a button is
 * clicked it simply reports the screen name back to whoever is listening
 * (via the onSelect callback). It does NOT know how screens are switched.
 */
public class SidebarPanel extends JPanel {

    // A callback supplied by MainFrame. We hand it the clicked screen's name.
    private final Consumer<String> onSelect;

    // Keep references to every button so we can manage the "selected" state.
    private final List<JButton> navButtons = new ArrayList<>();
    private JButton selectedButton;

    public SidebarPanel(Consumer<String> onSelect) {
        this.onSelect = onSelect;

        setBackground(ColorPalette.SIDEBAR);
        setPreferredSize(new Dimension(220, 0));               // fixed width, height stretches
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));      // stack children top -> bottom
        setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16)); // inner padding

        addBrand();
        add(Box.createVerticalStrut(30));                      // fixed 30px gap

        addNavButton("Dashboard");
        addNavButton("Sorting");
        addNavButton("Searching");
        addNavButton("Graph");

        add(Box.createVerticalGlue());                         // spring: pushes the rest down

        addNavButton("Settings");
        addNavButton("About");
    }

    /** The logo + tagline block at the top of the sidebar. */
    private void addBrand() {
        JLabel logo = new JLabel("AlgoVision");         // \u25C6 is a diamond ◆
        logo.setForeground(ColorPalette.ACCENT);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(logo);

        add(Box.createVerticalStrut(4));

        JLabel tagline = new JLabel("Visualize \u2022 Learn"); // \u2022 is a bullet •
        tagline.setForeground(ColorPalette.TEXT_SECONDARY);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(tagline);
    }

    /** Build one navigation button and wire up hover + click behaviour. */
    private void addNavButton(String name) {
        JButton button = new JButton(name);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44)); // full width, 44px tall
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setForeground(ColorPalette.TEXT_SECONDARY);
        button.setBackground(ColorPalette.SIDEBAR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setFocusPainted(false);     // no ugly focus rectangle
        button.setBorderPainted(false);    // we don't want the default 3D border
        button.setContentAreaFilled(true); // but DO let us paint a background colour
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect: lighten on mouse-enter, restore on mouse-exit
        // (but never override the currently-selected button).
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) button.setBackground(ColorPalette.SURFACE);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (button != selectedButton) button.setBackground(ColorPalette.SIDEBAR);
            }
        });

        // Click: mark selected, then report the name back to MainFrame.
        button.addActionListener(e -> {
            select(button);
            onSelect.accept(name);
        });

        navButtons.add(button);
        add(button);
        add(Box.createVerticalStrut(6));   // small gap between buttons
    }

    /** Visually mark one button as selected and reset the previous one. */
    private void select(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBackground(ColorPalette.SIDEBAR);
            selectedButton.setForeground(ColorPalette.TEXT_SECONDARY);
        }
        selectedButton = button;
        button.setBackground(ColorPalette.SURFACE);
        button.setForeground(ColorPalette.TEXT_PRIMARY);
    }

    /** Visually highlight a screen's button WITHOUT firing the select callback.
     *  MainFrame calls this from showScreen, so it must not re-trigger navigation. */
    public void markSelected(String name) {
        for (JButton b : navButtons) {
            if (b.getText().equals(name)) {
                select(b);
                return;
            }
        }
    }
}
