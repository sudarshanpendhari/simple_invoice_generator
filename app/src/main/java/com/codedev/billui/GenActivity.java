package com.codedev.billui;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.codedev.billui.AddItemActivity.itemCost;
import static com.codedev.billui.AddItemActivity.itemList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GenActivity extends AppCompatActivity {

    // variables for our buttons.
    Button saveprintbtn,printbtn,addItem,save,saveitemtolist,addreal;
    EditText name ,phone,qnty,itemName,itemcost;
    LinearLayout itemLL,itemdiscLL;
    TextView textItems;
    Spinner spinner;


    //    ArrayList<String> itemList;
    ArrayList<String>item;
    ArrayList<Integer>qty;
    ArrayList<Integer>amount;
//    ArrayList<Integer> itemCost;
    ArrayAdapter<String>adapter;
    MyHelper myHelper;
    String shopName;
    String shopAddress;
    SQLiteDatabase sqLiteDatabase;
    Date date=new Date();
    String dateFormat="dd-MM-yyyy";
    SimpleDateFormat datePatternFormat=new SimpleDateFormat(dateFormat);
    String timePattern="hh:mm a";
    SimpleDateFormat timePatternFormat=new SimpleDateFormat(timePattern);
    ScrollView scrollView;

    // declaring width and height
    // for our PDF file.
    int pageHeight = 900;
    int pagewidth = 1000;



    //creating a bitmap variable
    //for storing our images


    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    int total;
    int factor;
    int i=0;
    private ArrayList<Integer> rate;
      int income;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gen_activity);



        myHelper=new MyHelper(this);
        sqLiteDatabase=myHelper.getWritableDatabase();
        // initializing our variables.
        saveprintbtn = findViewById(R.id.btnSaveAndPrint);
        addreal=findViewById(R.id.additem);

        textItems=findViewById(R.id.textitems);
        saveitemtolist=findViewById(R.id.saveitemtolist);
        itemdiscLL=findViewById(R.id.LLitem);
        name=findViewById(R.id.editTextName);
        scrollView=findViewById(R.id.textScroll);
        phone=findViewById(R.id.editTextPhone);
        qnty=findViewById(R.id.editTextQty);
        spinner=findViewById(R.id.spinner);
        itemLL=findViewById(R.id.itemLinLay);
        itemName=findViewById(R.id.editTextItemName);
        itemcost=findViewById(R.id.editTextItemCost);

        shopName= "";
        shopAddress= "";

        shopName=getIntent().getStringExtra("shopName");
        shopAddress=getIntent().getStringExtra("shopAddress");







        item=new ArrayList<>();
        qty=new ArrayList<>();
        amount=new ArrayList<>();
        rate=new ArrayList<>();
        saveitemtolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addreal.setVisibility(View.VISIBLE);
                itemdiscLL.setVisibility(View.GONE);
                String it=spinner.getSelectedItem().toString();
                int q=Integer.parseInt(String.valueOf(qnty.getText()));
                int r=Integer.parseInt(String.valueOf(AddItemActivity.itemCost.get(spinner.getSelectedItemPosition())));
                int amt=q*Integer.parseInt(String.valueOf(AddItemActivity.itemCost.get(spinner.getSelectedItemPosition())));
                if(item==null||qty==null||amount==null)
                {
                item=new ArrayList<>();
                qty=new ArrayList<>();
                amount=new ArrayList<>();
                rate=new ArrayList<>();
                }
                    item.add(it);
                    qty.add(q);
                    amount.add(amt);
                    rate.add(r);

                    qnty.setText("1");
                    i++;

                    textItems.append(i+"    "+it+"    "+q+"      "+r+"      "+amt+"\n");
                    scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });

        addreal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdiscLL.setVisibility(View.VISIBLE);
            }
        });



        loadData();






        adapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, itemList);
        spinner.setAdapter(adapter);




        // below code is used for
        // checking our permissions.
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        saveprintbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty()||!phone.getText().toString().isEmpty()) {
                    callOnCLickListener();
                } else {
                    Toast.makeText(GenActivity.this, "Enter customer details.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void loadData() {
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);

        Gson gson=new Gson();
        String json=sharedPreferences.getString("items",null);
        String json2=sharedPreferences.getString("costs",null);

        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        Type type2=new TypeToken<ArrayList<String>>(){}.getType();


        itemList=gson.fromJson(json,type);
        AddItemActivity.itemCost=gson.fromJson(json2,type2);

        if(itemList==null ){
            itemList=new ArrayList<String>();
            itemCost=new ArrayList<Integer>();
        }
    }

    private void callOnCLickListener() {

                String customerName=String.valueOf(name.getText());
                String contactNo=String.valueOf(phone.getText());
                StringBuilder itemm=new StringBuilder();
                StringBuilder ratee=new StringBuilder();
                StringBuilder qtyy=new StringBuilder();
                StringBuilder amountt=new StringBuilder();

                for(int i=0;i<item.size();i++){
                    itemm.append(item.get(i)+",");
                    qtyy.append(qty.get(i)+",");
                    amountt.append(amount.get(i)+",");
                    ratee.append(rate.get(i)+",");
                }
                String items=itemm.toString();
                String qtys=qtyy.toString();
                String amounts=amountt.toString();
                String rates=ratee.toString();
//                String shop=shopName.getText().toString();
//                String shopAdd=shopAddress.getText().toString();


                myHelper.insert(customerName,contactNo,date.getTime(),items,qtys,amounts,rates);

                generatePDF();






    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generatePDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        //  variables for paint "paint" is used

        // for adding text in our PDF file.
        Paint myPaint = new Paint();

        String[] columns = {"invoiceNo", "CustomerName", "contactNo", "date", "item", "qty", "amount", "rates"};
        Cursor cursor = sqLiteDatabase.query("myTable", columns, null, null, null, null, null);
        cursor.move(cursor.getCount());

        String[] itemList = cursor.getString(4).split(",");
        String[] qtyList = cursor.getString(5).split(",");
        String[] amountList = cursor.getString(6).split(",");
        String[] rateList = cursor.getString(7).split(",");
        factor = itemList.length * 30;


        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight + (factor - 50), 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        myPaint.setTextSize(80);
        canvas.drawText(shopName, 30, 80, myPaint);
        myPaint.setTextSize(30);
        canvas.drawText(shopAddress, 30, 120, myPaint);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Invoice No.", canvas.getWidth() - 40, 40, myPaint);

        canvas.drawText(String.valueOf(cursor.getInt(0)), canvas.getWidth() - 40, 80, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);

        myPaint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 150, canvas.getWidth() - 30, 160, myPaint);

        myPaint.setColor(Color.BLACK);
        canvas.drawText("Date", 50, 200, myPaint);
        canvas.drawText(datePatternFormat.format(cursor.getLong(3)), 250, 200, myPaint);

        canvas.drawText("Time", 620, 200, myPaint);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timePatternFormat.format(cursor.getLong(3)), canvas.getWidth() - 40, 200, myPaint);
        myPaint.setTextAlign(Paint.Align.LEFT);

        myPaint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 250, 250, 300, myPaint);

        myPaint.setColor(Color.WHITE);
        canvas.drawText("Bill To:", 50, 285, myPaint);

        myPaint.setColor(Color.BLACK);
        canvas.drawText("Customer Name: ", 30, 350, myPaint);
        canvas.drawText(cursor.getString(1), 280, 350, myPaint);

        canvas.drawText("Phone: ", 620, 350, myPaint);
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(cursor.getString(2), canvas.getWidth() - 40, 350, myPaint);
        myPaint.setTextAlign(Paint.Align.LEFT);

        myPaint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 400, canvas.getWidth() - 30, 450, myPaint);


        myPaint.setColor(Color.WHITE);
        canvas.drawText("Item", 70, 435, myPaint);
        canvas.drawText("Qty", 432, 435, myPaint);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Rate", canvas.getWidth() - 300, 435, myPaint);
        canvas.drawText("Amount", canvas.getWidth() - 40, 435, myPaint);
        myPaint.setTextAlign(Paint.Align.LEFT);

        int y = 480;
        int x = 440;
        int x1 = 55 + 23;
        int x2 = canvas.getWidth() - 300;
        total = 0;
        for (int i = 0; i < itemList.length; i++) {

            myPaint.setColor(Color.BLACK);
            canvas.drawText(itemList[i], x1, y, myPaint);

            canvas.drawText(qtyList[i], x, y, myPaint);

            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(rateList[i], x2, y, myPaint);
            canvas.drawText(amountList[i], canvas.getWidth() - 40, y, myPaint);
            myPaint.setTextAlign(Paint.Align.RIGHT);
            if (x1 == 78) {
                x1 = 55;
            }
            total = total + Integer.parseInt(amountList[i]);
            y = y + 30;
            if (x == 440) {
                x = x + 14;
            }
            if (x1 == 55) {
                x1 = x1 + 100;
            }
        }
        factor = factor - 30;

        myPaint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, canvas.getHeight() - 330, canvas.getWidth() - 30, canvas.getHeight() - 350, myPaint);

        myPaint.setColor(Color.BLACK);
        canvas.drawText("SUBTOTAL", 550, 600 + factor, myPaint);
        canvas.drawText("Tax 4%", 550, 640 + factor, myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("TOTAL", 550, 680 + factor, myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        int t = total + (total * 4 / 100);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(total), 970, 600 + factor, myPaint);
        canvas.drawText(String.valueOf(total * 4 / 100), 970, 640 + factor, myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(String.valueOf(t), 970, 680 + factor, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Make all checks payable to\"Shop Name\"", 30, 800 + factor, myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        canvas.drawText("Thank you very much.Come back again", 30, 840 + factor, myPaint);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        String folder_main = "Invoices";


        File f = null;
        try {
            String rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Invoices/";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            f = new File(rootPath + cursor.getInt(0) + "Invoice.pdf");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            try {
                // after creating a file name we will
                // write our PDF file to that location.
                pdfDocument.writeTo(new FileOutputStream(f));

                // below line is to print toast message
                // on completion of PDF generation.
                Toast.makeText(GenActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // below line is used
                // to handle error
                e.printStackTrace();
            }
            // after storing our pdf to that
            // location we are closing our PDF file.
            pdfDocument.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri uri = FileProvider.getUriForFile(GenActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);


        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        income = income + t;
        Gson gson = new Gson();
        int json = Integer.valueOf(gson.toJson(income));

        editor.putInt("income", json);


        editor.apply();
        Toast.makeText(GenActivity.this, "Income updated", Toast.LENGTH_SHORT).show();


    }


    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
