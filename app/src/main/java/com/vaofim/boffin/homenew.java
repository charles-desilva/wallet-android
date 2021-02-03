package com.vaofim.boffin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import Models.Currency;
import Models.Offline;
import Models.UserCurrency;
import Stables.CurrentCurrency;
import Stables.CurrentUser;
import Stables.LastRemember;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class homenew extends AppCompatActivity {

    TextView timeview;
    TextView dateview;
    Calendar calendar;
    File datafile;
    NotificationManagerCompat notificationManager ;
    NotificationCompat.Builder builder;
    String CHANNELID = "default";
    int notificationId=1;
    int PROGRESS_MAX = 0;
    int PROGRESS_CURRENT = 0;
    File file;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference offlineref;
    FirebaseStorage firebaseStorage;
    StorageReference imgreference;

    DatabaseReference usercurrency;
    DatabaseReference currencyreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homenew);

        timeview=findViewById(R.id.timenew1);
        dateview=findViewById(R.id.datenew1);

        calendar=Calendar.getInstance();

        showTimeAndData();

        file=new File(getApplicationContext().getFilesDir().getAbsolutePath());

        firebaseDatabase=FirebaseDatabase.getInstance();
        usercurrency=firebaseDatabase.getReference("usercurrency");
        currencyreference=firebaseDatabase.getReference("currency");
        offlineref=firebaseDatabase.getReference("offline").child(CurrentUser.user.getId());
        firebaseStorage=FirebaseStorage.getInstance();
        imgreference=firebaseStorage.getReference("images").child(CurrentUser.user.getId());

        usercurrency.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable <DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for(DataSnapshot obj:dataSnapshots){
                    final UserCurrency userCurrency=obj.getValue(UserCurrency.class);
                    if(userCurrency.getUser().equals(CurrentUser.user.getId())){
                        currencyreference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> currencylist= dataSnapshot.getChildren();
                                for(DataSnapshot dataSnapshot1:currencylist){
                                    final Currency currencyobjf=dataSnapshot1.getValue(Currency.class);
                                    if(currencyobjf.getUser().equals(CurrentUser.user.getId()) && currencyobjf.getId().equals(userCurrency.getCurrency())){
                                        CurrentCurrency.set(java.util.Currency.getInstance(currencyobjf.getCurrency()).getSymbol());
                                        break;
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                .addButton(
                        "Cloud Sync",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                syncNow();
                            }
                        }
                )
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
        startActivity(new Intent(getApplicationContext(),add_income.class)
            .putExtra("status","2")
        );
    }

    public void goToAddPayments(View view){
            startActivity(new Intent(getApplicationContext(),addPayments.class)
                .putExtra("status","2")
            );
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
        startActivity(new Intent(homenew.this,login.class));
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
                                startActivity(new Intent(homenew.this,recordInvoices.class)
                                        .putExtra("status","2")
                                );
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
                                startActivity(new Intent(homenew.this,recordCreditNotes.class)
                                    .putExtra("status","2")
                                );
                            }
                        }
                )
                .show();
    }

    public void showvieweditentries(View view){
        new PrettyDialog(this)
                .setTitle("View / Edit / Delete Entries")
                .setMessage("Please select provided options to view or edit entities")
                .addButton(
                        "Expenses",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                startActivity(new Intent(homenew.this,viewExpences.class));
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
                                startActivity(new Intent(homenew.this,viewPayment.class));
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
                                startActivity(new Intent(homenew.this,viewInvoice.class));
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
                                startActivity(new Intent(homenew.this,viewcreditnotes.class));
                            }
                        }
                )
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public void syncNow(){
        if(isNetworkAvailable()){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    offlineref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                            for(DataSnapshot data:iterable){
                                notificationManager  = NotificationManagerCompat.from(getApplicationContext());
                                builder = new NotificationCompat.Builder(getApplicationContext(), CHANNELID);
                                builder.setContentTitle("Boffin")
                                        .setContentText("Sync in process")
                                        .setSmallIcon(R.drawable.ic_sync)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true);
                                notificationManager.notify(notificationId, builder.build());
                                final Offline offline=data.getValue(Offline.class);
                                File existfile=new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+offline.getPath());
                                if(existfile.exists()){
                                    UploadTask uploadTask=imgreference.child(offline.getPath()).putFile(Uri.fromFile(existfile));
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            offlineref.child(offline.getId()).removeValue();

                                            List<UploadTask> list1=imgreference.getActiveUploadTasks();
                                            if(list1.size()==0){
                                                builder.setContentTitle("Boffin")
                                                        .setContentText("Sync Complete")
                                                        .setSmallIcon(R.drawable.ic_sync)
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                                builder.setProgress(PROGRESS_MAX, PROGRESS_MAX, false);
                                                notificationManager.notify(notificationId, builder.build());
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }, 100);
        }else{
            Toast.makeText(this, "Connection not found for sync data with server", Toast.LENGTH_SHORT).show();
        }

    }

    public int getCount(Iterable iterable){
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).size();
        }
        int counter = 0;
        for (Object i : iterable) {
            counter++;
        }

        return  counter;
    }

    public void accessReports(View view){
        startActivity(new Intent(homenew.this,z_view_report.class));
    }

    public void summaryReports(View view){

    }
}
