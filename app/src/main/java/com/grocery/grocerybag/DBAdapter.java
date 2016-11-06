package com.grocery.grocerybag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by GroceryBag Team on 10-28-2016.
 */

//modified by Luke Harris for use in the GroceryBag app
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
    static final String ITEM_TABLE = "items";
    static final String LIST_ITEMS_TABLE = "list_items";
    static final String LIST_TABLE = "lists";
    static final String USER_TABLE = "users";
    static final int DATABASE_VERSION = 4;

    static final String CREATE_ITEM_TABLE =
            "create table items (_id integer primary key autoincrement, "
                    + "item_name text not null, price DECIMAL (6,2), sale_price DECIMAL (6,2), use_sale_price BIT(1), GST BIT(1), PST BIT(1), HST BIT(1) );";

    static final String CREATE_LIST_ITEM_TABLE =
            "create table list_items (_id integer primary key autoincrement, "
                    + "quantity integer, item_id integer, list_id integer, checked BIT(1) );";

    static final String CREATE_LIST_TABLE =
            "create table lists (_id integer primary key autoincrement, "
                    + "list_name text not null, budget DECIMAL (6,2), list_owner_id integer );";

    static final String CREATE_USER_TABLE =
            "create table users (_id integer primary key autoincrement, "
                    + "firstname text not null, lastname text, password text);";

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
                db.execSQL(CREATE_ITEM_TABLE);
                db.execSQL(CREATE_LIST_TABLE);
                db.execSQL(CREATE_LIST_ITEM_TABLE);
                db.execSQL(CREATE_USER_TABLE);
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
            db.insert(ITEM_TABLE, null, initialValues);


            ContentValues initialValues2 = new ContentValues();
            initialValues2.put("list_name", "Sunday Shopping");
            initialValues2.put("budget", 10.0);
            initialValues2.put("list_owner_id", 1);
            db.insert(LIST_TABLE, null, initialValues2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS items");
            db.execSQL("DROP TABLE IF EXISTS list_items");
            db.execSQL("DROP TABLE IF EXISTS lists");
            db.execSQL("DROP TABLE IF EXISTS users");
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

    //inserts an item into the database
    //INSERT INTO Items (item_name, price, sale_price, use_sale_price, GST, PST, HST)
    //VALUES ('Milk', 4.29, 3.49, 0, 0, 0, 0);
    public long insertItem(String name, float price, float sale_price, int use_sale_price, int GST, int PST, int HST)
    {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM_NAME, name);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_SALE_PRICE, sale_price);
        initialValues.put(KEY_USE_SALE_PRICE, use_sale_price);
        initialValues.put(KEY_GST, GST);
        initialValues.put(KEY_PST, PST);
        initialValues.put(KEY_HST, HST);
        return db.insert(ITEM_TABLE, null, initialValues);
    }

    public long insertList(String name, float price, int id)
    {
        try {
            ContentValues initialValues2 = new ContentValues();
            initialValues2.put("list_name", name);
            initialValues2.put("budget", price);
            initialValues2.put("list_owner_id", id);
            return db.insert(LIST_TABLE, null, initialValues2);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return 0;
    }

    public void insertItemsIntoList(ArrayList<Integer> itemIds, int listID)
    {
        try {
            for(int i = 0; i < itemIds.size(); ++i) {
                ContentValues initialValues = new ContentValues();
                initialValues.put("quantity", 1);
                initialValues.put("item_id", itemIds.get(i));
                initialValues.put("list_id", listID);
                initialValues.put("checked", 0);
                db.insert(LIST_ITEMS_TABLE, null, initialValues);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    //deletes an item
    public boolean deleteItem(long rowId)
    {
        return db.delete(ITEM_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //deletes an List Item
    public boolean deleteListItem(long rowID)
    {
        return db.delete(LIST_ITEMS_TABLE, KEY_ROWID + "=" + rowID, null) > 0;
    }

    //gets all items from the database
    public Cursor getAllItems()
    {
        return db.query(ITEM_TABLE, new String[] {KEY_ROWID, KEY_ITEM_NAME,
                KEY_PRICE, KEY_SALE_PRICE,KEY_USE_SALE_PRICE,KEY_GST, KEY_PST,KEY_HST}, null, null, null, null, null);
    }

    //gets all lists from the database
    public Cursor getAllLists()
    {
        return db.query(LIST_TABLE, new String[] {KEY_ROWID, "list_name",
                "budget", "list_owner_id"}, null, null, null, null, null);
    }


    //gets all list items from the database
    public ArrayList<Integer> getAllItemsForList(int id)
    {

        Cursor c;
        ArrayList<Integer> itemIds = new ArrayList<Integer>();
        //get all the item and put then into the arrayList
        c = db.query(LIST_ITEMS_TABLE, new String[] {KEY_ROWID, "quantity",
                "item_id", "list_id","checked"},"list_id" + "=" + id, null, null, null, null, null);
        if(c.moveToFirst())
        {
            do {
                itemIds.add(c.getInt(2));
            }while(c.moveToNext());
        }
        c.close();
        return itemIds;

    }

    //gets all list items from the database
    public ArrayList<Boolean> getAllChecksForList(int id)
    {

        Cursor c;
        ArrayList<Boolean> itemChecks= new ArrayList<Boolean>();
        //get all the checked statuses and put then into the arrayList
        c = db.query(LIST_ITEMS_TABLE, new String[] {KEY_ROWID, "quantity",
                "item_id", "list_id","checked"},"list_id" + "=" + id, null, null, null, null, null);
        if(c.moveToFirst())
        {
            do {
                itemChecks.add(c.getInt(4) != 0);
            }while(c.moveToNext());
        }
        c.close();
        return itemChecks;

    }

    //gets all items from the database
    public ArrayList<Integer> getAllListItemIDsForList(int id)
    {

        Cursor c;
        ArrayList<Integer> itemIds = new ArrayList<Integer>();
        //get all the items and put then into the arrayList
        c = db.query(LIST_ITEMS_TABLE, new String[] {KEY_ROWID, "quantity",
                "item_id", "list_id","checked"},"list_id" + "=" + id, null, null, null, null, null);
        if(c.moveToFirst())
        {
            do {
                itemIds.add(c.getInt(0));
            }while(c.moveToNext());
        }
        c.close();
        return itemIds;

    }

    //gets an item name by id from the database
    public String getItemName(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, ITEM_TABLE, new String[] {KEY_ROWID,
                                KEY_ITEM_NAME, KEY_PRICE, KEY_SALE_PRICE,KEY_USE_SALE_PRICE,KEY_GST,KEY_PST,KEY_HST}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            String name = mCursor.getString(1);
            mCursor.close();
            return name;
        }
        return "";
    }

    public String getListName(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, LIST_TABLE, new String[] {KEY_ROWID,
                                "list_name", "budget", "list_owner_id"}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            String name = mCursor.getString(1);
            mCursor.close();
            return name;
        }
        return "";
    }

    //gets an item by id from the database
    public Cursor getItem(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, ITEM_TABLE, new String[] {KEY_ROWID,
                                KEY_ITEM_NAME, KEY_PRICE, KEY_SALE_PRICE,KEY_USE_SALE_PRICE,KEY_GST,KEY_PST,KEY_HST}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        mCursor.close();
        return mCursor;
    }

    //gets an list item quantity by id from the database
    public int listItemQuantity(int list_item_id) throws SQLException
    {
        //Cursor mCursor = db.rawQuery("select * from "+LIST_ITEMS_TABLE+" where item_id= "+item_id+" and list_id = " + list_id);
        Cursor mCursor =
                db.query(true, LIST_ITEMS_TABLE, new String[] {KEY_ROWID,
                                "quantity", "item_id", "list_id","checked"}, KEY_ROWID + "=" + list_item_id, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            return mCursor.getInt(1);
        }

        mCursor.close();
        return 0;
    }


    //---updates an Item---
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
        return db.update(ITEM_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateListItem(int rowID,int quantity, int item_id, int list_id, int checked)
    {
        ContentValues args = new ContentValues();
        args.put("quantity", quantity);
        args.put("item_id", item_id);
        args.put("list_id", list_id);
        args.put("checked", checked);
        return db.update(LIST_ITEMS_TABLE, args, KEY_ROWID + "=" + rowID, null) > 0;
    }

    public void updateListItemCheck(int rowID, int checked)
    {
        ContentValues args = new ContentValues();
        args.put("checked", checked);
        db.update(LIST_ITEMS_TABLE, args, KEY_ROWID + "=" + rowID, null);
    }

}
