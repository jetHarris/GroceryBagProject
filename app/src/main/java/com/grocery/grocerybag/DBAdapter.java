package com.grocery.grocerybag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lwong on 9/28/2015.
 */

//modified by Luke Harris for use in the JocelynDressUp app
public class DBAdapter {


    //INSERT INTO Items (item_name, price, sale_price, use_sale_price, GST, PST, HST)
    //VALUES ('Milk', 4.29, 3.49, 0, 0, 0, 0);
    //columns that will be used
    static final String KEY_ROWID = "_id";
    static final String KEY_ITEM_NAME = "item_name";
    static final String KEY_PRICE = "price";
    static final String KEY_SALE_PRICE = "sale_price";
    static final String KEY_USE_SALE_PRICE = "use_sale_price";
    static final String KEY_GST = "GST";
    static final String KEY_PST = "PST";
    static final String KEY_HST = "HST";
    //name of the adapter
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "items";
    static final int DATABASE_VERSION = 3;

    static final String DATABASE_CREATE =
            "create table items (_id integer primary key autoincrement, "
                    + "item_name text not null, price DECIMAL (6,2), sale_price DECIMAL (6,2), use_sale_price BIT(1), GST BIT(1), PST BIT(1), HST BIT(1) );";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_ITEM_NAME, "Eggs");
            initialValues.put(KEY_PRICE, 10.0);
            initialValues.put(KEY_SALE_PRICE, 10.0);
            initialValues.put(KEY_USE_SALE_PRICE, 0);
            initialValues.put(KEY_GST, 0);
            initialValues.put(KEY_PST, 0);
            initialValues.put(KEY_HST, 0);
            db.insert(DATABASE_TABLE, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS items");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //inserts an outfit into the database
    //INSERT INTO Items (item_name, price, sale_price, use_sale_price, GST, PST, HST)
    //VALUES ('Milk', 4.29, 3.49, 0, 0, 0, 0);
    public long insertItem(String name, float price, float sale_price, int use_sale_price, int GST, int PST, int HST)
    {

//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_ITEM_NAME, "Eggs");
//        initialValues.put(KEY_PRICE, 10.0);
//        initialValues.put(KEY_SALE_PRICE, 10.0);
//        initialValues.put(KEY_USE_SALE_PRICE, 0);
//        initialValues.put(KEY_GST, 0);
//        initialValues.put(KEY_PST, 0);
//        initialValues.put(KEY_HST, 0);
//        return db.insert(DATABASE_TABLE, null, initialValues);


        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM_NAME, name);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_SALE_PRICE, sale_price);
        initialValues.put(KEY_USE_SALE_PRICE, use_sale_price);
        initialValues.put(KEY_GST, GST);
        initialValues.put(KEY_PST, PST);
        initialValues.put(KEY_HST, HST);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //deletes an outfit
    public boolean deleteItem(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //gets all outfits from the database
    public Cursor getAllItems()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ITEM_NAME,
                KEY_PRICE, KEY_SALE_PRICE,KEY_USE_SALE_PRICE,KEY_GST, KEY_PST,KEY_HST}, null, null, null, null, null);
    }

    //gets an outfit by id from the database
    public Cursor getItem(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_ITEM_NAME, KEY_PRICE, KEY_SALE_PRICE,KEY_USE_SALE_PRICE,KEY_GST,KEY_PST,KEY_HST}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    //---updates an outfit---
    public boolean updateItem(long rowId, String name, float price, float sale_price, int use_sale_price, int GST, int PST, int HST)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ITEM_NAME, name);
        args.put(KEY_PRICE, price);
        args.put(KEY_SALE_PRICE, sale_price);
        args.put(KEY_USE_SALE_PRICE, use_sale_price);
        args.put(KEY_GST, GST);
        args.put(KEY_PST, PST);
        args.put(KEY_HST, HST);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
