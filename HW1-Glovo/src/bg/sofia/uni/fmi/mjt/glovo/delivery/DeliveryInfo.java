package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public class DeliveryInfo {

    Location deliveryGuyLocation;
    double price;
    int estimatedTime;
    DeliveryType deliveryType;

    public DeliveryInfo(Location deliveryGuyLocation, double price, int estimatedTime, DeliveryType deliveryType) {
        this.deliveryGuyLocation = deliveryGuyLocation;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.deliveryType = deliveryType;
    }

    public double getPrice() {
        return price;
    }

    public int getTime() {
        return estimatedTime;
    }

    public Location getGuyLocation() {
        return  deliveryGuyLocation;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }
}
