package com.grocery.grocerybag;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DBAdapter db;
    private ListView lv;
    ArrayList<Integer> listIds= new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        db = new DBAdapter(this);
        lv = (ListView)findViewById(R.id.listList);

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

        Cursor c;
        ArrayList<String> listNames = new ArrayList<String>();
        //get all the outfits and put then into the arrayList
        db.open();
        c = db.getAllLists();
        if(c.moveToFirst())
        {
            do {
                listNames.add(c.getString(1));
                listIds.add(c.getInt(0));
                //DisplayContact(c);
            }while(c.moveToNext());
        }
        db.close();

        //use the array list to fill out the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);


    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, ListDetailsActivity.class);
        i.putExtra("action", "edit");
        int thisID = listIds.get(position);
        i.putExtra("id", thisID);
        startActivity(i);
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

    public void addListClicked(View view) {
        Intent i = new Intent(this, ListCreationActivity.class);
        startActivity(i);
    }
}
