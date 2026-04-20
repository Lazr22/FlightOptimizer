package flight.model;

import java.util.Objects;

public class Airport {
    private final String code;
    private final String name;

    public Airport(String code, String name) {
        this.code = code.toUpperCase();
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    @Override public String toString() { return code + " — " + name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport)) return false;
        return code.equals(((Airport) o).code);
    }

    @Override public int hashCode() { return Objects.hash(code); }
}
