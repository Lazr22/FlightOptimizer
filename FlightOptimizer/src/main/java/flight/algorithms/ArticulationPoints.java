package flight.algorithms;

import flight.model.*;

import java.util.*;

/**
 * Tarjan's articulation point detection.
 * Time: O(V + E)   Space: O(V)
 *
 * Uses disc[] (discovery time) and low[] (lowest reachable ancestor)
 * to identify nodes whose removal disconnects the graph.
 */
public class ArticulationPoints {

    private FlightGraph            graph;
    private Map<Airport, Integer>  disc, low;
    private Map<Airport, Airport>  parent;
    private Set<Airport>           visited;
    private List<Airport>          result;
    private int                    timer;

    public List<Airport> findCriticalAirports(FlightGraph graph) {
        this.graph   = graph;
        this.disc    = new HashMap<>();
        this.low     = new HashMap<>();
        this.parent  = new HashMap<>();
        this.visited = new HashSet<>();
        this.result  = new ArrayList<>();
        this.timer   = 0;

        for (Airport a : graph.getAllAirports()) {
            if (!visited.contains(a)) dfs(a, null);
        }
        return result;
    }

    private void dfs(Airport u, Airport par) {
        visited.add(u);
        disc.put(u, timer);
        low.put(u, timer++);
        int children = 0;

        for (Route r : graph.getUndirectedNeighbors(u)) {
            Airport v = r.getTo();
            if (!visited.contains(v)) {
                children++;
                parent.put(v, u);
                dfs(v, u);
                low.put(u, Math.min(low.get(u), low.get(v)));

                // Root with 2+ children OR non-root bridge condition
                if ((par == null && children > 1) ||
                    (par != null && low.get(v) >= disc.get(u))) {
                    if (!result.contains(u)) result.add(u);
                }
            } else if (!v.equals(par)) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }
    }
}
