# ✈ Flight Connection Optimizer — Java Swing UI

A graph-powered flight network analyzer with a modern dark dashboard UI.

## Quick Start in IntelliJ

### 1. Open the project
**File → Open** → select the `FlightOptimizerUI` folder.

### 2. Mark source roots
| Folder | Mark As |
|---|---|
| `src/main/java` | Sources Root |
| `src/main/resources` | Resources Root |

Right-click each folder → **Mark Directory As** → choose the type.

### 3. Add FlatLaf (for the modern dark theme)

**Option A — Maven (easiest):**
- File → Project Structure → Libraries → **+** → From Maven
- Search: `com.formdev:flatlaf:3.4.1`
- Click OK — IntelliJ downloads it automatically.

**Option B — JAR file:**
- Download `flatlaf-3.4.1.jar` from https://github.com/JFormDesigner/FlatLaf/releases
- Put it in the `lib/` folder
- File → Project Structure → Libraries → **+** → Java → select the jar

> **Without FlatLaf:** The app still runs and looks decent — it falls back to Nimbus dark automatically.

### 4. Run
Open `src/main/java/flight/ui/Main.java` → right-click → **Run 'Main.main()'**

---

## Project Structure

```
FlightOptimizerUI/
├── src/main/java/flight/
│   ├── model/
│   │   ├── Airport.java          Node: 3-letter code + name
│   │   ├── Route.java            Edge: cost + duration weights
│   │   ├── FlightGraph.java      Adjacency list graph + CSV loader
│   │   └── SearchResult.java     Path + weight wrapper
│   ├── algorithms/
│   │   ├── Dijkstra.java         Cheapest & fastest  O((V+E)logV)
│   │   ├── BFS.java              K-hop reachability  O(V+E)
│   │   ├── ArticulationPoints.java  Tarjan's DFS     O(V+E)
│   │   ├── PrimMST.java          Min spanning tree   O(E logV)
│   │   └── BudgetSearch.java     Budget filter       O((V+E)logV)
│   └── ui/
│       ├── Main.java             Entry point
│       ├── AppWindow.java        JFrame shell + sidebar nav
│       ├── components/
│       │   ├── Theme.java        Centralized colors + fonts
│       │   ├── Components.java   Reusable styled widgets
│       │   ├── SidebarButton.java  Custom nav toggle button
│       │   └── ResultRenderer.java  HTML output formatter
│       └── panels/
│           ├── RoutePanel.java    Dijkstra UI (cheapest/fastest)
│           ├── BFSPanel.java      BFS reachability UI
│           ├── FeaturePanels.java Critical / MST / Budget UIs
│           └── AirportsPanel.java Searchable airport table
└── src/main/resources/
    └── flights.csv               32 airports, 179 routes
```

---

## Features

| Panel | Feature | Algorithm | Complexity |
|---|---|---|---|
| Route Finder | Cheapest or fastest path | Dijkstra | O((V+E) log V) |
| Reachability | Airports within K hops | BFS | O(V + E) |
| Critical Airports | Network bottlenecks | Tarjan's APs | O(V + E) |
| Min Span Tree | Essential route backbone | Prim's MST | O(E log V) |
| Budget Explorer | All dests within budget | Modified Dijkstra | O((V+E) log V) |
| Airport Directory | Searchable table + stats | — | — |

---

## Push to GitHub

```bash
cd FlightOptimizerUI
git init
git add .
git commit -m "Flight Connection Optimizer - Java Swing UI"
git remote add origin https://github.com/YOUR_USERNAME/FlightOptimizerUI.git
git branch -M main
git push -u origin main
```

---

## Dataset

32 airports · 179 directed routes  
Regions: North America, Europe, Middle East, Asia-Pacific, South America

CSV format: `origin, destination, cost (USD), duration (minutes)`
