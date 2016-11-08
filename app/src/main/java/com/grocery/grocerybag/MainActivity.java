package com.grocery.grocerybag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    public static boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!loggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // set toolbar
        Toolbar my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        getSupportActionBar().setTitle("Grocery Bag");
        getSupportActionBar().setIcon(R.drawable.gb_logo_white);

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
