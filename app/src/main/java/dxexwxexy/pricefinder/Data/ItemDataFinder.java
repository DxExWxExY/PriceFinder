package dxexwxexy.pricefinder.Data;

import java.util.Random;

public class ItemDataFinder {

    private String url;

    ItemDataFinder(String url) {
        this.url = url;
    }

    /**
     * Simulates a price generation using a range and an initial price.
     * @param initial Initial price of the item.
     * @return Randomized price from range.
     */
    public double updatePrice(double initial) {
        return Math.random() * ((initial*2 - initial/2) + 1) + (initial/2);
    }

    public double getPrice() {
        // TODO: 7/26/2018 Parse item price
        Random random = new Random();
        return random.nextInt(10000);
    }

    public String getStore() {
        // TODO: 7/26/2018 Parse item name
        return "Default";
    }
}
