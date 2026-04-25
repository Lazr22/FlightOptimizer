package flight.ui;

import flight.model.FlightGraph;

import javax.swing.*;
import java.awt.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  Main — Application Entry Point
 * ═══════════════════════════════════════════════════════════════
 *
 * HOW TO RUN IN INTELLIJ:
 *   Right-click this file → Run 'Main.main()'
 *
 * FLATLAF SETUP (required for the modern dark theme):
 *   1. Open File → Project Structure → Libraries → + → From Maven
 *   2. Search: com.formdev:flatlaf:3.4.1
 *   3. Click OK — IntelliJ downloads it automatically
 *
 *   OR if you're using a lib/ folder:
 *   Download flatlaf-3.4.1.jar from https://github.com/JFormDesigner/FlatLaf/releases
 *   Put it in your lib/ folder and add it to your module dependencies.
 *
 * WITHOUT FLATLAF:
 *   The app still works — it falls back to Nimbus (a built-in Swing theme).
 *   It won't look as polished but all features function identically.
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Apply look-and-feel BEFORE creating any Swing components ──────
        applyLookAndFeel();

        // ── 2. Load the flight data ───────────────────────────────────────────
        FlightGraph graph = new FlightGraph();
        try {
            graph.loadFromCSV("flights.csv");
            System.out.println("Loaded " + graph.airportCount()
                    + " airports, " + graph.routeCount() + " routes.");
        } catch (Exception e) {
            // If loading fails, show a dialog and exit cleanly
            JOptionPane.showMessageDialog(null,
                    "Could not load flights.csv from resources.\n\n"
                    + "Make sure src/main/resources/ is marked as a Resources Root\n"
                    + "in IntelliJ: right-click the folder → Mark Directory As → Resources Root",
                    "Data Load Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // ── 3. Launch the UI on the Event Dispatch Thread (EDT) ──────────────
        //
        // Swing is NOT thread-safe. All UI creation must happen on the EDT.
        // SwingUtilities.invokeLater() schedules our code to run on the EDT.
        // This is the correct pattern — always launch Swing apps this way.

        final FlightGraph finalGraph = graph; // must be final to use inside lambda
        SwingUtilities.invokeLater(() -> new AppWindow(finalGraph));
    }

    /**
     * Applies FlatLaf dark theme if available on the classpath,
     * otherwise falls back to Nimbus.
     *
     * We use reflection (Class.forName) to check if FlatLaf is available
     * without causing a compile error if it's not in the project yet.
     */
    private static void applyLookAndFeel() {
        try {
            // Try FlatLaf IntelliJ Dark (looks very close to IntelliJ itself)
            Class<?> flatLaf = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.setLookAndFeel((LookAndFeel) flatLaf.getDeclaredConstructor().newInstance());
            System.out.println("FlatLaf dark theme applied.");

        } catch (ClassNotFoundException e) {
            // FlatLaf not on classpath — fall back to Nimbus
            System.out.println("FlatLaf not found — using Nimbus fallback.");
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        // Apply Nimbus dark colors manually
                        UIManager.put("control",       new Color(0x1C2330));
                        UIManager.put("info",          new Color(0x1C2330));
                        UIManager.put("nimbusBase",    new Color(0x0D1117));
                        UIManager.put("nimbusBlueGrey",new Color(0x161B22));
                        UIManager.put("nimbusLightBackground", new Color(0x161B22));
                        UIManager.put("text",          new Color(0xE6EDF3));
                        break;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Could not apply Nimbus either — using system default.");
            }
        } catch (Exception e) {
            System.out.println("Could not apply FlatLaf: " + e.getMessage());
        }
    }
}
