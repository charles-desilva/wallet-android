package com.vaofim.boffin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.nmaltais.calcdialog.CalcDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Models.Account;
import Models.Customer;
import Models.Offline;
import Models.addincome;
import Models.incomeCategory;
import Models.incomeSubCategory;
import Process_Classes.CheckAvailability;
import Stables.BitmapToUri;
import Stables.CurrencyConvert;
import Stables.CurrentUser;
import Stables.LastRemember;
import Stables.ValidateDate;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class add_income extends AppCompatActivity implements AdapterView.OnItemSelectedListener,CalcDialog.CalcDialogCallback {

    boolean change=false;

    private static int reqCode1=1;
    private static int reqCode2=2;
    private boolean customercount;
    private boolean categorycount;
    private boolean subcategorycount;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    String accountid;
    String accountname;
    DatabaseReference account_reference;

    String categoryid;
    String categoryname;
    DatabaseReference category_reference;

    String subcaregoryid;
    String subcategoryname;
    DatabaseReference sub_category_reference;

    String customerid;
    String customername;
    DatabaseReference customer_reference;

    DatabaseReference reciptsreference;

    StorageReference imagereference;

    private EditText datefield,total,refno,note;
    private CalcDialog calcDialog;
    private Spinner account_spinner,category_spinner, subCategory_spinner,customer_spinner;
    private TextView titletext;

    String imagepath,existkey;
    Uri imagedata;
    boolean status;

    int subcat;

    Button savebtn,deletebtn;

    ImageView imageView;

    ScrollView addinomescrollview;

    String categoryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);




        initializing();

        if(getIntent().getStringExtra("status").equals("1")){
            status=true;
            existkey=getIntent().getStringExtra("id");
            savebtn.setText("Update");
            deletebtn.setText("Delete");

            deletebtn.setBackgroundColor(getResources().getColor(R.color.pdlg_color_red));

            titletext.setText("Update Record Invoice");

        }

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    saveIncomeRecord();
                }else{
                    updateIncomeRecord();
                }

            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    clearIncomeRecord();
                }else{
                    deleteIncomeRecord();
                }

            }
        });

        finalizing();

        if(LastRemember.add_income!=null && status==false){
            datefield.setText(LastRemember.add_income.datefield.getText().toString());
            total.setText(LastRemember.add_income.total.getText().toString());
            refno.setText(LastRemember.add_income.refno.getText().toString());
            note.setText(LastRemember.add_income.note.getText().toString());
            subcat=LastRemember.add_income.subCategory_spinner.getSelectedItemPosition();
        }


    }

    private void initializing() {

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        account_reference=firebaseDatabase.getReference("account");
        category_reference=firebaseDatabase.getReference("income_category");
        sub_category_reference=firebaseDatabase.getReference("income_subcategory");
        customer_reference=firebaseDatabase.getReference("customer");
        reciptsreference=firebaseDatabase.getReference("recipts");
        imagereference=firebaseStorage.getReference("images").child(CurrentUser.user.getId());

        datefield=findViewById(R.id.datefield);
        calcDialog=new CalcDialog();
        total=findViewById(R.id.addincometotal);
        refno=findViewById(R.id.addincomerefno);
        note=findViewById(R.id.addincomenotefield);

        account_spinner = findViewById(R.id.vatspinner);
        category_spinner =  findViewById(R.id.category_select_spinner);
        subCategory_spinner = findViewById(R.id.subCategory_select_spinner);
        customer_spinner=  findViewById(R.id.customer_select_spinner);

        savebtn=findViewById(R.id.addincomesavebtn);
        deletebtn=findViewById(R.id.addincomedeletebtn);

        imageView=findViewById(R.id.addincomeimgview);

        titletext=findViewById(R.id.titletext);

        datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));

        addinomescrollview=findViewById(R.id.addinomescrollview);

    }

    private void finalizing() {

        if(status==true){
            reciptsreference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterable){
                        addincome addIncome=data.getValue(addincome.class);
                        if(addIncome.getId().equals(existkey) && addIncome.getUser().equals(CurrentUser.user.getId())){

                            datefield.setText(addIncome.getDate());
                            accountid=addIncome.getAccountid();
                            accountname=addIncome.getAccountname();
                            categoryid=addIncome.getCategoryid();
                            categoryString=addIncome.getCategoryid();
                            subcaregoryid=addIncome.getSubcategoryid();
                            categoryname=addIncome.getCategoryname();
                            subcategoryname=addIncome.getSubcategoryname();
                            total.setText(CurrencyConvert.Get(Double.parseDouble(addIncome.getTotlareceived())));
                            refno.setText(addIncome.getInternalrefno());
                            customerid=addIncome.getCustomerid();
                            customername=addIncome.getCustomername();
                            note.setText(addIncome.getNote());

                            if(addIncome.getImagepath()!=null && !addIncome.getImagepath().isEmpty() && status==true){
                                imagepath=addIncome.getImagepath();
                                if(isNetworkAvailable()){
                                    final addincome addIncome2=addIncome;
                                    imageView.setImageResource(R.drawable.loadingspinnernew);
                                    imagereference.child(addIncome.getImagepath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            try {
                                                final Uri uridata=uri;
                                                final File file=new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+addIncome2.getImagepath());
                                                if(!file.exists()){
                                                    final ProgressDialog dialog=new ProgressDialog(add_income.this);
                                                    dialog.setTitle("Please wait");
                                                    dialog.setMessage("Your Attachment Processing");
                                                    dialog.setCanceledOnTouchOutside(false);
                                                    dialog.show();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {

                                                                URL url=new URL(uridata.toString());
                                                                InputStream in=url.openStream();
                                                                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                                                                byte[] buffer = new byte[1024];
                                                                int len;
                                                                while ((len = in.read(buffer)) != -1) {
                                                                    byteArrayOutputStream.write(buffer, 0, len);
                                                                }
                                                                OutputStream outputStream = openFileOutput(addIncome2.getImagepath(), getApplicationContext().MODE_PRIVATE);
                                                                outputStream.write(byteArrayOutputStream.toByteArray());
                                                                outputStream.close();
                                                                dialog.dismiss();
                                                            }catch(Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();
                                                    Picasso.get().load(uri).into(imageView);
                                                }else{
                                                    Picasso.get().load(file).into(imageView);
                                                }

                                                imageView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent=new Intent(getApplicationContext(), ImageViewer.class);
                                                        intent.putExtra("img",file.getAbsolutePath());
                                                        intent.putExtra("name",addIncome2.getImagepath());
                                                        intent.putExtra("path",uridata.toString());
                                                        startActivity(intent);
                                                    }
                                                });
                                            }catch(Exception e){

                                            }
                                        }
                                    });
                                }else{
                                    imageView.setImageResource(R.drawable.nointernet);
                                }

                            }else{
                                imageView.setImageResource(R.drawable.upload_image);
                            }

                            getAccounts();
                            getCustomerrs();
                            getCategories();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            getAccounts();
            getCustomerrs();
            getCategories();
            addTextListeneres();
        }


    }

    ArrayList <Account> accountList;
    ArrayList <incomeCategory> catList;
    ArrayList <incomeSubCategory> subCatList;
    ArrayList <Customer> customerList;

    String catautoselid;
    String subcatautoselid;
    String customerautoid;

    public void getAccounts(){
        account_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                accountList=new ArrayList<>();

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                for(DataSnapshot data:iterable){
                    Account account=data.getValue(Account.class);
                    accountList.add(account);
                }

                Collections.sort(accountList, new Comparator<Account>() {
                    @Override
                    public int compare(Account t1, Account t2) {
                        return t1.getAccountName().toLowerCase().compareTo(t2.getAccountName().toLowerCase());
                    }
                });

                if(status==true){
                    namelist.add(accountname);
                    idlist.add(accountid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }

                for(Account account:accountList){
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        if(status==true){
                            if(!account.getId().equals(accountid)){
                                namelist.add(account.getAccountName());
                                idlist.add(account.getId());
                            }
                        }else{
                            namelist.add(account.getAccountName());
                            idlist.add(account.getId());
                        }

                    }
                }

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(add_income.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                account_spinner.setAdapter(account_adapter);
                account_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountid=idlist.get(position);
                        accountname=namelist.get(position);

                        if(!accountid.equals("none")){
                            change=true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(LastRemember.add_income!=null && status==false){
                    account_spinner.setSelection(LastRemember.add_income.account_spinner.getSelectedItemPosition());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCategories(){
        category_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                catList=new ArrayList<>();

                for(DataSnapshot data:iterable){
                    incomeCategory account=data.getValue(incomeCategory.class);
                    catList.add(account);
                }

                Collections.sort(catList, new Comparator<incomeCategory>() {
                    @Override
                    public int compare(incomeCategory incomeCategory, incomeCategory t1) {
                        return incomeCategory.getCategory().toLowerCase().compareTo(t1.getCategory().toLowerCase());
                    }
                });

                if(status==true){
                    namelist.add(categoryname);
                    idlist.add(categoryid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }

                for(incomeCategory account:catList){
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

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(add_income.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                category_spinner.setAdapter(account_adapter);

                if(categorycount){
                    category_spinner.setSelection(idlist.indexOf(catautoselid));
                    categorycount=false;
                }

                category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        categoryid=idlist.get(position);
                        categoryname=namelist.get(position);

                        final String x=categoryid;

                        sub_category_reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                final ArrayList <String> namelist=new ArrayList <String> ();
                                final ArrayList <String> idlist=new ArrayList <String> ();

                                subCatList=new ArrayList<>();

                                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();


                                if(status==true){
                                    if(categoryString==null){
                                        namelist.add(subcategoryname);
                                        idlist.add(subcaregoryid);
                                    }else{
                                        if(categoryid.equals(categoryString)){
                                            namelist.add(subcategoryname);
                                            idlist.add(subcaregoryid);
                                        }else{
                                            categoryString="-";
                                        }
                                    }

                                }

//                                for(DataSnapshot data:iterable){
//                                    incomeSubCategory account1=data.getValue(incomeSubCategory.class);
//                                    if(account1.getCategory().equals(x)){
//                                        if(status==true){
//                                            if(!account1.getId().equals(subcaregoryid)){
//                                                namelist.add(account1.getSubCategory());
//                                                idlist.add(account1.getId());
//                                            }
//                                        }else{
//                                            namelist.add(account1.getSubCategory());
//                                            idlist.add(account1.getId());
//                                        }
//                                    }
//                                }

                                for(DataSnapshot data:iterable){
                                    incomeSubCategory account1=data.getValue(incomeSubCategory.class);
                                    subCatList.add(account1);
                                }

                                Collections.sort(subCatList, new Comparator<incomeSubCategory>() {
                                    @Override
                                    public int compare(incomeSubCategory incomeSubCategory, incomeSubCategory t1) {
                                        return incomeSubCategory.getSubCategory().toLowerCase().compareTo(t1.getSubCategory().toLowerCase());
                                    }
                                });

                                for(incomeSubCategory account1:subCatList){
                                    if(account1.getCategory().equals(categoryid)){
                                        if(status==true){
                                            if(!account1.getId().equals(subcaregoryid)){
                                                namelist.add(account1.getSubCategory());
                                                idlist.add(account1.getId());
                                            }
                                        }else{
                                            namelist.add(account1.getSubCategory());
                                            idlist.add(account1.getId());
                                        }
                                    }
                                }

                                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(add_income.this,R.layout.spinner_dropdown_design, namelist);
                                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                                subCategory_spinner.setAdapter(account_adapter);
                                subCategory_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        subcaregoryid=idlist.get(position);
                                        subcategoryname=namelist.get(position);

                                        if(!subcaregoryid.equals("none")){
                                            change=true;
                                        }
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

                        if(!categoryid.equals("none")){
                            change=true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(LastRemember.add_income!=null && status==false){
                    category_spinner.setSelection(LastRemember.add_income.category_spinner.getSelectedItemPosition());

                    if(subcat!=0 && LastRemember.add_income!=null){
                        subCategory_spinner.setSelection(subcat);
                        subcat=0;
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setSu(){
        sub_category_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                subCatList=new ArrayList<>();

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                if(status==true){
                    namelist.add(subcategoryname);
                    idlist.add(subcaregoryid);
                }

                for(DataSnapshot data:iterable){

                    incomeSubCategory account1=data.getValue(incomeSubCategory.class);
                    if(status==true && subcaregoryid!=null){
                        if(!account1.getId().equals(subcaregoryid)){
                            subCatList.add(account1);
                        }
                    }else{
                        subCatList.add(account1);
                    }

                }

                Collections.sort(subCatList, new Comparator<incomeSubCategory>() {
                    @Override
                    public int compare(incomeSubCategory incomeSubCategory, incomeSubCategory t1) {
                        return incomeSubCategory.getSubCategory().toLowerCase().compareTo(t1.getSubCategory().toLowerCase());
                    }
                });

                for(incomeSubCategory account1:subCatList){
                    if(account1.getCategory().equals(categoryid)){
                        if(status==true){
                            if(!account1.getId().equals(subcaregoryid)){
                                namelist.add(account1.getSubCategory());
                                idlist.add(account1.getId());
                            }
                        }else{
                            namelist.add(account1.getSubCategory());
                            idlist.add(account1.getId());
                        }
                    }
                }

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(add_income.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                subCategory_spinner.setAdapter(account_adapter);
                subCategory_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        subcaregoryid=idlist.get(position);
                        subcategoryname=namelist.get(position);

                        if(!subcaregoryid.equals("none")){
                            change=true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



                if(subcategorycount){
                    subCategory_spinner.setSelection(idlist.indexOf(subcatautoselid));
                    subcategorycount=false;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCustomerrs(){
        customer_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                customerList=new ArrayList<>();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                if(status==true){
                    namelist.add(customername);
                    idlist.add(customerid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }

                for(DataSnapshot data:iterable){
                    Customer account1=data.getValue(Customer.class);
                    customerList.add(account1);
                }

                Collections.sort(customerList, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer customer, Customer cus) {
                        return customer.getName().toLowerCase().compareTo(cus.getName().toLowerCase());
                    }
                });

                for(Customer account1:customerList){
                    if(account1.getUser().equals(CurrentUser.user.getId())){
                        if(status==true){
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

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(add_income.this,R.layout.spinner_dropdown_design, namelist);
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

                if(LastRemember.add_income!=null && status==false){
                    customer_spinner.setSelection(LastRemember.add_income.customer_spinner.getSelectedItemPosition());
                }

                if(customercount){
                    customer_spinner.setSelection(idlist.indexOf(customerautoid));
                    customercount=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

                    if(new CheckAvailability().incomeCategoryCheck(catList,titleBox.getText().toString())){
                        String key=category_reference.push().getKey();
                        category_reference.child(key).setValue(new incomeCategory(key,titleBox.getText().toString(), CurrentUser.user.getId(),2));
                        String subkey=sub_category_reference.push().getKey();
                        sub_category_reference.child(subkey).setValue(new incomeSubCategory(subkey,"General", key,1));
                        categorycount=true;
                        Toast.makeText(add_income.this, "Category Saved", Toast.LENGTH_SHORT).show();
                        catautoselid=key;
                    }else{
                        Toast.makeText(add_income.this, "Income Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(add_income.this, "Please Enter Name For Save New Category", Toast.LENGTH_SHORT).show();
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
                        String key=sub_category_reference.push().getKey();
                        subcatautoselid=key;
                        sub_category_reference.child(key).setValue(new incomeSubCategory(key,titleBox.getText().toString(), categoryid,2));
                        subcategorycount=true;
                        setSu();
                        Toast.makeText(add_income.this, "Sub Category Saved", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(add_income.this, "Please Enter Name For Save New Sub Category", Toast.LENGTH_SHORT).show();
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
                        String key=customer_reference.push().getKey();
                        customerautoid=key;
                        customer_reference.child(key).setValue(new Customer(key,titleBox.getText().toString(), CurrentUser.user.getId()));
                        customercount=true;
                        Toast.makeText(add_income.this, "Customer Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(add_income.this, "Customer Name Exists, Please Try Another", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(add_income.this, "Please Enter Name For Save New Customer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(layout);
        builder.create();
        builder.show();
    }

    public void goToHome(View view){
        if(status==false){
            if(change){
                final AlertDialog.Builder builder = new AlertDialog.Builder(add_income.this);
                builder.setCancelable(true);
                builder.setTitle("This form has unsaved data.");
                builder.setMessage("Are you sure that you want to close without saving ?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.add_income=add_income.this;
                        onBackPressed();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.add_income=null;
                        onBackPressed();

                    }
                });
                builder.create();
                builder.show();
            }else{
                onBackPressed();
            }
        }else{
            LastRemember.add_income=add_income.this;
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
        if(!total.getText().toString().isEmpty()){
            calcDialog.setValue(BigDecimal.valueOf(Double.parseDouble(total.getText().toString())));
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }else {
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }
    }

    public void uploadimg(View view){
        final PrettyDialog prettyDialog=new PrettyDialog(this);
        prettyDialog.setTitle("Choose Source");
        prettyDialog.addButton(
                        "Camera",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, reqCode1);
                                    prettyDialog.cancel();
                                }
                            }
                        }
                );
        prettyDialog.addButton(
                        "File Browser",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                Intent upload_intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                upload_intent.setType("image/*");
                                upload_intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(upload_intent,"Upload Image"),reqCode2);
                                prettyDialog.cancel();
                            }
                        }
                );
        prettyDialog.show();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            String filename= new SimpleDateFormat("YYMMDDHHMMSS").format(new Date())+System.currentTimeMillis()+".jpg";
            if(requestCode==reqCode2){
                if(data!=null){

                }
                imagedata = data.getData();
                imageView.setImageURI(imagedata);
                imagepath=filename;
            }else if(requestCode==reqCode1){
                if(data!=null){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imagedata = new BitmapToUri().getUri(add_income.this,photo);
                    if(imagedata!=null){
                        imageView.setImageBitmap(photo);
                        imagepath=filename;
                    }else{
                        Toast.makeText(this, "Image Capturing Error", Toast.LENGTH_SHORT).show();
                    }
                    
                }

            }else{
                imagepath=null;
                imageView.setImageResource(R.drawable.upload_image);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void saveIncomeRecord(){
        if(!datefield.getText().toString().isEmpty() && new ValidateDate().validate(datefield.getText().toString())){
            if(!accountid.isEmpty() && accountid!=null && !accountid.equals("none")){
                if(!categoryid.isEmpty() && categoryid!=null && !categoryid.equals("none")){
                    if(!subcaregoryid.isEmpty() && subcaregoryid!=null && !subcaregoryid.equals("none")){
                        if(!total.getText().toString().isEmpty()){
                            if(!customerid.isEmpty() && customerid!=null && !customerid.equals("none")){
                                final String imgpath=imagepath;
                                if(imagepath!=null && !imagepath.equals("") && imagedata!=null){
                                    final String key=reciptsreference.push().getKey();
                                    final ProgressDialog dialog=new ProgressDialog(add_income.this);
                                    dialog.setTitle("Please wait");
                                    dialog.setMessage("Your patience is very important while upload attachments");
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    dialog.show();
                                    if(isNetworkAvailable()){
                                        try {
                                            UploadTask uploadTask=imagereference.child(imgpath).putFile(imagedata);
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    reciptsreference.child(key).setValue(new addincome(key,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcaregoryid,subcategoryname,CurrencyConvert.Get(Double.parseDouble(total.getText().toString())),refno.getText().toString(),customerid,customername,note.getText().toString(),imgpath,1,CurrentUser.user.getId()));
                                                    dialog.dismiss();
                                                    datefield.requestFocus();
                                                    datefield.setSelection(0);
                                                    Toast.makeText(add_income.this, "Receipt Saved", Toast.LENGTH_SHORT).show();
                                                    account_spinner.setSelection(0);
                                                    category_spinner.setSelection(0);
                                                    subCategory_spinner.setSelection(0);
                                                    customer_spinner.setSelection(0);
                                                    total.setText("");
                                                    refno.setText("");
                                                    note.setText("");
                                                    imagepath=null;
                                                    change=false;
                                                    imageView.setImageResource(R.drawable.upload_image);
                                                    addinomescrollview.fullScroll(ScrollView.FOCUS_UP);
                                                    datefield.requestFocus();
                                                    datefield.setSelection(0);
                                                    System.out.println("1 -------------------");

                                                }
                                            });
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        try {
                                            InputStream in=getContentResolver().openInputStream(imagedata);

                                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

                                            byte[] buffer = new byte[1024];
                                            int len;

                                            while ((len = in.read(buffer)) != -1) {
                                                byteArrayOutputStream.write(buffer, 0, len);
                                            }

                                            OutputStream outputStream = openFileOutput(imgpath, getApplicationContext().MODE_PRIVATE);
                                            outputStream.write(byteArrayOutputStream.toByteArray());
                                            outputStream.close();
                                            reciptsreference.child(key).setValue(new addincome(key,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcaregoryid,subcategoryname,CurrencyConvert.Get(Double.parseDouble(total.getText().toString())),refno.getText().toString(),customerid,customername,note.getText().toString(),imgpath,1,CurrentUser.user.getId()));
                                            datefield.requestFocus();
                                            datefield.setSelection(0);
                                            DatabaseReference offlineref=firebaseDatabase.getReference("offline").child(CurrentUser.user.getId());
                                            String offlinekey=offlineref.push().getKey();
                                            offlineref.child(offlinekey).setValue(new Offline(offlinekey,imagepath,CurrentUser.user.getId()));

                                            Toast.makeText(add_income.this, "Receipt Saved", Toast.LENGTH_SHORT).show();
                                            account_spinner.setSelection(0);
                                            category_spinner.setSelection(0);
                                            subCategory_spinner.setSelection(0);
                                            customer_spinner.setSelection(0);
                                            total.setText("");
                                            refno.setText("");
                                            note.setText("");
                                            imagepath=null;
                                            addinomescrollview.fullScroll(ScrollView.FOCUS_UP);
                                            imageView.setImageResource(R.drawable.upload_image);
                                            dialog.dismiss();
                                            datefield.requestFocus();
                                            datefield.setSelection(0);
                                            System.out.println("2 -------------------");
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }else{
                                    String key=reciptsreference.push().getKey();
                                    reciptsreference.child(key).setValue(new addincome(key,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcaregoryid,subcategoryname, CurrencyConvert.Get(Double.parseDouble(total.getText().toString())),refno.getText().toString(),customerid,customername,note.getText().toString(),imgpath,1,CurrentUser.user.getId()));
                                    Toast.makeText(add_income.this, "Receipt Saved", Toast.LENGTH_SHORT).show();
                                    account_spinner.setSelection(0);
                                    category_spinner.setSelection(0);
                                    subCategory_spinner.setSelection(0);
                                    customer_spinner.setSelection(0);
                                    total.setText("");
                                    refno.setText("");
                                    note.setText("");
                                    imagepath=null;
                                    addinomescrollview.fullScroll(ScrollView.FOCUS_UP);
                                    imageView.setImageResource(R.drawable.upload_image);
                                    datefield.requestFocus();
                                    datefield.setSelection(0);
                                }

                                change=false;

                            }else{
                                Toast.makeText(this, "Please select customer", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Please fill total received", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Please select sub category", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please select account", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Date field empty or invalid date.", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearIncomeRecord(){
        account_spinner.setSelection(0);
        category_spinner.setSelection(0);
        subCategory_spinner.setSelection(0);
        customer_spinner.setSelection(0);
        total.setText("");
        refno.setText("");
        change=false;
        note.setText("");
        imagepath=null;
        datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));
        imageView.setImageResource(R.drawable.upload_image);
        change=false;
        addinomescrollview.fullScroll(ScrollView.FOCUS_UP);
    }

    @Override
    public void onValueEntered(int requestCode, BigDecimal value) {
        total.setText(value.doubleValue()+"");
    }

    public void updateIncomeRecord(){
        if(!datefield.getText().toString().isEmpty() && new ValidateDate().validate(datefield.getText().toString())){
            if(!accountid.isEmpty() && accountid!=null && !accountid.equals("none")){
                if(!categoryid.isEmpty() && categoryid!=null && !categoryid.equals("none")){
                    if(!subcaregoryid.isEmpty() && subcaregoryid!=null && !subcaregoryid.equals("none")){
                        if(!total.getText().toString().isEmpty()){
                            if(!customerid.isEmpty() && customerid!=null && !customerid.equals("none")){
                                final String imgpath=imagepath;
                                if(imagepath!=null && !imagepath.equals("") && imagedata!=null){
                                    final ProgressDialog dialog=new ProgressDialog(add_income.this);
                                    dialog.setTitle("Please wait");
                                    dialog.setMessage("Your patience is very important while update attachments");
                                    dialog.show();
                                    if(isNetworkAvailable()){
                                        UploadTask uploadTask=imagereference.child(imgpath).putFile(imagedata);
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                reciptsreference.child(existkey).setValue(new addincome(existkey,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcaregoryid,subcategoryname,CurrencyConvert.Get(Double.parseDouble(total.getText().toString())),refno.getText().toString(),customerid,customername,note.getText().toString(),imgpath,1,CurrentUser.user.getId()));
                                                dialog.dismiss();
                                                Toast.makeText(add_income.this, "Receipt Updated", Toast.LENGTH_SHORT).show();
                                                datefield.requestFocus();
                                                imagedata=null;
                                            }
                                        });
                                    }else{
                                        try {
                                            InputStream in=getContentResolver().openInputStream(imagedata);

                                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

                                            byte[] buffer = new byte[1024];
                                            int len;

                                            while ((len = in.read(buffer)) != -1) {
                                                byteArrayOutputStream.write(buffer, 0, len);
                                            }

                                            OutputStream outputStream = openFileOutput(imgpath, getApplicationContext().MODE_PRIVATE);
                                            outputStream.write(byteArrayOutputStream.toByteArray());
                                            outputStream.close();
                                            reciptsreference.child(existkey).setValue(new addincome(existkey,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcaregoryid,subcategoryname,CurrencyConvert.Get(Double.parseDouble(total.getText().toString())),refno.getText().toString(),customerid,customername,note.getText().toString(),imgpath,1,CurrentUser.user.getId()));
                                            dialog.dismiss();
                                            DatabaseReference offlineref=firebaseDatabase.getReference("offline").child(CurrentUser.user.getId());
                                            String offlinekey=offlineref.push().getKey();
                                            offlineref.child(offlinekey).setValue(new Offline(offlinekey,imagepath,CurrentUser.user.getId()));
                                            Toast.makeText(add_income.this, "Receipt Updated", Toast.LENGTH_SHORT).show();
                                            imagedata=null;
                                            datefield.requestFocus();

                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }else{
                                    reciptsreference.child(existkey).setValue(new addincome(existkey,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcaregoryid,subcategoryname,CurrencyConvert.Get(Double.parseDouble(total.getText().toString())),refno.getText().toString(),customerid,customername,note.getText().toString(),imgpath,1,CurrentUser.user.getId()));
                                    Toast.makeText(add_income.this, "Receipt Updated", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(this, "Please select customer", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "Please fill total received", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "Please select sub category", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please select account", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Date field empty or invalid date.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteIncomeRecord(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(add_income.this);
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
                reciptsreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                        for(DataSnapshot data:iterable){
                            addincome addIncome=data.getValue(addincome.class);
                            if(addIncome.getId().equals(existkey) && addIncome.getUser().equals(CurrentUser.user.getId())){
                                reciptsreference.child(addIncome.getId()).removeValue();
                                Toast.makeText(add_income.this, "Record Removed", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

        total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!total.getText().toString().isEmpty()){
                    change=true;
                }
            }
        });

        refno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!refno.getText().toString().isEmpty()){
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
    }
}
