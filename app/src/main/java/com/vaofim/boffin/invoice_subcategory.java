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

import Models.addInvoice;
import Models.incomeSubCategory;
import Models.invoiceCategory;
import Models.invoiceSubCategory;
import Stables.CurrentUser;

public class invoice_subcategory extends AppCompatActivity {

    EditText name;
    String existkey;
    int syatuskey;

    String cate;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference invoicereference,invoicereference11;

    RecyclerView recyclerView;
    ArrayList<invoiceSubCategory> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_subcategory);

        name=findViewById(R.id.invoicesubcategoryname);

        cate=getIntent().getStringExtra("id");

        firebaseDatabase=FirebaseDatabase.getInstance();

        invoicereference=firebaseDatabase.getReference("invoice_subcategory");
        invoicereference.keepSynced(true);

        invoicereference11=firebaseDatabase.getReference("invoicerecord");
        invoicereference11.keepSynced(true);


        invoicereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList=new ArrayList<invoiceSubCategory>();

                try {
                    arrayList=new ArrayList<invoiceSubCategory>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        invoiceSubCategory customerobject=data.getValue(invoiceSubCategory.class);
                        if(cate.equals(customerobject.getCategory())){
                            arrayList.add(customerobject);
                            InvoiceSubCategoryAdapter expencesCategoryAdapter=new InvoiceSubCategoryAdapter(arrayList);
                            recyclerView=findViewById(R.id.invoicesubcatrecycler);
                            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(expencesCategoryAdapter);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<invoiceSubCategory>()
                    {
                        public int compare(invoiceSubCategory f1, invoiceSubCategory f2)
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

    public void saveExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){
            String key=invoicereference.push().getKey();
            invoicereference.child(key).setValue(new invoiceSubCategory(key,name.getText().toString(), cate,2));
            name.setText("");
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Fill Expence Sub Category Name", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToHome(View view){
        onBackPressed();
    }
    public void editExpencesCategory1(View view){
        if(!name.getText().toString().isEmpty()){
            invoicereference.child(existkey).setValue(new invoiceSubCategory(existkey,name.getText().toString(), cate,syatuskey));
            name.setText("");
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please Select Existing Sub Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanFields(View view){
        name.setText("");
    }


    class InvoiceSubCategoryAdapter extends RecyclerView.Adapter<InvoiceSubCategoryAdapter.InvoiceSubCategoryHolder>{

        private List<invoiceSubCategory> invoiceSubCategorylist;


        public InvoiceSubCategoryAdapter(List<invoiceSubCategory> invoiceSubCategorylist) {
            this.invoiceSubCategorylist = invoiceSubCategorylist;
        }

        public class InvoiceSubCategoryHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;
            CardView cardview;

            public InvoiceSubCategoryHolder(@NonNull View itemView) {
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
        public InvoiceSubCategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlecontent, viewGroup, false);

            return new InvoiceSubCategoryAdapter.InvoiceSubCategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull InvoiceSubCategoryAdapter.InvoiceSubCategoryHolder InvoiceSubCategoryHolder, int i) {
            final invoiceSubCategory invoiceSubCategory1=invoiceSubCategorylist.get(i);
            InvoiceSubCategoryHolder.id.setText(invoiceSubCategory1.getId());
            InvoiceSubCategoryHolder.name.setText(invoiceSubCategory1.getSubCategory());

            InvoiceSubCategoryHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(invoiceSubCategory1.getStatus()==2){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(invoice_subcategory.this);
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
                                invoicereference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                                        for(DataSnapshot data:iterator){
                                            invoiceSubCategory invoiceSubCategoryobject=data.getValue(invoiceSubCategory.class);
                                            if(invoiceSubCategoryobject.getId().equals(invoiceSubCategory1.getId())){
                                                invoicereference.child(invoiceSubCategoryobject.getId()).removeValue();
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
                        Toast.makeText(invoice_subcategory.this, "Inbuilt records cannot delete", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            InvoiceSubCategoryHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setText(invoiceSubCategory1.getSubCategory());
                    existkey=invoiceSubCategory1.getId();
                    syatuskey=invoiceSubCategory1.getStatus();
                }
            });
        }

        boolean check1=true;

        public void checkInvoices(final String accid){
            final String accounttid=accid;
            invoicereference11.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                    for(DataSnapshot snapshot:dataSnapshots){
                        addInvoice addincome=snapshot.getValue(addInvoice.class);

                        if(addincome.getSubcategoryid().equals(accounttid) && addincome.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    if(check1){
                        invoicereference.child(accid).removeValue();
                        Toast.makeText(invoice_subcategory.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(invoice_subcategory.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return invoiceSubCategorylist.size();
        }

    }
}
