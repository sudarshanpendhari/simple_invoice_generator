package com.codedev.billui;

import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class HistoryActivity extends AppCompatActivity {
    Button print;
    EditText editText;
    DataTable dataTable;
    MyHelper myHelper;
    int factor;
    int pageHeight = 900;
    int pagewidth = 1000;
    SQLiteDatabase sqLiteDatabase;
    Date date=new Date();
    String dateFormat="dd-MM-yyyy";
    SimpleDateFormat datePatternFormat=new SimpleDateFormat(dateFormat);
    String timePattern="hh:mm a";
    SimpleDateFormat timePatternFormat=new SimpleDateFormat(timePattern);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        print=findViewById(R.id.oldPrintBtn);
        editText=findViewById(R.id.oldPrintEditText);
        dataTable=findViewById(R.id.data_table);
        myHelper=new MyHelper(this);
        sqLiteDatabase=myHelper.getWritableDatabase();


        DataTableHeader header=new DataTableHeader.Builder()
                .item("Invoice No.",5)
                .item("Customer Name",5)
                .item("Date",5)
                .item("Time",5)
                .item("Amount",5)
                .build();

        ArrayList<DataTableRow> rows=new ArrayList<>();

        String[] columns={"invoiceNo","CustomerName","contactNo","date","item","qty","amount","rates"};

        Cursor cursor=sqLiteDatabase.query("myTable",columns,null,null,null,null,null);;


        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();


            String[] amt=cursor.getString(6).split(",");



            int t=0;
            for(int j=0;j< amt.length;j++){
                if(amt[j]!=""){
                t= t+Integer.parseInt(amt[j]);}
            }

            DataTableRow row=new DataTableRow.Builder()
                    .value(String.valueOf(cursor.getInt(0)))
                    .value(cursor.getString(1))
                    .value(datePatternFormat.format(cursor.getLong(3)))
                    .value(timePatternFormat.format(cursor.getLong(3)))
                    .value(String.valueOf(t))
                    .build();

            rows.add(row);
        }
        dataTable.setHeader(header);
        dataTable.setRows(rows);
        dataTable.inflate(this);
        print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()) {
                    printSelectedinvoice();
                } else {
                    Toast.makeText(HistoryActivity.this, "Enter Invoice No.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void printSelectedinvoice() {


                PdfDocument pdfDocument = new PdfDocument();

                //  variables for paint "paint" is used

                // for adding text in our PDF file.
                Paint myPaint = new Paint();

                int invoiceNo=Integer.parseInt(editText.getText().toString());
                Cursor cursor=sqLiteDatabase.rawQuery("select * from myTable where invoiceNo="+invoiceNo,null);
                cursor.moveToNext();

                String[] itemList=cursor.getString(4).split(",");
                String[] qtyList=cursor.getString(5).split(",");
                String[] amountList=cursor.getString(6).split(",");
                String[] rateList=cursor.getString(7).split(",");
                factor=itemList.length*30;



                // we are adding page info to our PDF file
                // in which we will be passing our pageWidth,
                // pageHeight and number of pages and after that
                // we are calling it to create our PDF.
                PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight+(factor-50), 1).create();

                // below line is used for setting
                // start page for our PDF file.
                PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

                // creating a variable for canvas
                // from our page of PDF.
                Canvas canvas = myPage.getCanvas();

                myPaint.setTextSize(80);
                canvas.drawText(MainActivity.shopName,30,80,myPaint);
                myPaint.setTextSize(30);
                canvas.drawText(MainActivity.shopAddress,30,120,myPaint);

                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Invoice No.",canvas.getWidth()-40,40,myPaint);

                canvas.drawText(String.valueOf(cursor.getInt(0)),canvas.getWidth()-40,80,myPaint);

                myPaint.setTextAlign(Paint.Align.LEFT);

                myPaint.setColor(Color.rgb(150,150,150));
                canvas.drawRect(30,150,canvas.getWidth()-30,160,myPaint);

                myPaint.setColor(Color.BLACK);
                canvas.drawText("Date",50,200,myPaint);
                canvas.drawText(datePatternFormat.format(cursor.getLong(3)),250,200,myPaint);

                canvas.drawText("Time",620,200,myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(timePatternFormat.format(cursor.getLong(3)),canvas.getWidth()-40,200,myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                myPaint.setColor(Color.rgb(150,150,150));
                canvas.drawRect(30,250,250,300,myPaint);

                myPaint.setColor(Color.WHITE);
                canvas.drawText("Bill To:",50,285,myPaint);

                myPaint.setColor(Color.BLACK);
                canvas.drawText("Customer Name: ",30,350,myPaint);
                canvas.drawText(cursor.getString(1),280,350,myPaint);

                canvas.drawText("Phone: ",620,350,myPaint);
                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(cursor.getString(2),canvas.getWidth()-40,350,myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                myPaint.setColor(Color.rgb(150,150,150));
                canvas.drawRect(30,400,canvas.getWidth()-30,450,myPaint);


                myPaint.setColor(Color.WHITE);
                canvas.drawText("Item",70,435,myPaint);
                canvas.drawText("Qty",432,435,myPaint);

                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Rate",canvas.getWidth()-300,435,myPaint);
                canvas.drawText("Amount",canvas.getWidth()-40,435,myPaint);
                myPaint.setTextAlign(Paint.Align.LEFT);

                int y = 480;
                int x=440;
                int x1=55+23;
                int x2=canvas.getWidth()-300;
                int total=0;
                for(int i=0;i<itemList.length;i++){

                    myPaint.setColor(Color.BLACK);
                    canvas.drawText(itemList[i],x1,y,myPaint);

                    canvas.drawText(qtyList[i],x,y,myPaint);

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(rateList[i],x2,y,myPaint);
                    canvas.drawText(amountList[i],canvas.getWidth()-40,y,myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    if(x1==78){x1=55;}
                    total=total+Integer.parseInt(amountList[i]);
                    y=y+30;
                    if(x==440){x=x+14;}
                    if(x1==55){x1=x1+100;}
                }
                factor=factor-30;

                myPaint.setColor(Color.rgb(150,150,150));
                canvas.drawRect(30,canvas.getHeight()-330,canvas.getWidth()-30,canvas.getHeight()-350,myPaint);

                myPaint.setColor(Color.BLACK);
                canvas.drawText("SUBTOTAL",550,600+factor,myPaint);
                canvas.drawText("Tax 4%",550,640+factor,myPaint);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                canvas.drawText("TOTAL",550,680+factor,myPaint);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));

                int t=total+(total*4/100);

                myPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.valueOf(total),970,600+factor,myPaint);
                canvas.drawText(String.valueOf(total*4/100),970,640+factor,myPaint);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                canvas.drawText(String.valueOf(t),970,680+factor,myPaint);

                myPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Make all checks payable to\"Shop Name\"",30,800+factor,myPaint);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));

                canvas.drawText("Thank you very much.Come back again",30,840+factor,myPaint);

                // after adding all attributes to our
                // PDF file we will be finishing our page.
                pdfDocument.finishPage(myPage);

                // below line is used to set the name of
                // our PDF file and its path.
                String folder_main = "Invoices";



                try {
                    String rootPath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/Invoices/";
                    File root = new File(rootPath);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File f = new File(rootPath + cursor.getInt(0)+"Invoice.pdf");
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
                        Toast.makeText(HistoryActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // below line is used
                        // to handle error
                        e.printStackTrace();
                    }
                    // after storing our pdf to that
                    // location we are closing our PDF file.
                    pdfDocument.close();
                    Uri uri = FileProvider.getUriForFile(HistoryActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "application/pdf");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }





    }
}