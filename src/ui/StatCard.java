package ui;

import util.ColorPalette;
import util.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * A rounded card showing a small caption and a large value, e.g. "Comparisons / 128".
 */
public class StatCard extends RoundedPanel {

    private final JLabel valueLabel;

    public StatCard(String caption) {
        super(14, ColorPalette.SURFACE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel cap = UIHelper.label(caption.toUpperCase(), ColorPalette.TEXT_SECONDARY, Font.BOLD, 11);
        cap.setAlignmentX(LEFT_ALIGNMENT);

        valueLabel = UIHelper.label("0", ColorPalette.TEXT_PRIMARY, Font.BOLD, 24);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        add(cap);
        add(Box.createVerticalStrut(6));
        add(valueLabel);
    }

    public void setValue(String value) { valueLabel.setText(value); }
}
