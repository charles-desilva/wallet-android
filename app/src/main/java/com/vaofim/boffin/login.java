package com.vaofim.boffin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.vaofim.boffin.R;
import com.vaofim.boffin.register;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import Models.User;
import Stables.CurrentUser;

public class login extends AppCompatActivity {

    EditText username,password;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userreference;
    ProgressDialog progressDialog;

    File datafile;
    FileOutputStream fileOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDatabase=FirebaseDatabase.getInstance();

        username=findViewById(R.id.loginemail);
        password=findViewById(R.id.loginpassword);

        userreference=firebaseDatabase.getReference("user");
        userreference.keepSynced(true);

        progressDialog=new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {

    }

    public void SignUp(View view){
        startActivity(new Intent(getApplicationContext(), register.class));
    }

    public void ForgetPassowrd(View view){
        if(isNetworkAvailable()){
            startActivity(new Intent(getApplicationContext(), forgetpassword.class));
        }else{
            Toast.makeText(this, "Internet Connection Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void doLogin(View view){

        if(!username.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){

            progressDialog.setMessage("Please wait while login");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            userreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> datalist=dataSnapshot.getChildren();
                    for(DataSnapshot child:datalist){
                        User user1=child.getValue(User.class);
                        if(user1.getEmail().equals(username.getText().toString().trim()) && user1.getPassword().equals(password.getText().toString().trim())){
                            Intent intent=new Intent(getApplicationContext(),homenew.class);

                            try {
                                datafile=new File(getApplicationContext().getFilesDir(),"data.voi");
                                if(datafile.exists()){
                                    datafile.delete();
                                }

                                if(datafile.createNewFile()){
                                    FileWriter fileWriter=new FileWriter(datafile);
                                    fileWriter.write(user1.getId());
                                    fileWriter.flush();
                                    fileWriter.close();

                                    CurrentUser.user=user1;
                                    progressDialog.hide();
                                    progressDialog.dismiss();
                                    startActivity(intent);

                                    return;
                                }else{
                                    Toast.makeText(login.this, "Login Process Error", Toast.LENGTH_SHORT).show();
                                }
                            }catch(Exception e){
                                return;
                            }

                        }
                    }

                    progressDialog.hide();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Empty Credentials",Toast.LENGTH_SHORT).show();
        }

    }


}
