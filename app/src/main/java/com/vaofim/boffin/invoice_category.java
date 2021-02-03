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

import Models.addInvoice;
import Models.incomeCategory;
import Models.incomeSubCategory;
import Models.invoiceCategory;
import Models.invoiceSubCategory;
import Process_Classes.CheckAvailability;
import Stables.CurrentUser;

public class invoice_category extends AppCompatActivity {

    EditText name;
    String existkey;
    int syatuskey;

    Button invoicecategorysavebtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference invoicereference;
    DatabaseReference invoicesubreference,invoicereference11;

    RecyclerView recyclerView;
    ArrayList<invoiceCategory> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_category);

        name=findViewById(R.id.invoicename);

        firebaseDatabase=FirebaseDatabase.getInstance();

        invoicereference=firebaseDatabase.getReference("invoice_category");
        invoicereference.keepSynced(true);

        invoicecategorysavebtn=findViewById(R.id.invoicecategorysavebtn);

        invoicesubreference=firebaseDatabase.getReference("invoice_subcategory");
        invoicesubreference.keepSynced(true);

        invoicereference11=firebaseDatabase.getReference("invoicerecord");
        invoicereference11.keepSynced(true);

        invoicereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList=new ArrayList<invoiceCategory>();

                try {
                    arrayList=new ArrayList<invoiceCategory>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        invoiceCategory customerobject=data.getValue(invoiceCategory.class);
                        if(customerobject.getUser().equals(CurrentUser.user.getId())){
                            arrayList.add(customerobject);
                            InvoiceCategoryAdapter expencesCategoryAdapter=new InvoiceCategoryAdapter(arrayList);
                            recyclerView=findViewById(R.id.invoicecategoryrecycler);
                            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(expencesCategoryAdapter);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<invoiceCategory>()
                    {
                        public int compare(invoiceCategory f1, invoiceCategory f2)
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
            if(new CheckAvailability().invoiceCategoryCheck(arrayList,name.getText().toString())){
                String key=invoicereference.push().getKey();
                invoicereference.child(key).setValue(new invoiceCategory(key,name.getText().toString(), CurrentUser.user.getId(),2));
                String subkey=invoicesubreference.push().getKey();
                invoicesubreference.child(subkey).setValue(new invoiceSubCategory(subkey,"General", key,2));
                name.setText("");
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Invoice Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please Fill Category Name", Toast.LENGTH_SHORT).show();
        }
    }
    public void editExpencesCategory(View view){
        if(!name.getText().toString().isEmpty()){
            if(new CheckAvailability().invoiceCategoryCheck(arrayList,name.getText().toString())){
                invoicereference.child(existkey).setValue(new invoiceCategory(existkey,name.getText().toString(), CurrentUser.user.getId(),2));
                name.setText("");
                invoicecategorysavebtn.setText("Save");
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Invoice Category Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please Select Category First", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanFields(View view){
        name.setText("");
    }

    class InvoiceCategoryAdapter extends RecyclerView.Adapter<InvoiceCategoryAdapter.InvoiceSubCategoryHolder>{

        private List<invoiceCategory> incomeCategorylist;


        public InvoiceCategoryAdapter(List<invoiceCategory> incomeCategorylist) {
            this.incomeCategorylist = incomeCategorylist;
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

            return new InvoiceCategoryAdapter.InvoiceSubCategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull InvoiceCategoryAdapter.InvoiceSubCategoryHolder InvoiceSubCategoryHolder, int i) {
            final invoiceCategory incomeCategory1=incomeCategorylist.get(i);
            InvoiceSubCategoryHolder.id.setText(incomeCategory1.getId());
            InvoiceSubCategoryHolder.name.setText(incomeCategory1.getCategory());

            InvoiceSubCategoryHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(incomeCategory1.getStatus()==1){
                        Toast.makeText(invoice_category.this, "Integrated records cannot delete", Toast.LENGTH_SHORT).show();
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(invoice_category.this);
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
                                            invoiceCategory incomeCategoryobject=data.getValue(invoiceCategory.class);
                                            if(incomeCategoryobject.getId().equals(incomeCategory1.getId())){
                                                checkInvoices(incomeCategoryobject.getId());
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

            InvoiceSubCategoryHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(invoice_category.this,invoice_subcategory.class)
                            .putExtra("id",incomeCategory1.getId())
                    );
                }
            });



            InvoiceSubCategoryHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(incomeCategory1.getStatus()==1){
                        Toast.makeText(invoice_category.this, "Integrated records cannot edit", Toast.LENGTH_SHORT).show();
                    }else{
                        name.setText(incomeCategory1.getCategory());
                        existkey=incomeCategory1.getId();
                    }


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

                        if(addincome.getCategoryid().equals(accounttid) && addincome.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    if(check1){
                        invoicereference.child(accid).removeValue();
                        Toast.makeText(invoice_category.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(invoice_category.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
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
