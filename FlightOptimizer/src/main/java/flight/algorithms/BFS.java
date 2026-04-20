package flight.algorithms;

import flight.model.*;

import java.util.*;

/**
 * BFS — airports reachable within K hops.
 * Time: O(V + E)   Space: O(V)
 */
public class BFS {

    public static Map<Airport, Integer> reachableWithinHops(FlightGraph g,
                                                             String srcCode,
                                                             int maxHops) {
        Airport src = g.getAirport(srcCode);
        if (src == null) return Collections.emptyMap();

        Map<Airport, Integer> visited = new LinkedHashMap<>();
        Queue<Airport>        queue   = new ArrayDeque<>();
        Map<Airport, Integer> hops    = new HashMap<>();

        visited.put(src, 0);
        queue.add(src);
        hops.put(src, 0);

        while (!queue.isEmpty()) {
            Airport cur    = queue.poll();
            int     curHop = hops.get(cur);
            if (curHop >= maxHops) continue;

            for (Route r : g.getNeighbors(cur)) {
                Airport nb = r.getTo();
                if (!visited.containsKey(nb)) {
                    int h = curHop + 1;
                    visited.put(nb, h);
                    hops.put(nb, h);
                    queue.add(nb);
                }
            }
        }
        visited.remove(src);
        return visited;
    }
}
