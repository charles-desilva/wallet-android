package com.vaofim.boffin;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Models.DownloadFile;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class home extends AppCompatActivity {

    TextView timeview;
    TextView dateview;
    Calendar calendar;
    Thread thread;
    File datafile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        timeview=findViewById(R.id.timeview);
        dateview=findViewById(R.id.dateview);

        calendar=Calendar.getInstance();

        showTimeAndData();
    }

    public void showSettings(View view){
        new PrettyDialog(this)

                .setTitle("Settings")
                .addButton(
                        "Accounts",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),createaccount.class));
                            }
                        }
                )
                .addButton(
                        "Currency",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),addcurrency.class));
                            }
                        }
                )
                .addButton(
                        "Customers",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),addCustomer.class));
                            }
                        }
                )

                .addButton(
                        "Expense Category",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),addexpencescategory.class));
                            }
                        }
                )
                .addButton(
                        "Income Category",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),incomecategory.class));
                            }
                        }
                )
                .addButton(
                        "Invoice Category",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),invoice_category.class));
                            }
                        }
                )
                .addButton(
                        "Pay Modes",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),addPayModes.class));
                            }
                        }
                )
                .addButton(
                        "VAT",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(getApplicationContext(),addVat.class));
                            }
                        }
                )
//                .addButton(
//                        "Dropbox Backup",
//                        R.color.pdlg_color_white,
//                        R.color.pdlg_color_green,
//                        new PrettyDialogCallback() {
//                            @Override
//                            public void onClick() {
//
//                            }
//                        }
//                )
                .show();
    }

    public void showTimeAndData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date=System.currentTimeMillis();
                                timeview.setText(new SimpleDateFormat("hh:mm:ss aa").format(date));
                                dateview.setText(new SimpleDateFormat("d MMM yyyy").format(date));
                            }
                        });
                        Thread.sleep(1000);
                    }
                }catch(Exception e){

                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {

    }

    public void gotto_IncomeRecords(View view){
        startActivity(new Intent(getApplicationContext(),add_income.class));
    }

    public void goToAddPayments(View view){
        startActivity(new Intent(getApplicationContext(),addPayments.class));
    }

    public void exitApp(View view){
            try {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
            }catch(Exception e){
                e.printStackTrace();
            }
    }

    public void logoutNow(View view){
        datafile=new File(getApplicationContext().getFilesDir(),"data.voi");
        if(datafile.exists()){
            datafile.delete();
        }
        startActivity(new Intent(home.this,login.class));
    }

    public void showrecordinvoicesandcreditnotes(View view){
        new PrettyDialog(this)
                .setTitle("Record Invoices & Credit Notes")
                .setMessage("Please select provided options to record")
                .addButton(
                        "Record Invoices",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(home.this,recordInvoices.class));
                            }
                        }
                )
                .addButton(
                        "Record Credit Notes",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                Intent intent=new Intent(home.this,recordCreditNotes.class);
                                intent.putExtra("status","2");
                                intent.putExtra("id","LjCQlGYpmhFMAL90cz2");

                                startActivity(intent);
                            }
                        }
                )
                .show();
    }

    public void showvieweditentries(View view){
        new PrettyDialog(this)
                .setTitle("View / Edit / Delete Entities")
                .setMessage("Please select provided options to view or edit entities")
                .addButton(
                        "Expenses",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(home.this,addexpencescategory.class));
                            }
                        }
                )
                .addButton(
                        "Income",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(home.this,incomecategory.class));
                            }
                        }
                )
                .addButton(
                        "Invoices",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(home.this,invoice_category.class));
                            }
                        }
                )
                .addButton(
                        "Credit Notes",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(home.this,viewcreditnotes.class));
                            }
                        }
                )
                .show();
    }

    public void viewReports(View view){
        new PrettyDialog(this)
                .setTitle("View Reports")
                .setMessage("Please select provided options to delete process")
                .addButton(
                        "Expenses",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                            }
                        }
                )
                .addButton(
                        "Receipts",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                            }
                        }
                )
                .addButton(
                        "Credit Notes",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                            }
                        }
                )
                .show();
    }

    public void gotocreateaccount(View view){
        startActivity(new Intent(getApplicationContext(),createaccount.class));
    }

    public void getReport(View view){
        try {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", new SimpleDateFormat("yyyyMMddhhMMss").format(new Date()));


            startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://vaofim.com/tools/boffin/reports/accessreport.html")
            ));
//
//            queue = Volley.newRequestQueue(this);
//            String url ="https://vaofim.com/tools/boffin/reports/accessreport.html";
//
//            JSONObject jsonObject1=new JSONObject();
//            jsonObject1.put("id","abc");
//
//            StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    System.out.println(response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//
//            queue.add(stringRequest);
        }catch(Exception e){

        }
    }
}
