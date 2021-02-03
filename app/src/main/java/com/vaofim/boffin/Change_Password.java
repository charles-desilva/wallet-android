package com.vaofim.boffin;

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

import Models.User;
import Process_Classes.SendMail;

public class Change_Password extends AppCompatActivity {

    String user;
    EditText pw1,pw2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);

        try {
            user=getIntent().getStringExtra("user");
            pw1=findViewById(R.id.newpw1);
            pw2=findViewById(R.id.newpw2);

            firebaseDatabase=FirebaseDatabase.getInstance();
            userreference=firebaseDatabase.getReference("user");
            userreference.keepSynced(true);
        }catch(Exception e){

            startActivity(new Intent(Change_Password.this,login.class));
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void changePasswords(View view){
        if(!pw1.getText().toString().isEmpty() && !pw2.getText().toString().isEmpty()){
            if(pw1.getText().toString().equals(pw2.getText().toString())){
                userreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable <DataSnapshot> datalist=dataSnapshot.getChildren();

                        for(DataSnapshot child:datalist){
                            User user1=child.getValue(User.class);
                            if(user1.getId().equals(user)){
                                userreference.child(user).setValue(new User(user1.getId(),user1.getName(),user1.getEmail(),pw1.getText().toString(),user1.getUsertype(),user1.getStatus()));
                                startActivity(new Intent(Change_Password.this,login.class));
                                return;
                            }
                        }
                        Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                Toast.makeText(this, "Passwords not match", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Passwords not filled", Toast.LENGTH_SHORT).show();
        }
    }
}
