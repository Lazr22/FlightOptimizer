package flight.ui.panels;

import flight.algorithms.*;
import flight.model.*;
import flight.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

// ─────────────────────────────────────────────────────────────────────────────
//  CriticalPanel — Articulation Points
// ─────────────────────────────────────────────────────────────────────────────

class CriticalPanel extends JPanel {

    private final FlightGraph graph;
    private JTextPane         resultPane;

    CriticalPanel(FlightGraph graph) {
        this.graph = graph;
        setOpaque(false);
        setLayout(new BorderLayout(0, Theme.PAD_MD));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        build();
    }

    private void build() {
        JLabel title = new JLabel("Critical Airports");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Articulation points — airports whose removal disconnects the network");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        JPanel header = new JPanel(new GridLayout(2,1,0,4));
        header.setOpaque(false);
        header.add(title); header.add(sub);

        Components.CardPanel card = new Components.CardPanel();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, Theme.PAD_MD, Theme.PAD_MD));
        card.setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));

        JLabel info = Components.bodyLabel("Uses Tarjan's DFS algorithm.  Time: O(V + E)");
        card.add(info);

        Components.StyledButton btn = Components.primaryButton("⚠  Analyze Network");
        btn.setPreferredSize(new Dimension(200, 42));
        btn.addActionListener(e -> run());
        card.add(btn);

        // Controls go in NORTH so scroll can fill CENTER
        JPanel topBlock = new JPanel(new BorderLayout(0, Theme.PAD_MD));
        topBlock.setOpaque(false);
        topBlock.add(header, BorderLayout.NORTH);
        topBlock.add(card,   BorderLayout.CENTER);
        JPanel rh = new JPanel(new BorderLayout());
        rh.setOpaque(false);
        rh.setBorder(new EmptyBorder(Theme.PAD_MD, 0, 6, 0));
        rh.add(Components.sectionHeader("Results"), BorderLayout.WEST);
        topBlock.add(rh, BorderLayout.SOUTH);
        add(topBlock, BorderLayout.NORTH);

        // Scroll fills all remaining height
        resultPane = Components.styledResultArea();
        resultPane.setContentType("text/html");
        resultPane.setText(ResultRenderer.info("Click Analyze Network to detect critical airports."));
        JScrollPane scroll = new JScrollPane(resultPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setMinimumSize(new Dimension(0, 320));
        add(scroll, BorderLayout.CENTER);
    }

    private void run() {
        List<Airport> aps = new ArticulationPoints().findCriticalAirports(graph);
        resultPane.setText(ResultRenderer.articulationResult(aps));
        resultPane.setCaretPosition(0);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  MSTPanel — Prim's Minimum Spanning Tree
// ─────────────────────────────────────────────────────────────────────────────

class MSTPanel extends JPanel {

    private final FlightGraph graph;
    private JToggleButton     costBtn, timeBtn;
    private JTextPane         resultPane;

    MSTPanel(FlightGraph graph) {
        this.graph = graph;
        setOpaque(false);
        setLayout(new BorderLayout(0, Theme.PAD_MD));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        build();
    }

    private void build() {
        JLabel title = new JLabel("Minimum Spanning Tree");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Prim's algorithm — the essential backbone of the network");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        JPanel header = new JPanel(new GridLayout(2,1,0,4));
        header.setOpaque(false);
        header.add(title); header.add(sub);

        Components.CardPanel card = new Components.CardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6); gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(Components.sectionHeader("Optimize for"), gbc);
        gbc.gridx = 1;
        card.add(buildToggle(), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Components.StyledButton btn = Components.primaryButton("🌐  Compute MST");
        btn.setPreferredSize(new Dimension(0, 42));
        btn.addActionListener(e -> run());
        card.add(btn, gbc);

        // Controls go in NORTH so scroll can fill CENTER
        JPanel topBlock = new JPanel(new BorderLayout(0, Theme.PAD_MD));
        topBlock.setOpaque(false);
        topBlock.add(header, BorderLayout.NORTH);
        topBlock.add(card,   BorderLayout.CENTER);
        JPanel rh = new JPanel(new BorderLayout());
        rh.setOpaque(false);
        rh.setBorder(new EmptyBorder(Theme.PAD_MD, 0, 6, 0));
        rh.add(Components.sectionHeader("Results"), BorderLayout.WEST);
        topBlock.add(rh, BorderLayout.SOUTH);
        add(topBlock, BorderLayout.NORTH);

        resultPane = Components.styledResultArea();
        resultPane.setContentType("text/html");
        resultPane.setText(ResultRenderer.info("Click Compute MST to calculate the minimum spanning tree."));
        JScrollPane scroll = new JScrollPane(resultPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setMinimumSize(new Dimension(0, 320));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildToggle() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        costBtn = toggleBtn("💰  Cost");
        timeBtn = toggleBtn("⏱  Duration");
        costBtn.setSelected(true);
        bg.add(costBtn); bg.add(timeBtn);
        p.add(costBtn); p.add(timeBtn);
        return p;
    }

    private JToggleButton toggleBtn(String text) {
        JToggleButton b = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? Theme.ACCENT : Theme.BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setFont(Theme.FONT_BODY_BOLD);
                g2.setColor(isSelected() ? Theme.BG_DEEP : Theme.TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(120,36));
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> repaint());
        return b;
    }

    private void run() {
        String wt = costBtn.isSelected() ? "cost" : "time";
        List<PrimMST.MSTEdge> edges = PrimMST.compute(graph, wt);
        resultPane.setText(ResultRenderer.mstResult(edges, wt));
        resultPane.setCaretPosition(0);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  BudgetPanel — Budget / Time Search
// ─────────────────────────────────────────────────────────────────────────────

class BudgetPanel extends JPanel {

    private final FlightGraph graph;
    private JComboBox<String> fromCombo;
    private JToggleButton     costBtn, timeBtn;
    private JSpinner          budgetSpinner;
    private JLabel            budgetLabel;
    private JTextPane         resultPane;

    BudgetPanel(FlightGraph graph) {
        this.graph = graph;
        setOpaque(false);
        setLayout(new BorderLayout(0, Theme.PAD_MD));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        build();
    }

    private void build() {
        JLabel title = new JLabel("Budget Explorer");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Find all destinations reachable within a cost or time budget");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        JPanel header = new JPanel(new GridLayout(2,1,0,4));
        header.setOpaque(false);
        header.add(title); header.add(sub);

        Components.CardPanel card = new Components.CardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6); gbc.anchor = GridBagConstraints.WEST;

        String[] codes = graph.getAllAirports().stream()
                .map(Airport::getCode).sorted().toArray(String[]::new);

        gbc.gridx=0; gbc.gridy=0; card.add(Components.sectionHeader("From"), gbc);
        gbc.gridx=1; fromCombo = Components.styledCombo(codes); card.add(fromCombo, gbc);

        gbc.gridx=0; gbc.gridy=1; card.add(Components.sectionHeader("Mode"), gbc);
        gbc.gridx=1; card.add(buildToggle(), gbc);

        gbc.gridx=0; gbc.gridy=2;
        budgetLabel = Components.sectionHeader("Budget ($)");
        card.add(budgetLabel, gbc);
        gbc.gridx=1;
        budgetSpinner = Components.styledSpinner(50, 100000, 700);
        budgetSpinner.setPreferredSize(new Dimension(130, 36));
        card.add(budgetSpinner, gbc);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Components.StyledButton btn = Components.primaryButton("💸  Search Destinations");
        btn.setPreferredSize(new Dimension(0, 42));
        btn.addActionListener(e -> run());
        card.add(btn, gbc);

        // Controls go in NORTH so scroll can fill CENTER
        JPanel topBlock = new JPanel(new BorderLayout(0, Theme.PAD_MD));
        topBlock.setOpaque(false);
        topBlock.add(header, BorderLayout.NORTH);
        topBlock.add(card,   BorderLayout.CENTER);
        JPanel rh = new JPanel(new BorderLayout());
        rh.setOpaque(false);
        rh.setBorder(new EmptyBorder(Theme.PAD_MD, 0, 6, 0));
        rh.add(Components.sectionHeader("Results"), BorderLayout.WEST);
        topBlock.add(rh, BorderLayout.SOUTH);
        add(topBlock, BorderLayout.NORTH);

        resultPane = Components.styledResultArea();
        resultPane.setContentType("text/html");
        resultPane.setText(ResultRenderer.info("Set your departure and budget, then search."));
        JScrollPane scroll = new JScrollPane(resultPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setMinimumSize(new Dimension(0, 320));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildToggle() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        costBtn = toggleBtn("💰  Cost ($)");
        timeBtn = toggleBtn("⏱  Time (min)");
        costBtn.setSelected(true);

        costBtn.addActionListener(e -> { budgetLabel.setText("Budget ($)"); budgetSpinner.setValue(700); });
        timeBtn.addActionListener(e -> { budgetLabel.setText("Budget (min)"); budgetSpinner.setValue(600); });

        bg.add(costBtn); bg.add(timeBtn);
        p.add(costBtn); p.add(timeBtn);
        return p;
    }

    private JToggleButton toggleBtn(String text) {
        JToggleButton b = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? Theme.ACCENT : Theme.BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setFont(Theme.FONT_BODY_BOLD);
                g2.setColor(isSelected() ? Theme.BG_DEEP : Theme.TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(135,36));
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void run() {
        String src    = (String) fromCombo.getSelectedItem();
        int    budget = (int) budgetSpinner.getValue();
        String wt     = costBtn.isSelected() ? "cost" : "time";

        Map<Airport, Integer> results = wt.equals("cost")
                ? BudgetSearch.findWithinBudget(graph, src, budget)
                : BudgetSearch.findWithinTime(graph, src, budget);

        resultPane.setText(ResultRenderer.budgetResult(results, src, budget, wt));
        resultPane.setCaretPosition(0);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Public factory — one place to create all panels
// ─────────────────────────────────────────────────────────────────────────────

public class FeaturePanels {
    public static JPanel critical(FlightGraph g) { return new CriticalPanel(g); }
    public static JPanel mst     (FlightGraph g) { return new MSTPanel(g); }
    public static JPanel budget  (FlightGraph g) { return new BudgetPanel(g); }
}
