package flight.ui.panels;

import flight.model.Airport;
import flight.model.FlightGraph;
import flight.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays all airports in a searchable, styled JTable.
 * Also shows network stats (V, E, density).
 *
 * JTable with a custom TableCellRenderer is the right Swing
 * pattern for displaying tabular data with custom colors.
 */
public class AirportsPanel extends JPanel {

    private final FlightGraph graph;
    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        searchField;

    public AirportsPanel(FlightGraph graph) {
        this.graph = graph;
        setOpaque(false);
        setLayout(new BorderLayout(0, Theme.PAD_MD));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        build();
    }

    private void build() {
        // ── Header ───────────────────────────────────────────────────────────
        JLabel title = new JLabel("Airport Directory");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel sub = new JLabel("All " + graph.airportCount()
                + " airports in the network · " + graph.routeCount() + " total routes");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // ── Stats cards row ───────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.PAD_MD, 0));
        statsRow.setOpaque(false);

        int v = graph.airportCount();
        int e = graph.routeCount();
        double density = (double) e / (v * (v - 1));

        statsRow.add(statCard("Airports (V)", String.valueOf(v)));
        statsRow.add(statCard("Routes (E)", String.valueOf(e)));
        statsRow.add(statCard("Avg Out-Degree", String.format("%.1f", (double) e / v)));
        statsRow.add(statCard("Density", String.format("%.3f", density)));

        add(statsRow, BorderLayout.NORTH);

        // ── Search bar ───────────────────────────────────────────────────────
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);

        searchField = Components.styledTextField(24);
        searchField.putClientProperty("JTextField.placeholderText", "Search airports…");
        searchField.setPreferredSize(new Dimension(280, 36));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e2) { filter(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e2) { filter(); }
            public void insertUpdate (javax.swing.event.DocumentEvent e2) { filter(); }
        });
        searchRow.add(searchField);

        // Wrap both stats + search in a north compound panel
        JPanel topCompound = new JPanel();
        topCompound.setOpaque(false);
        topCompound.setLayout(new BoxLayout(topCompound, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel("Airport Directory");
        titleLbl.setFont(Theme.FONT_TITLE);
        titleLbl.setForeground(Theme.TEXT_PRIMARY);
        JLabel subLbl = new JLabel("All " + graph.airportCount()
                + " airports · " + graph.routeCount() + " routes");
        subLbl.setFont(Theme.FONT_BODY);
        subLbl.setForeground(Theme.TEXT_SECONDARY);

        topCompound.add(titleLbl);
        topCompound.add(Box.createVerticalStrut(3));
        topCompound.add(subLbl);
        topCompound.add(Box.createVerticalStrut(Theme.PAD_MD));
        topCompound.add(statsRow);
        topCompound.add(Box.createVerticalStrut(Theme.PAD_MD));
        topCompound.add(searchRow);
        topCompound.add(Box.createVerticalStrut(Theme.PAD_MD));

        // Override the earlier add — put this compound at the top
        removeAll();
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG, Theme.PAD_LG));
        add(topCompound, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] columns = {"Code", "Airport Name", "Outbound Routes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        // Populate table
        graph.getAllAirports().stream()
             .sorted((a, b) -> a.getCode().compareTo(b.getCode()))
             .forEach(a -> tableModel.addRow(new Object[]{
                     a.getCode(),
                     a.getName(),
                     graph.getNeighbors(a).size()
             }));

        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Theme.BG_DEEP);
        scroll.getViewport().setBackground(Theme.BG_DEEP);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        add(scroll, BorderLayout.CENTER);
    }

    private void styleTable(JTable t) {
        t.setBackground(Theme.BG_DEEP);
        t.setForeground(Theme.TEXT_PRIMARY);
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xF0A500, false));
        t.setSelectionForeground(Theme.BG_DEEP);
        t.getTableHeader().setBackground(Theme.BG_PANEL);
        t.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        t.getTableHeader().setFont(Theme.FONT_LABEL);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Theme.BORDER));

        // Column widths
        t.getColumnModel().getColumn(0).setPreferredWidth(70);
        t.getColumnModel().getColumn(0).setMaxWidth(90);
        t.getColumnModel().getColumn(1).setPreferredWidth(340);
        t.getColumnModel().getColumn(2).setPreferredWidth(120);

        // Custom renderer — alternating row colors + code in amber
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, selected, focused, row, col);

                if (selected) {
                    setBackground(Theme.ACCENT_DIM);
                    setForeground(Theme.TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? Theme.BG_DEEP : Theme.BG_PANEL);
                    setForeground(col == 0 ? Theme.ACCENT : Theme.TEXT_PRIMARY);
                }

                setFont(col == 0 ? Theme.FONT_MONO_MED : Theme.FONT_BODY);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    /** Filter table rows based on search text */
    private void filter() {
        String query = searchField.getText().trim().toUpperCase();
        tableModel.setRowCount(0);

        graph.getAllAirports().stream()
             .sorted((a, b) -> a.getCode().compareTo(b.getCode()))
             .filter(a -> query.isEmpty()
                     || a.getCode().contains(query)
                     || a.getName().toUpperCase().contains(query))
             .forEach(a -> tableModel.addRow(new Object[]{
                     a.getCode(),
                     a.getName(),
                     graph.getNeighbors(a).size()
             }));
    }

    /** A small stat display card */
    private JPanel statCard(String label, String value) {
        Components.CardPanel card = new Components.CardPanel("", false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 16, 10, 16));
        card.setPreferredSize(new Dimension(130, 62));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(Theme.FONT_MONO_LARGE);
        valLbl.setForeground(Theme.ACCENT);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(Theme.FONT_SMALL);
        nameLbl.setForeground(Theme.TEXT_DIM);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(valLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(nameLbl);
        return card;
    }
}
