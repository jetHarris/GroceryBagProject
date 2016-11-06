package com.grocery.grocerybag;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ItemCacheActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DBAdapter db;
    private ListView lv;
    ArrayList<Integer> itemIds= new ArrayList<Integer>();
    ArrayList<Integer> checkedItemIds= new ArrayList<Integer>();
    boolean addingItemsToList = false;
    int listId;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_cache);
        db = new DBAdapter(this);
        lv = (ListView)findViewById(R.id.itemList);
        addBtn = (Button)findViewById(R.id.addItemsBtn);

        //get the existing database file or from assets folder if doesn't exist
        try
        {
            String destPath = "/data/data/" + getPackageName() + "/databases";
            File f = new File(destPath);
            if(!f.exists()){
                f.mkdirs();
                f.createNewFile();
                //copy from the db from the assets fodler
                CopyDB(getBaseContext().getAssets().open("mydb"), new FileOutputStream(destPath + "/MyDB"));
            }
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

        lv = (ListView)findViewById(R.id.itemList);

        if (getIntent().getExtras() != null) {
            listId = getIntent().getExtras().getInt("listid");
            if (listId != 0) {
                addingItemsToList = true;
                addBtn.setText("Add Items to List");
            }
        }

        Cursor c;
        ArrayList<String> itemNames = new ArrayList<String>();
        //get all the outfits and put then into the arrayList
        db.open();
        c = db.getAllItems();
        if(c.moveToFirst())
        {
            do {
                itemNames.add(c.getString(1));
                itemIds.add(c.getInt(0));
                //DisplayContact(c);
            }while(c.moveToNext());
        }
        db.close();

        if(addingItemsToList){
            db.open();
            ArrayList<Integer> tempItems = db.getAllItemsForList(listId);

            for(int i = 0; i < tempItems.size();++i){
                String itemName = db.getItemName(tempItems.get(i));
                itemNames.remove(itemName);
                itemIds.remove(tempItems.get(i));
            }
            db.close();

        }

        //use the array list to fill out the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemNames);
        if(addingItemsToList){
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_checked, itemNames);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(addingItemsToList){
            CheckedTextView cv = (CheckedTextView)view;
            if(cv.isChecked()) {
                checkedItemIds.add(itemIds.get(position));
            }
            else
                checkedItemIds.remove(itemIds.get(position));

        }
        else {
            String selectedFromList = (String) (lv.getItemAtPosition(position));
            Intent i = new Intent(this, ItemDetailsActivity.class);
            i.putExtra("action", "edit");
            int thisID = itemIds.get(position);
            i.putExtra("id", thisID);
            startActivity(i);
        }
    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
        //Copy one byte at a time
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0){
            outputStream.write(buffer,0,length);
        }
        inputStream.close();  //close streams
        outputStream.close();
    }

    public void addItemClick(View view) {
        if(addingItemsToList)
        {
            //add all the items to the list
            db.open();
            db.insertItemsIntoList(checkedItemIds, listId);
            db.close();

            Intent i = new Intent(this, ListDetailsActivity.class);
            i.putExtra("id", listId);
            startActivity(i);
        }
        else {
            Intent i = new Intent(this, ItemDetailsActivity.class);
            i.putExtra("action", "add");
            startActivity(i);
        }
    }

    public void backToMainClicked(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
