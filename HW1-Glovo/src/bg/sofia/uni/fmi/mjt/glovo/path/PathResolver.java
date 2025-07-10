package bg.sofia.uni.fmi.mjt.glovo.path;

import bg.sofia.uni.fmi.mjt.glovo.Utils;
import bg.sofia.uni.fmi.mjt.glovo.comparator.NodeComparator;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class PathResolver {

    public Map<MapEntity, Set<MapEntity>> graph;

    public PathResolver(char [][] mapLayout) {
        this.graph = Utils.parseMapLayout(mapLayout);
    }

    private boolean validateEntity(MapEntity current) {

        return current != null && current.type() != MapEntityType.WALL;
    }

    private boolean hasTimeConstraint(double maxTime) {
        return maxTime != -1;
    }

    private boolean hasCostConstraint(double maxCost) {
        return maxCost != -1;
    }

    public double getBestPath(MapEntity start, MapEntity end, double maxPrice, double maxTime) {
        if (start.equals(end)) {
            return 0;
        }

        PriorityQueue<Node> openList = new PriorityQueue<>(new NodeComparator());
        Node startNode = new Node(start, 0, 0);
        openList.add(startNode);

        Map<MapEntity, Integer> closeList = new HashMap<>();
        closeList.put(start, 0);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.getMapEntity().equals(end)) {
                return current.getG();
            }
            traverseNeighbours(current, end, maxPrice, maxTime, openList, closeList);
        }
        return -1;
    }

    void traverseNeighbours(Node current, MapEntity end, double maxPrice, double maxTime,
                            PriorityQueue<Node> openList, Map<MapEntity, Integer> closeList) {

        for (MapEntity neighbour : graph.get(current.getMapEntity())) {
            int neighbourCost = closeList.get(current.getMapEntity()) + 1;
            if (hasTimeConstraint(maxTime) && neighbourCost >= maxTime) {
                continue;
            }
            if (hasCostConstraint(maxPrice) && neighbourCost >= maxPrice) {
                continue;
            }
            if (validateEntity(neighbour)) {
                double neighbourHeuristic = getManhattanDistance(neighbour, end);
                double totalF = neighbourCost + neighbourHeuristic;

                if (!closeList.containsKey(neighbour) ||  totalF < closeList.get(neighbour)
                        && neighbour.type() != MapEntityType.WALL) {
                    closeList.put(neighbour, neighbourCost);
                    Node neighbourNode = new Node(neighbour, neighbourCost, neighbourHeuristic);
                    openList.add(neighbourNode);
                }
            }
        }
    }

    double getManhattanDistance(MapEntity current, MapEntity goal) {
        return Math.abs(current.location().x() - goal.location().x()) +
                    Math.abs(current.location().y() - goal.location().y());
    }
}
