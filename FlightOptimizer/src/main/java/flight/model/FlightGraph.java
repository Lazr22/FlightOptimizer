package flight.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Weighted directed graph using adjacency lists.
 *
 * Space: O(V + E)
 * Add airport / route: O(1)
 * Get neighbors: O(1)
 */
public class FlightGraph {

    private final Map<String, Airport>       airports       = new LinkedHashMap<>();
    private final Map<Airport, List<Route>>  adjacencyList  = new HashMap<>();
    private final Map<Airport, List<Route>>  undirectedAdj  = new HashMap<>();

    // ── Mutation ─────────────────────────────────────────────────────────────

    public void addAirport(String code, String name) {
        String key = code.toUpperCase();
        airports.putIfAbsent(key, new Airport(key, name));
        Airport a = airports.get(key);
        adjacencyList.putIfAbsent(a, new ArrayList<>());
        undirectedAdj.putIfAbsent(a, new ArrayList<>());
    }

    public void addRoute(String from, String to, int cost, int mins) {
        Airport f = airports.get(from.toUpperCase());
        Airport t = airports.get(to.toUpperCase());
        if (f == null || t == null) return;
        adjacencyList.get(f).add(new Route(f, t, cost, mins));
        undirectedAdj.get(f).add(new Route(f, t, cost, mins));
        undirectedAdj.get(t).add(new Route(t, f, cost, mins));
    }

    // ── Query ────────────────────────────────────────────────────────────────

    public List<Route>        getNeighbors(Airport a)            { return adjacencyList.getOrDefault(a, Collections.emptyList()); }
    public List<Route>        getUndirectedNeighbors(Airport a)  { return undirectedAdj.getOrDefault(a, Collections.emptyList()); }
    public Airport            getAirport(String code)            { return airports.get(code.toUpperCase()); }
    public boolean            hasAirport(String code)            { return airports.containsKey(code.toUpperCase()); }
    public Collection<Airport> getAllAirports()                  { return airports.values(); }
    public int                airportCount()                     { return airports.size(); }
    public int                routeCount()                       { return adjacencyList.values().stream().mapToInt(List::size).sum(); }

    // ── CSV Loader ───────────────────────────────────────────────────────────

    public void loadFromCSV(String resource) throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
        if (in == null) throw new Exception("Resource not found: " + resource);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] p = line.trim().split(",");
                if (p.length < 4) continue;
                String orig = p[0].trim().toUpperCase();
                String dest = p[1].trim().toUpperCase();
                int cost = Integer.parseInt(p[2].trim());
                int dur  = Integer.parseInt(p[3].trim());
                addAirport(orig, NAMES.getOrDefault(orig, orig));
                addAirport(dest, NAMES.getOrDefault(dest, dest));
                addRoute(orig, dest, cost, dur);
            }
        }
    }

    private static final Map<String, String> NAMES = new HashMap<>() {{
        put("JFK","John F. Kennedy Intl, New York");  put("LAX","Los Angeles Intl");
        put("ORD","O'Hare Intl, Chicago");             put("MIA","Miami Intl");
        put("ATL","Hartsfield-Jackson, Atlanta");      put("DFW","Dallas/Fort Worth Intl");
        put("DEN","Denver Intl");                      put("SFO","San Francisco Intl");
        put("SEA","Seattle-Tacoma Intl");              put("YYZ","Toronto Pearson Intl");
        put("YVR","Vancouver Intl");                   put("LHR","Heathrow, London");
        put("CDG","Charles de Gaulle, Paris");         put("FRA","Frankfurt Intl");
        put("AMS","Amsterdam Schiphol");               put("MAD","Madrid-Barajas");
        put("FCO","Leonardo da Vinci, Rome");          put("IST","Istanbul Intl");
        put("CAI","Cairo Intl");                       put("DXB","Dubai Intl");
        put("SIN","Singapore Changi");                 put("KUL","Kuala Lumpur Intl");
        put("HKG","Hong Kong Intl");                   put("NRT","Narita Intl, Tokyo");
        put("ICN","Incheon Intl, Seoul");              put("SYD","Sydney Kingsford Smith");
        put("HNL","Daniel K. Inouye, Honolulu");       put("MEX","Mexico City Intl");
        put("CUN","Cancún Intl");                      put("BOG","El Dorado, Bogotá");
        put("LIM","Jorge Chávez, Lima");               put("GRU","São Paulo–Guarulhos");
    }};
}
