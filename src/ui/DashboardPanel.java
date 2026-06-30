package ui;

import model.AppState;
import util.ColorPalette;
import util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Landing screen: a welcome header, live stat cards from the most recent run,
 * and quick-launch buttons into each visualizer.
 */
public class DashboardPanel extends JPanel {

    private final StatCard sizeCard = new StatCard("Array Size");
    private final StatCard cmpCard  = new StatCard("Comparisons");
    private final StatCard swapCard = new StatCard("Swaps");
    private final StatCard timeCard = new StatCard("Exec Time");
    private final StatCard algoCard = new StatCard("Last Algorithm");

    public DashboardPanel(Consumer<String> onOpen) {
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout(0, 22));
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = UIHelper.label("Welcome to AlgoVision", ColorPalette.TEXT_PRIMARY, Font.BOLD, 30);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UIHelper.label("Watch classic algorithms come to life. Pick a module to begin.",
                ColorPalette.TEXT_SECONDARY, Font.PLAIN, 15);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        // center: stat cards + quick launch
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel statsHeading = UIHelper.label("LAST RUN", ColorPalette.ACCENT, Font.BOLD, 12);
        statsHeading.setAlignmentX(LEFT_ALIGNMENT);
        center.add(statsHeading);
        center.add(Box.createVerticalStrut(10));

        JPanel cards = new JPanel(new GridLayout(1, 5, 14, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        cards.add(sizeCard);
        cards.add(cmpCard);
        cards.add(swapCard);
        cards.add(timeCard);
        cards.add(algoCard);
        center.add(cards);
        center.add(Box.createVerticalStrut(28));

        JLabel launchHeading = UIHelper.label("MODULES", ColorPalette.ACCENT, Font.BOLD, 12);
        launchHeading.setAlignmentX(LEFT_ALIGNMENT);
        center.add(launchHeading);
        center.add(Box.createVerticalStrut(10));

        JPanel launch = new JPanel(new GridLayout(1, 3, 16, 0));
        launch.setOpaque(false);
        launch.setAlignmentX(LEFT_ALIGNMENT);
        launch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        launch.add(moduleCard("Sorting", "6 algorithms, animated bars", "Sorting", onOpen));
        launch.add(moduleCard("Searching", "Linear & binary search", "Searching", onOpen));
        launch.add(moduleCard("Graph", "BFS & DFS traversal", "Graph", onOpen));
        center.add(launch);
        center.add(Box.createVerticalGlue());

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    private JComponent moduleCard(String name, String desc, String screen, Consumer<String> onOpen) {
        RoundedPanel card = new RoundedPanel(16, ColorPalette.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel n = UIHelper.label(name, ColorPalette.TEXT_PRIMARY, Font.BOLD, 20);
        n.setAlignmentX(LEFT_ALIGNMENT);
        JLabel d = UIHelper.label(desc, ColorPalette.TEXT_SECONDARY, Font.PLAIN, 13);
        d.setAlignmentX(LEFT_ALIGNMENT);
        JButton open = UIHelper.primaryButton("Open \u2192");
        open.setAlignmentX(LEFT_ALIGNMENT);
        open.addActionListener(e -> onOpen.accept(screen));

        card.add(n);
        card.add(Box.createVerticalStrut(6));
        card.add(d);
        card.add(Box.createVerticalStrut(16));
        card.add(open);
        return card;
    }

    /** Refresh the stat cards from AppState (called when the dashboard is shown).
     *  Cards relabel themselves to suit the module that last ran, since a graph
     *  traversal has different metrics (nodes/visited) than an array sort. */
    public void refresh() {
        boolean hasData = !AppState.lastModule.equals("\u2014");

        if (AppState.lastModule.equals("Graph")) {
            sizeCard.setCaption("Nodes");
            cmpCard.setCaption("Visited");
            swapCard.setCaption("Edges");
        } else {
            sizeCard.setCaption("Array Size");
            cmpCard.setCaption("Comparisons");
            swapCard.setCaption("Swaps");
        }

        sizeCard.setValue(hasData ? String.valueOf(AppState.lastArraySize) : "\u2014");
        cmpCard.setValue(hasData ? String.valueOf(AppState.lastComparisons) : "\u2014");
        swapCard.setValue(hasData ? String.valueOf(AppState.lastSwaps) : "\u2014");
        timeCard.setValue(UIHelper.fmtMs(AppState.lastExecMs));
        algoCard.setValue(AppState.lastAlgorithm);
    }
}