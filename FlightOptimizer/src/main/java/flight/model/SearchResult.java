package flight.model;

import java.util.List;

public class SearchResult {
    private final List<Airport> path;
    private final int totalWeight;
    private final boolean found;
    private final String weightLabel;

    public SearchResult(List<Airport> path, int totalWeight, String weightLabel) {
        this.path = path; this.totalWeight = totalWeight;
        this.found = true; this.weightLabel = weightLabel;
    }

    public SearchResult() {
        this.path = null; this.totalWeight = 0;
        this.found = false; this.weightLabel = "";
    }

    public List<Airport> getPath()        { return path; }
    public int           getTotalWeight() { return totalWeight; }
    public boolean       isFound()        { return found; }
    public String        getWeightLabel() { return weightLabel; }

    public int layoverCount() {
        return (path == null || path.size() < 2) ? 0 : path.size() - 2;
    }

    public String getFormattedWeight() {
        if (weightLabel.equals("time")) return Route.formatMinutes(totalWeight);
        return "$" + totalWeight;
    }
}
