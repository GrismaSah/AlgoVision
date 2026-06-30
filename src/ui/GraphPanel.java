package ui;

import model.AlgorithmInfo;
import model.AlgorithmStep;
import model.AppState;
import service.GraphService;
import util.ColorPalette;
import util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Graph traversal visualizer: animates BFS / DFS over a fixed graph,
 * coloring the current node, visited nodes and the frontier.
 */
public class GraphPanel extends JPanel {

    private final GraphCanvas canvas = new GraphCanvas();
    private final StepPlayer player = new StepPlayer(220);

    private final JComboBox<String> algoBox = new JComboBox<>(new String[]{"BFS", "DFS"});
    private final JSlider speedSlider = new JSlider(1, 100, 55);
    private final JButton startBtn = UIHelper.primaryButton("\u25B6  Traverse");
    private final JButton stepBtn  = UIHelper.ghostButton("\u23ED  Step");
    private final JButton resetBtn = UIHelper.ghostButton("\u21BA  Reset");

    private final JLabel messageLabel = UIHelper.label("Ready", ColorPalette.TEXT_PRIMARY, Font.PLAIN, 15);
    private final JLabel orderLabel = UIHelper.label("Visit order:  \u2014", ColorPalette.TEXT_SECONDARY, Font.PLAIN, 14);
    private final StringBuilder order = new StringBuilder();

    private final JLabel defLabel = new JLabel();
    private final JLabel bestLabel = new JLabel(), spaceLabel = new JLabel();

    public GraphPanel() {
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        player.configure(this::applyStep, this::onFinish);

        add(buildCenter(), BorderLayout.CENTER);
        add(buildInfoPanel(), BorderLayout.EAST);
        add(buildControls(), BorderLayout.SOUTH);

        wireActions();
        updateInfo();
        reset();
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        RoundedPanel card = new RoundedPanel(16, ColorPalette.SURFACE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(canvas, BorderLayout.CENTER);
        JPanel south = new JPanel(new GridLayout(2, 1));
        south.setOpaque(false);
        south.add(messageLabel);
        south.add(orderLabel);
        center.add(card, BorderLayout.CENTER);
        center.add(south, BorderLayout.SOUTH);
        return center;
    }

    private JComponent buildControls() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        row.setOpaque(false);
        algoBox.setBackground(ColorPalette.SURFACE_LIGHT);
        algoBox.setForeground(ColorPalette.TEXT_PRIMARY);
        algoBox.setFont(UIHelper.font(Font.BOLD, 13));
        algoBox.setFocusable(false);
        row.add(algoBox);
        JPanel sp = new JPanel();
        sp.setOpaque(false);
        sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));
        JLabel l = UIHelper.label("Speed", ColorPalette.TEXT_SECONDARY, Font.PLAIN, 11);
        l.setAlignmentX(CENTER_ALIGNMENT);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(140, 24));
        sp.add(l); sp.add(speedSlider);
        row.add(sp);
        row.add(startBtn);
        row.add(stepBtn);
        row.add(resetBtn);
        return row;
    }

    private JComponent buildInfoPanel() {
        RoundedPanel card = new RoundedPanel(16, ColorPalette.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(320, 0));

        JLabel heading = UIHelper.label("Algorithm", ColorPalette.ACCENT, Font.BOLD, 13);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        defLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        defLabel.setFont(UIHelper.font(Font.PLAIN, 13));
        defLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(defLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(complexityRow("Time", bestLabel));
        card.add(complexityRow("Space", spaceLabel));
        card.add(Box.createVerticalStrut(14));

        JPanel legend = new JPanel();
        legend.setOpaque(false);
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setAlignmentX(LEFT_ALIGNMENT);
        legend.add(legendRow(ColorPalette.ACCENT, "Current node"));
        legend.add(legendRow(ColorPalette.BAR_SORTED, "Visited"));
        legend.add(legendRow(ColorPalette.BAR_SWAPPING, "Frontier (queued)"));
        card.add(legend);

        card.add(Box.createVerticalGlue());
        return card;
    }

    private JComponent legendRow(Color c, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JPanel dot = new JPanel();
        dot.setBackground(c);
        dot.setPreferredSize(new Dimension(14, 14));
        row.add(dot);
        row.add(UIHelper.label(text, ColorPalette.TEXT_SECONDARY, Font.PLAIN, 13));
        return row;
    }

    private JComponent complexityRow(String name, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel n = UIHelper.label(name, ColorPalette.TEXT_SECONDARY, Font.PLAIN, 13);
        valueLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        valueLabel.setFont(UIHelper.mono(13));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(n, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private void wireActions() {
        algoBox.addActionListener(e -> { updateInfo(); reset(); });
        speedSlider.addChangeListener(e -> player.setDelay(420 - speedSlider.getValue() * 4));
        startBtn.addActionListener(e -> start());
        stepBtn.addActionListener(e -> player.step());
        resetBtn.addActionListener(e -> reset());
    }

    private void reset() {
        player.reset();
        canvas.reset();
        order.setLength(0);
        orderLabel.setText("Visit order:  \u2014");
        messageLabel.setText("Ready");
    }

    private void start() {
        canvas.reset();
        order.setLength(0);
        List<AlgorithmStep> steps = GraphService.run((String) algoBox.getSelectedItem(), 0);
        player.load(steps);
        player.setDelay(420 - speedSlider.getValue() * 4);
        player.play();
    }

    private void applyStep(AlgorithmStep s) {
        switch (s.type) {
            case VISIT_NODE:
                canvas.setCurrent(s.a);
                if (order.length() > 0) order.append("  \u2192  ");
                order.append(s.a);
                orderLabel.setText("Visit order:  " + order);
                break;
            case FRONTIER:
                canvas.addFrontier(s.a);
                break;
            default: break;
        }
        if (s.message != null) messageLabel.setText(s.message);
    }

    private void onFinish() {
        canvas.finish();
        AppState.lastAlgorithm = (String) algoBox.getSelectedItem();
    }

    private void updateInfo() {
        AlgorithmInfo info = AlgorithmInfo.of((String) algoBox.getSelectedItem());
        defLabel.setText("<html><body style='width:260px'>" + info.definition + "</body></html>");
        bestLabel.setText(info.timeAvg);
        spaceLabel.setText(info.space);
    }
}
