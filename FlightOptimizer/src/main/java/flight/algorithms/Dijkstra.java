package flight.algorithms;

import flight.model.*;

import java.util.*;

/**
 * Dijkstra's shortest path — O((V + E) log V)
 *
 * Works for both cheapest (weight = cost) and fastest (weight = duration).
 * Uses a PriorityQueue (binary min-heap) for O(log V) extraction.
 */
public class Dijkstra {

    public static SearchResult findCheapest(FlightGraph g, String src, String dst) {
        return run(g, src, dst, "cost");
    }

    public static SearchResult findFastest(FlightGraph g, String src, String dst) {
        return run(g, src, dst, "time");
    }

    private static SearchResult run(FlightGraph g, String srcCode,
                                    String dstCode, String weightType) {
        Airport src = g.getAirport(srcCode);
        Airport dst = g.getAirport(dstCode);
        if (src == null || dst == null) return new SearchResult();
        if (src.equals(dst)) return new SearchResult(List.of(src), 0, weightType);

        Map<Airport, Integer> dist = new HashMap<>();
        Map<Airport, Airport> prev = new HashMap<>();
        for (Airport a : g.getAllAirports()) dist.put(a, Integer.MAX_VALUE);
        dist.put(src, 0);

        // PriorityQueue ordered by current known distance
        PriorityQueue<Airport> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(src);

        while (!pq.isEmpty()) {
            Airport u = pq.poll();
            if (u.equals(dst)) break;
            if (dist.get(u) == Integer.MAX_VALUE) break;

            for (Route r : g.getNeighbors(u)) {
                Airport v = r.getTo();
                int nd = dist.get(u) + r.getWeight(weightType);
                if (nd < dist.get(v)) {
                    dist.put(v, nd);
                    prev.put(v, u);
                    pq.remove(v);   // re-insert with updated priority
                    pq.add(v);
                }
            }
        }

        if (dist.get(dst) == Integer.MAX_VALUE) return new SearchResult();

        // Reconstruct path by walking back through prev[]
        LinkedList<Airport> path = new LinkedList<>();
        for (Airport cur = dst; cur != null; cur = prev.get(cur)) path.addFirst(cur);

        return new SearchResult(new ArrayList<>(path), dist.get(dst), weightType);
    }
}
