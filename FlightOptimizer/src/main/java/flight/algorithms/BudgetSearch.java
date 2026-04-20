package flight.algorithms;

import flight.model.*;

import java.util.*;

/**
 * Budget Search — find all destinations reachable within a cost/time budget.
 * Modified Dijkstra that collects all reachable nodes (not just one target).
 * Time: O((V + E) log V)
 */
public class BudgetSearch {

    public static Map<Airport, Integer> findWithinBudget(FlightGraph g,
                                                          String srcCode, int budget) {
        return run(g, srcCode, budget, "cost");
    }

    public static Map<Airport, Integer> findWithinTime(FlightGraph g,
                                                        String srcCode, int maxMin) {
        return run(g, srcCode, maxMin, "time");
    }

    private static Map<Airport, Integer> run(FlightGraph g, String srcCode,
                                              int budget, String wt) {
        Airport src = g.getAirport(srcCode);
        if (src == null) return Collections.emptyMap();

        Map<Airport, Integer> dist = new HashMap<>();
        for (Airport a : g.getAllAirports()) dist.put(a, Integer.MAX_VALUE);
        dist.put(src, 0);

        PriorityQueue<Airport> pq =
                new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(src);

        while (!pq.isEmpty()) {
            Airport u = pq.poll();
            int d = dist.get(u);
            if (d > budget) continue;

            for (Route r : g.getNeighbors(u)) {
                Airport v  = r.getTo();
                int    nd = d + r.getWeight(wt);
                if (nd <= budget && nd < dist.get(v)) {
                    dist.put(v, nd);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        Map<Airport, Integer> result = new LinkedHashMap<>();
        dist.entrySet().stream()
            .filter(e -> !e.getKey().equals(src) && e.getValue() != Integer.MAX_VALUE)
            .sorted(Map.Entry.comparingByValue())
            .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
