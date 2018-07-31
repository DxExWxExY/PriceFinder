package dxexwxexy.pricefinder.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ItemDatabase extends SQLiteOpenHelper {

    private static final String TB_NAME = "items";
    private static final String NAME = "product";
    private static final String INITIAL = "initial_price";
    private static final String CURRENT = "current_price";
    private static final String URL = "url";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public ItemDatabase(Context context) {
        super(context, TB_NAME, null, 1);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TB_NAME + " ( " + NAME + " TEXT, "
                + URL + " TEXT, "
                + INITIAL + " REAL, "
                + CURRENT + " REAL" +" )");
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public void addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, item.getName());
        contentValues.put(URL, item.getURL());
        contentValues.put(INITIAL, item.getInitialPrice());
        contentValues.put(CURRENT, item.getCurrentPrice());
        db.insert(TB_NAME, null, contentValues);
    }

    public void deleteItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TB_NAME + " WHERE " + URL + "=" + "'" + item.getURL() + "'");
    }

    public void editItem(Item item, String name, String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(URL, url);
        db.update(TB_NAME, contentValues, URL + "=?", new String[]{item.getURL()});
    }

    public ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        if (data.getCount() == 0) {
            return new ArrayList<>();
        }
        while (data.moveToNext()) {
            items.add(new Item(data.getString(0), data.getString(1),
                    data.getDouble(2), data.getDouble(3)));
        }
        data.close();
        return items;
    }
}