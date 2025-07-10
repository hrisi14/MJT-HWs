package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

public record MapEntity(Location location, MapEntityType type) {
    public MapEntity {
        if (location == null || type == null) {
            throw new IllegalArgumentException("Invalid parameters passed " +
                    "for a MapEntity initialization!");
        }
    }
}
