package com.vaofim.boffin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Forget_Password_Pincode extends AppCompatActivity {

    String pincode;
    String user;
    EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password__pincode);
        pincode=getIntent().getStringExtra("code")+"".trim();
        user=getIntent().getStringExtra("user");
        code=findViewById(R.id.pincodenew);
    }

    public void checkProcess(View view){
            if(!code.getText().toString().isEmpty()){
                if(pincode.equals(code.getText().toString().trim())){
                    startActivity(new Intent(Forget_Password_Pincode.this,Change_Password.class)
                        .putExtra("user",user)
                    );
                }else{
                    Toast.makeText(this, "Pincode Invalid", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please fill pincode provided by email", Toast.LENGTH_SHORT).show();
            }
    }
}
