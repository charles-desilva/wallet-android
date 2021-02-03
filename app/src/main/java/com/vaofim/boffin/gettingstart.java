package com.vaofim.boffin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class gettingstart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettingstart);
    }

    public void doGettingStart(View view){
        try {
            if(new File(getApplicationContext().getFilesDir(),"record.voi").createNewFile()){
                startActivity(new Intent(this,login.class));
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(gettingstart.this);
                builder.setCancelable(true);
                builder.setTitle("Terminated");
                builder.setMessage("This application working with temporary files and other statics. but application can't use resources correctly.");
                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.create();
                builder.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
