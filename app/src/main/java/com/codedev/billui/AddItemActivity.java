package com.codedev.billui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

     public static ArrayList<String> itemList;
      public static ArrayList<Integer> itemCost;
    Button save,back;

    private EditText itemName,itemcost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_item);

        save=findViewById(R.id.save);

        itemName=findViewById(R.id.editTextItemName);
        itemcost=findViewById(R.id.editTextItemCost);










        

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itemList==null ||itemCost==null){
                    itemList=new ArrayList<String>();
                    itemCost=new ArrayList<Integer>();
                }



                itemList.add(itemName.getText().toString());
                itemCost.add(Integer.parseInt(itemcost.getText().toString()));
                Toast.makeText(AddItemActivity.this, "Item saved", Toast.LENGTH_SHORT).show();
                itemName.setText("");
                itemcost.setText("");

                SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                Gson gson=new Gson();
                String json=gson.toJson(itemList);
                String json2=gson.toJson(itemCost);

                editor.putString("items",json);
                editor.putString("costs",json2);

                editor.apply();
                Toast.makeText(AddItemActivity.this, "saved to Shared Prefs", Toast.LENGTH_SHORT).show();
            }
        });


    }


}