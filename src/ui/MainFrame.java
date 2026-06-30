package ui;

import util.ColorPalette;

import javax.swing.*;
import java.awt.*;

/**
 * The main window. Hosts the sidebar (WEST) and a main area (CENTER) that
 * stacks the top bar over a CardLayout content region. Each sidebar button
 * flips the visible card and updates the top bar.
 */
public class MainFrame extends JFrame {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final TopBar topBar;
    private final SidebarPanel sidebar;
    private final DashboardPanel dashboard;

    public MainFrame() {
        setTitle("AlgoVision  \u2014  Visualize \u2022 Learn \u2022 Understand");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 800);
        setMinimumSize(new Dimension(1120, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // All real screens now wired into the CardLayout.
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        dashboard = new DashboardPanel(this::showScreen);
        contentPanel.add(dashboard, "Dashboard");
        contentPanel.add(new SortingPanel(), "Sorting");
        contentPanel.add(new SearchingPanel(), "Searching");
        contentPanel.add(new GraphPanel(), "Graph");
        contentPanel.add(new SettingsPanel(), "Settings");
        contentPanel.add(new AboutPanel(), "About");

        topBar = new TopBar();
        sidebar = new SidebarPanel(this::showScreen);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(topBar, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainArea, BorderLayout.CENTER);

        showScreen("Dashboard");
    }

    private void showScreen(String name) {
        if (name.equals("Dashboard")) {
            dashboard.refresh();
        }
        cardLayout.show(contentPanel, name);
        topBar.setScreen(name);
        sidebar.markSelected(name);
    }
}
