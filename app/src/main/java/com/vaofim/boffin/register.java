package com.vaofim.boffin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaofim.boffin.R;

import java.util.ArrayList;
import java.util.List;

import Models.Account;
import Models.Currency;
import Models.PayModes;
import Models.User;
import Models.UserCurrency;
import Models.Vat;
import Models.expenceCategory;
import Models.expenceSubCategory;
import Models.incomeCategory;
import Models.incomeSubCategory;
import Models.invoiceCategory;
import Models.invoiceSubCategory;
import Stables.CurrentUser;

public class register extends AppCompatActivity {

    EditText firstname,signupemail,password1,password2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userreference,vatreference;
    DatabaseReference currencyreference;
    DatabaseReference usercurrency;
    Iterable <DataSnapshot> datalist;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseDatabase=FirebaseDatabase.getInstance();

        firstname=findViewById(R.id.signupname);
        signupemail=findViewById(R.id.signupemail);
        password1=findViewById(R.id.signuppassword1);
        password2=findViewById(R.id.signuppassword2);

        userreference = firebaseDatabase.getReference("user");
        userreference.keepSynced(true);
        vatreference=firebaseDatabase.getReference("vat");
        vatreference.keepSynced(true);
        currencyreference=firebaseDatabase.getReference("currency");
        usercurrency=firebaseDatabase.getReference("usercurrency");

        progressDialog=new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void doToLogin(View view){

        super.onBackPressed();

    }

    String currencykey1="";


    public void doRegister(View view){
        try {
            if(!firstname.getText().toString().isEmpty() && !signupemail.getText().toString().isEmpty() && !password1.getText().toString().isEmpty() && !password2.getText().toString().isEmpty()){
                if(password1.getText().toString().equals(password2.getText().toString())){

                    progressDialog.setMessage("Please wait while registering");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    userreference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            datalist=dataSnapshot.getChildren();

                            boolean check=true;

                            for(DataSnapshot child:datalist){
                                User user1=child.getValue(User.class);
                                if(signupemail.getText().toString().trim().equals(user1.getEmail())){
                                    check=false;
                                    break;
                                }
                            }

                            progressDialog.hide();
                            progressDialog.dismiss();

                            if(check){
                                String key=userreference.push().getKey();

                                User user=new User(key,firstname.getText().toString().trim(),signupemail.getText().toString().trim(),password1.getText().toString().trim(),1,1);

                                userreference.child(key).setValue(user);
                                firstname.setText("");
                                signupemail.setText("");
                                password1.setText("");
                                password2.setText("");

                                CurrentUser.user=user;

                                String vatkey=vatreference.push().getKey();
                                vatreference.child(vatkey).setValue(new Vat(vatkey,5.0,key,0,"","",""));

                                final DatabaseReference databaseReference=firebaseDatabase.getReference("currency");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> currencylist= dataSnapshot.getChildren();
                                        boolean c=true;
                                        for(DataSnapshot dataSnapshot1:currencylist){
                                            Currency currencyobjf=dataSnapshot1.getValue(Currency.class);
                                            if(currencyobjf.getUser().equals(CurrentUser.user.getId())){
                                                c=false;
                                                break;
                                            }
                                        }

                                        if(c){
                                            String x=databaseReference.push().getKey();
                                            currencykey1=x;
                                            databaseReference.child(x).setValue(new Currency(x,"GBP",CurrentUser.user.getId(),1));

                                            x=databaseReference.push().getKey();
                                            databaseReference.child(x).setValue(new Currency(x,"USD",CurrentUser.user.getId(),1));

                                            x=databaseReference.push().getKey();
                                            databaseReference.child(x).setValue(new Currency(x,"EUR",CurrentUser.user.getId(),1));

                                            x=databaseReference.push().getKey();
                                            databaseReference.child(x).setValue(new Currency(x,"AUD",CurrentUser.user.getId(),1));
                                        }

                                        String currencykey=usercurrency.push().getKey();

                                        usercurrency.child(currencykey).setValue(new UserCurrency(currencykey,CurrentUser.user.getId(),currencykey1));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                saveAccounts();
                                saveExpenceCat();
                                saveIncomeCat();
                                saveInvoiceCat();
                                savePaymodes();

                                Toast.makeText(getApplicationContext(),"Congratulations. you have been registered.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Provided Email Exists",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    Toast.makeText(this,"Passwords not match",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveAccounts(){
        DatabaseReference accountreference=firebaseDatabase.getReference("account");
        accountreference.keepSynced(true);
        String key=accountreference.push().getKey();
        accountreference.child(key).setValue(new Account(key,"Business Current Account","BCA",1, CurrentUser.user.getId()));

        key=accountreference.push().getKey();
        accountreference.child(key).setValue(new Account(key,"Business Savings Account","BSA",1, CurrentUser.user.getId()));

        key=accountreference.push().getKey();
        accountreference.child(key).setValue(new Account(key,"Cash Payments","CP",1, CurrentUser.user.getId()));
    }

    private void saveExpenceCat(){
        DatabaseReference expencescategory=firebaseDatabase.getReference("expences_category");
        expencescategory.keepSynced(true);

        DatabaseReference subexpencescategory=firebaseDatabase.getReference("expences_subcategory");
        subexpencescategory.keepSynced(true);

        String key=expencescategory.push().getKey();
        expencescategory.child(key).setValue(new expenceCategory(key,"Accommodation", CurrentUser.user.getId(),1));
        String subkey=subexpencescategory.push().getKey();
        subexpencescategory.child(subkey).setValue(new expenceSubCategory(subkey,"General", key,1));

        key=expencescategory.push().getKey();
        expencescategory.child(key).setValue(new expenceCategory(key,"Food And Drinks", CurrentUser.user.getId(),1));
        subkey=subexpencescategory.push().getKey();
        subexpencescategory.child(subkey).setValue(new expenceSubCategory(subkey,"General", key,1));

        key=expencescategory.push().getKey();
        expencescategory.child(key).setValue(new expenceCategory(key,"Travel", CurrentUser.user.getId(),1));
        subkey=subexpencescategory.push().getKey();
        subexpencescategory.child(subkey).setValue(new expenceSubCategory(subkey,"General", key,1));
    }

    private void saveIncomeCat() {
        DatabaseReference incomereference = firebaseDatabase.getReference("income_category");
        incomereference.keepSynced(true);

        DatabaseReference incomesubreference = firebaseDatabase.getReference("income_subcategory");
        incomesubreference.keepSynced(true);

        String key = incomereference.push().getKey();
        incomereference.child(key).setValue(new incomeCategory(key, "Business", CurrentUser.user.getId(),1));
        String subkey = incomesubreference.push().getKey();
        incomesubreference.child(subkey).setValue(new incomeSubCategory(subkey, "General", key,1));

        key = incomereference.push().getKey();
        incomereference.child(key).setValue(new incomeCategory(key, "Freelancing", CurrentUser.user.getId(),1));
        subkey = incomesubreference.push().getKey();
        incomesubreference.child(subkey).setValue(new incomeSubCategory(subkey, "General", key,1));

        key = incomereference.push().getKey();
        incomereference.child(key).setValue(new incomeCategory(key, "Salary", CurrentUser.user.getId(),1));
        subkey = incomesubreference.push().getKey();
        incomesubreference.child(subkey).setValue(new incomeSubCategory(subkey, "General", key,1));
    }

    private void saveInvoiceCat(){
        DatabaseReference invoicereference=firebaseDatabase.getReference("invoice_category");
        invoicereference.keepSynced(true);

        DatabaseReference invoicesubreference=firebaseDatabase.getReference("invoice_subcategory");
        invoicesubreference.keepSynced(true);

        String key=invoicereference.push().getKey();
        invoicereference.child(key).setValue(new invoiceCategory(key,"General", CurrentUser.user.getId(),1));
        String subkey=invoicesubreference.push().getKey();
        invoicesubreference.child(subkey).setValue(new invoiceSubCategory(subkey,"General", key,1));

        key=invoicereference.push().getKey();
        invoicereference.child(key).setValue(new invoiceCategory(key,"Consultation", CurrentUser.user.getId(),1));
        subkey=invoicesubreference.push().getKey();
        invoicesubreference.child(subkey).setValue(new invoiceSubCategory(subkey,"General", key,1));

        key=invoicereference.push().getKey();
        invoicereference.child(key).setValue(new invoiceCategory(key,"Freelance Work", CurrentUser.user.getId(),1));
        subkey=invoicesubreference.push().getKey();
        invoicesubreference.child(subkey).setValue(new invoiceSubCategory(subkey,"General", key,1));
    }

    private void savePaymodes(){
        DatabaseReference paymodereference=firebaseDatabase.getReference("paymodes");
        paymodereference.keepSynced(true);

        String key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Bank Deposit Account", CurrentUser.user.getId(),1));

        key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Cash", CurrentUser.user.getId(),1));

        key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Cheque", CurrentUser.user.getId(),1));

        key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Credit Card - Master Card", CurrentUser.user.getId(),1));

        key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Credit Card - VISA", CurrentUser.user.getId(),1));

        key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Moneyboker", CurrentUser.user.getId(),1));

        key=paymodereference.push().getKey();
        paymodereference.child(key).setValue(new PayModes(key,"Paypal", CurrentUser.user.getId(),1));
    }
}
