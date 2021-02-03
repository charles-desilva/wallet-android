package com.vaofim.boffin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Models.Customer;
import Models.Expences;
import Models.expenceCategory;
import Models.expenceSubCategory;
import Process_Classes.CheckAvailability;
import Stables.CurrentUser;

public class addexpencescategory extends AppCompatActivity {

    EditText name;
    String existkey;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference expencescategory;
    DatabaseReference subexpencescategory,expencereference;

    RecyclerView recyclerView;
    ArrayList <expenceCategory> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpencescategory);

        name=findViewById(R.id.addexpencescategory);

        firebaseDatabase=FirebaseDatabase.getInstance();

        subexpencescategory=firebaseDatabase.getReference("expences_subcategory");
        subexpencescategory.keepSynced(true);

        expencescategory=firebaseDatabase.getReference("expences_category");
        expencescategory.keepSynced(true);

        expencereference=firebaseDatabase.getReference("expences");
        expencereference.keepSynced(true);

        expencescategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList=new ArrayList<expenceCategory>();

                try {
                    arrayList=new ArrayList<expenceCategory>();
                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        expenceCategory customerobject=data.getValue(expenceCategory.class);
                        if(customerobject.getUser().equals(CurrentUser.user.getId())){
                            arrayList.add(customerobject);
                            ExpencesCategoryAdapter expencesCategoryAdapter=new ExpencesCategoryAdapter(arrayList);
                            recyclerView=findViewById(R.id.addexpensescateegoryrecyclerview);
                            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(expencesCategoryAdapter);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<expenceCategory>()
                    {
                        public int compare(expenceCategory f1, expenceCategory f2)
                        {
                            return f1.getCategory().toLowerCase().compareTo(f2.getCategory().toLowerCase());
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
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

    class ExpencesCategoryAdapter extends RecyclerView.Adapter<ExpencesCategoryAdapter.ExpencesCategoryHolder>{

        private List<expenceCategory> expenceCategorylist;


        public ExpencesCategoryAdapter(List<expenceCategory> expenceCategorylist) {
            this.expenceCategorylist = expenceCategorylist;
        }

        public class ExpencesCategoryHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;
            CardView cardview;

            public ExpencesCategoryHolder(@NonNull View itemView) {
                super(itemView);
                cardview=itemView.findViewById(R.id.singlecard);
                id=itemView.findViewById(R.id.singlecontentid);
                name=itemView.findViewById(R.id.singlecontentdisplay);
                deletebtn=itemView.findViewById(R.id.singlecontentdelete);
                editbtn=itemView.findViewById(R.id.singlecontentedit);
            }
        }

        @NonNull
        @Override
        public ExpencesCategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlecontent, viewGroup, false);

            return new ExpencesCategoryAdapter.ExpencesCategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpencesCategoryAdapter.ExpencesCategoryHolder ExpencesCategoryHolder, int i) {
            final expenceCategory expenceCategory1=expenceCategorylist.get(i);
            ExpencesCategoryHolder.id.setText(expenceCategory1.getId());
            ExpencesCategoryHolder.name.setText(expenceCategory1.getCategory());

            ExpencesCategoryHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(expenceCategory1.getStatus()==1){
                        Toast.makeText(addexpencescategory.this, "Integrated records cannot delete", Toast.LENGTH_SHORT).show();
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(addexpencescategory.this);
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
                                expencescategory.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                                        for(DataSnapshot data:iterator){
                                            expenceCategory expenceCategoryobject=data.getValue(expenceCategory.class);
                                            if(expenceCategoryobject.getId().equals(expenceCategory1.getId())){
                                                checkExpence(expenceCategoryobject.getId());
                                                break;
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



                }
            });

            ExpencesCategoryHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(addexpencescategory.this,addexpencessubcategory.class)
                        .putExtra("id",expenceCategory1.getId())
                    );
                }
            });

            ExpencesCategoryHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(expenceCategory1.getStatus()==1){
                        Toast.makeText(addexpencescategory.this, "Integrated records cannot edit", Toast.LENGTH_SHORT).show();
                    }else{
                        name.setText(expenceCategory1.getCategory());
                        existkey=expenceCategory1.getId();
                    }

                }
            });
        }

        boolean check1=true;

        public void checkExpence(final String accid){
            final String accounttid=accid;
            expencereference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                    for(DataSnapshot snapshot:dataSnapshots){
                        Expences expences=snapshot.getValue(Expences.class);

                        if(expences.getCategoryid().equals(accounttid) && expences.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    if(check1){
                        expencescategory.child(accid).removeValue();
                        Toast.makeText(addexpencescategory.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(addexpencescategory.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return expenceCategorylist.size();
        }

    }
    
    public void saveExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){
            
            if(new CheckAvailability().expenceCategoryCheck(arrayList,name.getText().toString())){
                String key=expencescategory.push().getKey();
                expencescategory.child(key).setValue(new expenceCategory(key,name.getText().toString(), CurrentUser.user.getId(),2));
                String subkey=subexpencescategory.push().getKey();
                subexpencescategory.child(subkey).setValue(new expenceSubCategory(subkey,"General", key,1));
                name.setText("");
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Expence Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }
            

        }else{
            Toast.makeText(this, "Please Fill Expense Category Name", Toast.LENGTH_SHORT).show();
        }
    }
    public void editExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){

            if(new CheckAvailability().expenceCategoryCheck(arrayList,name.getText().toString())){
                expencescategory.child(existkey).setValue(new expenceCategory(existkey,name.getText().toString(), CurrentUser.user.getId(),2));
                name.setText("");
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Expence Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please Select Existing Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanFields(View view){
        name.setText("");
    }
}
