package dxexwxexy.pricefinder.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Locale;

public class Item implements Parcelable, Comparable<Item> {

    /**
     * Fields used by Item.
     */
    private String name, url, store;
    private double initialPrice, currentPrice;
    private ItemDataFinder itemDataFinder;
    private Boolean isSelected;

    /**
     * Fields for sorting and website.
     */
    public static final Comparator<Item> COMPARE_BY_NAME = (a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
    public static final Comparator<Item> COMPARE_BY_DIFF = (a, b) -> a.getDifference().compareTo(b.getDifference());
    public static final Comparator<Item> COMPARE_BY_CURR = (a, b) -> Double.compare(Double.parseDouble(a.getCurrentPrice()), Double.parseDouble(b.getCurrentPrice()));
    public static final Comparator<Item> COMPARE_BY_STORE = (a, b) -> a.getStore().compareTo(b.getStore());

    Item(String name, String url, double initialPrice, double currentPrice) {
        itemDataFinder = new ItemDataFinder(url, initialPrice);
        this.name = name;
        this.url = url;
        this.initialPrice = initialPrice;
        this.currentPrice = currentPrice;
        this.isSelected = false;
        this.store = itemDataFinder.getStore();
    }

    /**
     * Default constructor.
     * @param name Item name.
     * @param url Item image URL.
     */
    public Item(String name, String url) {
        itemDataFinder = new ItemDataFinder(url);
        this.name = name;
        this.url = url;
        this.isSelected = false;
        this.store = itemDataFinder.getStore();
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
        itemDataFinder = new ItemDataFinder(url);
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
        return String.format(Locale.getDefault(), "%.2f", initialPrice);
    }

    public String getCurrentPrice() {
        return String.format(Locale.getDefault(), "%.2f", currentPrice);
    }

    public String getStore() {
        return store;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public String getDifference() {
        int difference = (int) (((currentPrice - initialPrice) / initialPrice ) * 100);
        if (Double.isNaN(difference)){
            return String.format(Locale.getDefault(), "%d", 0);
        } else {
            return String.format(Locale.getDefault(), "%d", difference);
        }
    }

    public String getURL() {
        return url;
    }

    public boolean isFetched() {
        return itemDataFinder.isFetched();
    }

    /**
     * Updates item current price using ItemDataFinder.
     */
    public void updateCurrentPrice() {
        if (initialPrice == 0.0) {
            this.initialPrice = itemDataFinder.getInitialPrice();
        }
        this.currentPrice = itemDataFinder.getCurrentPrice();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item that = (Item) obj;
            return this.name.equals(that.name) && this.url.equals(that.url);
        }
        return false;
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
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param that the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(@NonNull Item that) {
        return this.getDifference().compareTo(that.getDifference());
    }

    @Override
    public String toString() {
        return name + " " + initialPrice + " " + currentPrice;
    }

}
