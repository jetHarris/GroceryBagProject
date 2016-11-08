package com.grocery.grocerybag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ListCreationActivity extends AppCompatActivity {

    private DBAdapter db;
    EditText nameView;
    EditText budgetView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_creation);
        db = new DBAdapter(this);
        nameView = (EditText)findViewById(R.id.nameText);
        budgetView = (EditText)findViewById(R.id.budgetText);
    }

    public void addListClicked(View view) {
        db.open();
        String name = nameView.getText().toString();
        float budget = Float.parseFloat(budgetView.getText().toString());
        db.insertList(name, budget, 1);
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
        db.close();
    }
}
