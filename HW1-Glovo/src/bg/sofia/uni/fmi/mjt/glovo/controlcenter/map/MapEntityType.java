package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

public enum MapEntityType {
    ROAD('.'),
    WALL('#'),
    RESTAURANT('R'),
    CLIENT('C'),
    DELIVERY_GUY_CAR('A'),
    DELIVERY_GUY_BIKE('B');


    private char symbolId;

    MapEntityType(char newSymbolId) {

        this.symbolId = newSymbolId;
    }

    public static MapEntityType getTypeFromChar(char ch) {
        for (MapEntityType mapType : values()) {
            if (mapType.symbolId == ch) {
                return mapType;
            }
        }
        return null;
    }
}