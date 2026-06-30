package ui;

import model.AlgorithmInfo;
import model.AlgorithmStep;
import model.AppState;
import service.SortingService;
import util.ColorPalette;
import util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * The sorting visualizer: a bar canvas, full playback controls, live counters,
 * an algorithm-info side panel and line-highlighted pseudocode.
 */
public class SortingPanel extends JPanel {

    private final Random rng = new Random();

    private int[] original = new int[0];   // the generated starting array
    private int[] data = new int[0];        // working copy mutated during playback

    private final BarCanvas canvas = new BarCanvas();
    private final StepPlayer player = new StepPlayer(AppState.defaultDelay);

    private int comparisons = 0, swaps = 0;
    private double computeMs = 0;   // real time the algorithm took to run (sub-ms typical)

    private final JComboBox<String> algoBox = new JComboBox<>(new String[]{
        "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort", "Heap Sort"});
    private final JSlider sizeSlider  = new JSlider(10, 120, AppState.defaultSize);
    private final JSlider speedSlider = new JSlider(1, 100, 100 - AppState.defaultDelay);
    private final JButton startBtn  = UIHelper.primaryButton("\u25B6  Start");
    private final JButton pauseBtn  = UIHelper.ghostButton("\u275A\u275A  Pause");
    private final JButton stepBtn   = UIHelper.ghostButton("\u23ED  Step");
    private final JButton resetBtn  = UIHelper.ghostButton("\u21BA  Reset");
    private final JButton genBtn    = UIHelper.ghostButton("\u21BB  Generate");

    private final JLabel messageLabel = UIHelper.label("Ready", ColorPalette.TEXT_PRIMARY, Font.PLAIN, 15);
    private final StatCard cmpCard  = new StatCard("Comparisons");
    private final StatCard swapCard = new StatCard("Swaps");
    private final StatCard timeCard = new StatCard("Time");

    // info side panel
    private final JLabel defLabel = new JLabel();
    private final JLabel bestLabel = new JLabel(), avgLabel = new JLabel(), worstLabel = new JLabel(), spaceLabel = new JLabel();
    private final JPanel codeBox = new JPanel();
    private JLabel[] codeLines = new JLabel[0];

    public SortingPanel() {
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
        buildArray(sizeSlider.getValue());
    }

    // ---------- layout builders ----------
    private JComponent buildStatStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 3, 14, 0));
        strip.setOpaque(false);
        strip.add(cmpCard);
        strip.add(swapCard);
        strip.add(timeCard);
        strip.setPreferredSize(new Dimension(0, 84));
        return strip;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);

        RoundedPanel canvasCard = new RoundedPanel(16, ColorPalette.SURFACE);
        canvasCard.setLayout(new BorderLayout());
        canvasCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        canvasCard.add(canvas, BorderLayout.CENTER);

        messageLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        center.add(canvasCard, BorderLayout.CENTER);
        center.add(messageLabel, BorderLayout.SOUTH);
        return center;
    }

    private JComponent buildControls() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        row1.setOpaque(false);
        styleCombo(algoBox);
        row1.add(algoBox);
        row1.add(genBtn);
        row1.add(sliderWithLabel("Size", sizeSlider));
        row1.add(sliderWithLabel("Speed", speedSlider));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        row2.setOpaque(false);
        row2.add(startBtn);
        row2.add(pauseBtn);
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
        card.setPreferredSize(new Dimension(340, 0));

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
        card.add(complexityRow("Space", spaceLabel));
        card.add(Box.createVerticalStrut(14));

        JLabel codeHeading = UIHelper.label("Pseudocode", ColorPalette.ACCENT, Font.BOLD, 13);
        codeHeading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(codeHeading);
        card.add(Box.createVerticalStrut(6));

        codeBox.setOpaque(false);
        codeBox.setLayout(new BoxLayout(codeBox, BoxLayout.Y_AXIS));
        codeBox.setAlignmentX(LEFT_ALIGNMENT);
        card.add(codeBox);

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
        slider.setPreferredSize(new Dimension(130, 24));
        p.add(l);
        p.add(slider);
        return p;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(ColorPalette.SURFACE_LIGHT);
        box.setForeground(ColorPalette.TEXT_PRIMARY);
        box.setFont(UIHelper.font(Font.BOLD, 13));
        box.setFocusable(false);
    }

    // ---------- behaviour ----------
    private void wireActions() {
        genBtn.addActionListener(e -> generate(sizeSlider.getValue()));
        algoBox.addActionListener(e -> { updateInfo(); reset(); });
        sizeSlider.addChangeListener(e -> { if (!sizeSlider.getValueIsAdjusting()) generate(sizeSlider.getValue()); });
        speedSlider.addChangeListener(e -> player.setDelay(101 - speedSlider.getValue()));
        startBtn.addActionListener(e -> start());
        pauseBtn.addActionListener(e -> togglePause());
        stepBtn.addActionListener(e -> { player.step(); });
        resetBtn.addActionListener(e -> reset());
    }

    private void generate(int size) {
        buildArray(size);
        publishRun();   // a freshly generated array shows up on the Dashboard immediately
    }

    /** Builds a random array without touching shared Dashboard state (used at startup). */
    private void buildArray(int size) {
        original = new int[size];
        for (int i = 0; i < size; i++) original[i] = 5 + rng.nextInt(96);
        reset();
    }

    private void reset() {
        player.reset();
        data = original.clone();
        canvas.setValues(data);
        comparisons = 0; swaps = 0; computeMs = 0;
        updateStats();
        messageLabel.setText("Ready");
        highlightCode(-1);
    }

    private void start() {
        String algo = (String) algoBox.getSelectedItem();
        data = original.clone();
        canvas.setValues(data);
        comparisons = 0; swaps = 0;
        // Measure how long the algorithm itself takes to run (the record phase),
        // NOT the animation. This is the real, meaningful execution time.
        long t0 = System.nanoTime();
        List<AlgorithmStep> steps = SortingService.run(algo, original.clone());
        computeMs = (System.nanoTime() - t0) / 1_000_000.0;
        player.load(steps);
        player.setDelay(101 - speedSlider.getValue());
        pauseBtn.setText("\u275A\u275A  Pause");
        player.play();
    }

    private void togglePause() {
        if (!player.isLoaded() || player.isFinished()) return;
        if (player.isPlaying()) {
            player.pause();
            pauseBtn.setText("\u25B6  Resume");
        } else {
            player.play();
            pauseBtn.setText("\u275A\u275A  Pause");
        }
    }

    private void applyStep(AlgorithmStep s) {
        canvas.clearTransient();
        switch (s.type) {
            case COMPARE:   comparisons++; canvas.setCompare(s.a, s.b); break;
            case SWAP:      swaps++; int t = data[s.a]; data[s.a] = data[s.b]; data[s.b] = t; canvas.setSwap(s.a, s.b); break;
            case OVERWRITE: data[s.a] = s.value; canvas.setSwap(s.a, s.a); break;
            case PIVOT:     canvas.setPivot(s.a); break;
            case MARK_SORTED: canvas.addSorted(s.a); break;
            default: break;
        }
        if (s.message != null) messageLabel.setText(s.message);
        highlightCode(s.codeLine);
        updateStats();
        publishRun();
        canvas.repaint();
    }

    private void onFinish() {
        for (int i = 0; i < data.length; i++) canvas.addSorted(i);
        messageLabel.setText("Sorted \u2713   (" + comparisons + " comparisons, " + swaps + " swaps)");
        highlightCode(-1);
        updateStats();
        publishRun();
    }

    private void updateStats() {
        cmpCard.setValue(String.valueOf(comparisons));
        swapCard.setValue(String.valueOf(swaps));
        timeCard.setValue(UIHelper.fmtMs(computeMs));
    }

    /** Push the current run's numbers into AppState so the Dashboard reflects them
     *  the moment it's opened — live during the animation, not only when it ends. */
    private void publishRun() {
        AppState.lastModule     = "Sorting";
        AppState.lastArraySize  = data.length;
        AppState.lastComparisons = comparisons;
        AppState.lastSwaps      = swaps;
        AppState.lastExecMs     = computeMs;
        AppState.lastAlgorithm  = (String) algoBox.getSelectedItem();
    }

    private void updateInfo() {
        AlgorithmInfo info = AlgorithmInfo.of((String) algoBox.getSelectedItem());
        defLabel.setText("<html><body style='width:280px'>" + info.definition + "</body></html>");
        bestLabel.setText(info.timeBest);
        avgLabel.setText(info.timeAvg);
        worstLabel.setText(info.timeWorst);
        spaceLabel.setText(info.space);

        codeBox.removeAll();
        codeLines = new JLabel[info.pseudocode.length];
        for (int i = 0; i < info.pseudocode.length; i++) {
            JLabel line = new JLabel(" " + info.pseudocode[i]);
            line.setFont(UIHelper.mono(12));
            line.setForeground(ColorPalette.TEXT_SECONDARY);
            line.setOpaque(true);
            line.setBackground(ColorPalette.SURFACE);
            line.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            line.setAlignmentX(LEFT_ALIGNMENT);
            codeLines[i] = line;
            codeBox.add(line);
        }
        codeBox.revalidate();
        codeBox.repaint();
    }

    private void highlightCode(int line) {
        for (int i = 0; i < codeLines.length; i++) {
            boolean active = (i == line);
            codeLines[i].setBackground(active ? ColorPalette.ACCENT : ColorPalette.SURFACE);
            codeLines[i].setForeground(active ? Color.WHITE : ColorPalette.TEXT_SECONDARY);
        }
    }
}