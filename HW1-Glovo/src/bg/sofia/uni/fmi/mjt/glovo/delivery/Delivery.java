package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public class Delivery {

    Location client;
    Location restaurant;
    Location deliveryGuy;
    String foodItem;
    double price;
    int estimatedTime;

    public Delivery(Location client, Location restaurant, Location deliveryGuy,
                    String foodItem, double price, int estimatedTime) {

        this.client = client;
        this.restaurant = restaurant;
        this.deliveryGuy = deliveryGuy;
        this.foodItem = foodItem;
        this.price = price;
        this.estimatedTime = estimatedTime;

    }

    public double getPrice() {
        return price;
    }

    public int getTime() {
        return estimatedTime;
    }
}
