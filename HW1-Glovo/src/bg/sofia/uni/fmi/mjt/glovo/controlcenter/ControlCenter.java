package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.Utils;
import bg.sofia.uni.fmi.mjt.glovo.exception.InexistentPathException;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapLayoutException;
import bg.sofia.uni.fmi.mjt.glovo.path.PathResolver;
import java.util.HashSet;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType.DELIVERY_GUY_BIKE;
import static bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType.BIKE;
import static bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType.CAR;

public class ControlCenter  implements ControlCenterApi {

    MapEntity [][] map;
    Set<MapEntity> deliveryGuys;
    public PathResolver pathResolver;

    public ControlCenter(char[][] mapLayout) {
        if (!Utils.validateLayout(mapLayout)) {
            throw new InvalidMapLayoutException("MapLayout is null/empty " +
                    "or contains invalid symbols!");
        }
        map = Utils.parseToMapEntityMatrix(mapLayout);
        pathResolver = new PathResolver(mapLayout);
        deliveryGuys = initializeDeliveryGuys();
    }

    private Set<MapEntity> initializeDeliveryGuys() {
        Set<MapEntity> deliveryGuys = new HashSet<>();
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[0].length; column++) {
                if (map[row][column].type() == DELIVERY_GUY_BIKE
                        || map[row][column].type() == MapEntityType.DELIVERY_GUY_CAR) {
                    deliveryGuys.add(map[row][column]);
                }
            }
        }
        return deliveryGuys;
    }

    private boolean hasTimeConstraint(int maxTime) {
        return maxTime != -1;
    }

    private boolean hasCostConstraint(double maxCost) {
        return maxCost != -1;
    }

    private boolean isNewDeliverySuperiorToCurrent(DeliveryInfo currentDelivery,
                                                   ShippingMethod criteria, double newPrice, int newTime) {
        if (criteria == ShippingMethod.CHEAPEST) {
            return currentDelivery.getPrice() > newPrice;
        }

        if (criteria == ShippingMethod.FASTEST) {
            return currentDelivery.getTime() > newTime;
        }
        return false;
    }

    private double getTotalPrice(double pathLength, DeliveryType deliveryType) {
        return pathLength * deliveryType.getTimePerKm();
    }

    double getTotalTime(int pathLength, DeliveryType deliveryType) {
        return pathLength * deliveryType.getPricePerKm();
    }

    private double modifyCostConstraint(double maxPrice, DeliveryType type) {
        if (hasCostConstraint(maxPrice)) {
            return maxPrice / type.getPricePerKm();
        }
        return -1;
    }

    private double modifyTimeConstraint(int maxTime,  DeliveryType type) {
        if (hasTimeConstraint(maxTime)) {
            return (double) maxTime / type.getTimePerKm();
        }
        return -1;
    }

    private DeliveryInfo checkOptions(DeliveryInfo currentAnswer, double totalPath, double maxPrice, int maxTime,
                              ShippingMethod shippingMethod, MapEntity deliveryGuy, DeliveryType type) {

        int totalTime = (int) Math.round(totalPath * type.getTimePerKm());
        double totalPrice = totalPath * type.getPricePerKm();

        if ((hasTimeConstraint(maxTime) && totalTime > maxTime) ||
                (hasCostConstraint(maxPrice) && totalPrice > maxPrice)) {
            return currentAnswer;
        }

        if (currentAnswer == null || isNewDeliverySuperiorToCurrent(currentAnswer,
                shippingMethod, totalPrice, totalTime)) {
            currentAnswer = new DeliveryInfo(deliveryGuy.location(), totalPrice, totalTime, type);
        }
        return currentAnswer;
    }

    private double getTotalPath(MapEntity deliveryGuy, DeliveryType type, double maxPrice, int maxTime,
                                Location restaurantLocation, Location clientLocation) {
        double costConstraint = modifyCostConstraint(maxPrice, type);
        double timeConstraint = modifyTimeConstraint(maxTime, type);
        if (!checkForSuchLocation(deliveryGuy) ||
                !checkForSuchLocation(map[restaurantLocation.x()][restaurantLocation.y()])) {
            return -1;
        }
        double guyToRestaurantPath = pathResolver.getBestPath(deliveryGuy,
                map[restaurantLocation.x()][restaurantLocation.y()], -1, timeConstraint);
        if (guyToRestaurantPath == -1) {
            return -1;
        }
        double restaurantToClientPath = pathResolver.getBestPath(map[restaurantLocation.x()][restaurantLocation.y()],
                map[clientLocation.x()][clientLocation.y()], costConstraint, timeConstraint);
        if (restaurantToClientPath == -1) {
            return -1;
        }
        return guyToRestaurantPath + restaurantToClientPath;
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        if (deliveryGuys.isEmpty()) {
            return null;
        }
        DeliveryInfo currentAnswer = null;
        for (MapEntity deliveryGuy : deliveryGuys) {
            DeliveryType type = deliveryGuy.type() == DELIVERY_GUY_BIKE ? BIKE : CAR;

            double totalPath = getTotalPath(deliveryGuy, type, maxPrice, maxTime,
                  restaurantLocation, clientLocation);

            currentAnswer = checkOptions(currentAnswer, totalPath, maxPrice, maxTime,
                    shippingMethod, deliveryGuy, type);
        }
        if (currentAnswer == null) {
            throw new InexistentPathException("There is no path between the client and restaurant specified!");
        }
        return currentAnswer;
    }

    public boolean checkForSuchLocation(MapEntity entity) {
        int entityX = entity.location().x();
        int entityY = entity.location().y();
        int mapLength = map[0].length;
        int mapWidth = map.length;

        if (entityX < 0 || entityY < 0 || entityX >= mapLength || entityY >= mapWidth) {
            return false;
        }
        return map[entityX][entityY].type() == entity.type();
    }

    @Override
    public MapEntity[][] getLayout() {
        if (map == null) {
            throw new NullPointerException("Map is not accessible as it is null pointer!");
        }
        return  map;
    }
}
