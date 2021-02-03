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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Models.Customer;
import Models.Vat;
import Models.recordCreditNote;
import Process_Classes.CheckAvailability;
import Stables.CurrencyConvert;
import Stables.CurrentCurrency;
import Stables.CurrentUser;
import Stables.LastRemember;
import Stables.ValidateDate;

public class recordCreditNotes extends AppCompatActivity implements CalcDialog.CalcDialogCallback{

    private EditText datefield,creditnotenumber,creditnotenet,creditnotetotal,note;
    public Spinner vatspinner,customerspinner;
    String vatid,vatname,customerid,customername;
    private CalcDialog calcDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference customer_reference;
    DatabaseReference vat_reference;
    DatabaseReference recordcredit_reference;
    ImageButton vatbtn;
    ConstraintLayout vatconslayout;
    TextView vat,vattxt,lastcreditnotenumber,titletext;
    double vatval=0.0;

    Button creditnotesavebtn,creditnotedeletebtn;

    boolean status,change;
    String existid;
    int vid,cusid;

    ScrollView recordcreditnotesscrollview;

    boolean customercheck;

    int vatboxckeched=1;
    double vatprecentage=0.0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_credit_notes);

        firebaseDatabase=FirebaseDatabase.getInstance();
        customer_reference=firebaseDatabase.getReference("customer");
        vat_reference=firebaseDatabase.getReference("vat");
        recordcredit_reference=firebaseDatabase.getReference("recordcredit");
        vatconslayout=findViewById(R.id.vatconslayout);
        titletext=findViewById(R.id.titletext);
        creditnotesavebtn=findViewById(R.id.creditnotesavebtn);
        creditnotedeletebtn=findViewById(R.id.creditnotedeletebtn);

        recordcreditnotesscrollview=findViewById(R.id.recordcreditnotesscrollview);

        if(getIntent().getStringExtra("status").equals("1")){
            status=true;
            existid=getIntent().getStringExtra("id");
            creditnotesavebtn.setText("Update");
            creditnotedeletebtn.setText("Delete");

            creditnotedeletebtn.setBackgroundColor(getResources().getColor(R.color.pdlg_color_red));

            titletext.setText("Update Record Credit Note");

        }

        creditnotesavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    saveData();
                }else{
                    updateData();
                }

            }
        });

        creditnotedeletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    clearAllTxt();
                }else{
                    DeleteRecord();
                }

            }
        });

        initializing();

    }

    private void initializing() {
        datefield=findViewById(R.id.datefield1);
        calcDialog=new CalcDialog();

        vatbtn=findViewById(R.id.vatbtn1);


        vatspinner=findViewById(R.id.vatspinner1);
        customerspinner=findViewById(R.id.customer_select_spinner1);

        lastcreditnotenumber=findViewById(R.id.txt_last_credit_note_number1);
        creditnotenumber=findViewById(R.id.creditnotenumber1);
        creditnotenet=findViewById(R.id.invoicenet1);
        vat=findViewById(R.id.vatval1);
        vattxt=findViewById(R.id.vatviewtxt);
        creditnotetotal=findViewById(R.id.creditnotetotal1);
        note=findViewById(R.id.notetxt1);


        setVatDefaults();

        vatid="";

        vatname="";


        if(status==false && LastRemember.recordCreditNotes!=null){
            vatboxckeched=LastRemember.recordCreditNotes.vatboxckeched;
            vatval=LastRemember.recordCreditNotes.vatval;
            vatprecentage=LastRemember.recordCreditNotes.vatval;
            vattxt.setText(LastRemember.recordCreditNotes.vattxt.getText());
            vat.setText(LastRemember.recordCreditNotes.vat.getText());
            datefield.setText(LastRemember.recordCreditNotes.datefield.getText());
            vid=LastRemember.recordCreditNotes.vatspinner.getSelectedItemPosition();
            vatspinner.setSelection(vid);
            creditnotenumber.setText(LastRemember.recordCreditNotes.creditnotenumber.getText());
            creditnotenet.setText(LastRemember.recordCreditNotes.creditnotenet.getText());
            creditnotetotal.setText(LastRemember.recordCreditNotes.creditnotetotal.getText());
            note.setText(LastRemember.recordCreditNotes.note.getText());
            cusid=LastRemember.recordCreditNotes.customerspinner.getSelectedItemPosition();
        }


        checkData();

        addTextListeneres();


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

        creditnotenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!creditnotenumber.getText().toString().isEmpty()){
                    change=true;
                }

            }
        });

        creditnotenet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!creditnotenet.getText().toString().isEmpty()){
                    change=true;
                }
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
                if(!note.getText().toString().isEmpty()){
                    change=true;
                }
            }
        });

        vat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!vat.getText().toString().isEmpty()){
                    change=true;
                }
            }
        });
    }

    public void checkData(){
        if(status==false){
            datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));

            Query lastQuery = recordcredit_reference.orderByChild("addeddate").limitToLast(1);
            lastQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterable){
                        recordCreditNote recordCreditNote=data.getValue(recordCreditNote.class);
                        if(recordCreditNote!=null && recordCreditNote.getUser().equals(CurrentUser.user.getId())){
                            lastcreditnotenumber.setText(recordCreditNote.getCreditnotenumber());
                        }
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            loadCustomers();

            creditnotenet.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!creditnotenet.getText().toString().isEmpty()){
                        double val1=Double.parseDouble(creditnotenet.getText().toString());

                        if(vatboxckeched==2){
                            vat.setText(vatval+"");
                            creditnotetotal.setText(BigDecimal.valueOf(val1+vatval).setScale(2, RoundingMode.HALF_EVEN).toString());
                            vatboxckeched=2;
                            vatprecentage=vatval;
                        }else{
                            if(vatval!=0.0){
                                double value1=(val1*vatval)/(100);
                                BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                                vat.setText(balance.doubleValue()+"");
                                creditnotetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).doubleValue()+"".trim());
                            }else{
                                creditnotetotal.setText(CurrencyConvert.Get(val1));
                            }
                        }


                    }
                }
            });
        }else{

            recordcredit_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterable){
                        recordCreditNote recordCreditNote=data.getValue(recordCreditNote.class);

                        if(recordCreditNote.getId().equals(existid)){
                            datefield.setText(recordCreditNote.getDate());
                            lastcreditnotenumber.setText(recordCreditNote.getLastcreditnotenumber());
                            creditnotenumber.setText(recordCreditNote.getCreditnotenumber());
                            creditnotenet.setText(CurrencyConvert.Get(Double.parseDouble(recordCreditNote.getCreditnotenet())));


                            if(recordCreditNote.getVat().equals("") || recordCreditNote.getVat()==null){
                                vatspinner.setSelection(0);
                            }else{
                                vat.setText(recordCreditNote.getVat());
                                vatspinner.setSelection(1);
                                vatboxckeched=recordCreditNote.getVatbutton();
                                if(vatboxckeched==1){
                                    vatval=Double.parseDouble(recordCreditNote.getVat());

                                }else{
                                    vatval=recordCreditNote.getVatval();
                                }
                                vatprecentage=recordCreditNote.getVatval();
                                vattxt.setText(recordCreditNote.getVattext());
                            }

                            creditnotetotal.setText(CurrencyConvert.Get(Double.parseDouble(recordCreditNote.getCreditnotetotal())));

                            customerid=recordCreditNote.getCustomerid();
                            customername=recordCreditNote.getCustomername();
                            note.setText(recordCreditNote.getNote());

                            loadCustomers();

                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            creditnotenet.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!creditnotenet.getText().toString().isEmpty()){
                        double val1=Double.parseDouble(creditnotenet.getText().toString());
                        if(vatval!=0.0){
                            double value1=(val1*vatval)/(100+vatval);
                            BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                            vat.setText(balance.doubleValue()+"");
                            creditnotetotal.setText(BigDecimal.valueOf(val1-balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                        }else{
                            creditnotetotal.setText(BigDecimal.valueOf(val1).setScale(2, RoundingMode.HALF_EVEN).toString());
                        }
                    }
                }
            });
        }
    }

    ArrayList <Customer> customerList;
    String customergetid;

    public void loadCustomers(){
        customer_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                if(status==true && customerid!=null){
                    namelist.add(customername);
                    idlist.add(customerid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }

                customerList=new ArrayList<>();

                for(DataSnapshot data:iterable){
                    Customer account1=data.getValue(Customer.class);
                    customerList.add(account1);
                }

                Collections.sort(customerList, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer customer1, Customer customer2) {
                        return customer1.getName().toLowerCase().compareTo(customer2.getName().toLowerCase());
                    }
                });


                for(Customer account1:customerList){
                    if(account1.getUser().equals(CurrentUser.user.getId())){
                        if(status==true && customerid!=null){
                            if(!account1.getId().equals(customerid)){
                                namelist.add(account1.getName());
                                idlist.add(account1.getId());
                            }

                        }else{
                            namelist.add(account1.getName());
                            idlist.add(account1.getId());
                        }
                    }
                }

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(recordCreditNotes.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                customerspinner.setAdapter(account_adapter);
                customerspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        customerid=idlist.get(position);
                        customername=namelist.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(cusid!=0 && LastRemember.recordCreditNotes!=null){
                    customerspinner.setSelection(cusid);
                }


                if(customercheck){
                    customerspinner.setSelection(idlist.indexOf(customergetid));
                    customercheck=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToHome(View view){
        if(status==false){
            System.out.println(change);
            if(change){
                final AlertDialog.Builder builder = new AlertDialog.Builder(recordCreditNotes.this);
                builder.setCancelable(true);
                builder.setTitle("This form has unsaved data.");
                builder.setMessage("Are you sure that you want to close without saving ?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.recordCreditNotes=recordCreditNotes.this;
                        onBackPressed();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.recordCreditNotes=null;
                        onBackPressed();

                    }
                });
                builder.create();
                builder.show();
            }else{
                LastRemember.recordCreditNotes=null;
                onBackPressed();
            }
        }else{
            LastRemember.recordCreditNotes=recordCreditNotes.this;
            onBackPressed();
        }
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
        if(!creditnotenet.getText().toString().isEmpty()){
            calcDialog.setValue(BigDecimal.valueOf(Double.parseDouble(creditnotenet.getText().toString())).setScale(2,BigDecimal.ROUND_CEILING));
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }else{
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }
    }

    public void clearAllTxt(){
        datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));
        vatspinner.setSelection(0);
        lastcreditnotenumber.setText("");
        creditnotenumber.setText("");
        customerspinner.setSelection(0);
        creditnotenet.setText("");
        vat.setText("");
        creditnotetotal.setText("");
        note.setText("");
        creditnotenumber.setText("");
        LastRemember.recordCreditNotes=null;
        change=false;
        recordcreditnotesscrollview.fullScroll(ScrollView.FOCUS_UP);
    }

    @Override
    public void onValueEntered(int requestCode, BigDecimal value) {
        creditnotenet.setText(value.setScale(2, RoundingMode.CEILING).toString());
    }

    public void saveData(){
        if(datefield!=null && !datefield.getText().toString().equals("") && !datefield.getText().toString().isEmpty()){
            if(new ValidateDate().validate(datefield.getText().toString())){
                if(creditnotenumber!=null && !creditnotenumber.getText().toString().equals("") && !creditnotenumber.getText().toString().isEmpty()){
                    if(customerid!=null && !customerid.equals("none")){
                        if(creditnotenet!=null && !creditnotenet.getText().toString().equals("") && !creditnotenet.getText().toString().isEmpty()){
                            final String key=recordcredit_reference.push().getKey();
                            if(vatid.equals("1")){
                                if(!vat.getText().toString().equals("")){
                                    recordcredit_reference.child(key).setValue(new recordCreditNote(key,datefield.getText().toString(),lastcreditnotenumber.getText().toString(),creditnotenumber.getText().toString(),customerid,customername,creditnotenet.getText().toString(),vat.getText().toString(),creditnotetotal.getText().toString(),note.getText().toString(),CurrentUser.user.getId(),1,vattxt.getText().toString(),vatval,vatboxckeched));
                                    Toast.makeText(this, "Record Invoice Saved", Toast.LENGTH_SHORT).show();

                                    vatspinner.setSelection(0);
                                    creditnotenumber.setText("");
                                    customerspinner.setSelection(0);
                                    creditnotenet.setText("");
                                    vat.setText("");
                                    creditnotetotal.setText("");
                                    note.setText("");
                                    creditnotenumber.setText("");
                                    LastRemember.recordCreditNotes=null;
                                    recordcreditnotesscrollview.fullScroll(ScrollView.FOCUS_UP);
                                    change=false;
                                    datefield.requestFocus();
                                }else{
                                    Toast.makeText(this, "Please insert vat value", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                recordcredit_reference.child(key).setValue(new recordCreditNote(key,datefield.getText().toString(),lastcreditnotenumber.getText().toString(),creditnotenumber.getText().toString(),customerid,customername,creditnotenet.getText().toString(),vat.getText().toString(),creditnotetotal.getText().toString(),note.getText().toString(),CurrentUser.user.getId(),1,vattxt.getText().toString(),vatval,vatboxckeched));
                                Toast.makeText(this, "Record Invoice Saved", Toast.LENGTH_SHORT).show();

                                vatspinner.setSelection(0);
                                creditnotenumber.setText("");
                                customerspinner.setSelection(0);
                                creditnotenet.setText("");
                                vat.setText("");
                                creditnotetotal.setText("");
                                note.setText("");
                                creditnotenumber.setText("");
                                LastRemember.recordCreditNotes=null;

                                recordcreditnotesscrollview.fullScroll(ScrollView.FOCUS_UP);
                                change=false;

                                datefield.requestFocus();
                            }

                            LastRemember.recordCreditNotes=null;
                            change=false;
                        }else{
                            Toast.makeText(this, "Credit note net empty", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Please select customer", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Please fill credit note number", Toast.LENGTH_SHORT).show();
                }
            } else{
                Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please fill date", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateData(){
        if(datefield!=null && !datefield.getText().toString().equals("") && !datefield.getText().toString().isEmpty()){
            if(new ValidateDate().validate(datefield.getText().toString())){
                if(creditnotenumber!=null && !creditnotenumber.getText().toString().equals("") && !creditnotenumber.getText().toString().isEmpty()){
                    if(customerid!=null && !customerid.equals("none")){
                        if(creditnotenet!=null && !creditnotenet.getText().toString().equals("") && !creditnotenet.getText().toString().isEmpty()){
                            if(vatid.equals("1")){
                                if(!vat.getText().toString().equals("")){
                                    recordcredit_reference.child(existid).setValue(new recordCreditNote(existid,datefield.getText().toString(),lastcreditnotenumber.getText().toString(),creditnotenumber.getText().toString(),customerid,customername,creditnotenet.getText().toString(),vat.getText().toString(),creditnotetotal.getText().toString(),note.getText().toString(),CurrentUser.user.getId(),1,vattxt.getText().toString(),vatval,vatboxckeched));
                                    Toast.makeText(this, "Record Invoice Updated", Toast.LENGTH_SHORT).show();
                                    datefield.requestFocus();
                                    checkData();
                                }else{
                                    Toast.makeText(this, "Please insert vat value", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                recordcredit_reference.child(existid).setValue(new recordCreditNote(existid,datefield.getText().toString(),lastcreditnotenumber.getText().toString(),creditnotenumber.getText().toString(),customerid,customername,creditnotenet.getText().toString(),vat.getText().toString(),creditnotetotal.getText().toString(),note.getText().toString(),CurrentUser.user.getId(),1,vattxt.getText().toString(),vatval,vatboxckeched));
                                Toast.makeText(this, "Record Invoice Updated", Toast.LENGTH_SHORT).show();

                                checkData();
                            }
                        }else{
                            Toast.makeText(this, "Credit note net empty", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Please select customer", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Please fill credit note number", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please fill date", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoCustomer(View view){
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
                        String key=customer_reference.push().getKey();
                        customergetid=key;
                        customer_reference.child(key).setValue(new Customer(key,titleBox.getText().toString(), CurrentUser.user.getId()));
                        Toast.makeText(recordCreditNotes.this, "Customer Saved", Toast.LENGTH_SHORT).show();
                        customercheck=true;
                    }else{
                        Toast.makeText(recordCreditNotes.this, "Customer Name Exists, Please Try Another", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(recordCreditNotes.this, "Please Enter Name For Save New Customer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(layout);
        builder.create();
        builder.show();
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

                    vatval=0.0;
                    vat.setText("");

                    creditnotetotal.setText(creditnotenet.getText().toString());
                }else{

                    if(status==false && LastRemember.recordCreditNotes==null || (LastRemember.recordCreditNotes!=null && LastRemember.recordCreditNotes.vatid.equals("2"))){
                        change=true;
                        vat_reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                for(DataSnapshot data:iterable){
                                    Vat vat1=data.getValue(Vat.class);
                                    if(vat1.getUser().equals(CurrentUser.user.getId())){
                                        vattxt.setText("VAT "+ CurrencyConvert.Get(vat1.getValue()) +"% ("+ CurrentCurrency.get() +")");
                                        vatval=vat1.getValue();
                                        vatprecentage=vat1.getValue();
                                        if(!creditnotenet.getText().toString().isEmpty()){
                                            double val1=Double.parseDouble(creditnotenet.getText().toString());
                                            if(vatval!=0.0){
                                                double value1=(val1*vatval)/(100);
                                                BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                                                vat.setText(balance.toString());
                                                creditnotetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                                            }else{
                                                creditnotetotal.setText(CurrencyConvert.Get(val1));
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

                        vatconslayout.setVisibility(View.VISIBLE);
                        if(vid!=0 && LastRemember.recordCreditNotes!=null && LastRemember.recordCreditNotes.vatspinner.getSelectedItemPosition()!=0){
                            vatspinner.setSelection(vid);
                            vatval=LastRemember.recordCreditNotes.vatval;
                            vat.setText(LastRemember.recordCreditNotes.vatval+"");
                            if(!LastRemember.recordCreditNotes.creditnotenet.getText().toString().isEmpty() && !LastRemember.recordCreditNotes.vat.getText().toString().isEmpty()){
                                double val1=Double.parseDouble(creditnotenet.getText().toString());
                                if(vatval!=0.0){
                                    creditnotetotal.setText(BigDecimal.valueOf(val1+Double.parseDouble(vat.getText().toString())).setScale(2, RoundingMode.HALF_EVEN).toString());
                                }else{
                                    creditnotetotal.setText(CurrencyConvert.Get(val1));
                                }
                            }
                            vid=0;
                        }
                    }



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void goToVat(View view){
        if(!creditnotetotal.getText().toString().isEmpty()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Change VAT Value");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleBox = new EditText(this);
            titleBox.setHint("Expected Value");
            titleBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            titleBox.setText(CurrencyConvert.Get(vatval));

            if(vatboxckeched==1){
                titleBox.setText(CurrencyConvert.Get(vatprecentage));
            }else{
                titleBox.setText(CurrencyConvert.Get(vatval));
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
                        double val1=Double.parseDouble(creditnotenet.getText().toString());
                        double valvat=Double.parseDouble(titleBox.getText().toString());
                        if(radioButton.isChecked()){
                            double value1=(val1*valvat)/(100);
                            BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                            vat.setText(balance.toString()+"");
                            creditnotetotal.setText(BigDecimal.valueOf(val1+balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString());
                            vattxt.setText("VAT (Custom Percentage: "+CurrencyConvert.Get(valvat)+"%)");
                            vatboxckeched=1;
                            vatprecentage=valvat;
                        }else if(radioButton1.isChecked()){
                            if(val1<= valvat){
                                Toast.makeText(recordCreditNotes.this, "Custom VAT values cannot be equals or greater than net total", Toast.LENGTH_SHORT).show();
                            }else{
                                vat.setText(CurrencyConvert.Get(valvat));
                                creditnotetotal.setText(BigDecimal.valueOf(val1+valvat).setScale(2, RoundingMode.HALF_EVEN).toString());
                                vattxt.setText("VAT (Custom fixed Value: "+ CurrentCurrency.get()+CurrencyConvert.Get(valvat)+")");
                                vatboxckeched=2;
                                vatprecentage=valvat;
                            }



                        }

                        vatval=valvat;
                    }else{
                        Toast.makeText(recordCreditNotes.this, "Please Enter Value", Toast.LENGTH_SHORT).show();
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

    public void DeleteRecord(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(recordCreditNotes.this);
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
                recordcredit_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                        for(DataSnapshot data:iterable){
                            recordCreditNote recordCreditNote=data.getValue(recordCreditNote.class);

                            if(recordCreditNote.getId().equals(existid)){
                                recordcredit_reference.child(existid).removeValue();
                                Toast.makeText(recordCreditNotes.this, "Record Deleted", Toast.LENGTH_SHORT).show();
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
}
