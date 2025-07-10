package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;

public class Glovo implements GlovoApi {

    public ControlCenter controlCenter;

    public Glovo(char[][] mapLayout) {

        controlCenter = new ControlCenter(mapLayout);
    }

    private boolean verifyNotNull(MapEntity client, MapEntity restaurant, String foodItem) {
        return client != null && restaurant != null &&
                foodItem != null && !foodItem.isBlank();
    }

    private boolean validateDataContent(MapEntity client, MapEntity restaurant) {
        return (controlCenter.checkForSuchLocation(client) && controlCenter.checkForSuchLocation(restaurant));
    }

    @Override
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant,
                                        String foodItem) throws NoAvailableDeliveryGuyException {

        if (!verifyNotNull(client, restaurant, foodItem)) {
            throw new NullPointerException("Order's details must not be null!");
        }

        if (!validateDataContent(client, restaurant)) {
            throw new InvalidOrderException("Entity does not correspond to its location " +
                    "or it is outside of map's boundaries!");
        }

        DeliveryInfo deliveryInfo = controlCenter.findOptimalDeliveryGuy(restaurant.location(), client.location(),
                -1, -1, ShippingMethod.CHEAPEST);

        if (deliveryInfo == null) {
            throw new NoAvailableDeliveryGuyException("No delivery guys available to " +
                    "complete this order!");
        }

        return new Delivery(client.location(), restaurant.location(), deliveryInfo.getGuyLocation(),
                foodItem, deliveryInfo.getPrice(), deliveryInfo.getTime());
    }

    @Override
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant,
                                       String foodItem) throws NoAvailableDeliveryGuyException {

        if (!verifyNotNull(client, restaurant, foodItem)) {
            throw new NullPointerException("Order's details must not be null!");
        }

        if (!validateDataContent(client, restaurant)) {
            throw new InvalidOrderException("Entity does not correspond to its location " +
                    "or it is outside of map's boundaries!");
        }

        DeliveryInfo deliveryInfo = controlCenter.findOptimalDeliveryGuy(restaurant.location(), client.location(),
                -1, -1, ShippingMethod.FASTEST);

        if (deliveryInfo == null) {
            throw new NoAvailableDeliveryGuyException("No delivery guys available to" +
                    "complete this order!");
        }

        return new Delivery(client.location(), restaurant.location(), deliveryInfo.getGuyLocation(),
                foodItem, deliveryInfo.getPrice(), deliveryInfo.getTime());
    }

    @Override
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant,
                                                 String foodItem, double maxPrice)
            throws NoAvailableDeliveryGuyException {

        if (!verifyNotNull(client, restaurant, foodItem)) {
            throw new NullPointerException("Order's details must not be null!");
        }

        if (!validateDataContent(client, restaurant)) {
            throw new InvalidOrderException("Entity does not correspond to its location " +
                    "or it is outside of map's boundaries!");
        }

        DeliveryInfo deliveryInfo = controlCenter.findOptimalDeliveryGuy(restaurant.location(), client.location(),
                maxPrice, -1, ShippingMethod.FASTEST);

        if (deliveryInfo == null) {
            throw new NoAvailableDeliveryGuyException("No delivery guys available to" +
                    "complete this order!");
        }

        return new Delivery(client.location(), restaurant.location(), deliveryInfo.getGuyLocation(),
                foodItem, deliveryInfo.getPrice(), deliveryInfo.getTime());
    }

    @Override
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant,
                                                       String foodItem, int maxTime)
            throws NoAvailableDeliveryGuyException {

        if (!verifyNotNull(client, restaurant, foodItem)) {
            throw new NullPointerException("Order's details must not be null!");
        }

        if (!validateDataContent(client, restaurant)) {
            throw new InvalidOrderException("Entity does not correspond to its location " +
                    "or it is outside of map's boundaries!");
        }

        DeliveryInfo deliveryInfo = controlCenter.findOptimalDeliveryGuy(restaurant.location(), client.location(),
                -1, maxTime, ShippingMethod.CHEAPEST);

        if (deliveryInfo == null) {
            throw new NoAvailableDeliveryGuyException("No delivery guys available to" +
                    "complete this order!");
        }

        return new Delivery(client.location(), restaurant.location(), deliveryInfo.getGuyLocation(),
                foodItem, deliveryInfo.getPrice(), deliveryInfo.getTime());
    }
}
