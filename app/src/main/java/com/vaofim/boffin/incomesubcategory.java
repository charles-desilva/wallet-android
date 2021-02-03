package com.vaofim.boffin;

import android.content.DialogInterface;
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
import Models.expenceSubCategory;
import Models.incomeSubCategory;
import Models.invoiceSubCategory;
import Stables.CurrentUser;

public class incomesubcategory extends AppCompatActivity {

    EditText name;
    String existkey;
    int syatuskey;

    String cate;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference incomereference,sub_category_reference,reciptsreference;

    RecyclerView recyclerView;
    ArrayList<incomeSubCategory> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incomesubcategory);

        name=findViewById(R.id.incomesubcategoryname);

        cate=getIntent().getStringExtra("id");

        firebaseDatabase=FirebaseDatabase.getInstance();

        incomereference=firebaseDatabase.getReference("income_subcategory");
        incomereference.keepSynced(true);

        sub_category_reference=firebaseDatabase.getReference("expences_subcategory");
        sub_category_reference.keepSynced(true);

        reciptsreference=firebaseDatabase.getReference("recipts");
        reciptsreference.keepSynced(true);


        incomereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList=new ArrayList<incomeSubCategory>();

                try {
                    arrayList=new ArrayList<incomeSubCategory>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        incomeSubCategory customerobject=data.getValue(incomeSubCategory.class);
                        if(cate.equals(customerobject.getCategory())){
                            arrayList.add(customerobject);
                            IncomeSubCategoryAdapter expencesCategoryAdapter=new IncomeSubCategoryAdapter(arrayList);
                            recyclerView=findViewById(R.id.incomesubcategoryrecycleview);
                            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(expencesCategoryAdapter);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<incomeSubCategory>()
                    {
                        public int compare(incomeSubCategory f1, incomeSubCategory f2)
                        {
                            return f1.getSubCategory().toLowerCase().compareTo(f2.getSubCategory().toLowerCase());
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
            String key=incomereference.push().getKey();
            incomereference.child(key).setValue(new incomeSubCategory(key,name.getText().toString(), cate,2));
            name.setText("");
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Fill Expence Sub Category Name", Toast.LENGTH_SHORT).show();
        }
    }
    public void editExpencesCategory1(View view){
        if(!name.getText().toString().isEmpty()){
            incomereference.child(existkey).setValue(new incomeSubCategory(existkey,name.getText().toString(), cate,syatuskey));
            name.setText("");
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Select Existing Sub Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanFields(View view){
        name.setText("");
    }


    class IncomeSubCategoryAdapter extends RecyclerView.Adapter<IncomeSubCategoryAdapter.IncomeSubCategoryHolder>{

        private List<incomeSubCategory> incomeSubCategorylist;


        public IncomeSubCategoryAdapter(List<incomeSubCategory> incomeSubCategorylist) {
            this.incomeSubCategorylist = incomeSubCategorylist;
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

            return new IncomeSubCategoryAdapter.IncomeSubCategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull IncomeSubCategoryAdapter.IncomeSubCategoryHolder IncomeSubCategoryHolder, int i) {
            final incomeSubCategory incomeSubCategory1=incomeSubCategorylist.get(i);
            IncomeSubCategoryHolder.id.setText(incomeSubCategory1.getId());
            IncomeSubCategoryHolder.name.setText(incomeSubCategory1.getSubCategory());

            IncomeSubCategoryHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(incomeSubCategory1.getStatus()==2){


                        final AlertDialog.Builder builder = new AlertDialog.Builder(incomesubcategory.this);
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
                                            incomeSubCategory incomeSubCategoryobject=data.getValue(incomeSubCategory.class);
                                            if(incomeSubCategoryobject.getId().equals(incomeSubCategory1.getId())){
                                                checkExpence(incomeSubCategoryobject.getId());
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




                    }else{
                        Toast.makeText(incomesubcategory.this, "Inbuilt records cannot delete", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            IncomeSubCategoryHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setText(incomeSubCategory1.getSubCategory());
                    existkey=incomeSubCategory1.getId();
                    syatuskey=incomeSubCategory1.getStatus();
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

                        if(expences.getSubcategoryid().equals(accounttid) && expences.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    if(check1){
                        incomereference.child(accid).removeValue();
                        Toast.makeText(incomesubcategory.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(incomesubcategory.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return incomeSubCategorylist.size();
        }

    }
}
