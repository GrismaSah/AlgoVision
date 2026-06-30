package ui;

import service.GraphService;
import util.ColorPalette;
import util.UIHelper;

import javax.swing.JComponent;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Draws the fixed graph (nodes + edges) and colors nodes by traversal state:
 * current (accent), visited (green) and frontier/discovered (yellow).
 */
public class GraphCanvas extends JComponent {

    private final Set<Integer> visited = new HashSet<>();
    private final Set<Integer> frontier = new HashSet<>();
    private int current = -1;

    public void reset() { visited.clear(); frontier.clear(); current = -1; repaint(); }

    public void setCurrent(int n) {
        if (current >= 0) visited.add(current);
        current = n;
        visited.add(n);
        frontier.remove(n);
        repaint();
    }
    public void addFrontier(int n) { frontier.add(n); repaint(); }
    public void finish() { if (current >= 0) visited.add(current); current = -1; frontier.clear(); repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g2.setColor(ColorPalette.BACKGROUND);
        g2.fillRect(0, 0, w, h);

        int pad = 50;
        int innerW = w - pad * 2, innerH = h - pad * 2;
        int r = 24;

        int[] px = new int[GraphService.NODES];
        int[] py = new int[GraphService.NODES];
        for (int i = 0; i < GraphService.NODES; i++) {
            px[i] = pad + (int) (GraphService.POS[i][0] * innerW);
            py[i] = pad + (int) (GraphService.POS[i][1] * innerH);
        }

        // edges
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(ColorPalette.BORDER);
        for (int[] e : GraphService.EDGES) {
            g2.drawLine(px[e[0]], py[e[0]], px[e[1]], py[e[1]]);
        }

        // nodes
        g2.setFont(UIHelper.font(Font.BOLD, 16));
        for (int i = 0; i < GraphService.NODES; i++) {
            Color c = ColorPalette.SURFACE_LIGHT;
            if (i == current) c = ColorPalette.ACCENT;
            else if (visited.contains(i)) c = ColorPalette.BAR_SORTED;
            else if (frontier.contains(i)) c = ColorPalette.BAR_SWAPPING;

            g2.setColor(c);
            g2.fillOval(px[i] - r, py[i] - r, r * 2, r * 2);
            g2.setColor(ColorPalette.BACKGROUND);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(px[i] - r, py[i] - r, r * 2, r * 2);

            String s = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.WHITE);
            g2.drawString(s, px[i] - fm.stringWidth(s) / 2, py[i] + fm.getAscent() / 2 - 2);
        }
    }
}
