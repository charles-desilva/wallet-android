package com.vaofim.boffin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;

import Models.User;
import Process_Classes.Codes;
import Process_Classes.SendMail;
import Stables.CurrentUser;

public class forgetpassword extends AppCompatActivity {

    EditText email;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userreference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        email=findViewById(R.id.forgetpasswordemail);

        firebaseDatabase=FirebaseDatabase.getInstance();
        userreference=firebaseDatabase.getReference("user");
        userreference.keepSynced(true);

        progressDialog=new ProgressDialog(this);
    }

    public void doSendNoow(View view){
        if(email!=null && !email.getText().toString().trim().isEmpty()){
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Password Reset Engine Starting");
            progressDialog.setCancelable(false);
            progressDialog.show();
            userreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> datalist=dataSnapshot.getChildren();

                    for(DataSnapshot child:datalist){
                        User user1=child.getValue(User.class);
                        if(user1.getEmail().equals(email.getText().toString().trim())){
                            final String code=new Codes().getcode()+"";
                            File datafile=new File(getApplicationContext().getFilesDir(),"data.voi");
                            if(datafile.exists()){
                                datafile.delete();
                            }
                            new SendMail().doSend("Password Reset | Boffin",email.getText().toString().trim(),"<h5>Hi "+user1.getName()+",Welcome to boffin password reset service by VAOFIM.</h5><br><br><h6>Please follow the pin code below.</h6><br><center><h4 style='background-color:blue; color:white; padding:10px;'>"+code+"</h4></center>");
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),Forget_Password_Pincode.class)
                                .putExtra("code",code)
                                    .putExtra("user",user1.getId())
                            );
                            return;
                        }
                    }
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(this, "We need your existing email for send email", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToLogin(View view){
        onBackPressed();
    }
}
