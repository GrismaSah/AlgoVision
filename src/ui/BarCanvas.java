package ui;

import model.AppState;
import util.ColorPalette;
import util.UIHelper;

import javax.swing.JComponent;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Draws an int array as colored vertical bars using Java2D. Different states
 * (comparing, swapping, sorted, pivot, probe, in/out of search range) are
 * shown with different colors read live from the current scheme.
 */
public class BarCanvas extends JComponent {

    private int[] values = new int[0];
    private final Set<Integer> sorted = new HashSet<>();
    private int compareA = -1, compareB = -1;
    private int swapA = -1, swapB = -1;
    private int pivot = -1, probe = -1;
    private int lo = -1, hi = -1;   // active search window (for binary search dimming)

    public BarCanvas() {
        setBackground(ColorPalette.BACKGROUND);
    }

    /** Point the canvas at an array (held by reference, so in-place edits show up). */
    public void setValues(int[] values) {
        this.values = values;
        reset();
    }

    public void reset() {
        sorted.clear();
        clearTransient();
        lo = hi = -1;
        repaint();
    }

    /** Clear per-step highlights but keep the persistent "sorted" marks and range. */
    public void clearTransient() {
        compareA = compareB = swapA = swapB = pivot = probe = -1;
    }

    public void setCompare(int a, int b) { compareA = a; compareB = b; repaint(); }
    public void setSwap(int a, int b)    { swapA = a; swapB = b; repaint(); }
    public void setPivot(int i)          { pivot = i; repaint(); }
    public void setProbe(int i)          { probe = i; repaint(); }
    public void setRange(int lo, int hi) { this.lo = lo; this.hi = hi; repaint(); }
    public void addSorted(int i)         { sorted.add(i); repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g2.setColor(ColorPalette.BACKGROUND);
        g2.fillRect(0, 0, w, h);

        int n = values.length;
        if (n == 0) return;

        int pad = 16;
        int topRoom = 22;                       // space above bars for value labels
        double slot = (double) (w - pad * 2) / n;
        int barW = Math.max(2, (int) (slot * 0.82));

        int maxVal = 1;
        for (int v : values) maxVal = Math.max(maxVal, v);

        Color[] scheme = ColorPalette.schemeColors(AppState.scheme);
        boolean showLabels = n <= 30;
        if (showLabels) g2.setFont(UIHelper.font(Font.PLAIN, 11));

        for (int i = 0; i < n; i++) {
            double x = pad + i * slot + (slot - barW) / 2.0;
            int barH = (int) ((double) values[i] / maxVal * (h - pad * 2 - topRoom));
            int y = h - pad - barH;

            g2.setColor(colorFor(i, scheme));
            g2.fillRoundRect((int) x, y, barW, barH, 6, 6);

            if (showLabels) {
                g2.setColor(ColorPalette.TEXT_SECONDARY);
                String s = String.valueOf(values[i]);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (int) (x + barW / 2.0 - fm.stringWidth(s) / 2.0);
                g2.drawString(s, tx, y - 5);
            }
        }
    }

    private Color colorFor(int i, Color[] scheme) {
        Color def = scheme[0], cmp = scheme[1], swp = scheme[2], srt = scheme[3], piv = scheme[4];
        if (sorted.contains(i)) return srt;
        if (i == swapA || i == swapB) return swp;
        if (i == probe) return cmp;
        if (i == compareA || i == compareB) return cmp;
        if (i == pivot) return piv;
        if (lo >= 0 && hi >= 0 && (i < lo || i > hi)) {
            return new Color(def.getRed(), def.getGreen(), def.getBlue(), 55); // dimmed out of range
        }
        return def;
    }
}
