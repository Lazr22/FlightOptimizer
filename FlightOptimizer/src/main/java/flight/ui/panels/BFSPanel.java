package flight.ui.panels;

import flight.algorithms.BFS;
import flight.model.Airport;
import flight.model.FlightGraph;
import flight.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class BFSPanel extends JPanel {

    private final FlightGraph graph;
    private JComboBox<String> fromCombo;
    private JSpinner          hopsSpinner;
    private JTextPane         resultPane;

    public BFSPanel(FlightGraph graph) {
        this.graph = graph;
        setOpaque(false);
        setLayout(new BorderLayout(0, Theme.PAD_MD));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        build();
    }

    private void build() {
        JLabel title = new JLabel("Reachability Explorer");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel sub = new JLabel("BFS — find all airports reachable within K connections");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(title); header.add(sub);

        Components.CardPanel card = new Components.CardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        String[] codes = graph.getAllAirports().stream()
                .map(Airport::getCode).sorted().toArray(String[]::new);

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(Components.sectionHeader("Departure"), gbc);
        gbc.gridx = 1;
        fromCombo = Components.styledCombo(codes);
        card.add(fromCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(Components.sectionHeader("Max Hops (K)"), gbc);
        gbc.gridx = 1;
        hopsSpinner = Components.styledSpinner(1, 10, 2);
        card.add(hopsSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Components.StyledButton btn = Components.primaryButton("🔍  Find Reachable Airports");
        btn.setPreferredSize(new Dimension(0, 42));
        btn.addActionListener(e -> runSearch());
        card.add(btn, gbc);

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

        resultPane = Components.styledResultArea();
        resultPane.setContentType("text/html");
        resultPane.setText(ResultRenderer.info("Select a departure airport and max hops, then search."));

        JScrollPane scroll = new JScrollPane(resultPane);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setMinimumSize(new Dimension(0, 320));
        add(scroll, BorderLayout.CENTER);
    }

    private void runSearch() {
        String src = (String) fromCombo.getSelectedItem();
        int k      = (int) hopsSpinner.getValue();
        Map<Airport, Integer> result = BFS.reachableWithinHops(graph, src, k);
        resultPane.setText(ResultRenderer.bfsResult(result, src, k));
        resultPane.setCaretPosition(0);
    }
}
