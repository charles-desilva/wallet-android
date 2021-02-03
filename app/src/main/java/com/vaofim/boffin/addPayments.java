package com.vaofim.boffin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Models.Account;
import Models.Expences;
import Models.Offline;
import Models.Vat;
import Models.expenceCategory;
import Models.expenceSubCategory;
import Process_Classes.CheckAvailability;
import Stables.BitmapToUri;
import Stables.CurrencyConvert;
import Stables.CurrentCurrency;
import Stables.CurrentUser;
import Stables.LastRemember;
import Stables.ValidateDate;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class addPayments extends AppCompatActivity implements CalcDialog.CalcDialogCallback{

    boolean change=false;

    private static int reqCode1=1;
    private static int reqCode2=2;

    EditText datefield;
    EditText supplierinvdate1;
    CalcDialog calcDialog;
    TextView vat;
    EditText invoicetotal;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    DatabaseReference vat_reference;
    DatabaseReference account_reference;
    DatabaseReference category_reference;
    DatabaseReference sub_category_reference;
    DatabaseReference expencereference;
    StorageReference imgreference;

    String accountid;
    String accountname;
    Spinner accountspinner;

    String categoryid;
    String categoryname;
    Spinner categoryspinner;

    String subcategoryid;
    String subcategoryname;
    Spinner subcategoryspinner;

    String vatid;
    Spinner vatspinner;

    EditText interrnalrefno,suppliername,supplierinvno,note;
    ImageView image;
    String imagepath;

    String vattext;
    String notetext;
    String imgpath;

    ImageButton vatbrn;

    TextView vatdown,vattxt,invoicetxt,invoicenet,titletext;

    double vatvalue=0.0;
    public double vatprecentage=0.0;

    Uri imagedata;

    ConstraintLayout invoicenetconstraintlayout,vatconstraintlayout,invoicetotlaconstraintlayout,internalrefnoconstraintlayout;

    boolean status;
    Button savebtn,deletebtn;
    String existkey;
    String key;

    public int ano;
    public int cno;
    public int vatno;
    public int subno;

    boolean catcheck1;
    boolean catcheck2;

    ScrollView scrollView;

    public int vatboxckeched=1;

    TextView invoiceTotalLabel,expenceinvoicenetlabel;

    String categoryString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payments);

        invoiceTotalLabel=findViewById(R.id.invoicetotallabel);
        expenceinvoicenetlabel=findViewById(R.id.expenceinvoicenetlabel);

        invoiceTotalLabel.setText("Invoice Total ("+CurrentCurrency.get()+") *");
        expenceinvoicenetlabel.setText("Invoice Net ("+CurrentCurrency.get()+")");

        invoicenetconstraintlayout=findViewById(R.id.invoicenetconstraintlayout);
        vatconstraintlayout=findViewById(R.id.vatconstraintlayout);
        invoicetotlaconstraintlayout=findViewById(R.id.invoicetotlaconstraintlayout);
        internalrefnoconstraintlayout=findViewById(R.id.internalrefnoconstraintlayout);

        image=findViewById(R.id.expenceimgview);
        invoicenet=findViewById(R.id.invoicenet1);
        interrnalrefno=findViewById(R.id.expenceinternalrefno);
        suppliername=findViewById(R.id.expencesuppliername);
        supplierinvno=findViewById(R.id.expencesupplierinvoiceno);
        note=findViewById(R.id.expencenote);
        vatbrn=findViewById(R.id.vatbrn1);
        vatdown=findViewById(R.id.expenceinvoicenetlabel);
        vattxt=findViewById(R.id.vatviewtxt);
        invoicetxt=findViewById(R.id.expenceinvoicenetlabel);

        datefield=findViewById(R.id.dateField);
        supplierinvdate1=findViewById(R.id.supplierinvdate1);
        calcDialog=new CalcDialog();
        vat=findViewById(R.id.vatfield1);
        invoicetotal=findViewById(R.id.invoicetotal);

        datefield.setText(new SimpleDateFormat("dd/M/yyyy").format(new Date()));

        firebaseDatabase=FirebaseDatabase.getInstance();

        firebaseStorage=FirebaseStorage.getInstance();
        imgreference=firebaseStorage.getReference("images").child(CurrentUser.user.getId());

        account_reference=firebaseDatabase.getReference("account");
        account_reference.keepSynced(true);
        vat_reference=firebaseDatabase.getReference("vat");
        vat_reference.keepSynced(true);
        category_reference=firebaseDatabase.getReference("expences_category");
        category_reference.keepSynced(true);
        sub_category_reference=firebaseDatabase.getReference("expences_subcategory");
        sub_category_reference.keepSynced(true);
        expencereference=firebaseDatabase.getReference("expences");
        expencereference.keepSynced(true);

        accountspinner=findViewById(R.id.accountspinner);
        categoryspinner=findViewById(R.id.categoryspinner);
        subcategoryspinner=findViewById(R.id.subcategoryspinner1);

        vatid="1";
        vatspinner=findViewById(R.id.vatspinner);

        scrollView=findViewById(R.id.addPaymentsScrollview);

        double savedvatvalue=0.0;

        savebtn=findViewById(R.id.addpaymentsavebtn);
        deletebtn=findViewById(R.id.addpaymentclearbtn);
        titletext=findViewById(R.id.titletext);

        if(getIntent().getStringExtra("status").equals("1")){
            status=true;
            existkey=getIntent().getStringExtra("id");
            savebtn.setText("Update");
            deletebtn.setText("Delete");

            deletebtn.setBackgroundColor(getResources().getColor(R.color.pdlg_color_red));

            titletext.setText("Update Expence");

        }

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    saveRecord();
                }else{
                    updatePaymentRecord();
                }

            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==false){
                    clearRecords();
                }else{
                    deletePaymentRecord();
                }

            }
        });

        if(status==true){
            setVatDefaults();
            expencereference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterable){
                        final Expences expence=data.getValue(Expences.class);
                        if(expence.getUser().equals(CurrentUser.user.getId()) && expence.getId().equals(existkey)){
                            datefield.setText(expence.getDate1());
                            accountid=expence.getAccountid();
                            accountname=expence.getAccountname();
                            categoryid=expence.getCategoryid();
                            categoryString=expence.getCategoryid();
                            categoryname=expence.getCategoryname();
                            subcategoryid=expence.getSubcategoryid();
                            subcategoryname=expence.getSubcategoryname();
                            invoicetotal.setText(CurrencyConvert.Get(Double.parseDouble(expence.getInvoicetotal())));
                            interrnalrefno.setText(expence.getInternalref());
                            suppliername.setText(expence.getSupplierName());
                            supplierinvdate1.setText(expence.getDate2());
                            supplierinvno.setText(expence.getSupplierinvno());
                            note.setText(expence.getNote());

                            if(expence.getVat()!=null && !expence.getVat().equals("") && !expence.getVat().equals("0.0")){
                                vatspinner.setSelection(1);
                                vat.setText(expence.getVat());
                                vatboxckeched=expence.getVatbutton();
                                if(vatboxckeched==1){
                                    vatvalue=Double.parseDouble(expence.getVat());

                                }else{
                                    vatvalue=expence.getVatval();
                                }
                                vatprecentage=expence.getVatval();
                                vattxt.setText(expence.getVattext());
                            }

                            invoicenet.setText(expence.getInvoicenet());

                            if(expence.getPath()!=null && !expence.getPath().isEmpty() && status==true){
                                imagepath=expence.getPath();
                                if(isNetworkAvailable()){
                                    final Expences addIncome2=expence;
                                    image.setImageResource(R.drawable.loadingspinnernew);
                                    imgreference.child(expence.getPath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            try {
                                                final Uri uridata=uri;
                                                final File file=new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+addIncome2.getPath());
                                                if(!file.exists()){
                                                    final ProgressDialog dialog=new ProgressDialog(addPayments.this);
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
                                                                OutputStream outputStream = openFileOutput(addIncome2.getPath(), getApplicationContext().MODE_PRIVATE);
                                                                outputStream.write(byteArrayOutputStream.toByteArray());
                                                                outputStream.close();
                                                                dialog.dismiss();
                                                            }catch(Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();
                                                    Picasso.get().load(uri).into(image);
                                                }else{
                                                    Picasso.get().load(file).into(image);
                                                }

                                                image.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent=new Intent(getApplicationContext(), ImageViewer.class);
                                                        intent.putExtra("img",file.getAbsolutePath());
                                                        intent.putExtra("name",expence.getPath());
                                                        intent.putExtra("path",uridata.toString());
                                                        startActivity(intent);
                                                    }
                                                });
                                            }catch(Exception e){

                                            }
                                        }
                                    });
                                }else{
                                    image.setImageResource(R.drawable.nointernet);
                                }

                            }else{
                                image.setImageResource(R.drawable.upload_image);
                            }




                            break;
                        }
                    }

                    addCalcListener();
                    setAccounts();
                    setCategory();
                    setSubCategory();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            addCalcListener();
            setAccounts();
            setCategory();
            setSubCategory();
            setVatDefaults();
            addTextListeneres();
        }

        if(LastRemember.addPayments!=null && status==false){
            vatboxckeched=LastRemember.addPayments.vatboxckeched;
            vatvalue=LastRemember.addPayments.vatvalue;
            vatprecentage=LastRemember.addPayments.vatprecentage;
            vattxt.setText(LastRemember.addPayments.vattxt.getText());

            datefield.setText(LastRemember.addPayments.datefield.getText().toString());
            ano=LastRemember.addPayments.accountspinner.getSelectedItemPosition();
            cno=LastRemember.addPayments.categoryspinner.getSelectedItemPosition();
            subno=LastRemember.addPayments.subcategoryspinner.getSelectedItemPosition();
            vatno=LastRemember.addPayments.vatspinner.getSelectedItemPosition();
            invoicetotal.setText(LastRemember.addPayments.invoicetotal.getText().toString());
            invoicenet.setText(LastRemember.addPayments.invoicenet.getText().toString());
            interrnalrefno.setText(LastRemember.addPayments.interrnalrefno.getText().toString());
            suppliername.setText(LastRemember.addPayments.suppliername.getText().toString());
            supplierinvdate1.setText(LastRemember.addPayments.supplierinvdate1.getText().toString());
            supplierinvno.setText(LastRemember.addPayments.supplierinvno.getText().toString());
            note.setText(LastRemember.addPayments.note.getText().toString());
//            LastRemember.addPayments=null;
        }

    }

    public void updatePaymentRecord(){
        try {
            if(!datefield.getText().toString().isEmpty()){
                if(new ValidateDate().validate(datefield.getText().toString())){
                    if(!accountid.equals("") && accountid!=null && !accountid.equals("none")){
                        if(!categoryid.equals("") && categoryid!=null && !categoryid.equals("none")){
                            if(!subcategoryid.equals("") && subcategoryid!=null && !subcategoryid.equals("none")){
                                if(!invoicetotal.getText().toString().isEmpty()){

                                    if(supplierinvdate1.getText().toString().isEmpty()){
                                        doSaveProcess();
                                    }else{
                                        if(new ValidateDate().validate(supplierinvdate1.getText().toString())){
                                            doSaveProcess();
                                        }else{
                                            Toast.makeText(this, "Supplier Invoice Date Not Valid", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                }else{
                                    invoicetotal.setFocusable(true);
                                    Toast.makeText(this, "Invoice Total Empty", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                subcategoryspinner.setFocusable(true);
                                Toast.makeText(this, "Sub Category Empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            categoryspinner.setFocusable(true);
                            Toast.makeText(this, "Category Empty", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        accountspinner.setFocusable(true);
                        Toast.makeText(this, "Account Empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    datefield.setFocusable(true);
                    Toast.makeText(this, "Expense Date Not Valid", Toast.LENGTH_SHORT).show();
                }


            }else{
                datefield.setFocusable(true);
                Toast.makeText(this, "Expense Date Empty", Toast.LENGTH_SHORT).show();
            }
        }catch (IllegalArgumentException e){
            Toast.makeText(this, "Given types cannot be process", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deletePaymentRecord(){


        final AlertDialog.Builder builder = new AlertDialog.Builder(addPayments.this);
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
                expencereference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                        for(DataSnapshot data:iterable){
                            final Expences expence=data.getValue(Expences.class);
                            if(expence.getUser().equals(CurrentUser.user.getId()) && expence.getId().equals(existkey)){
                                expencereference.child(existkey).removeValue();
                                Toast.makeText(addPayments.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                                onBackPressed();
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

    public void saveRecord(){
        try {
            if(!datefield.getText().toString().isEmpty()){
                if(new ValidateDate().validate(datefield.getText().toString())){
                    if(!accountid.equals("") && accountid!=null && !accountid.equals("none")){
                        if(!categoryid.equals("") && categoryid!=null && !categoryid.equals("none")){
                            if(!subcategoryid.equals("") && subcategoryid!=null && !subcategoryid.equals("none")){
                                if(!invoicetotal.getText().toString().isEmpty()){

                                    if(supplierinvdate1.getText().toString().isEmpty()){
                                        doSaveProcess();
                                    }else{
                                        if(new ValidateDate().validate(supplierinvdate1.getText().toString())){
                                            doSaveProcess();
                                            change=false;
                                        }else{
                                            Toast.makeText(this, "Supplier Invoice Date Not Valid", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    change=false;
                                }else{
                                    invoicetotal.setFocusable(true);
                                    Toast.makeText(this, "Invoice Total Empty", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                subcategoryspinner.setFocusable(true);
                                Toast.makeText(this, "Sub Category Empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            categoryspinner.setFocusable(true);
                            Toast.makeText(this, "Category Empty", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        accountspinner.setFocusable(true);
                        Toast.makeText(this, "Account Empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    datefield.setFocusable(true);
                    Toast.makeText(this, "Expense Date Not Valid", Toast.LENGTH_SHORT).show();
                }


            }else{
                datefield.setFocusable(true);
                Toast.makeText(this, "Expense Date Empty", Toast.LENGTH_SHORT).show();
            }
        }catch (IllegalArgumentException e){
            Toast.makeText(this, "Given types cannot be process", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void doSaveProcess(){
        if(status==true){
            key=existkey;
        }else{
            key=expencereference.push().getKey();
        }
        if(imagepath!=null && !imagepath.equals("") && imagedata!=null){
            if(isNetworkAvailable()){
                final ProgressDialog dialog=new ProgressDialog(addPayments.this);
                dialog.setTitle("Please wait");
                dialog.setMessage("Your patience is very important while upload attachments.");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                UploadTask uploadTask=imgreference.child(imagepath).putFile(imagedata);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        expencereference.child(key).setValue(new Expences(key,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcategoryid,subcategoryname,invoicetotal.getText().toString(),vat.getText().toString(),invoicenet.getText().toString(),interrnalrefno.getText().toString(),suppliername.getText().toString(),supplierinvdate1.getText().toString(),supplierinvno.getText().toString(),note.getText().toString(),imagepath,1,CurrentUser.user.getId(),vattxt.getText().toString(),vatvalue,vatboxckeched));
                        Toast.makeText(addPayments.this, "Expense Saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        change=false;
                        scrollView.fullScroll(ScrollView.FOCUS_UP);

                        if(status==false){
                            //accountspinner.setSelection(0);
                            vatspinner.setSelection(0);
                            categoryspinner.setSelection(0);
                            subcategoryspinner.setSelection(0);
                            invoicetotal.setText("");
                            vat.setText("");
                            invoicenet.setText("");
                            interrnalrefno.setText("");
                            suppliername.setText("");
                            supplierinvdate1.setText("");
                            supplierinvno.setText("");
                            note.setText("");
                            vatid=null;
                            accountid=null;
                            categoryid=null;
                            subcategoryid=null;
                            imagepath=null;
                            image.setImageResource(R.drawable.upload_image);
                            imagedata=null;
                            change=false;
                            datefield.requestFocus();
                            datefield.setSelection(0);
                        }
                    }
                });
            }else{
                final ProgressDialog dialog=new ProgressDialog(addPayments.this);
                dialog.setTitle("Please wait");
                dialog.setMessage("Your patience is very important while saving attachments.");
                dialog.show();
                expencereference.child(key).setValue(new Expences(key,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcategoryid,subcategoryname,invoicetotal.getText().toString(),vat.getText().toString(),invoicenet.getText().toString(),interrnalrefno.getText().toString(),suppliername.getText().toString(),supplierinvdate1.getText().toString(),supplierinvno.getText().toString(),note.getText().toString(),imagepath,1,CurrentUser.user.getId(),vattxt.getText().toString(),vatvalue,vatboxckeched));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            InputStream in=getContentResolver().openInputStream(imagedata);

                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

                            byte[] buffer = new byte[1024];
                            int len;

                            while ((len = in.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, len);
                            }


                            OutputStream outputStream = openFileOutput(imagepath, getApplicationContext().MODE_PRIVATE);
                            outputStream.write(byteArrayOutputStream.toByteArray());
                            outputStream.close();
                            DatabaseReference offlineref=firebaseDatabase.getReference("offline").child(CurrentUser.user.getId());
                            String offlinekey=offlineref.push().getKey();
                            offlineref.child(offlinekey).setValue(new Offline(offlinekey,imagepath,CurrentUser.user.getId()));
                            dialog.dismiss();
                            imagedata=null;
                            imagepath=null;
                            vatid=null;
                            accountid=null;
                            categoryid=null;
                            subcategoryid=null;
                            change=false;
                            datefield.requestFocus();
                            datefield.setSelection(0);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                change=false;
                if(status==false){
                    //accountspinner.setSelection(0);
                    vatspinner.setSelection(0);
                    categoryspinner.setSelection(0);
                    subcategoryspinner.setSelection(0);
                    invoicetotal.setText("");
                    vat.setText("");
                    invoicenet.setText("");
                    interrnalrefno.setText("");
                    suppliername.setText("");
                    supplierinvdate1.setText("");
                    supplierinvno.setText("");
                    note.setText("");
                    image.setImageResource(R.drawable.upload_image);
                    change=false;
                    datefield.requestFocus();
                    datefield.setSelection(0);
                }
                Toast.makeText(addPayments.this, "Expense Saved", Toast.LENGTH_SHORT).show();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }

        }else{
            imgpath="-";
            expencereference.child(key).setValue(new Expences(key,datefield.getText().toString(),accountid,accountname,categoryid,categoryname,subcategoryid,subcategoryname,invoicetotal.getText().toString(),vat.getText().toString(),invoicenet.getText().toString(),interrnalrefno.getText().toString(),suppliername.getText().toString(),supplierinvdate1.getText().toString(),supplierinvno.getText().toString(),note.getText().toString(),imagepath,1,CurrentUser.user.getId(),vattxt.getText().toString(),vatvalue,vatboxckeched));
            Toast.makeText(this, "Expense Saved", Toast.LENGTH_SHORT).show();
            if(status==false){
                //accountspinner.setSelection(0);
                vatspinner.setSelection(0);
                categoryspinner.setSelection(0);
                subcategoryspinner.setSelection(0);
                invoicetotal.setText("");
                vat.setText("");
                invoicenet.setText("");
                interrnalrefno.setText("");
                suppliername.setText("");
                supplierinvdate1.setText("");
                supplierinvno.setText("");
                note.setText("");
                vatid=null;
                accountid=null;
                categoryid=null;
                subcategoryid=null;
                imagepath=null;
                image.setImageResource(R.drawable.upload_image);
                imagedata=null;
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                change=false;
                datefield.requestFocus();
                datefield.setSelection(0);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                "Local Storage",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            String filename= new SimpleDateFormat("YYMMDDHHMMSS").format(new Date())+System.currentTimeMillis()+".jpg";
            if(requestCode==reqCode2){
                if(data!=null){
                    imagedata = data.getData();
                    image.setImageURI(imagedata);
                    imagepath=filename;
                }

            }else if(requestCode==reqCode1){
                if(data!=null){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imagedata = new BitmapToUri().getUri(addPayments.this,photo);
                    if(imagedata!=null){
                        image.setImageBitmap(photo);
                        imagepath=filename;
                    }else{
                        Toast.makeText(this, "Image Capturing Error", Toast.LENGTH_SHORT).show();
                    }

                }

            }else{
                imagepath=null;
                image.setImageResource(R.drawable.upload_image);
            }


//            String filename= new SimpleDateFormat("YYMMDDHHMMSS").format(new Date())+System.currentTimeMillis()+".jpg";
//            if(requestCode==reqCode2){
//                imagedata = data.getData();
//                image.setImageURI(imagedata);
//                imagepath=filename;
//            }else if(requestCode==reqCode1){
//                imagedata = data.getData();
//                image.setImageURI(imagedata);
//                imagepath=filename;
//                Toast.makeText(this, "Camera Option Selected", Toast.LENGTH_SHORT).show();
//            }else{
//                imagepath=null;
//                image.setImageResource(R.drawable.upload_image);
//            }
        }catch(Exception e){
            showfiletoolarge();
        }
    }

    private void showfiletoolarge(){
        Toast.makeText(this, "File too large", Toast.LENGTH_SHORT).show();
    }

    public void goToCustomer(View view){
        startActivity(new Intent(addPayments.this,addCustomer.class));
    }


    public void clearRecords(){
        datefield.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        accountspinner.setSelection(0);
        vatspinner.setSelection(0);
        categoryspinner.setSelection(0);
        subcategoryspinner.setSelection(0);
        change=false;
        invoicetotal.setText("");
        vat.setText("");
        invoicenet.setText("");
        interrnalrefno.setText("");
        suppliername.setText("");
        supplierinvdate1.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        supplierinvno.setText("");
        note.setText("");
        vatid=null;
        accountid=null;
        categoryid=null;
        subcategoryid=null;
        imagepath=null;
        image.setImageResource(R.drawable.upload_image);
        imagedata=null;
        LastRemember.addPayments=null;
        change=false;

        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void addCalcListener(){
        invoicetotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(status==false){
                    change=true;
                }

                if(!invoicetotal.getText().toString().isEmpty()){
                    double val1=Double.parseDouble(invoicetotal.getText().toString());
                    if(vatvalue!=0.0){
                        if(vatboxckeched==1){
                            double value1=(val1*vatvalue)/(100+vatvalue);
                            BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.CEILING);
                            vat.setText(balance.toString());
                            invoicenet.setText(BigDecimal.valueOf(val1-balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString().trim());
                        }else{
                            vat.setText(BigDecimal.valueOf(vatvalue).setScale(2, RoundingMode.HALF_EVEN).toString()+"");
                            invoicenet.setText(BigDecimal.valueOf(val1-vatvalue).setScale(2, RoundingMode.HALF_EVEN).toString()+"".trim());
                            vattxt.setText("VAT (Custom fixed Value: "+CurrentCurrency.get()+vatvalue+")");
                        }

                    }else{
                        invoicenet.setText(CurrencyConvert.Get(val1)+"".trim());
                    }
                }
            }
        });
    }


    ArrayList <expenceCategory> expenceCategoryList=null;
    ArrayList <expenceSubCategory> expenceSubCategoryList=null;

    public void setCategory(){
        category_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                expenceCategoryList=new ArrayList <expenceCategory> ();

                final ArrayList <String> idlist=new ArrayList <String> ();
                final ArrayList <String> namelist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                if(status==true){
                    System.out.println(categoryname);
                    namelist.add(categoryname);
                    idlist.add(categoryid);
                }else{
                    namelist.add("Select");
                    idlist.add("none");
                }


                for(DataSnapshot data:iterable){
                    expenceCategory account=data.getValue(expenceCategory.class);
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        if(status==true){
                            if(!account.getId().equals(categoryid)){
                                expenceCategoryList.add(account);
                            }
                        }else {
                            expenceCategoryList.add(account);
                        }
                    }
                }

                Collections.sort(expenceCategoryList,new Comparator<expenceCategory>()
                {
                    public int compare(expenceCategory f1, expenceCategory f2)
                    {
                        return f1.getCategory().toLowerCase().compareTo(f2.getCategory().toLowerCase());
                    }
                });

                for(expenceCategory account:expenceCategoryList){
                    if(status==true && !account.getId().equals(categoryid)){
                        namelist.add(account.getCategory());
                        idlist.add(account.getId());
                    }else{
                        namelist.add(account.getCategory());
                        idlist.add(account.getId());
                    }
                }


                ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,namelist);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
                categoryspinner.setAdapter(spinnerArrayAdapter);



                categoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        categoryid=idlist.get(position);
                        categoryname=namelist.get(position);


                        if(idlist.get(position).equals("none")){
                            subcategoryspinner.setEnabled(false);
                            subcategoryspinner.setClickable(false);
                        }else{
                            subcategoryspinner.setEnabled(true);
                            subcategoryspinner.setClickable(true);

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

                if(catcheck1){
                    int ss=1;
                    for(expenceCategory expenceCategory:expenceCategoryList){
                        if(expenceCategory.getId().equals(savedkey.getId())){
                            categoryspinner.setSelection(ss);
                            catcheck1=false;
                            break;
                        }

                        ss++;
                    }

                }

                if(LastRemember.addPayments!=null && status==false && LastRemember.addPayments.categoryspinner.getSelectedItemPosition()!=0){
                    categoryspinner.setSelection(LastRemember.addPayments.categoryspinner.getSelectedItemPosition());
                    setSubCategory();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ArrayList <Account> accountList=null;

    public void setAccounts(){
        account_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                accountList=new ArrayList<>();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                for(DataSnapshot data:iterable){
                    Account account=data.getValue(Account.class);
                    accountList.add(account);
                }

                Collections.sort(accountList,new Comparator<Account>()
                {
                    public int compare(Account f1, Account f2)
                    {
                        return f1.getAccountName().toLowerCase().compareTo(f2.getAccountName().toLowerCase());
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
                        if(status==true && !account.getId().equals(accountid)){
                            namelist.add(account.getAccountName());
                            idlist.add(account.getId());
                        }else{
                            namelist.add(account.getAccountName());
                            idlist.add(account.getId());
                        }
                    }
                }

                ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,namelist);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
                accountspinner.setAdapter(spinnerArrayAdapter);
                accountspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                if(LastRemember.addPayments!=null && status==false){
                    accountspinner.setSelection(LastRemember.addPayments.accountspinner.getSelectedItemPosition());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setSubCategory(){

        sub_category_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                expenceSubCategoryList=new ArrayList <> ();

                for(DataSnapshot data:iterable) {
                    expenceSubCategory account = data.getValue(expenceSubCategory.class);
                    if(status==true && subcategoryid!=null){
                        if(!account.getId().equals(subcategoryid)){
                            expenceSubCategoryList.add(account);
                        }
                    }else{
                        expenceSubCategoryList.add(account);
                    }

                }

                Collections.sort(expenceSubCategoryList,new Comparator<expenceSubCategory>()
                {
                    public int compare(expenceSubCategory f1, expenceSubCategory f2)
                    {
                        return f1.getSubCategory().toLowerCase().compareTo(f2.getSubCategory().toLowerCase());
                    }
                });

                final ArrayList <String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

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

                for(expenceSubCategory account:expenceSubCategoryList){
                    if(account.getCategory().equals(categoryid)){
                        if(status==true && account.getId().equals(subcategoryid)){
                            namelist.add(account.getSubCategory());
                            idlist.add(account.getId());
                        }else{
                            namelist.add(account.getSubCategory());
                            idlist.add(account.getId());
                        }
                    }
                }

                ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,namelist);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
                subcategoryspinner.setAdapter(spinnerArrayAdapter);
                if(catcheck2){
                    subcategoryspinner.setSelection(idlist.size()-1,true);
                    catcheck2=false;
                }
                subcategoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        subcategoryid=idlist.get(position);
                        subcategoryname=namelist.get(position);
//                        if(!categoryid.equals("none") && status==false && idlist.size()>1){
//                            subcategoryspinner.setSelection(1);
//                        }

                        if(!subcategoryid.equals("none")){
                            change=true;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



                if(subno!=0 && LastRemember.addPayments!=null){
                    subcategoryspinner.setSelection(subno);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToHome(View view){
//        startActivity(new Intent(getApplicationContext(),home.class));
        System.out.println("awaaa");
        onBackPressed();

    }

    @Override
    public void onBackPressed() {

        if(status==true){
            LastRemember.addPayments=null;
            super.onBackPressed();
            System.out.println("true");
        }else{
            System.out.println("false");
            if(change){
                final AlertDialog.Builder builder = new AlertDialog.Builder(addPayments.this);
                builder.setCancelable(true);
                builder.setTitle("This form has unsaved data.");
                builder.setMessage("Are you sure that you want to close without saving ?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.addPayments=addPayments.this;
                        doBack();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LastRemember.addPayments=null;
                        doBack();

                    }
                });
                builder.create();
                builder.show();
            }else{
                LastRemember.addPayments=null;
                super.onBackPressed();
                System.out.println("true");
            }

        }
    }

    private void doBack(){
        super.onBackPressed();
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

    public void datePick2(View view){

        Calendar calendar = Calendar.getInstance();

        if(!supplierinvdate1.getText().toString().isEmpty() && new ValidateDate().validate(supplierinvdate1.getText().toString())){
            try {
                calendar.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(supplierinvdate1.getText().toString()));
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
                supplierinvdate1.setText(dayOfMonth+"/"+currentmont+"/"+year);
            }
        }, Year, Month, Day).show();

    }

    public void showCalculator(View view){
        if(!invoicetotal.getText().toString().isEmpty()){
            calcDialog.setValue(BigDecimal.valueOf(Double.parseDouble(invoicetotal.getText().toString())));
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
        }else {
            calcDialog.show(getSupportFragmentManager(), "calc_dialog");
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
                vatno=position;

                if(idlist.get(position).equals("2")){
                    vatconstraintlayout.setVisibility(View.GONE);

                    vatvalue=0.0;

                    invoicenet.setText(invoicetotal.getText().toString());
                }else{

                    if(status==false){
                        if(vatboxckeched==1){

                            if(LastRemember.addPayments==null){
                                vat_reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                        for(DataSnapshot data:iterable){
                                            Vat vat1=data.getValue(Vat.class);
                                            if(vat1.getUser().equals(CurrentUser.user.getId())){
                                                vattxt.setText("VAT "+vat1.getValue()+"% ("+CurrentCurrency.get()+")");
                                                vatvalue=vat1.getValue();
                                                vatprecentage=vatvalue;
                                                if(!invoicetotal.getText().toString().isEmpty()){
                                                    double val1=Double.parseDouble(invoicetotal.getText().toString());
                                                    if(vatvalue!=0.0){
                                                        double value1=(val1*vatvalue)/(100+vatvalue);
                                                        BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.CEILING);
                                                        vat.setText(balance.toString());
                                                        invoicenet.setText(BigDecimal.valueOf(val1-balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString()+"".trim());
                                                    }else{
                                                        invoicenet.setText(val1+"".trim());
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
                            }else if(LastRemember.addPayments!=null && LastRemember.addPayments.vatid.equals("2")){
                                vat_reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                        for(DataSnapshot data:iterable){
                                            Vat vat1=data.getValue(Vat.class);
                                            if(vat1.getUser().equals(CurrentUser.user.getId())){
                                                vattxt.setText("VAT "+vat1.getValue()+"% ("+CurrentCurrency.get()+")");
                                                vatvalue=vat1.getValue();
                                                vatprecentage=vatvalue;
                                                if(!invoicetotal.getText().toString().isEmpty()){
                                                    double val1=Double.parseDouble(invoicetotal.getText().toString());
                                                    if(vatvalue!=0.0){
                                                        double value1=(val1*vatvalue)/(100+vatvalue);
                                                        BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.CEILING);
                                                        vat.setText(balance.toString());
                                                        invoicenet.setText(BigDecimal.valueOf(val1-balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString()+"".trim());
                                                    }else{
                                                        invoicenet.setText(val1+"".trim());
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




                        }else{
                            if(!invoicetotal.getText().toString().isEmpty()){
                                double val1=Double.parseDouble(invoicetotal.getText().toString());
                                vat.setText(vatvalue+"");
                                invoicenet.setText(BigDecimal.valueOf(val1-vatvalue).setScale(2, RoundingMode.HALF_EVEN).toString()+"".trim());
                                vattxt.setText("VAT (Custom fixed Value: "+CurrentCurrency.get()+vatvalue+")");
                                vatboxckeched=2;
                            }

                        }

                        vatconstraintlayout.setVisibility(View.VISIBLE);
                    }



                }

                if(!vatid.equals("2")){
                    change=true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(LastRemember.addPayments!=null && status==false){
            vatspinner.setSelection(LastRemember.addPayments.vatspinner.getSelectedItemPosition());
            if(!LastRemember.addPayments.vat.getText().equals("")  && !LastRemember.addPayments.invoicetotal.getText().toString().equals("") && vatspinner.getSelectedItemPosition()==1){
                vat.setText(LastRemember.addPayments.vat.getText());
                invoicenet.setText(Double.parseDouble(LastRemember.addPayments.invoicetotal.getText().toString())-Double.parseDouble(LastRemember.addPayments.vat.getText().toString())+"");
            }
        }
    }

    public void goToVat(View view){
        if(!invoicetotal.getText().toString().isEmpty()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(addPayments.this);
            builder.setCancelable(true);
            builder.setTitle("Change VAT Value");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleBox = new EditText(this);
            titleBox.setHint("Expected Value");
            titleBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            if(vatboxckeched==1){
                titleBox.setText(CurrencyConvert.Get(vatprecentage));
            }else{
                titleBox.setText(CurrencyConvert.Get(vatvalue));
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
                        double val1=Double.parseDouble(invoicetotal.getText().toString());
                        double valvat=Double.parseDouble(titleBox.getText().toString());

                        if(radioButton.isChecked()){
                            double value1=(val1*valvat)/(100+valvat);
                            BigDecimal balance=BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_EVEN);
                            vat.setText(balance.toString());
                            invoicenet.setText(BigDecimal.valueOf(val1-balance.doubleValue()).setScale(2, RoundingMode.HALF_EVEN).toString()+"".trim());
                            vattxt.setText("VAT (Custom Percentage: "+CurrencyConvert.Get(valvat)+"%)");
                            vatvalue=BigDecimal.valueOf(valvat).setScale(2, RoundingMode.DOWN).doubleValue();
                            vatboxckeched=1;
                            vatprecentage=valvat;
                        }else if(radioButton1.isChecked()){

                            if(val1<= valvat){
                                Toast.makeText(addPayments.this, "Custom VAT values cannot be equals or greater than total", Toast.LENGTH_SHORT).show();
                            }else{
                                //BigDecimal.valueOf(valvat).setScale(2, RoundingMode.FLOOR)
                                vat.setText(CurrencyConvert.Get(valvat)+"");
                                invoicenet.setText(BigDecimal.valueOf(val1-valvat).setScale(2, RoundingMode.HALF_EVEN).toString().trim());
                                vattxt.setText("VAT (Custom fixed Value: "+CurrentCurrency.get()+ CurrencyConvert.Get(valvat) +")");
                                vatvalue=BigDecimal.valueOf(valvat).setScale(2, RoundingMode.DOWN).doubleValue();;
                                vatboxckeched=2;
                            }


                        }

//
//                        double val3=(valvat/val1)*100;
//
////                        vat.setText(new BigDecimal(val3).toString);
//
//                        DecimalFormat df = new DecimalFormat("#.#####");
//                        vat.setText(df.format(val3));
                    }else{
                        Toast.makeText(addPayments.this, "Please Enter Value", Toast.LENGTH_SHORT).show();
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

    public void goToCstegory(View view){
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

                    if(new CheckAvailability().expenceCategoryCheck(expenceCategoryList,titleBox.getText().toString())){
                        String key=category_reference.push().getKey();
                        expenceCategory newexpencecategory=new expenceCategory(key,titleBox.getText().toString(), CurrentUser.user.getId(),2);
                        savedkey=newexpencecategory;
                        category_reference.child(key).setValue(newexpencecategory);
                        String subkey=sub_category_reference.push().getKey();
                        sub_category_reference.child(subkey).setValue(new expenceSubCategory(subkey,"General", key,1));
                        catcheck1=true;
                        Toast.makeText(addPayments.this, "Category Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(addPayments.this, "Expence Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(addPayments.this, "Please Enter Name For Save New Category", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(layout);
        builder.create();
        builder.show();
    }

    expenceCategory savedkey;

    public void goToSubCategory(View view){
        if(!categoryid.equals("none")){
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

                        sub_category_reference.child(key).setValue(new expenceSubCategory(key,titleBox.getText().toString(), categoryid,2));
                        catcheck2=true;
                        Toast.makeText(addPayments.this, "Sub Category Saved", Toast.LENGTH_SHORT).show();
                        setSubCategory();

                    }else{
                        Toast.makeText(addPayments.this, "Please Enter Name For Save New Sub Category", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setView(layout);
            builder.create();
            builder.show();
        }else{
            Toast.makeText(this, "Plase Select Category First", Toast.LENGTH_SHORT).show();
        }
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

        interrnalrefno.addTextChangedListener(new TextWatcher() {
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

        suppliername.addTextChangedListener(new TextWatcher() {
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

//        supplierinvdate1.addTextChangedListener(new TextWatcher() {
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

        supplierinvno.addTextChangedListener(new TextWatcher() {
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

    @Override
    public void onValueEntered(int requestCode, BigDecimal value) {
        invoicetotal.setText(value.setScale(2, RoundingMode.CEILING).toString());
    }

}
