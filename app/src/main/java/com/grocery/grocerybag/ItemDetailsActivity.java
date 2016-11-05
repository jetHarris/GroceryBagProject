package com.grocery.grocerybag;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ItemDetailsActivity extends AppCompatActivity {

    boolean adding = false;
    private DBAdapter db;
    EditText name;
    EditText price;
    EditText sales_price;
    CheckBox use_sales_price;
    CheckBox gstC;
    CheckBox pstC;
    CheckBox hstC;
    int itemID = -1;
    boolean editingList = false;
    int listID = 0;
    Button saveBtn;
    Button deleteBtn;
    EditText quantityText;
    int listItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        db = new DBAdapter(this);

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

        name = (EditText)findViewById(R.id.NameText);
        price = (EditText)findViewById(R.id.PriceText);
        sales_price = (EditText)findViewById(R.id.SalesPriceText);
        use_sales_price = (CheckBox)findViewById(R.id.useSalesPrice);
        gstC = (CheckBox)findViewById(R.id.GST);
        pstC = (CheckBox)findViewById(R.id.PST);
        hstC = (CheckBox)findViewById(R.id.HST);
        saveBtn = (Button)findViewById(R.id.SaveButton);
        deleteBtn = (Button)findViewById(R.id.buttonDelete);
        quantityText = (EditText)findViewById(R.id.QuantityText);


        //get intent which would be from the outfits page
        //then use the outfit name to set the images appropriately
        if( getIntent().getExtras() != null)
        {
            String action = getIntent().getExtras().getString("action");
            if (action != null)
                adding = true;
            else
            {
                itemID = getIntent().getExtras().getInt("id");
                if(getIntent().getExtras().getInt("listid") != 0) {
                    listID = getIntent().getExtras().getInt("listid");
                    editingList = true;
                    listItemId = getIntent().getExtras().getInt("list_item_id");
                }
            }
        }

        if(editingList){
            saveBtn.setText("Update");
            deleteBtn.setText("Delete from list");
            quantityText.setVisibility(View.VISIBLE);
        }


        if(itemID != -1) {
            Cursor c;
            db.open();
            c = db.getItem(itemID);

            if (c.moveToFirst()) {

                name.setText(c.getString(1));
                price.setText(c.getString(2));
                sales_price.setText(c.getString(3));

                if(c.getInt(4) == 1)
                    use_sales_price.setChecked( true);
                if(c.getInt(5) == 1)
                    gstC.setChecked( true);
                if(c.getInt(6) == 1)
                    pstC.setChecked( true);
                if(c.getInt(7) == 1)
                    hstC.setChecked( true);

                if(editingList)
                {
                    int temp = db.listItemQuantity(listItemId);
                    quantityText.setText(""+temp);
                }

            } else {
                Toast.makeText(this, "Get failed on " + itemID, Toast.LENGTH_SHORT).show();
            }
            db.close();
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


    public void saveClicked(View view) {

        int use_sales = 0;
        if(use_sales_price.isChecked())
            use_sales = 1;

        int gst = 0;
        if(gstC.isChecked())
            gst = 1;
        int pst = 0;
        if(pstC.isChecked())
            pst = 1;
        int hst = 0;
        if(hstC.isChecked())
            hst = 1;

        if(editingList){
            int quantity = Integer.parseInt(quantityText.getText().toString());
            db.open();
            db.updateItem(itemID, name.getText().toString(),Float.parseFloat(price.getText().toString()),
                    Float.parseFloat(sales_price.getText().toString()), use_sales,gst,pst,hst);
            db.updateListItem(listItemId,quantity,itemID,listID,0);
            Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
            db.close();
            Intent i = new Intent(this, ListDetailsActivity.class);
            i.putExtra("id", listID);
            startActivity(i);
        }
        else if(adding){
            db.open();
            db.insertItem(name.getText().toString(),Float.parseFloat(price.getText().toString()),
                    Float.parseFloat(sales_price.getText().toString()), use_sales,gst,pst,hst);
            Toast.makeText(this, "Item Inserted", Toast.LENGTH_SHORT).show();
            db.close();
            Intent i = new Intent(this, ItemCacheActivity.class);
            startActivity(i);
        }
        else
        {
            db.open();
            db.updateItem(itemID, name.getText().toString(),Float.parseFloat(price.getText().toString()),
                    Float.parseFloat(sales_price.getText().toString()), use_sales,gst,pst,hst);
            Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
            db.close();
            Intent i = new Intent(this, ItemCacheActivity.class);
            startActivity(i);
        }



    }

    public void cancelClicked(View view) {
        if(editingList){
            Intent i = new Intent(this, ListDetailsActivity.class);
            i.putExtra("id", listID);
            startActivity(i);
        }
        else {
            Intent i = new Intent(this, ItemCacheActivity.class);
            startActivity(i);
        }
    }

    public void deleteClicked(View view) {
        if(editingList){
            db.open();
            db.deleteListItem(listItemId);
            Toast.makeText(this, "Item Removed from list", Toast.LENGTH_SHORT).show();
            db.close();
            Intent i = new Intent(this, ListDetailsActivity.class);
            i.putExtra("id", listID);
            startActivity(i);
        }
        else if(itemID != -1)
        {
            db.open();
            db.deleteItem(itemID);
            Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
            db.close();
            Intent i = new Intent(this, ItemCacheActivity.class);
            startActivity(i);
        }

    }
}
