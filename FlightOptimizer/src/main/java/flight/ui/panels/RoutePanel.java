package flight.ui.panels;

import flight.algorithms.Dijkstra;
import flight.model.FlightGraph;
import flight.model.SearchResult;
import flight.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel for Feature 1 & 2: Find cheapest or fastest route.
 * Lays out two airport selectors, a mode toggle, and a search button.
 */
public class RoutePanel extends JPanel {

    private final FlightGraph graph;
    private JComboBox<String> fromCombo, toCombo;
    private JToggleButton     costBtn, timeBtn;
    private JTextPane         resultPane;

    public RoutePanel(FlightGraph graph) {
        this.graph = graph;
        setOpaque(false);
        setLayout(new BorderLayout(0, Theme.PAD_MD));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        build();
    }

    private void build() {
        // ── Top: title ───────────────────────────────────────────────────────
        JLabel title = new JLabel("Route Finder");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Find the cheapest or fastest path between any two airports");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(sub);

        // ── Center: controls card ────────────────────────────────────────────
        Components.CardPanel card = new Components.CardPanel("Route Options");
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Airport codes sorted
        String[] codes = graph.getAllAirports().stream()
                .map(a -> a.getCode())
                .sorted()
                .toArray(String[]::new);

        // Row 0: From
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(Components.sectionHeader("From"), gbc);
        gbc.gridx = 1;
        fromCombo = Components.styledCombo(codes);
        card.add(fromCombo, gbc);

        // Row 1: To
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(Components.sectionHeader("To"), gbc);
        gbc.gridx = 1;
        toCombo = Components.styledCombo(codes);
        // Default destination to something interesting
        toCombo.setSelectedItem("SYD");
        card.add(toCombo, gbc);

        // Row 2: Mode toggle
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(Components.sectionHeader("Optimize for"), gbc);
        gbc.gridx = 1;
        card.add(buildModeToggle(), gbc);

        // Row 3: Search button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Components.StyledButton searchBtn = Components.primaryButton("✈  Search Route");
        searchBtn.setPreferredSize(new Dimension(0, 42));
        searchBtn.addActionListener(e -> runSearch());
        card.add(searchBtn, gbc);

        // Wrap header + card into NORTH so CENTER can be the result area
        JPanel topBlock = new JPanel(new BorderLayout(0, Theme.PAD_MD));
        topBlock.setOpaque(false);
        topBlock.add(header, BorderLayout.NORTH);
        topBlock.add(card,   BorderLayout.CENTER);

        JPanel resultHeader = new JPanel(new BorderLayout());
        resultHeader.setOpaque(false);
        resultHeader.setBorder(new EmptyBorder(Theme.PAD_MD, 0, 6, 0));
        resultHeader.add(Components.sectionHeader("Results"), BorderLayout.WEST);
        topBlock.add(resultHeader, BorderLayout.SOUTH);

        add(topBlock, BorderLayout.NORTH);

        // ── CENTER: result scroll — grows to fill all remaining height ───────
        resultPane = Components.styledResultArea();
        resultPane.setContentType("text/html");
        resultPane.setText(ResultRenderer.info("Select airports and click Search Route."));

        JScrollPane scroll = new JScrollPane(resultPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setMinimumSize(new Dimension(0, 320));

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildModeToggle() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        costBtn = styledToggle("💰  Cheapest");
        timeBtn = styledToggle("⏱  Fastest");
        costBtn.setSelected(true);
        bg.add(costBtn); bg.add(timeBtn);
        p.add(costBtn); p.add(timeBtn);
        return p;
    }

    private JToggleButton styledToggle(String text) {
        JToggleButton btn = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? Theme.ACCENT : Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(Theme.FONT_BODY_BOLD);
                g2.setColor(isSelected() ? Theme.BG_DEEP : Theme.TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> repaint());
        return btn;
    }

    private void runSearch() {
        String from = (String) fromCombo.getSelectedItem();
        String to   = (String) toCombo.getSelectedItem();

        if (from == null || to == null) return;

        String mode = costBtn.isSelected() ? "cost" : "time";
        SearchResult result = mode.equals("cost")
                ? Dijkstra.findCheapest(graph, from, to)
                : Dijkstra.findFastest(graph, from, to);

        resultPane.setText(ResultRenderer.routeResult(result));
        resultPane.setCaretPosition(0);
    }
}
