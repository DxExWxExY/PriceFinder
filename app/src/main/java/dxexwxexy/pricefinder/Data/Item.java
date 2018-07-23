package dxexwxexy.pricefinder.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Locale;

public class Item implements Parcelable {

    /**
     * Fields used by Item.
     */
    private String name, url;
    private double initialPrice;
    private double currentPrice;
    private static PriceFinder priceFinder;

    /**
     * Default constructor.
     * @param name Item name.
     * @param url Item image URL.
     * @param initialPrice Item initial price.
     */
    public Item(String name, String url, double initialPrice) {
        priceFinder = new PriceFinder();
        this.name = name;
        this.url = url;
        this.initialPrice = initialPrice;
        this.currentPrice = initialPrice;
    }

    /**
     * Parcel constructor.
     * @param in Parcel bundle.
     */
    private Item(Parcel in) {
        name = in.readString();
        url = in.readString();
        initialPrice = in.readDouble();
        currentPrice = in.readDouble();
        priceFinder = new PriceFinder();
    }

    /**
     * {@inheritDoc}
     */
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    /**
     * Getter methods.
     */
    public String getName() {
        return name;
    }

    public String getInitialPrice() {
        return String.valueOf(initialPrice);
    }

    public String getCurrentPrice() {
        return String.format(Locale.getDefault(), "%.2f", currentPrice);
    }

    public String getDifference() {
        return String.format(Locale.getDefault(), "%.0f", (((currentPrice - initialPrice) / initialPrice ) * 100));
    }

    public String getURL() {
        return url;
    }

    /**
     * Updates item current price using PriceFinder.
     */
    public void updateCurrentPrice() {
        this.currentPrice = priceFinder.updatePrice(initialPrice);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return hashCode();
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeDouble(initialPrice);
        dest.writeDouble(currentPrice);
        Log.d("++=++++++++++++++++=", "WRITTEN");
    }
}
