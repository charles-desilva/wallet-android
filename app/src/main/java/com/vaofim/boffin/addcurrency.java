package com.vaofim.boffin;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.Currency;
import Models.UserCurrency;
import Stables.CurrentCurrency;
import Stables.CurrentUser;

public class addcurrency extends AppCompatActivity {

    EditText currencynamee;
    Spinner spinner;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference currencyreference;
    DatabaseReference usercurrency;

    String existingcurrencyid="";
    String existingusercurrentid="";

    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcurrency);

        firebaseDatabase=FirebaseDatabase.getInstance();

        currencyreference=firebaseDatabase.getReference("currency");
        usercurrency=firebaseDatabase.getReference("usercurrency");

        currencyreference.keepSynced(true);
        usercurrency.keepSynced(true);

        currencynamee=findViewById(R.id.currencyname);
        spinner=findViewById(R.id.currencyshowspinner);
        usercurrency.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable <DataSnapshot> dataSnapshots=dataSnapshot.getChildren();


                for(DataSnapshot obj:dataSnapshots){
                    UserCurrency userCurrency=obj.getValue(UserCurrency.class);
                    if(userCurrency.getUser().equals(CurrentUser.user.getId())){
                        existingcurrencyid=userCurrency.getCurrency();
                        existingusercurrentid=userCurrency.getId();

                        break;
                    }
                }

                addItems();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void goToHome(View view){
        onBackPressed();
    }

    String currencyid1;

    public void addItems(){

        currencyreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> currencylist= dataSnapshot.getChildren();

                final List <String> currencies=new ArrayList<String>();
                final List <String> currenciesid=new ArrayList<String>();

                if(existingcurrencyid.equals("") && existingusercurrentid.equals("")){
                    currencies.add("Select");
                    currenciesid.add("none");
                }else{
                    currencies.add("");
                    currenciesid.add("");
                }

                for(DataSnapshot dataSnapshot1:currencylist){
                    Currency currencyobjf=dataSnapshot1.getValue(Currency.class);
                    if(currencyobjf.getUser().equals(CurrentUser.user.getId())){
                        if(existingcurrencyid.equals("") && existingusercurrentid.equals("")){
                            currencies.add(currencyobjf.getCurrency());
                            currenciesid.add(currencyobjf.getId());

                        }else{
                            if(!currencyobjf.getId().equals(existingcurrencyid)){
                                currencies.add(currencyobjf.getCurrency());
                                currenciesid.add(currencyobjf.getId());
                            }else{
                                currencies.set(0,currencyobjf.getCurrency());
                                currenciesid.set(0,currencyobjf.getId());
                            }

                        }
                    }
                }

                ArrayAdapter<String> spinnerArrayAdapter =new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_dropdown_design,currencies);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_field);
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currencyid1=currenciesid.get(position);
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

    public void updateCurrency(View view){

        if(!currencyid1.equals("none") && currencyid1!=null){
            if(existingcurrencyid.equals("") && existingusercurrentid.equals("")){
                String key=usercurrency.push().getKey();
                usercurrency.child(key).setValue(new UserCurrency(key,CurrentUser.user.getId(),currencyid1));
                Toast.makeText(addcurrency.this, "Currency Saved", Toast.LENGTH_SHORT).show();
            }else{
                final AlertDialog.Builder builder = new AlertDialog.Builder(addcurrency.this);
                builder.setCancelable(true);
                builder.setTitle("Are you sure ?");
                builder.setMessage("Please confirm that you really need to update this record.");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        usercurrency.child(existingusercurrentid).setValue(new UserCurrency(existingusercurrentid,CurrentUser.user.getId(),currencyid1));

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
                                                        try {
                                                            CurrentCurrency.set(java.util.Currency.getInstance(currencyobjf.getCurrency()).getSymbol());
                                                        }catch(Exception e){
                                                            CurrentCurrency.set("Error");
                                                        }
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


                        Toast.makeText(addcurrency.this, "Currency Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create();
                builder.show();
            }
        }else{
            Toast.makeText(this, "Select the curreny you need to update", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCurrency(View view){
        if(!currencynamee.getText().toString().isEmpty()){
            if(CurrentCurrency.validateCurrencyCode(currencynamee.getText().toString())){
                String key=currencyreference.push().getKey();
                currencyreference.child(key).setValue(new Currency(key,currencynamee.getText().toString(), CurrentUser.user.getId(),1));
                currencynamee.setText("");
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Invalid Currency Code", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Empty Currency Name", Toast.LENGTH_SHORT).show();
        }
    }
}
