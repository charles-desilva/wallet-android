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

import Models.Expences;
import Models.expenceCategory;
import Models.expenceSubCategory;
import Stables.CurrentUser;

public class addexpencessubcategory extends AppCompatActivity {

    EditText name;
    String existkey;
    int existstatus;

    String cate;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference expencescategory,expencereference;

    RecyclerView recyclerView;
    ArrayList<expenceSubCategory> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpencessubcategory);

        name=findViewById(R.id.addexpencessubcategory);

        firebaseDatabase=FirebaseDatabase.getInstance();

        cate=getIntent().getStringExtra("id");

        expencescategory=firebaseDatabase.getReference("expences_subcategory");
        expencescategory.keepSynced(true);

        expencereference=firebaseDatabase.getReference("expences");
        expencereference.keepSynced(true);

        expencescategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList=new ArrayList<expenceSubCategory>();

                try {
                    arrayList=new ArrayList<expenceSubCategory>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        expenceSubCategory customerobject=data.getValue(expenceSubCategory.class);
                        if(cate.equals(customerobject.getCategory())){
                            arrayList.add(customerobject);
                            ExpencesSubCategoryAdapter expencesCategoryAdapter=new ExpencesSubCategoryAdapter(arrayList);
                            recyclerView=findViewById(R.id.addexpensessubcateegoryrecyclerview);
                            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(expencesCategoryAdapter);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<expenceSubCategory>()
                    {
                        public int compare(expenceSubCategory f1, expenceSubCategory f2)
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

    class ExpencesSubCategoryAdapter extends RecyclerView.Adapter<ExpencesSubCategoryAdapter.ExpencesSubCategoryHolder>{

        private List<expenceSubCategory> expenceSubCategorylist;


        public ExpencesSubCategoryAdapter(List<expenceSubCategory> expenceSubCategorylist) {
            this.expenceSubCategorylist = expenceSubCategorylist;
        }

        public class ExpencesSubCategoryHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;
            CardView cardview;

            public ExpencesSubCategoryHolder(@NonNull View itemView) {
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
        public ExpencesSubCategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlecontent, viewGroup, false);

            return new ExpencesSubCategoryAdapter.ExpencesSubCategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpencesSubCategoryAdapter.ExpencesSubCategoryHolder ExpencesSubCategoryHolder, int i) {
            final expenceSubCategory expenceSubCategory1=expenceSubCategorylist.get(i);
            ExpencesSubCategoryHolder.id.setText(expenceSubCategory1.getId());
            ExpencesSubCategoryHolder.name.setText(expenceSubCategory1.getSubCategory());

            ExpencesSubCategoryHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(expenceSubCategory1.getStatus()==2){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(addexpencessubcategory.this);
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
                                            expenceSubCategory expenceSubCategoryobject=data.getValue(expenceSubCategory.class);
                                            if(expenceSubCategoryobject.getId().equals(expenceSubCategory1.getId())){
                                                checkExpence(expenceSubCategoryobject.getId());
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
                        Toast.makeText(addexpencessubcategory.this, "Inbuilt records cannot delete", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            ExpencesSubCategoryHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setText(expenceSubCategory1.getSubCategory());
                    existkey=expenceSubCategory1.getId();
                    existstatus=expenceSubCategory1.getStatus();
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

                        if(expences.getSubcategoryid().equals(accounttid) && expences.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    if(check1){
                        expencescategory.child(accid).removeValue();
                        Toast.makeText(addexpencessubcategory.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(addexpencessubcategory.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return expenceSubCategorylist.size();
        }

    }

    public void saveExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){
            String key=expencescategory.push().getKey();
            expencescategory.child(key).setValue(new expenceSubCategory(key,name.getText().toString(), cate,2));
            name.setText("");
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Fill Expence Sub Category Name", Toast.LENGTH_SHORT).show();
        }
    }
    public void editExpencesCategory1(View view){
        if(!name.getText().toString().isEmpty()){
            expencescategory.child(existkey).setValue(new expenceSubCategory(existkey,name.getText().toString(), cate,existstatus));
            name.setText("");
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Select Existing Sub Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanFields(View view){
        name.setText("");
    }

    public void goToHome(View view){
        onBackPressed();
    }
}
