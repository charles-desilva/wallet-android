package com.vaofim.boffin;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.Customer;
import Models.PayModes;
import Stables.CurrentUser;

public class addPayModes extends AppCompatActivity {

    EditText paymodename;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference paymodereference;

    RecyclerView recyclerView;
    ArrayList<PayModes> arrayList;

    String key2;

    PayModeAdapter payModeAdapter;

    Button paymodesave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pay_modes);

        firebaseDatabase=FirebaseDatabase.getInstance();

        paymodereference=firebaseDatabase.getReference("paymodes");
        paymodereference.keepSynced(true);

        paymodename=findViewById(R.id.paymodename);

        paymodesave=findViewById(R.id.paymodesave);

        paymodereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList=new ArrayList<PayModes>();
                Iterable <DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for(DataSnapshot data:dataSnapshots){
                    PayModes payModes=data.getValue(PayModes.class);
                    if(payModes.getUser().equals(CurrentUser.user.getId())){
                        arrayList.add(payModes);
                        payModeAdapter=new PayModeAdapter(arrayList);
                        recyclerView=findViewById(R.id.addpaymoderecycleview);
                        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(payModeAdapter);
                    }
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

    public void savePayMode(View view){
        if(!paymodename.getText().toString().isEmpty()){
            String key=paymodereference.push().getKey();
            paymodereference.child(key).setValue(new PayModes(key,paymodename.getText().toString(), CurrentUser.user.getId(),2));
            Toast.makeText(this, "Paymode Saved", Toast.LENGTH_SHORT).show();
            clearFields(view);
        }else{
            Toast.makeText(this, "Please Fill Pay MOde Name", Toast.LENGTH_SHORT).show();
        }
    }

    public void editPayMode(View view){
        if(key2!=null){
            if(!paymodename.getText().toString().isEmpty()){
                paymodereference.child(key2).setValue(new PayModes(key2,paymodename.getText().toString(), CurrentUser.user.getId(),2));
                Toast.makeText(this, "Paymode Saved", Toast.LENGTH_SHORT).show();
                clearFields(view);
            }else{
                Toast.makeText(this, "Please Fill Pay MOde Name", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //---------------------------------------------------

    class PayModeAdapter extends RecyclerView.Adapter<PayModeAdapter.PayModeViewHolder>{

        private List<PayModes> payModesList;


        public PayModeAdapter(List<PayModes> payModesList) {
            this.payModesList = payModesList;
        }

        public class PayModeViewHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;

            public PayModeViewHolder(@NonNull View itemView) {
                super(itemView);

                id=itemView.findViewById(R.id.singlecontentid);
                name=itemView.findViewById(R.id.singlecontentdisplay);
                deletebtn=itemView.findViewById(R.id.singlecontentdelete);
                editbtn=itemView.findViewById(R.id.singlecontentedit);
            }
        }

        @NonNull
        @Override
        public PayModeAdapter.PayModeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlecontent, viewGroup, false);

            return new PayModeAdapter.PayModeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PayModeAdapter.PayModeViewHolder paymodeViewHolder, int i) {
            final PayModes customer=payModesList.get(i);
            paymodeViewHolder.id.setText(customer.getId());
            paymodeViewHolder.name.setText(customer.getMode());

            paymodeViewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(customer.getStatus()==1){
                        Toast.makeText(addPayModes.this, "Integrated records cannot delete", Toast.LENGTH_SHORT).show();
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(addPayModes.this);
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
                                paymodereference.child(customer.getId()).removeValue();
                                Toast.makeText(addPayModes.this, "Pay Mode Removed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.create();
                        builder.show();
                    }

                }
            });

            paymodeViewHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(customer.getStatus()==1){
                        Toast.makeText(addPayModes.this, "Integrated records cannot edit", Toast.LENGTH_SHORT).show();
                    }else{
                        key2=customer.getId();
                        paymodename.setText(customer.getMode());
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return payModesList.size();
        }

    }

    public void clearFields(View view){
        paymodename.setText("");
    }
}
