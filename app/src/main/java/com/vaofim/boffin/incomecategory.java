package com.vaofim.boffin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import Models.addincome;
import Models.expenceCategory;
import Models.incomeCategory;
import Models.incomeSubCategory;
import Process_Classes.CheckAvailability;
import Stables.CurrentUser;

public class incomecategory extends AppCompatActivity {

    EditText name;
    String existkey;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference incomereference;
    DatabaseReference incomesubreference;
    DatabaseReference reciptsreference;

    RecyclerView recyclerView;
    ArrayList<incomeCategory> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomecategory);

        name=findViewById(R.id.incomecategoryname);

        firebaseDatabase=FirebaseDatabase.getInstance();

        incomereference=firebaseDatabase.getReference("income_category");
        incomereference.keepSynced(true);

        incomesubreference=firebaseDatabase.getReference("income_subcategory");
        incomesubreference.keepSynced(true);

        reciptsreference=firebaseDatabase.getReference("recipts");
        reciptsreference.keepSynced(true);



        incomereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList=new ArrayList<incomeCategory>();

                try {
                    arrayList=new ArrayList<incomeCategory>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        incomeCategory customerobject=data.getValue(incomeCategory.class);
                        if(customerobject.getUser().equals(CurrentUser.user.getId())){
                            arrayList.add(customerobject);
                            IncomeCategoryAdapter expencesCategoryAdapter=new IncomeCategoryAdapter(arrayList);
                            recyclerView=findViewById(R.id.incomecategoryrecycleview);
                            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(expencesCategoryAdapter);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<incomeCategory>()
                    {
                        public int compare(incomeCategory f1, incomeCategory f2)
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

    public void saveExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){

            if(new CheckAvailability().incomeCategoryCheck(arrayList,name.getText().toString())){
                String key=incomereference.push().getKey();
                incomereference.child(key).setValue(new incomeCategory(key,name.getText().toString(), CurrentUser.user.getId(),2));
                String subkey=incomesubreference.push().getKey();
                incomesubreference.child(subkey).setValue(new incomeSubCategory(subkey,"General", key,1));
                name.setText("");
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Income Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please Fill Category Name", Toast.LENGTH_SHORT).show();
        }
    }
    public void editExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){
            if(new CheckAvailability().incomeCategoryCheck(arrayList,name.getText().toString())){
                incomereference.child(existkey).setValue(new incomeCategory(existkey,name.getText().toString(), CurrentUser.user.getId(),2));
                name.setText("");
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Income Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(this, "Please Select Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanFields(View view){
        name.setText("");
    }

    class IncomeCategoryAdapter extends RecyclerView.Adapter<IncomeCategoryAdapter.IncomeSubCategoryHolder>{

        private List<incomeCategory> incomeCategorylist;


        public IncomeCategoryAdapter(List<incomeCategory> incomeCategorylist) {
            this.incomeCategorylist = incomeCategorylist;
        }

        public class IncomeSubCategoryHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;
            CardView cardview;

            public IncomeSubCategoryHolder(@NonNull View itemView) {
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
        public IncomeSubCategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlecontent, viewGroup, false);
            return new IncomeCategoryAdapter.IncomeSubCategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull IncomeCategoryAdapter.IncomeSubCategoryHolder IncomeSubCategoryHolder, int i) {
            final incomeCategory incomeCategory1=incomeCategorylist.get(i);
            IncomeSubCategoryHolder.id.setText(incomeCategory1.getId());
            IncomeSubCategoryHolder.name.setText(incomeCategory1.getCategory());

            IncomeSubCategoryHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(incomeCategory1.getStatus()==1){
                        Toast.makeText(incomecategory.this, "Integrated records cannot delete", Toast.LENGTH_SHORT).show();
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(incomecategory.this);
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
                                incomereference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                                        for(DataSnapshot data:iterator){
                                            incomeCategory incomeCategoryobject=data.getValue(incomeCategory.class);
                                            if(incomeCategoryobject.getId().equals(incomeCategory1.getId())){
                                                checkExpence(incomeCategoryobject.getId());
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

            IncomeSubCategoryHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(incomecategory.this,incomesubcategory.class)
                            .putExtra("id",incomeCategory1.getId())
                    );
                }
            });

            IncomeSubCategoryHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(incomeCategory1.getStatus()==1){
                        Toast.makeText(incomecategory.this, "Integrated records cannot edit", Toast.LENGTH_SHORT).show();
                    }else {
                        name.setText(incomeCategory1.getCategory());
                        existkey = incomeCategory1.getId();
                    }
                }
            });
        }

        boolean check1=true;

        public void checkExpence(final String accid){
            final String accounttid=accid;
            reciptsreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                    for(DataSnapshot snapshot:dataSnapshots){
                        addincome expences=snapshot.getValue(addincome.class);

                        if(expences.getCategoryid().equals(accounttid) && expences.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    if(check1){
                        incomereference.child(accid).removeValue();
                        Toast.makeText(incomecategory.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(incomecategory.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return incomeCategorylist.size();
        }

    }
}
