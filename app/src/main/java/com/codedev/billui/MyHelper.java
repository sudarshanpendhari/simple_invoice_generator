package com.codedev.billui;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class MyHelper<contentValues> extends SQLiteOpenHelper {
    public MyHelper(@Nullable Context context) {
        super(context,"MyDatabase",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable="create table myTable(invoiceNO INTEGER PRIMARY KEY AUTOINCREMENT,customerName TEXT,contactNo TEXT,date INTEGER,item TEXT,qty TEXT,amount TEXT,rates TEXT);";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String customerName, String contactNo, Long date, String item, String qty, String amount, String rates){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();


        contentValues.put("customerName",customerName);

        contentValues.put("contactNo",contactNo);
        contentValues.put("date",date);

        contentValues.put("item", item);
        contentValues.put("qty",qty);
        contentValues.put("amount",amount);
        contentValues.put("rates",rates);

        sqLiteDatabase.insert("myTable",null,contentValues);

    }
}
