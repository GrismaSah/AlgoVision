import ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Its ONLY job is to launch the UI
 * correctly on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
