package bg.sofia.uni.fmi.mjt.glovo.path;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;

public class Node {
    private final MapEntity mapEntity;
    double g;
    double h;
    double f;  //I'll use it for heuristics later

    public Node(MapEntity mapEntity) {
        this.mapEntity = mapEntity;
    }

    Node(MapEntity mapEntity, double g, double h) {

        this.mapEntity = mapEntity;
        setValues(g, h);
    }

    void setValues(double newG, double newH) {
        this.g = newG;
        this.h = newH;
        this.f = g + h;
    }

    public MapEntity getMapEntity() {
        return mapEntity;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public double getF() {
        return g + h;
    }
}
