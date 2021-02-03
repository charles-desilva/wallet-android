package com.vaofim.boffin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import Models.Vat;
import Stables.CurrentUser;
import Stables.VatSettings;

public class addVat extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference vatreference;
    TextView vatprecentage,vatdesc;
    EditText vattext,vatregname,vatregnumber;
    Button vatbtn;
    String keyreecord;
    Spinner vatsettspinner;

    int vatsettid;
    String vatsettname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vat);

        firebaseDatabase=FirebaseDatabase.getInstance();

        vatreference=firebaseDatabase.getReference("vat");
        vatreference.keepSynced(true);

        vattext=findViewById(R.id.vatnumber);
        vatregname=findViewById(R.id.vatregname1);
        vatregnumber=findViewById(R.id.vatregnumber1);
        vatsettspinner=findViewById(R.id.vatsettingsspinner1);

        vatprecentage=findViewById(R.id.vatprecentage);
        vatdesc=findViewById(R.id.vatdesc);
        vatbtn=findViewById(R.id.vatbtn);

        ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(addVat.this,R.layout.spinner_dropdown_design, new VatSettings().getVatSettingsNames());
        account_adapter.setDropDownViewResource(R.layout.spinner_field);
        vatsettspinner.setAdapter(account_adapter);
        vatsettspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vatsettid=new VatSettings().getVatSettingsId().get(position);
                vatsettname=new VatSettings().getVatSettingsNames().get(position);
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
                        vatprecentage.setText(vat.getValue()+" %");
                        vattext.setText(vat.getValue()+"");
                        vatdesc.setVisibility(View.VISIBLE);
                        vatbtn.setText("Update");
                        vatregname.setText(vat.getVatregname());
                        vatregnumber.setText(vat.getVatregno());
                        keyreecord=vat.getId();
                        vatsettspinner.setSelection(vat.getVatselectionid());
                        b=false;

                        vatbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateVat(v);
                            }
                        });

                        break;
                    }
                }

                if(b){
                    vatprecentage.setText("No VAT Record Found");
                    vatdesc.setVisibility(View.INVISIBLE);
                    vatbtn.setText("Save");
                    vatbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveVat(v);
                        }
                    });


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

    public void saveVat(View view){
        if(!vattext.getText().toString().isEmpty() && vatsettname!=null && !vatregname.getText().toString().isEmpty() && !vatregnumber.getText().toString().isEmpty()){
            String key=vatreference.push().getKey();
            vatreference.child(key).setValue(new Vat(key,Double.parseDouble(vattext.getText().toString()),CurrentUser.user.getId(),vatsettid,vatsettname,vatregname.getText().toString(),vatregnumber.getText().toString()));
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Fill All Inputs For Save", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateVat(View view){
        if(!vattext.getText().toString().isEmpty() && vatsettname!=null && !vatregname.getText().toString().isEmpty() && !vatregnumber.getText().toString().isEmpty()){
            vatreference.child(keyreecord).setValue(new Vat(keyreecord,Double.parseDouble(vattext.getText().toString()),CurrentUser.user.getId(),vatsettid,vatsettname,vatregname.getText().toString(),vatregnumber.getText().toString()));
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Fill All Inputs For Save", Toast.LENGTH_SHORT).show();
        }
    }
}

