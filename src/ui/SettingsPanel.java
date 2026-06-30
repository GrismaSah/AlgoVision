package ui;

import model.AppState;
import util.ColorPalette;

import javax.swing.*;
import java.awt.*;

/**
 * Working settings screen. Three real controls that write to {@link AppState}:
 *
 *  - Bar Color Scheme  -> AppState.scheme   (takes effect immediately; BarCanvas
 *                                            reads the scheme live on every repaint)
 *  - Default Array Size -> AppState.defaultSize  (applied when a panel is next built)
 *  - Default Speed      -> AppState.defaultDelay (applied when a panel is next built)
 *
 * The live preview row repaints whenever the scheme changes so the user sees
 * exactly which colours their next animation will use.
 */
public class SettingsPanel extends JPanel {

    private static final String[] SCHEME_NAMES = {"Classic", "Ocean", "Sunset"};
    private static final String[] SWATCH_CAPTIONS = {"Bar", "Compare", "Swap", "Sorted", "Pivot"};

    private final JPanel previewRow = new JPanel();

    public SettingsPanel() {
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 48, 40, 48));

        JLabel title = new JLabel("Settings");
        title.setForeground(ColorPalette.TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Preferences are saved for this session.");
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        header.add(title);
        header.add(subtitle);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));

        body.add(schemeSection());
        body.add(Box.createVerticalStrut(28));
        body.add(sizeSection());
        body.add(Box.createVerticalStrut(28));
        body.add(speedSection());

        add(body, BorderLayout.CENTER);
        rebuildPreview();
    }

    // ---- Bar colour scheme (immediate effect) --------------------------------

    private JComponent schemeSection() {
        JPanel p = sectionShell("Bar Color Scheme",
                "Applied immediately to the next animation.");

        JPanel choices = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        choices.setOpaque(false);
        choices.setAlignmentX(LEFT_ALIGNMENT);

        ButtonGroup bg = new ButtonGroup();
        for (int i = 0; i < SCHEME_NAMES.length; i++) {
            final int idx = i;
            JRadioButton rb = new JRadioButton(SCHEME_NAMES[i], AppState.scheme == i);
            rb.setOpaque(false);
            rb.setForeground(ColorPalette.TEXT_PRIMARY);
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            rb.addActionListener(e -> {
                AppState.scheme = idx;
                rebuildPreview();
            });
            bg.add(rb);
            choices.add(rb);
        }
        p.add(choices);
        p.add(Box.createVerticalStrut(14));

        previewRow.setOpaque(false);
        previewRow.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 0));
        previewRow.setAlignmentX(LEFT_ALIGNMENT);
        p.add(previewRow);
        return p;
    }

    private void rebuildPreview() {
        previewRow.removeAll();
        Color[] colors = ColorPalette.schemeColors(AppState.scheme);
        for (int i = 0; i < colors.length; i++) {
            previewRow.add(swatch(colors[i], SWATCH_CAPTIONS[i]));
        }
        previewRow.revalidate();
        previewRow.repaint();
    }

    private JComponent swatch(Color color, String caption) {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        JPanel chip = new JPanel();
        chip.setBackground(color);
        chip.setPreferredSize(new Dimension(46, 30));
        chip.setMaximumSize(new Dimension(46, 30));
        chip.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER));
        chip.setAlignmentX(CENTER_ALIGNMENT);

        JLabel cap = new JLabel(caption);
        cap.setForeground(ColorPalette.TEXT_SECONDARY);
        cap.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cap.setAlignmentX(CENTER_ALIGNMENT);
        cap.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        col.add(chip);
        col.add(cap);
        return col;
    }

    // ---- Default array size (next session) -----------------------------------

    private JComponent sizeSection() {
        JPanel p = sectionShell("Default Array Size",
                "Applied to new visualizations.");
        JLabel value = new JLabel(String.valueOf(AppState.defaultSize));
        value.setForeground(ColorPalette.ACCENT_HOVER);
        value.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JSlider slider = new JSlider(10, 120, AppState.defaultSize);
        styleSlider(slider);
        slider.addChangeListener(e -> {
            AppState.defaultSize = slider.getValue();
            value.setText(String.valueOf(slider.getValue()));
        });

        p.add(sliderRow(slider, value));
        return p;
    }

    // ---- Default speed (next session) ----------------------------------------

    private JComponent speedSection() {
        JPanel p = sectionShell("Default Animation Speed",
                "Higher is faster. Applied to new visualizations.");
        // Show 1..100 to the user; store as a per-step delay (101 - value) like the panels.
        int shownSpeed = 101 - AppState.defaultDelay;
        shownSpeed = Math.max(1, Math.min(100, shownSpeed));

        JLabel value = new JLabel(shownSpeed + " / 100");
        value.setForeground(ColorPalette.ACCENT_HOVER);
        value.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JSlider slider = new JSlider(1, 100, shownSpeed);
        styleSlider(slider);
        slider.addChangeListener(e -> {
            int v = slider.getValue();
            AppState.defaultDelay = 101 - v;   // higher slider -> smaller delay -> faster
            value.setText(v + " / 100");
        });

        p.add(sliderRow(slider, value));
        return p;
    }

    // ---- Small shared helpers ------------------------------------------------

    private JComponent sliderRow(JSlider slider, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(520, 40));
        row.add(slider, BorderLayout.CENTER);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private void styleSlider(JSlider s) {
        s.setOpaque(false);
        s.setForeground(ColorPalette.ACCENT);
    }

    private JPanel sectionShell(String title, String hint) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(620, Integer.MAX_VALUE));

        JLabel t = new JLabel(title);
        t.setForeground(ColorPalette.ACCENT_HOVER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel h = new JLabel(hint);
        h.setForeground(ColorPalette.TEXT_SECONDARY);
        h.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        h.setAlignmentX(LEFT_ALIGNMENT);
        h.setBorder(BorderFactory.createEmptyBorder(2, 0, 12, 0));

        p.add(t);
        p.add(h);
        return p;
    }
}
