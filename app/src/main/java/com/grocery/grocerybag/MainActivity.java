package com.grocery.grocerybag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

// alexis testing
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //comment added by Jon
        //comment added by Luke
    }

    public void onClick(View view) {
        Intent i = new Intent(this, ItemCacheActivity.class);
        startActivity(i);

    }

    public void viewListsClicked(View view) {
        Intent i2 = new Intent(this, ListActivity.class);
        startActivity(i2);
    }
}
