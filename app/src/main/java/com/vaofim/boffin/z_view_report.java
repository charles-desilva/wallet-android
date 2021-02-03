package com.vaofim.boffin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Models.Account;
import Models.Vat;
import Models.expenceCategory;
import Models.incomeCategory;
import Stables.CurrentUser;
import Stables.Reports;
import Stables.VatSettings;

public class z_view_report extends AppCompatActivity {

    private EditText s_date,e_date,bankbalancefield;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference account_reference;
    DatabaseReference category_reference;
    DatabaseReference vatreference;
    Spinner accountspinner,category_spinner,reporttitlespinner;

    String accountid,accountnane,categoryid,categoryname;

    int reporttitleid,entityid;
    String reporttitlename,entityname,entrynumber;

    TextView vatregno,vatregname;

    ConstraintLayout bankbalancelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_view_report);

        firebaseDatabase=FirebaseDatabase.getInstance();
        account_reference=firebaseDatabase.getReference("account");
        category_reference=firebaseDatabase.getReference("expences_category");

        vatreference=firebaseDatabase.getReference("vat");
        vatreference.keepSynced(true);

        accountspinner=findViewById(R.id.income_report_accountType_select);
        category_spinner=findViewById(R.id.income_report_category_select);
        reporttitlespinner=findViewById(R.id.reporttitlespinner);
//        income_report_entity_name=findViewById(R.id.income_report_entity_name);

        vatregno=findViewById(R.id.vatregno);
        bankbalancefield=findViewById(R.id.bankbalancefield);
        bankbalancelayout=findViewById(R.id.bankbalancelayout);
        vatregname=findViewById(R.id.vatregname);

        s_date=findViewById(R.id.income_start_date_f);
        e_date=findViewById(R.id.income_end_date_f);

        bankbalancelayout.setVisibility(View.GONE);

        getAccounts();

        ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(z_view_report.this,R.layout.spinner_dropdown_design, new VatSettings().getVatSettingsNames());
        account_adapter.setDropDownViewResource(R.layout.spinner_field);
//        income_report_entity_name.setAdapter(account_adapter);
//        income_report_entity_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                entityid=new VatSettings().getVatSettingsId().get(position);
//                entityname=new VatSettings().getVatSettingsNames().get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        ArrayAdapter<String> account_adapter1 = new ArrayAdapter<String>(z_view_report.this,R.layout.spinner_dropdown_design, new Reports().getVatSettingsNames());
        account_adapter.setDropDownViewResource(R.layout.spinner_field);
        reporttitlespinner.setAdapter(account_adapter1);
        reporttitlespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reporttitleid=new Reports().getVatSettingsId().get(position);
                reporttitlename=new Reports().getVatSettingsNames().get(position);

                if(reporttitleid!=0 && reporttitleid!=1){
                    bankbalancelayout.setVisibility(View.VISIBLE);
                }else{
                    bankbalancelayout.setVisibility(View.GONE);
                }


                loadCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean b=true;
                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                for(DataSnapshot data:iterable){
                    Vat vat=data.getValue(Vat.class);
                    if(vat.getUser().equals(CurrentUser.user.getId())){

                        vatregno.setText(vat.getVatregno());
                        entrynumber=vat.getVatregno();
                        vatregname.setText(vat.getVatregname());
                        entityname=vat.getVatregname();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void goToHome(View view){
        onBackPressed();
    }



    public void datePick1(View view){

        try {
            Calendar calendar = Calendar.getInstance();
            if(s_date.getText().toString().isEmpty()){
                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                int Month = calendar.get(Calendar.MONTH);
                int Year = calendar.get(Calendar.YEAR);

                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int currentmont=++month;
                        s_date.setText(dayOfMonth+"/"+currentmont+"/"+year);
                    }
                }, Year, Month, Day).show();
            }else{

                calendar.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(s_date.getText().toString()));

                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                int Month = calendar.get(Calendar.MONTH);
                int Year = calendar.get(Calendar.YEAR);

                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int currentmont=++month;
                        s_date.setText(dayOfMonth+"/"+currentmont+"/"+year);
                    }
                }, Year, Month, Day).show();
            }
        }catch(Exception e){

        }



    }

    public void datePick2(View view){
        try {
            Calendar calendar = Calendar.getInstance();
            if(e_date.getText().toString().isEmpty()){
                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                int Month = calendar.get(Calendar.MONTH);
                int Year = calendar.get(Calendar.YEAR);

                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int currentmont=++month;
                        e_date.setText(dayOfMonth+"/"+currentmont+"/"+year);
                    }
                }, Year, Month, Day).show();
            }else{

                calendar.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(e_date.getText().toString()));

                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                int Month = calendar.get(Calendar.MONTH);
                int Year = calendar.get(Calendar.YEAR);

                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int currentmont=++month;
                        e_date.setText(dayOfMonth+"/"+currentmont+"/"+year);
                    }
                }, Year, Month, Day).show();
            }
        }catch(Exception e){

        }

    }

    public void getAccounts(){
        account_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();
                idlist.add("none");

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                namelist.add("Select");

                for(DataSnapshot data:iterable){
                    Account account=data.getValue(Account.class);
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        namelist.add(account.getAccountName());
                        idlist.add(account.getId());
                    }
                }

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(z_view_report.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                accountspinner.setAdapter(account_adapter);
                accountspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountid=idlist.get(position);
                        accountnane=namelist.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadCategories(){

        if(reporttitleid==0){
            category_reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final ArrayList <String> namelist=new ArrayList <String> ();
                    final ArrayList <String> idlist=new ArrayList <String> ();

                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                    namelist.add("All");
                    idlist.add("all");


                    for(DataSnapshot data:iterable){
                        expenceCategory account=data.getValue(expenceCategory.class);
                        if(account.getUser().equals(CurrentUser.user.getId())){
                            namelist.add(account.getCategory());
                            idlist.add(account.getId());
                        }
                    }

                    ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(z_view_report.this,R.layout.spinner_dropdown_design, namelist);
                    account_adapter.setDropDownViewResource(R.layout.spinner_field);
                    category_spinner.setAdapter(account_adapter);

                    category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            categoryid=idlist.get(position);
                            categoryname=namelist.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(reporttitleid==1){

            DatabaseReference databaseReference=firebaseDatabase.getReference("income_category");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final ArrayList <String> namelist=new ArrayList <String> ();
                    final ArrayList <String> idlist=new ArrayList <String> ();

                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                    namelist.add("All");
                    idlist.add("all");


                    for(DataSnapshot data:iterable){
                        incomeCategory account=data.getValue(incomeCategory.class);
                        if(account.getUser().equals(CurrentUser.user.getId())){
                            namelist.add(account.getCategory());
                            idlist.add(account.getId());
                        }
                    }

                    ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(z_view_report.this,R.layout.spinner_dropdown_design, namelist);
                    account_adapter.setDropDownViewResource(R.layout.spinner_field);
                    category_spinner.setAdapter(account_adapter);

                    category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            categoryid=idlist.get(position);
                            categoryname=namelist.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    public void printReport(View view){
        if(!accountid.equals("none")){
            String startDate=s_date.getText().toString();
            String endDate=e_date.getText().toString();


            if(startDate.isEmpty() && endDate.isEmpty()){
                startActivity(new Intent(z_view_report.this,report1.class)
                        .putExtra("accountid",accountid)
                        .putExtra("accountname",accountnane)
                        .putExtra("categoryid",categoryid)
                        .putExtra("categoryname",categoryname)
                        .putExtra("startdate",startDate)
                        .putExtra("enddate",endDate)
                        .putExtra("entrynumber",entrynumber)
                        .putExtra("entityname",entityname)
                        .putExtra("reporttitleid",reporttitleid+"")
                        .putExtra("reporttitlename",reporttitlename)
                );
            }else{
                if(!startDate.isEmpty() || !endDate.isEmpty()){
                    if(!startDate.isEmpty() && !endDate.isEmpty()){
                        startActivity(new Intent(z_view_report.this,report1.class)
                                .putExtra("accountid",accountid)
                                .putExtra("accountname",accountnane)
                                .putExtra("categoryid",categoryid)
                                .putExtra("categoryname",categoryname)
                                .putExtra("startdate",startDate)
                                .putExtra("enddate",endDate)
                                .putExtra("entrynumber",entrynumber)
                                .putExtra("entityname",entityname)
                                .putExtra("reporttitleid",reporttitleid)
                                .putExtra("reporttitlename",reporttitlename)
                        );
                    }else{
                        Toast.makeText(this, "Please fill start date and end date to filter", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            
        }else{
            Toast.makeText(this, "Please select account type", Toast.LENGTH_SHORT).show();
        }
    }
}
