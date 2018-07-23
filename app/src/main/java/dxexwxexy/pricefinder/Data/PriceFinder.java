package dxexwxexy.pricefinder.Data;

public class PriceFinder {

    PriceFinder() { }

    /**
     * Simulates a price generation using a range and an initial price.
     * @param initial Initial price of the item.
     * @return Randomized price from range.
     */
    public double updatePrice(double initial) {
        return Math.random() * ((initial*2 - initial/2) + 1) + (initial/2);
    }
}
