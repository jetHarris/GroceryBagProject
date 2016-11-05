package com.grocery.grocerybag;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListDetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DBAdapter db;
    ListView lv;
    ArrayList<Integer> itemIds= new ArrayList<Integer>();
    ArrayList<Boolean> checks= new ArrayList<Boolean>();
    int listID;
    TextView label;
    ArrayList<Integer> listItemIds= new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        db = new DBAdapter(this);
        lv = (ListView)findViewById(R.id.itemListList);
        label = (TextView)findViewById(R.id.nameLabel);

        if( getIntent().getExtras() != null)
        {
            listID= getIntent().getExtras().getInt("id");
        }

        Cursor c;
        ArrayList<String> itemNames = new ArrayList<String>();
        //get all the outfits and put then into the arrayList
        db.open();
        //c = db.getAllItemsForList(listID);
        itemIds = db.getAllItemsForList(listID);
        listItemIds = db.getAllListItemIDsForList(listID);
        label.setText(db.getListName(listID));
        checks = db.getAllChecksForList(listID);

        for(int i = 0; i < itemIds.size();++i)
        {
            itemNames.add(db.getItemName(itemIds.get(i)));
        }
        db.close();

        //use the array list to fill out the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, itemNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                CheckedTextView cv = (CheckedTextView)arg1;
                cv.setChecked(!cv.isChecked());
                checks.set(pos,!checks.get(pos));
                db.open();
                if(checks.get(pos)){
                    db.updateListItemCheck(listItemIds.get(pos),1);
                }
                else{
                    db.updateListItemCheck(listItemIds.get(pos),0);
                }
                db.close();
                return true;
            }
        });

        for(int i = 0; i < checks.size();++i)
        {
            lv.setItemChecked(i,false);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, ItemDetailsActivity.class);
        i.putExtra("listid", listID);
        i.putExtra("list_item_id",listItemIds.get(position));
        int thisID = itemIds.get(position);
        i.putExtra("id", thisID);
        startActivity(i);
    }

    public void addItemClicked(View view) {
        Intent i = new Intent(this, ItemCacheActivity.class);
        i.putExtra("listid", listID);
        startActivity(i);
    }
}
