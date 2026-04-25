package flight.ui;

import flight.model.FlightGraph;
import flight.ui.components.*;
import flight.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * AppWindow — the main JFrame that holds everything.
 *
 * Layout:
 *   ┌─────────────┬──────────────────────────────────┐
 *   │  SIDEBAR    │         CONTENT AREA             │
 *   │  (nav)      │   (swaps panel on nav click)     │
 *   │             │                                  │
 *   └─────────────┴──────────────────────────────────┘
 *
 * We use a CardLayout for the content area — it holds all panels
 * stacked, and shows only one at a time.
 */
public class AppWindow extends JFrame {

    private final FlightGraph graph;

    // CardLayout lets us switch panels by name without destroying them
    private final CardLayout  cardLayout = new CardLayout();
    private final JPanel      contentArea = new JPanel(cardLayout);

    // Panel name constants — avoids typo bugs with raw strings
    public static final String PANEL_ROUTE    = "route";
    public static final String PANEL_BFS      = "bfs";
    public static final String PANEL_CRITICAL = "critical";
    public static final String PANEL_MST      = "mst";
    public static final String PANEL_BUDGET   = "budget";
    public static final String PANEL_AIRPORTS = "airports";

    public AppWindow(FlightGraph graph) {
        this.graph = graph;
        setTitle("✈  Flight Connection Optimizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 700);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null); // center on screen

        // Set the window background so there's no white flash on open
        getContentPane().setBackground(Theme.BG_DEEP);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // Root layout: sidebar on the left, content on the right
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DEEP);

        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Theme.BG_PANEL);
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));

        // Right border to visually separate sidebar from content
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));

        // ── Logo / app name ──────────────────────────────────────────────────
        JPanel logoArea = new JPanel();
        logoArea.setOpaque(false);
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBorder(new EmptyBorder(28, 18, 20, 14));

        JLabel plane = new JLabel("✈");
        plane.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        plane.setForeground(Theme.ACCENT);
        plane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("Flight Optimizer");
        appName.setFont(Theme.FONT_BODY_BOLD);
        appName.setForeground(Theme.TEXT_PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel version = new JLabel("32 airports · 179 routes");
        version.setFont(Theme.FONT_SMALL);
        version.setForeground(Theme.TEXT_DIM);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoArea.add(plane);
        logoArea.add(Box.createVerticalStrut(8));
        logoArea.add(appName);
        logoArea.add(Box.createVerticalStrut(3));
        logoArea.add(version);

        sidebar.add(logoArea, BorderLayout.NORTH);

        // ── Nav buttons ──────────────────────────────────────────────────────
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(8, 6, 8, 6));

        // Section label
        JLabel routesLabel = navSectionLabel("ROUTING");
        nav.add(routesLabel);
        nav.add(Box.createVerticalStrut(4));

        ButtonGroup group = new ButtonGroup();

        SidebarButton routeBtn    = navBtn(group, "✈", "Route Finder        ",    PANEL_ROUTE,    true);
        SidebarButton bfsBtn      = navBtn(group, "🔍", "Reachability        ",    PANEL_BFS,      false);

        nav.add(routeBtn);
        nav.add(Box.createVerticalStrut(2));
        nav.add(bfsBtn);
        nav.add(Box.createVerticalStrut(14));

        nav.add(navSectionLabel("ANALYSIS"));
        nav.add(Box.createVerticalStrut(4));

        SidebarButton criticalBtn = navBtn(group, "⚠", "Critical Airports        ", PANEL_CRITICAL, false);
        SidebarButton mstBtn      = navBtn(group, "🌐", "Min Span Tree        ",     PANEL_MST,      false);
        SidebarButton budgetBtn   = navBtn(group, "💸", "Budget Explorer        ",   PANEL_BUDGET,   false);

        nav.add(criticalBtn);
        nav.add(Box.createVerticalStrut(2));
        nav.add(mstBtn);
        nav.add(Box.createVerticalStrut(2));
        nav.add(budgetBtn);
        nav.add(Box.createVerticalStrut(14));

        nav.add(navSectionLabel("DATA"));
        nav.add(Box.createVerticalStrut(4));

        SidebarButton airportsBtn = navBtn(group, "📍", "All Airports    ",    PANEL_AIRPORTS, false);
        nav.add(airportsBtn);

        sidebar.add(nav, BorderLayout.CENTER);

        // ── Bottom status bar ────────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 16, 16, 16));

        JLabel complexityNote = new JLabel("<html>"
                + "<span style='color:#484F58;font-size:10px'>"
                + "Dijkstra: O((V+E)logV)<br>"
                + "BFS: O(V+E) · Tarjan: O(V+E)"
                + "</span></html>");
        complexityNote.setFont(Theme.FONT_SMALL);
        footer.add(complexityNote, BorderLayout.CENTER);

        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private SidebarButton navBtn(ButtonGroup group, String icon, String label,
                                  String panelName, boolean selected) {
        SidebarButton btn = new SidebarButton(icon, label);
        btn.setSelected(selected);
        group.add(btn);
        btn.addActionListener(e -> cardLayout.show(contentArea, panelName));
        return btn;
    }

    private JLabel navSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        lbl.setForeground(Theme.TEXT_DIM);
        lbl.setBorder(new EmptyBorder(0, 4, 0, 0));
        return lbl;
    }

    // ── Content Area ─────────────────────────────────────────────────────────

    private JPanel buildContent() {
        contentArea.setBackground(Theme.BG_DEEP);

        // Register all panels with their card names
        contentArea.add(new RoutePanel(graph),          PANEL_ROUTE);
        contentArea.add(new BFSPanel(graph),            PANEL_BFS);
        contentArea.add(FeaturePanels.critical(graph),  PANEL_CRITICAL);
        contentArea.add(FeaturePanels.mst(graph),       PANEL_MST);
        contentArea.add(FeaturePanels.budget(graph),    PANEL_BUDGET);
        contentArea.add(new AirportsPanel(graph),       PANEL_AIRPORTS);

        // Show the route panel first
        cardLayout.show(contentArea, PANEL_ROUTE);
        return contentArea;
    }
}
