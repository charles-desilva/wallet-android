package com.vaofim.boffin;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nmaltais.calcdialog.CalcDialog;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Models.Account;
import Models.Customer;
import Models.Vat;
import Models.addInvoice;
import Models.expenceCategory;
import Models.incomeSubCategory;
import Models.invoiceCategory;
import Models.invoiceSubCategory;
import Models.recordCreditNote;
import Process_Classes.CheckAvailability;
import Stables.CurrencyConvert;
import Stables.CurrentCurrency;
import Stables.CurrentUser;
import Stables.LastRemember;
import Stables.ValidateDate;

public class recordInvoices extends AppCompatActivity implements CalcDialog.CalcDialogCallback{

    public EditText datefield,invoiceno,note,invoicenet;
    public CalcDialog calcDialog;
    public Spinner customer_spinner,category_spinner, subCategory_spinner,vatspinner;
    public TextView titletext,lastinvoiceno,vat,invoicetotal,vatviewtxt,invoicetotallabel,textViewInvTot;
    ConstraintLayout vatconslayout;

    Button savebtn,deletebtn;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference vatreference;
    String vatid;
    String vatname;

    DatabaseReference customerreference;
    String customerid;
    String customername;

    DatabaseReference categoryreference;
    String categoryid;
    String categoryname;

    DatabaseReference subcategoryreference;
    String subcategoryid;
    String subcategoryname;

    int vid,catid,subcatid,cusid;

    DatabaseReference invoicereference;

    ImageButton vatbtn;

    boolean status;
    String existid;
    double vatval;
    boolean change=false;


    boolean customercheck;
    boolean catcheck;
    boolean subcatcheck;

    ScrollView recordinvoicesscrollview;

    public int vatboxckeched=0;
    public double vatprecentage=0.0;

    String categoryString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_invoices);

        invoicetotallabel=findViewById(R.id.invoicetotallabel);
        textViewInvTot=findViewById(R.id.textViewInvTot);
        invoicetotallabel.setText("Invoice Net ("+CurrentCurrency.get()+") *");
        textViewInvTot.setText("Invoice Total ("+CurrentCurrency.get()+")");

        initializing();

        recordinvoicesscrollview=findViewById(R.id.recordinvoicesscrollview);

        if(getIntent().getStringExtra("status").equals("1")){
            status=true;
            existid=getIntent().getStringExtra("id");
            savebtn.setText("Update");
            deletebtn.setText("Delete");

            deletebtn.setBackgroundColor(getResources().getColor(R.color.pdlg_color_red));

            titletext.setText("Update Record Invoice");

        }

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    saveRecords();
                }else{
                    updateData();
                }

            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    clearAll();
                }else{
                    DeleteRecord();
                }

            }
        });


        if(status==true){
            invoicereference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterable){
                        addInvoice addInvoice=data.getValue(Models.addInvoice.class);
                        if(addInvoice.getId().equals(existid)){
                            datefield.setText(addInvoice.getDate());
                            invoiceno.setText(addInvoice.getInvoicenuber());
                            vat.setText(addInvoice.getVat());
                            note.setText(addInvoice.getNote());
                            if(addInvoice.getVat().equals("") || addInvoice.getVat()==null){
                                vatspinner.setSelection(0);
                            }else{

                                vatspinner.setSelection(1);
//                                vatval=Double.parseDouble(addInvoice.getVat());
                                vatboxckeched=addInvoice.getVatbutton();
                                if(vatboxckeched==1){
                                    vatval=Double.parseDouble(addInvoice.getVat());

                                }else{
                                    vatval=addInvoice.getVatval();
                                }
                                vatprecentage=addInvoice.getVatval();
                                vat.setText(addInvoice.getVat());
                            }

                            customerid=addInvoice.getCustomerid();
                            customername=addInvoice.getCustomername();
                            categoryid=addInvoice.getCategoryid();
                            categoryString=addInvoice.getCategoryid();
                            categoryname=addInvoice.getCategoryname();
                            subcategoryid=addInvoice.getSubcategoryid();
                            subcategoryname=addInvoice.getSubcategoryname();
                            invoicenet.setText(CurrencyConvert.Get(Double.parseDouble(addInvoice.getInvoicenet())));
                            invoicetotal.setText(CurrencyConvert.Get(Double.parseDouble(addInvoice.getInvoicetotal())));

                            break;
                        }
                    }
                    invoicenet.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(!invoicenet.getText().toString().isEmpty()){
                                double val1=Double.parseDouble(invoicenet.getText().toString());

                                if(vatboxckeched==2){
                                    vat.setText(CurrencyConvert.Get(vatval));
                                    invoicetotal.setText(BigDecimal.valueOf(val1+vatval).setScale(2, RoundingMode.HALF_EVEN).toString());
                                    vatboxckeched=2;
                                    vatprecentage=vatval;
                                }else{
                                    if(vatval!=0.0){
                                        double value1=(val1*vatval)/(100);
                                        BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                                        vat.setText(balance.toString()+"");
                                        invoicetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                                    }else{
                                        invoicetotal.setText(CurrencyConvert.Get(val1));
                                    }
                                }


                            }
                        }
                    });
                    finalizing();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            addTextListeneres();
            if(LastRemember.recordInvoices!=null){

                vatboxckeched=LastRemember.recordInvoices.vatboxckeched;
                vatval=LastRemember.recordInvoices.vatval;
                vatprecentage=LastRemember.recordInvoices.vatprecentage;
                vatviewtxt.setText(LastRemember.recordInvoices.vatviewtxt.getText());

                datefield.setText(LastRemember.recordInvoices.datefield.getText());
                invoiceno.setText(LastRemember.recordInvoices.invoiceno.getText());
                invoicenet.setText(LastRemember.recordInvoices.invoicenet.getText());
                note.setText(LastRemember.recordInvoices.note.getText());

                vatid=LastRemember.recordInvoices.vatid;
                vatname=LastRemember.recordInvoices.vatname;
                vid=LastRemember.recordInvoices.vatspinner.getSelectedItemPosition();

                customerid=LastRemember.recordInvoices.customerid;
                customername=LastRemember.recordInvoices.customername;
                cusid=LastRemember.recordInvoices.customer_spinner.getSelectedItemPosition();

                categoryid=LastRemember.recordInvoices.categoryid;
                categoryname=LastRemember.recordInvoices.categoryname;
                catid=LastRemember.recordInvoices.category_spinner.getSelectedItemPosition();

                subcategoryid=LastRemember.recordInvoices.subcategoryid;
                subcategoryname=LastRemember.recordInvoices.subcategoryname;
                subcatid=LastRemember.recordInvoices.subCategory_spinner.getSelectedItemPosition();
            }

            invoicenet.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!invoicenet.getText().toString().isEmpty()){
                        double val1=Double.parseDouble(invoicenet.getText().toString());

                        if(vatboxckeched==2){
                            vat.setText(vatval+"");
                            invoicetotal.setText(BigDecimal.valueOf(val1+vatval).setScale(2, RoundingMode.HALF_EVEN).toString());
                            vatboxckeched=2;
                            vatprecentage=vatval;
                        }else{
                            if(vatval!=0.0){
                                double value1=(val1*vatval)/(100);
                                BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                                vat.setText(balance.toString());
                                invoicetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                            }else{
                                invoicetotal.setText(CurrencyConvert.Get(val1));
                            }
                        }


                    }
                }
            });
            finalizing();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initializing() {

        datefield=findViewById(R.id.datefield);
        calcDialog=new CalcDialog();
        customer_spinner= findViewById(R.id.customer_select_spinner);
        category_spinner = findViewById(R.id.category_select_spinner);
        vatspinner=findViewById(R.id.vatspinner);
        subCategory_spinner = findViewById(R.id.subCategory_select_spinner);
        lastinvoiceno=findViewById(R.id.txt_last_credit_note_number);
        vat=findViewById(R.id.vatval);
        invoicenet=findViewById(R.id.invoicenet);
        note=findViewById(R.id.notetxt);
        invoiceno=findViewById(R.id.invoiceno);
        invoicetotal=findViewById(R.id.invoicetotal);
        vatbtn=findViewById(R.id.vatbtn);
        savebtn=findViewById(R.id.savebtn);
        deletebtn=findViewById(R.id.deletebtn);
        vatconslayout=findViewById(R.id.vatconslayout);
        vatviewtxt=findViewById(R.id.vatviewtxt);
        titletext=findViewById(R.id.titletext);

        firebaseDatabase=FirebaseDatabase.getInstance();

        vatreference=firebaseDatabase.getReference("vat");
        customerreference=firebaseDatabase.getReference("customer");
        categoryreference=firebaseDatabase.getReference("invoice_category");
        subcategoryreference=firebaseDatabase.getReference("invoice_subcategory");
        invoicereference=firebaseDatabase.getReference("invoicerecord");

        datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));

        invoicereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                ArrayList<Integer> invoiceList=new ArrayList<>();
                System.out.println("-------------------------");
                for(DataSnapshot data:iterable){
                    addInvoice recordCreditNote=data.getValue(addInvoice.class);
                    if(recordCreditNote!=null && recordCreditNote.getUser().equals(CurrentUser.user.getId())){
                        invoiceList.add(Integer.parseInt(recordCreditNote.getInvoicenuber()));
                    }
                }

                Collections.sort(invoiceList);

                if(invoiceList.size()>0){
                    lastinvoiceno.setText(invoiceList.get(invoiceList.size()-1)+"");
                }

                System.out.println("-------------------------");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Query lastQuery = invoicereference.limitToLast(1);
//        lastQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
//
//                for(DataSnapshot data:iterable){
//                    addInvoice recordCreditNote=data.getValue(addInvoice.class);
//                    if(recordCreditNote!=null && recordCreditNote.getUser().equals(CurrentUser.user.getId())){
//                        lastinvoiceno.setText(recordCreditNote.getInvoicenuber());
//                    }
//                    break;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        setVatDefaults();
    }



    public void goToHome(View view){
        if(status==false){
            if(change){
                final AlertDialog.Builder builder = new AlertDialog.Builder(recordInvoices.this);
                builder.setCancelable(true);
                builder.setTitle("This form has unsaved data.");
                builder.setMessage("Are you sure that you want to close without saving ?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.recordInvoices=recordInvoices.this;
                        onBackPressed();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        LastRemember.recordInvoices=null;
                        onBackPressed();
                    }
                });
                builder.create();
                builder.show();
            }else{
                onBackPressed();
            }
        }else{
            onBackPressed();
        }
    }

    private void finalizing() {
        setCustomer();
        setCategory();
    }

    String customernewid;
    String invoicecatnewid;
    String invoicesubcatnewid;

    ArrayList <Customer> customerList;
    ArrayList <invoiceCategory> invoicecatList;
    ArrayList <invoiceSubCategory> invoiceSubCatList;

    public void setCustomer(){
        customerreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                customerList=new ArrayList<>();

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                if(status==true && customerid!=null){
                    namelist.add(customername);
                    idlist.add(customerid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }

                for(DataSnapshot data:iterable){
                    Customer account=data.getValue(Customer.class);
                    customerList.add(account);
                }

                Collections.sort(customerList, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer customer1, Customer customer2) {
                        return customer1.getName().toLowerCase().compareTo(customer2.getName().toLowerCase());
                    }
                });

                for(Customer account:customerList){
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        if(status==true && customerid!=null){
                            if(!account.getId().equals(customerid)){
                                namelist.add(account.getName());
                                idlist.add(account.getId());
                            }
                        }else{
                            namelist.add(account.getName());
                            idlist.add(account.getId());
                        }
                    }
                }

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(recordInvoices.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                customer_spinner.setAdapter(account_adapter);
                customer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        customerid=idlist.get(position);
                        customername=namelist.get(position);

                        if(!customerid.equals("none")){
                            change=true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(cusid!=0 && LastRemember.recordInvoices!=null && LastRemember.recordInvoices.customer_spinner.getSelectedItemPosition()!=0){
                    customer_spinner.setSelection(cusid);
                }

                if(customercheck){
                    customer_spinner.setSelection(idlist.indexOf(customernewid));
                    customercheck=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setCategory(){
        categoryreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                invoicecatList=new ArrayList<>();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                if(status==true && categoryid!=null){
                    namelist.add(categoryname);
                    idlist.add(categoryid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }

                for(DataSnapshot data:iterable){
                    invoiceCategory account=data.getValue(invoiceCategory.class);
                    invoicecatList.add(account);
                }

                Collections.sort(invoicecatList, new Comparator<invoiceCategory>() {
                    @Override
                    public int compare(invoiceCategory invoiceCategory1, invoiceCategory invoiceCategory2) {
                        return invoiceCategory1.getCategory().toLowerCase().compareTo(invoiceCategory2.getCategory().toLowerCase());
                    }
                });

                for(invoiceCategory account:invoicecatList){
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        if(status==true){
                            if(!account.getId().equals(categoryid)){
                                namelist.add(account.getCategory());
                                idlist.add(account.getId());
                            }
                        }else{
                            namelist.add(account.getCategory());
                            idlist.add(account.getId());
                        }
                    }
                }

                ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,namelist);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
                category_spinner.setAdapter(spinnerArrayAdapter);
                category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        categoryid=idlist.get(position);
                        categoryname=namelist.get(position);

                        if(idlist.get(position).equals("none")){
                            subCategory_spinner.setEnabled(false);
                            subCategory_spinner.setClickable(false);
                        }else{
                            subCategory_spinner.setEnabled(true);
                            subCategory_spinner.setClickable(true);

                            setSubCategory();
                        }


                        if(!categoryid.equals("none")){
                            change=true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(catid!=0 && LastRemember.recordInvoices!=null && LastRemember.recordInvoices.category_spinner.getSelectedItemPosition()!=0){
                    category_spinner.setSelection(catid);
                }

                if(catcheck){
                    category_spinner.setSelection(idlist.indexOf(invoicecatnewid));
                    catcheck=false;
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setSubCategory(){
        subcategoryreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                invoiceSubCatList=new ArrayList<>();

//                if(status==true && subcategoryid!=null){
//                    namelist.add(subcategoryname);
//                    idlist.add(subcategoryid);
//                }

                if(status==true){
                    if(categoryString==null){
                        namelist.add(subcategoryname);
                        idlist.add(subcategoryid);
                    }else{
                        if(categoryid.equals(categoryString)){
                            namelist.add(subcategoryname);
                            idlist.add(subcategoryid);
                        }else{
                            categoryString="-";
                        }
                    }

                }

                for(DataSnapshot data:iterable){
                    invoiceSubCategory account=data.getValue(invoiceSubCategory.class);

                    if(status==true && subcategoryid!=null){
                        if(!account.getId().equals(subcategoryid)){
                            invoiceSubCatList.add(account);
                        }
                    }else{
                        invoiceSubCatList.add(account);
                    }
                }

                System.out.println(invoiceSubCatList.size()+" ------------------- ");

                Collections.sort(invoiceSubCatList, new Comparator<invoiceSubCategory>() {
                    @Override
                    public int compare(invoiceSubCategory invoiceSubCategory1, invoiceSubCategory invoiceSubCategory2) {
                        return invoiceSubCategory1.getSubCategory().toLowerCase().compareTo(invoiceSubCategory2.getSubCategory().toLowerCase());
                    }
                });

                for(invoiceSubCategory account:invoiceSubCatList){
                    if(account.getCategory().equals(categoryid)){
                        if(status==true && subcategoryid!=null){
                            if(!subcategoryid.equals(account.getSubCategory())){
                                namelist.add(account.getSubCategory());
                                idlist.add(account.getId());
                            }
                        }else{
                            namelist.add(account.getSubCategory());
                            idlist.add(account.getId());
                        }
                    }
                }



                ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,namelist);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
                subCategory_spinner.setAdapter(spinnerArrayAdapter);
                subCategory_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        subcategoryid=idlist.get(position);
                        subcategoryname=namelist.get(position);

//                        if(!categoryid.equals("none") && status==false && idlist.size()>1){
//                            subCategory_spinner.setSelection(1);
//                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(subcatid!=0 && LastRemember.recordInvoices!=null){
                    subCategory_spinner.setSelection(subcatid);
                }

                if(subcatcheck){
                    subCategory_spinner.setSelection(idlist.indexOf(invoicesubcatnewid));
                    subcatcheck=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToCategory(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("New Category");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleBox = new EditText(this);
        titleBox.setHint("Category Name");

        layout.setPadding(15,30,15,0);
        layout.addView(titleBox);

        titleBox.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setCancelable(false);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!titleBox.getText().toString().isEmpty()){


                    if(new CheckAvailability().invoiceCategoryCheck(invoicecatList,titleBox.getText().toString())){
                        String key=customerreference.push().getKey();
                        invoicecatnewid=key;
                        categoryreference.child(key).setValue(new invoiceCategory(key,titleBox.getText().toString(), CurrentUser.user.getId(),2));
                        String key1=subcategoryreference.push().getKey();

                        subcategoryreference.child(key1).setValue(new invoiceSubCategory(key1,"Genaral", key,1));
                        Toast.makeText(recordInvoices.this, "Category Saved", Toast.LENGTH_SHORT).show();
                        catcheck=true;
                    }else{
                        Toast.makeText(recordInvoices.this, "Record Invoice Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(recordInvoices.this, "Please Enter Name For Save New Category", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(layout);
        builder.create();
        builder.show();
    }

    public void goToSubcategory(View view){
        if(categoryid!=null && !categoryid.equals("") && !categoryid.equals("none")){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("New Sub Category");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleBox = new EditText(this);
            titleBox.setHint("Sub Category Name");

            layout.setPadding(15,30,15,0);
            layout.addView(titleBox);

            titleBox.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

            builder.setCancelable(false);
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!titleBox.getText().toString().isEmpty()){
                        String key=subcategoryreference.push().getKey();
                        invoicesubcatnewid=key;
                        subcategoryreference.child(key).setValue(new invoiceSubCategory(key,titleBox.getText().toString(), categoryid,2));
                        Toast.makeText(recordInvoices.this, "Sub Category Saved", Toast.LENGTH_SHORT).show();
                        subcatcheck=true;
                        setSubCategory();

                    }else{
                        Toast.makeText(recordInvoices.this, "Please Enter Name For Save New Sub Category", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setView(layout);
            builder.create();
            builder.show();
        }else{
            Toast.makeText(this, "Please Select The Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void setVatDefaults(){
        ArrayList <String> namelist=new ArrayList <String> ();
        final ArrayList <String> idlist=new ArrayList <String> ();

        namelist.add("No");
        namelist.add("Yes");
        idlist.add("2");
        idlist.add("1");

        ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,namelist);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
        vatspinner.setAdapter(spinnerArrayAdapter);
        vatspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vatid=idlist.get(position);

                if(idlist.get(position).equals("2")){
                    vatconslayout.setVisibility(View.GONE);
                    vat.setText("");
                    vatval=0.0;

                    invoicetotal.setText(invoicenet.getText().toString());
                }else{

                    if(status==false){
                        change=true;

                        if(LastRemember.recordInvoices==null || (LastRemember.recordInvoices!=null && LastRemember.recordInvoices.vatid.equals("2"))){
                            vatreference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                    for(DataSnapshot data:iterable){
                                        Vat vat1=data.getValue(Vat.class);
                                        if(vat1.getUser().equals(CurrentUser.user.getId())){
                                            vatviewtxt.setText("VAT "+vat1.getValue()+"% ("+ CurrentCurrency.get() +")");
                                            vatval=vat1.getValue();
                                            if(!invoicetotal.getText().toString().isEmpty()){
                                                double val1=Double.parseDouble(invoicenet.getText().toString());
                                                if(vatval!=0.0){
                                                    double value1=(val1*vatval)/(100);
                                                    BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                                                    vat.setText(balance.toString());
                                                    invoicetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                                                }else{
                                                    invoicetotal.setText(CurrencyConvert.Get(val1));
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }


                    }

                    vatconslayout.setVisibility(View.VISIBLE);
                }

                if(vid!=0 && LastRemember.recordInvoices!=null && LastRemember.recordInvoices.vatspinner.getSelectedItemPosition()!=0){
                    vatspinner.setSelection(vid);
                    vatval=LastRemember.recordInvoices.vatval;
                    vat.setText(LastRemember.recordInvoices.vat.getText());
                    if(!LastRemember.recordInvoices.invoicetotal.getText().toString().isEmpty() && !LastRemember.recordInvoices.vat.getText().toString().isEmpty()){
//                        vatviewtxt.setText("VAT");
                        double val1=Double.parseDouble(invoicenet.getText().toString());
                        if(vatval!=0.0){
                            invoicetotal.setText(BigDecimal.valueOf(val1+Double.parseDouble(vat.getText().toString())).setScale(2, RoundingMode.HALF_EVEN).toString());
                        }else{
                            invoicetotal.setText(CurrencyConvert.Get(val1));
                        }
                    }
                    vid=0;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void goToVat(View view){
        if(!invoicetotal.getText().toString().isEmpty()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Change VAT Value");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleBox = new EditText(this);
            titleBox.setHint("Expected Value");
            titleBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            if(vatboxckeched==1){
                titleBox.setText(CurrencyConvert.Get(vatprecentage));
            }else if(vatboxckeched==2){
                titleBox.setText(CurrencyConvert.Get(vatprecentage));
            }else{
                titleBox.setText(CurrencyConvert.Get(vatval));
                vatboxckeched=1;
            }


            RadioGroup radioGroup=new RadioGroup(this);
            radioGroup.setOrientation(LinearLayout.HORIZONTAL);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 15, 15, 10);

            final RadioButton radioButton=new RadioButton(this);
            radioButton.setText("Percentage");
            radioButton.setTextColor(Color.BLACK);
            final RadioButton radioButton1=new RadioButton(this);
            radioButton1.setText("Value");
            radioButton1.setTextColor(Color.BLACK);

            radioButton.setLayoutParams(params);
            radioButton1.setLayoutParams(params);
            layout.setPadding(15,0,15,0);

            radioGroup.addView(radioButton);
            radioGroup.addView(radioButton1);
            layout.addView(radioGroup);
            layout.addView(titleBox);
            radioButton.setChecked(true);

            if(vatboxckeched==1){
                radioButton.setChecked(true);
            }else{
                radioButton1.setChecked(true);
            }

            builder.setCancelable(false);
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!titleBox.getText().toString().isEmpty()){
                        double val1=Double.parseDouble(invoicenet.getText().toString());
                        double valvat=Double.parseDouble(titleBox.getText().toString());
                        if(radioButton.isChecked()){
                            double value1=(val1*valvat)/(100);
                            BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                            vat.setText(balance.toString()+"");
                            invoicetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                            vatviewtxt.setText("VAT (Custom Percentage: "+CurrencyConvert.Get(valvat)+"%)");
                            vatboxckeched=1;
                            vatprecentage=valvat;
                        }else if(radioButton1.isChecked()){

                            if(val1<= valvat){
                                Toast.makeText(recordInvoices.this, "Custom VAT values cannot be equals or greater than net total", Toast.LENGTH_SHORT).show();
                            }else{
                                vat.setText(CurrencyConvert.Get(valvat));
                                invoicetotal.setText(BigDecimal.valueOf(val1+valvat).setScale(2, RoundingMode.HALF_EVEN).toString());
                                vatviewtxt.setText("VAT (Custom fixed Value: "+ CurrentCurrency.get() +CurrencyConvert.Get(valvat)+")");
                                vatboxckeched=2;
                                vatprecentage=valvat;
                            }



                        }
                        vatval=valvat;
                    }else{
                        Toast.makeText(recordInvoices.this, "Please Enter Value", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setView(layout);
            builder.create();
            builder.show();
        }else{
            Toast.makeText(this, "Please fill invoice total for calculate vat manually according to the valuable totals", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToCustomer(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("New Customer");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleBox = new EditText(this);
        titleBox.setHint("Customer Name");

        layout.setPadding(15,30,15,0);
        layout.addView(titleBox);

        titleBox.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setCancelable(false);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!titleBox.getText().toString().isEmpty()){
                    if(new CheckAvailability().checkCustomerAvailability(customerList,titleBox.getText().toString())){
                        String key=customerreference.push().getKey();
                        customernewid=key;
                        customerreference.child(key).setValue(new Customer(key,titleBox.getText().toString(), CurrentUser.user.getId()));
                        customercheck=true;
                        Toast.makeText(recordInvoices.this, "Customer Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(recordInvoices.this, "Customer Name Exists, Please Try Another", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(recordInvoices.this, "Please Enter Name For Save New Customer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(layout);
        builder.create();
        builder.show();
    }

    public void datePick1(View view){

        Calendar calendar = Calendar.getInstance();

        if(!datefield.getText().toString().isEmpty() && new ValidateDate().validate(datefield.getText().toString())){
            try {
                calendar.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(datefield.getText().toString()));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int currentmont=++month;
                datefield.setText(dayOfMonth+"/"+currentmont+"/"+year);
            }
        }, Year, Month, Day).show();

    }

    public void showCal(View view){
        if(invoicetotal.getText().toString().isEmpty()){
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }else{
            calcDialog.setValue(BigDecimal.valueOf(Double.parseDouble(invoicenet.getText().toString())));
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }
    }

    @Override
    public void onValueEntered(int requestCode, BigDecimal value) {
        invoicenet.setText(BigDecimal.valueOf(value.doubleValue()).setScale(2,BigDecimal.ROUND_CEILING)+"");
    }


    public void clearAll(){
        datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));
        vatspinner.setSelection(0);
        lastinvoiceno.setText("");
        invoiceno.setText("");
        customer_spinner.setSelection(0);
        change=false;
        invoicenet.setText("");
        invoicetotal.setText("");
        category_spinner.setSelection(0);
        subCategory_spinner.setSelection(0);
        note.setText("");
        change=false;

        recordinvoicesscrollview.fullScroll(ScrollView.FOCUS_UP);
    }

    public void saveRecords(){
        if(!datefield.getText().toString().isEmpty() && datefield!=null && !datefield.equals("") && new ValidateDate().validate(datefield.getText().toString())){
            if(!invoiceno.getText().toString().isEmpty()){
                if(!customerid.equals("") && customerid!=null && !customerid.equals("none")){
                    if(!invoicenet.getText().toString().isEmpty() && invoicenet!=null && !invoicenet.equals("")){
                        if(!categoryid.equals("none") && categoryid!=null){
                            if(!subcategoryid.equals("none") && subcategoryid!=null){
                                invoicereference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean checkInvoiceNumberExists=true;
                                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                        for(DataSnapshot data:iterable){
                                            addInvoice addInvoice=data.getValue(Models.addInvoice.class);
                                            if(addInvoice.getUser().equals(CurrentUser.user.getId()) && invoiceno.getText().toString().trim().equals(addInvoice.getInvoicenuber())){
                                                checkInvoiceNumberExists=false;
                                            }
                                        }

                                        if(checkInvoiceNumberExists){
                                            String key=invoicereference.push().getKey();
                                            invoicereference.child(key).setValue(new addInvoice(key,datefield.getText().toString(),lastinvoiceno.getText().toString(),invoiceno.getText().toString(),customerid,customername,invoicenet.getText().toString(),vat.getText().toString(),invoicetotal.getText().toString(),categoryid,categoryname,subcategoryid,subcategoryname,note.getText().toString(),CurrentUser.user.getId(),1,vatviewtxt.getText().toString(),vatval,vatboxckeched));
                                            Toast.makeText(recordInvoices.this, "Invoice Saved", Toast.LENGTH_SHORT).show();

                                            vatspinner.setSelection(0);
                                            invoiceno.setText("");
                                            customer_spinner.setSelection(0);
                                            invoicenet.setText("");
                                            invoicetotal.setText("");
                                            category_spinner.setSelection(0);
                                            subCategory_spinner.setSelection(0);
                                            note.setText("");
                                            change=false;
                                            recordinvoicesscrollview.fullScroll(ScrollView.FOCUS_UP);
                                            datefield.requestFocus();
                                        }else{
                                            Toast.makeText(recordInvoices.this, "Invoice Number Exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }else{
                                Toast.makeText(this, "Please select subcategory", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Please fill invoice net", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Please select customer", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please fill invoice number", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Date empty or inserted date not valid", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateData(){
        if(!datefield.getText().toString().isEmpty() && datefield!=null && !datefield.equals("") && new ValidateDate().validate(datefield.getText().toString())){
            if(!invoiceno.getText().toString().isEmpty()){
                if(!customerid.equals("") && customerid!=null && !customerid.equals("none")){
                    if(!invoicenet.getText().toString().isEmpty() && invoicenet!=null && !invoicenet.equals("")){
                        if(!categoryid.equals("none") && categoryid!=null){
                            if(!subcategoryid.equals("none") && subcategoryid!=null){
                                invoicereference.child(existid).setValue(new addInvoice(existid,datefield.getText().toString(),lastinvoiceno.getText().toString(),invoiceno.getText().toString(),customerid,customername,invoicenet.getText().toString(),vat.getText().toString(),invoicetotal.getText().toString(),categoryid,categoryname,subcategoryid,subcategoryname,note.getText().toString(),CurrentUser.user.getId(),1,vatviewtxt.getText().toString(),vatval,vatboxckeched));
                                Toast.makeText(this, "Invoice Updated", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(this, "Please select subcategory", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Please fill invoice net", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Please select customer", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please fill invoice number", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Date empty or inserted date not valid", Toast.LENGTH_SHORT).show();
        }
    }

    public void DeleteRecord(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(recordInvoices.this);
        builder.setCancelable(true);
        builder.setTitle("Are you sure ?");
        builder.setMessage("Please confirm that you really need to delete this record.");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                invoicereference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                        for(DataSnapshot data:iterable){
                            addInvoice addInvoice=data.getValue(Models.addInvoice.class);
                            if(addInvoice.getId().equals(existid)){
                                invoicereference.child(existid).removeValue();
                                Toast.makeText(recordInvoices.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                onBackPressed();
            }
        });
        builder.create();
        builder.show();
    }

    private void addTextListeneres(){
//        datefield.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                change=true;
//            }
//        });

        invoiceno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                change=true;
            }
        });

        invoicenet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                change=true;
            }
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                change=true;
            }
        });
    }
}
