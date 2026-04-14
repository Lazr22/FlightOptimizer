package flight.algorithms;

import flight.model.*;

import java.util.*;

/**
 * Prim's Minimum Spanning Tree — O(E log V)
 * Finds the minimum set of routes to keep all airports connected.
 */
public class PrimMST {

    public record MSTEdge(Airport from, Airport to, int weight, String label) {
        public String formatted() {
            return from.getCode() + " — " + to.getCode() + "  "
                 + (label.equals("time") ? Route.formatMinutes(weight) : "$" + weight);
        }
    }

    public static List<MSTEdge> compute(FlightGraph g, String weightType) {
        List<MSTEdge>         edges = new ArrayList<>();
        Set<Airport>          inMST = new HashSet<>();

        // PQ entry: [weight, fromAirport, toAirport]  — sorted by weight
        PriorityQueue<Object[]> pq = new PriorityQueue<>(
                Comparator.comparingInt(e -> (int) e[0]));

        Airport start = g.getAllAirports().iterator().next();
        inMST.add(start);
        addEdges(g, start, inMST, pq, weightType);

        while (!pq.isEmpty() && edges.size() < g.airportCount() - 1) {
            Object[] e  = pq.poll();
            Airport  to = (Airport) e[2];
            if (inMST.contains(to)) continue;

            inMST.add(to);
            edges.add(new MSTEdge((Airport) e[1], to, (int) e[0], weightType));
            addEdges(g, to, inMST, pq, weightType);
        }
        return edges;
    }

    private static void addEdges(FlightGraph g, Airport u, Set<Airport> inMST,
                                  PriorityQueue<Object[]> pq, String wt) {
        for (Route r : g.getUndirectedNeighbors(u)) {
            if (!inMST.contains(r.getTo()))
                pq.add(new Object[]{ r.getWeight(wt), u, r.getTo() });
        }
    }
}
