package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {

    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private static boolean validateLocation(int x, int y, int maxX, int maxY) {
        return x >= 0 && x <= maxX && y >= 0 && y <= maxY;
    }

    private static boolean isValidSymbol(char symbol) {
        return symbol == '.' || symbol == '#' || symbol == 'R' ||
               symbol == 'C' || symbol == 'A' || symbol == 'B';
    }

    public static boolean validateLayout(char [][] mapLayout) {
        if (mapLayout == null || mapLayout.length == 0 ||
                mapLayout[0].length == 0) {
            return false;
        }

        int commonLength = mapLayout[0].length;
        for (int row = 0; row < mapLayout.length; row++) {
            if (mapLayout[row] == null || mapLayout[row].length != commonLength) {
                return false;
            }

            for (int col = 0; col < commonLength; col++) {
                if (!isValidSymbol(mapLayout[row][col])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Map<MapEntity, Set<MapEntity>> parseMapLayout(char[][] mapLayout) {
        Map<MapEntity, Set<MapEntity>> parsedMap = new HashMap<>();
        int maxX = mapLayout.length - 1;
        int maxY = mapLayout[0].length - 1;

        for (int row = 0; row <= maxX; row++) {
            for (int column = 0; column <= maxY; column++) {
                MapEntityType type = MapEntityType.getTypeFromChar(mapLayout[row][column]);
                Location currentLocation = new Location(row, column);
                MapEntity currentEntity = new MapEntity(currentLocation, type);
                parsedMap.putIfAbsent(currentEntity, new HashSet<>());

                for (int[] directions : DIRECTIONS) {
                    int newX = row + directions[0];
                    int newY = column + directions[1];

                    if (validateLocation(newX, newY, maxX, maxY)) {
                        MapEntityType neighbourType = MapEntityType.getTypeFromChar(mapLayout[newX][newY]);
                        Location neighbourLocation = new Location(newX, newY);
                        MapEntity newNeighbour = new MapEntity(neighbourLocation, neighbourType);
                        parsedMap.get(currentEntity).add(newNeighbour);
                    }
                }
            }
        }
        return parsedMap;
    }

    public static MapEntity[][] parseToMapEntityMatrix(char [][] mapLayout) {

        MapEntity[][] resultMatrix = new MapEntity [mapLayout.length][mapLayout[0].length];

        for (int row = 0; row < mapLayout.length; row++) {

            for (int column = 0; column < mapLayout[0].length; column++) {

                MapEntityType type = MapEntityType.getTypeFromChar(mapLayout[row][column]);
                Location currentLocation = new Location(row, column);
                resultMatrix[row][column] = new MapEntity(currentLocation, type);
            }
        }
        return resultMatrix;
    }
}
