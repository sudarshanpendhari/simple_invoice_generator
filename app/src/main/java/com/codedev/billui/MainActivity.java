package com.codedev.billui;


import android.app.Dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class MainActivity extends AppCompatActivity {

    Button gen,history,report,items,addDetails;
     static String shopName = "shop";
     static String shopAddress="Address";
    TextView t1,t2,tvIncome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gen=findViewById(R.id.genBill);
        history=findViewById(R.id.history);
        report=findViewById(R.id.report);
        items=findViewById(R.id.items);
        addDetails=findViewById(R.id.adddetails);
        t1=findViewById(R.id.textViewShopName);
        t2=findViewById(R.id.textViewShopAddress);
        tvIncome=findViewById(R.id.textViewIncome);

        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);

        Gson gson=new Gson();
        int json=sharedPreferences.getInt("income",0);



        Type type=new TypeToken<Integer>(){}.getType();


        int income=gson.fromJson(String.valueOf(json),type);

        tvIncome.setText(String.valueOf(income));

        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.layout_dialog);

                final EditText tsN=dialog.findViewById(R.id.edit_shopName);
                final EditText tsA=dialog.findViewById(R.id.edit_shopAddress);
                Button okButton=dialog.findViewById(R.id.okbutton);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shopName=tsN.getText().toString();
                        shopAddress=tsA.getText().toString();


                        t1.setText(shopName);
                        t2.setText(shopAddress);

                        dialog.dismiss();;
                    }
                });
                dialog.show();

            }
        });








        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,GenActivity.class);
                intent.putExtra("shopName",shopName);
                intent.putExtra("shopAddress",shopAddress);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ReportActivity.class));
            }
        });

        items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ItemActivity.class));
            }
        });
    }


}