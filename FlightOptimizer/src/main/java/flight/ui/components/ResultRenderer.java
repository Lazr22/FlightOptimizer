package flight.ui.components;

import flight.model.*;

import java.util.List;
import java.util.Map;

/**
 * Converts algorithm results into styled HTML strings
 * for display in the result JTextPane.
 *
 * We use HTML because Swing's JTextPane supports it natively,
 * letting us use colors, bold, and layout without custom painting.
 *
 * This is a "presenter" — it knows nothing about algorithms,
 * just how to format their output.
 */
public final class ResultRenderer {

    private ResultRenderer() {}

    // Shared HTML wrapper with our dark theme colors
    private static String wrap(String body) {
        return "<html><body style='"
             + "font-family:monospace;"
             + "font-size:13px;"
             + "color:#E6EDF3;"
             + "background:#0D1117;"
             + "padding:4px;"
             + "'>" + body + "</body></html>";
    }

    private static String gold(String s)  { return "<span style='color:#F0A500;font-weight:bold'>" + s + "</span>"; }
    private static String green(String s) { return "<span style='color:#3FB950;font-weight:bold'>" + s + "</span>"; }
    private static String red(String s)   { return "<span style='color:#F85149;font-weight:bold'>" + s + "</span>"; }
    private static String blue(String s)  { return "<span style='color:#58A6FF'>" + s + "</span>"; }
    private static String dim(String s)   { return "<span style='color:#8B949E'>" + s + "</span>"; }
    private static String purple(String s){ return "<span style='color:#BC8CFF'>" + s + "</span>"; }

    // ── Route Result (Dijkstra) ───────────────────────────────────────────────

    public static String routeResult(SearchResult r) {
        if (!r.isFound()) return wrap(red("✖  No route found between these airports."));

        List<Airport> path = r.getPath();
        StringBuilder sb = new StringBuilder();

        // Path display: JFK  →  ORD  →  LHR
        sb.append("<div style='font-size:15px;margin-bottom:10px'>");
        for (int i = 0; i < path.size(); i++) {
            sb.append(gold(path.get(i).getCode()));
            if (i < path.size() - 1)
                sb.append("<span style='color:#484F58'>  ✈  </span>");
        }
        sb.append("</div>");

        // Stats row
        String label = r.getWeightLabel().equals("time") ? "⏱  Duration" : "💰  Cost";
        sb.append("<table style='border-collapse:collapse;width:100%'>");
        sb.append("<tr><td style='padding:4px 12px 4px 0'>")
          .append(dim(label)).append("</td>")
          .append("<td>").append(green(r.getFormattedWeight())).append("</td></tr>");
        sb.append("<tr><td style='padding:4px 12px 4px 0'>")
          .append(dim("🛑  Layovers")).append("</td>")
          .append("<td>").append(blue(r.layoverCount() + "")).append("</td></tr>");
        sb.append("<tr><td style='padding:4px 12px 4px 0'>")
          .append(dim("📍  Full path")).append("</td>");
        sb.append("<td>");
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(dim(" → "));
            sb.append(path.get(i).getCode());
        }
        sb.append("</td></tr></table>");

        return wrap(sb.toString());
    }

    // ── BFS Reachability ─────────────────────────────────────────────────────

    public static String bfsResult(Map<Airport, Integer> reachable, String src, int k) {
        if (reachable.isEmpty()) return wrap(
                dim("No airports reachable from " + src + " within " + k + " hop(s)."));

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin-bottom:10px'>")
          .append(green(reachable.size() + " airports"))
          .append(dim(" reachable from "))
          .append(gold(src))
          .append(dim(" within " + k + " connection(s)"))
          .append("</div>");

        // Group by hop count
        for (int hop = 1; hop <= k; hop++) {
            final int h = hop;
            List<Airport> atHop = reachable.entrySet().stream()
                    .filter(e -> e.getValue() == h)
                    .map(Map.Entry::getKey)
                    .sorted((a, b) -> a.getCode().compareTo(b.getCode()))
                    .toList();
            if (atHop.isEmpty()) continue;

            String hopColor = hop == 1 ? "#F0A500" : hop == 2 ? "#58A6FF" : "#BC8CFF";
            sb.append("<div style='margin:6px 0'>");
            sb.append("<span style='color:").append(hopColor)
              .append(";font-weight:bold'>HOP ").append(hop).append("</span>")
              .append(dim("  "));
            for (Airport a : atHop) {
                sb.append("<span style='color:").append(hopColor)
                  .append("'>").append(a.getCode()).append("</span>  ");
            }
            sb.append("</div>");
        }
        return wrap(sb.toString());
    }

    // ── Articulation Points ──────────────────────────────────────────────────

    public static String articulationResult(List<Airport> aps) {
        StringBuilder sb = new StringBuilder();
        if (aps.isEmpty()) {
            sb.append(green("✔  Network is robust!")).append("<br>")
              .append(dim("No single airport would disconnect the network if removed."));
        } else {
            sb.append(red("⚠  " + aps.size() + " critical airport(s) detected"))
              .append("<br>")
              .append(dim("Removing any of these would disconnect the network."))
              .append("<br><br>");
            for (Airport a : aps) {
                sb.append("<div style='margin:3px 0'>")
                  .append(red("● "))
                  .append(gold(a.getCode()))
                  .append("  ")
                  .append(dim(a.getName()))
                  .append("</div>");
            }
        }
        return wrap(sb.toString());
    }

    // ── MST ──────────────────────────────────────────────────────────────────

    public static String mstResult(List<flight.algorithms.PrimMST.MSTEdge> edges, String wt) {
        int total = edges.stream().mapToInt(e -> e.weight()).sum();
        StringBuilder sb = new StringBuilder();

        sb.append("<div style='margin-bottom:10px'>")
          .append(purple(edges.size() + " essential routes"))
          .append(dim("  |  Total: "))
          .append(green(wt.equals("time") ? Route.formatMinutes(total) : "$" + total))
          .append("</div>");

        List<flight.algorithms.PrimMST.MSTEdge> sorted =
                edges.stream().sorted((a, b) -> a.weight() - b.weight()).toList();

        sb.append("<table style='width:100%;border-collapse:collapse'>");
        for (int i = 0; i < sorted.size(); i++) {
            var e = sorted.get(i);
            String rowBg = i % 2 == 0 ? "#161B22" : "#1C2330";
            String val   = wt.equals("time") ? Route.formatMinutes(e.weight()) : "$" + e.weight();
            sb.append("<tr style='background:").append(rowBg).append("'>")
              .append("<td style='padding:3px 8px;color:#8B949E'>").append(i+1).append(".</td>")
              .append("<td style='padding:3px 8px'>").append(gold(e.from().getCode()))
              .append("<span style='color:#484F58'> — </span>")
              .append(gold(e.to().getCode())).append("</td>")
              .append("<td style='padding:3px 8px;color:#3FB950'>").append(val).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");

        return wrap(sb.toString());
    }

    // ── Budget Search ────────────────────────────────────────────────────────

    public static String budgetResult(Map<Airport, Integer> results,
                                       String src, int budget, String wt) {
        if (results.isEmpty()) return wrap(
                dim("No destinations reachable within that budget."));

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin-bottom:10px'>")
          .append(green(results.size() + " destinations"))
          .append(dim(" reachable from "))
          .append(gold(src))
          .append(dim(" within "))
          .append(gold(wt.equals("time") ? budget + " min" : "$" + budget))
          .append("</div>");

        sb.append("<table style='width:100%;border-collapse:collapse'>");
        int i = 0;
        for (Map.Entry<Airport, Integer> e : results.entrySet()) {
            String rowBg = i++ % 2 == 0 ? "#161B22" : "#1C2330";
            String val = wt.equals("time") ? Route.formatMinutes(e.getValue()) : "$" + e.getValue();
            sb.append("<tr style='background:").append(rowBg).append("'>")
              .append("<td style='padding:3px 8px'>").append(gold(e.getKey().getCode())).append("</td>")
              .append("<td style='padding:3px 8px;color:#8B949E'>").append(e.getKey().getName()).append("</td>")
              .append("<td style='padding:3px 8px;color:#3FB950;text-align:right'>").append(val).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");
        return wrap(sb.toString());
    }

    // ── Error / Info ─────────────────────────────────────────────────────────

    public static String error(String msg) {
        return wrap(red("✖  " + msg));
    }

    public static String info(String msg) {
        return wrap(dim(msg));
    }
}
