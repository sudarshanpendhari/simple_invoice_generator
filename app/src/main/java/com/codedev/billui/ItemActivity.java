package com.codedev.billui;

import static com.codedev.billui.AddItemActivity.itemCost;
import static com.codedev.billui.AddItemActivity.itemList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class ItemActivity extends AppCompatActivity {

    DataTable dataTable;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        fab = findViewById(R.id.fab);
        dataTable = findViewById(R.id.item_data_table);
        DataTableHeader header = new DataTableHeader.Builder()
                .item("Item No.", 5)
                .item("Item Name", 5)
                .item("cost/unit", 5)

                .build();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ItemActivity.this,AddItemActivity.class));
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("items", null);
        String json2 = sharedPreferences.getString("costs", null);

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Type type2 = new TypeToken<ArrayList<String>>() {}.getType();

        itemList = gson.fromJson(json, type);
        itemCost = gson.fromJson(json2, type2);

        if (itemList == null) {
            itemList = new ArrayList<String>();
            itemCost = new ArrayList<Integer>();
        }

        ArrayList<DataTableRow> rows = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            DataTableRow row = new DataTableRow.Builder()
                    .value(String.valueOf(i + 1))
                    .value(String.valueOf(itemList.get(i)))
                    .value(String.valueOf(itemCost.get(i)))
                    .build();
            rows.add(row);
        }
        dataTable.setHeader(header);
        dataTable.setRows(rows);
        dataTable.inflate(this);


    }



}