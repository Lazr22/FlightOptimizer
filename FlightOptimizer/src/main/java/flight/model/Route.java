package flight.model;

public class Route {
    private final Airport from;
    private final Airport to;
    private final int cost;
    private final int minutes;

    public Route(Airport from, Airport to, int cost, int minutes) {
        this.from = from; this.to = to;
        this.cost = cost; this.minutes = minutes;
    }

    public Airport getFrom()    { return from; }
    public Airport getTo()      { return to; }
    public int     getCost()    { return cost; }
    public int     getMinutes() { return minutes; }

    public int getWeight(String type) {
        return type.equals("time") ? minutes : cost;
    }

    public static String formatMinutes(int total) {
        return (total / 60) + "h " + (total % 60) + "m";
    }
}
