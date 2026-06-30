package ui;

import model.AlgorithmInfo;
import model.AlgorithmStep;
import model.AppState;
import service.SearchingService;
import util.ColorPalette;
import util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Searching visualizer. Keeps the array sorted ascending so binary search works,
 * and animates linear / binary search with probe + range highlighting.
 */
public class SearchingPanel extends JPanel {

    private final Random rng = new Random();
    private int[] data = new int[0];

    private final BarCanvas canvas = new BarCanvas();
    private final StepPlayer player = new StepPlayer(AppState.defaultDelay);

    private final JComboBox<String> algoBox = new JComboBox<>(new String[]{"Linear Search", "Binary Search"});
    private final JSlider sizeSlider  = new JSlider(8, 60, 24);
    private final JSlider speedSlider = new JSlider(1, 100, 70);
    private final JSpinner targetSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 100, 1));
    private final JButton startBtn = UIHelper.primaryButton("\u25B6  Search");
    private final JButton stepBtn  = UIHelper.ghostButton("\u23ED  Step");
    private final JButton resetBtn = UIHelper.ghostButton("\u21BA  Reset");
    private final JButton genBtn   = UIHelper.ghostButton("\u21BB  New Array");

    private final JLabel messageLabel = UIHelper.label("Ready", ColorPalette.TEXT_PRIMARY, Font.PLAIN, 15);
    private final StatCard probeCard = new StatCard("Probes");
    private final StatCard resultCard = new StatCard("Result");
    private int probes = 0;

    private final JLabel defLabel = new JLabel();
    private final JLabel bestLabel = new JLabel(), avgLabel = new JLabel(), worstLabel = new JLabel();

    public SearchingPanel() {
        setBackground(ColorPalette.BACKGROUND);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        player.configure(this::applyStep, this::onFinish);

        add(buildStatStrip(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildInfoPanel(), BorderLayout.EAST);
        add(buildControls(), BorderLayout.SOUTH);

        wireActions();
        updateInfo();
        generate(sizeSlider.getValue());
    }

    private JComponent buildStatStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 2, 14, 0));
        strip.setOpaque(false);
        strip.add(probeCard);
        strip.add(resultCard);
        strip.setPreferredSize(new Dimension(0, 84));
        return strip;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        RoundedPanel card = new RoundedPanel(16, ColorPalette.SURFACE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(canvas, BorderLayout.CENTER);
        center.add(card, BorderLayout.CENTER);
        center.add(messageLabel, BorderLayout.SOUTH);
        return center;
    }

    private JComponent buildControls() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        row1.setOpaque(false);
        algoBox.setBackground(ColorPalette.SURFACE_LIGHT);
        algoBox.setForeground(ColorPalette.TEXT_PRIMARY);
        algoBox.setFont(UIHelper.font(Font.BOLD, 13));
        algoBox.setFocusable(false);
        row1.add(algoBox);
        row1.add(genBtn);
        row1.add(UIHelper.label("Target", ColorPalette.TEXT_SECONDARY, Font.PLAIN, 13));
        targetSpinner.setPreferredSize(new Dimension(70, 30));
        row1.add(targetSpinner);
        row1.add(sliderWithLabel("Size", sizeSlider));
        row1.add(sliderWithLabel("Speed", speedSlider));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        row2.setOpaque(false);
        row2.add(startBtn);
        row2.add(stepBtn);
        row2.add(resetBtn);

        wrap.add(row1);
        wrap.add(row2);
        return wrap;
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
        card.add(complexityRow("Best", bestLabel));
        card.add(complexityRow("Average", avgLabel));
        card.add(complexityRow("Worst", worstLabel));
        card.add(Box.createVerticalStrut(12));

        JLabel tip = new JLabel("<html><body style='width:260px'>Tip: the array is kept sorted so binary search is valid. Out-of-range bars dim as the window shrinks.</body></html>");
        tip.setForeground(ColorPalette.TEXT_SECONDARY);
        tip.setFont(UIHelper.font(Font.ITALIC, 12));
        tip.setAlignmentX(LEFT_ALIGNMENT);
        card.add(tip);

        card.add(Box.createVerticalGlue());
        return card;
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

    private JComponent sliderWithLabel(String name, JSlider slider) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = UIHelper.label(name, ColorPalette.TEXT_SECONDARY, Font.PLAIN, 11);
        l.setAlignmentX(CENTER_ALIGNMENT);
        slider.setOpaque(false);
        slider.setPreferredSize(new Dimension(120, 24));
        p.add(l);
        p.add(slider);
        return p;
    }

    private void wireActions() {
        genBtn.addActionListener(e -> generate(sizeSlider.getValue()));
        algoBox.addActionListener(e -> { updateInfo(); reset(); });
        sizeSlider.addChangeListener(e -> { if (!sizeSlider.getValueIsAdjusting()) generate(sizeSlider.getValue()); });
        speedSlider.addChangeListener(e -> player.setDelay(101 - speedSlider.getValue()));
        startBtn.addActionListener(e -> start());
        stepBtn.addActionListener(e -> player.step());
        resetBtn.addActionListener(e -> reset());
    }

    private void generate(int size) {
        data = new int[size];
        for (int i = 0; i < size; i++) data[i] = 5 + rng.nextInt(96);
        Arrays.sort(data);   // keep sorted so binary search is valid
        canvas.setValues(data);
        reset();
    }

    private void reset() {
        player.reset();
        canvas.reset();
        probes = 0;
        probeCard.setValue("0");
        resultCard.setValue("\u2014");
        messageLabel.setText("Ready");
    }

    private void start() {
        String algo = (String) algoBox.getSelectedItem();
        int target = (Integer) targetSpinner.getValue();
        canvas.reset();
        probes = 0;
        resultCard.setValue("\u2014");
        List<AlgorithmStep> steps = SearchingService.run(algo, data, target);
        player.load(steps);
        player.setDelay(101 - speedSlider.getValue());
        player.play();
    }

    private void applyStep(AlgorithmStep s) {
        canvas.clearTransient();
        switch (s.type) {
            case RANGE:     canvas.setRange(s.a, s.b); break;
            case PROBE:     probes++; canvas.setProbe(s.a); probeCard.setValue(String.valueOf(probes)); break;
            case COMPARE:   canvas.setProbe(s.a); break;
            case FOUND:     canvas.addSorted(s.a); resultCard.setValue("Index " + s.a); break;
            case NOT_FOUND: resultCard.setValue("Absent"); break;
            default: break;
        }
        if (s.message != null) messageLabel.setText(s.message);
        canvas.repaint();
    }

    private void onFinish() {
        AppState.lastAlgorithm = (String) algoBox.getSelectedItem();
    }

    private void updateInfo() {
        AlgorithmInfo info = AlgorithmInfo.of((String) algoBox.getSelectedItem());
        defLabel.setText("<html><body style='width:260px'>" + info.definition + "</body></html>");
        bestLabel.setText(info.timeBest);
        avgLabel.setText(info.timeAvg);
        worstLabel.setText(info.timeWorst);
    }
}
