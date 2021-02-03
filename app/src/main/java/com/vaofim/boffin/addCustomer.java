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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import Models.Account;
import Models.Customer;
import Models.addInvoice;
import Models.addincome;
import Models.invoiceSubCategory;
import Models.recordCreditNote;
import Process_Classes.CheckAvailability;
import Stables.CurrentUser;

public class addCustomer extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference customerreference;
    EditText customername;
    RecyclerView recyclerView;
    ArrayList <Customer> arrayList;
    CustomerAdapter customerAdapter;
    String editkey;
    public DatabaseReference recordcredit_reference,reciptsreference,invoicereference;
    Button savebtnaddcustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        firebaseDatabase=FirebaseDatabase.getInstance();

        customerreference=firebaseDatabase.getReference("customer");

        customerreference.keepSynced(true);

        customername=findViewById(R.id.addcustomername);

        recordcredit_reference=firebaseDatabase.getReference("recordcredit");
        recordcredit_reference.keepSynced(true);

        reciptsreference=firebaseDatabase.getReference("recipts");
        reciptsreference.keepSynced(true);

        invoicereference=firebaseDatabase.getReference("invoicerecord");
        invoicereference.keepSynced(true);

        savebtnaddcustomer=findViewById(R.id.savebtnaddcustomer);

        customerreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    arrayList=new ArrayList<Customer>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        Customer customerobject=data.getValue(Customer.class);
                        if(customerobject.getUser().equals(CurrentUser.user.getId())){
                            arrayList.add(customerobject);
                        }
                    }

                    Collections.sort(arrayList,new Comparator<Customer>()
                    {
                        public int compare(Customer f1, Customer f2)
                        {
                            return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
                        }
                    });

                    customerAdapter=new CustomerAdapter(arrayList);
                    recyclerView=findViewById(R.id.addcustomerrecyclerview);
                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(customerAdapter);


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

    public void doCustomerSave(View view){
        if(!customername.getText().toString().isEmpty()){
            if(new CheckAvailability().checkCustomerAvailability(arrayList,customername.getText().toString())){
                String key=customerreference.push().getKey();

                customerreference.child(key).setValue(new Customer(key,customername.getText().toString(), CurrentUser.user.getId()));
                Toast.makeText(this, "Customer Saved", Toast.LENGTH_SHORT).show();

                clearFileds(view);
            }else{
                Toast.makeText(this, "Customer Name Exists, Please Try Another", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Please Enter Name For Save New Customer", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFileds(View view) {
        customername.setText("");
    }


    //-----------------------------------------------------------------------------


    class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>{

        private List<Customer> customerlist;


        public CustomerAdapter(List<Customer> customerlist) {
            this.customerlist = customerlist;
        }

        public class CustomerViewHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;

            public CustomerViewHolder(@NonNull View itemView) {
                super(itemView);

                id=itemView.findViewById(R.id.singlecontentid);
                name=itemView.findViewById(R.id.singlecontentdisplay);
                deletebtn=itemView.findViewById(R.id.singlecontentdelete);
                editbtn=itemView.findViewById(R.id.singlecontentedit);
            }
        }

        @NonNull
        @Override
        public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlecontent, viewGroup, false);

            return new CustomerAdapter.CustomerViewHolder(itemView);
        }

        public int deleteposition=0;

        @Override
        public void onBindViewHolder(@NonNull CustomerAdapter.CustomerViewHolder customerViewHolder, int i) {
            final int x=i;
            final Customer customer=customerlist.get(i);
            customerViewHolder.id.setText(customer.getId());
            customerViewHolder.name.setText(customer.getName());


            customerViewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteposition=x;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(addCustomer.this);
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
                            customerreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                                    for(DataSnapshot data:iterator){
                                        Customer customerobject=data.getValue(Customer.class);
                                        System.out.println("Customerrrrrrrrrrrrrrrrrrrrr - "+customer.getId());
                                        if(customerobject.getId().equals(customer.getId())){
                                            checkIncome(customer.getId());
                                            notifyItemRemoved(x);
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
            });

            customerViewHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customername.setText(customer.getName());
                    editkey=customer.getId();
                }
            });
        }



        @Override
        public int getItemCount() {
            return customerlist.size();
        }

    }


    public void editRecord(View view){
        if(editkey!=null){
            if(!customername.getText().toString().isEmpty()){

                customerreference.child(editkey).setValue(new Customer(editkey,customername.getText().toString(), CurrentUser.user.getId()));
                Toast.makeText(this, "Customer Updated", Toast.LENGTH_SHORT).show();

                clearFileds(view);
            }else{
                Toast.makeText(this, "Please Enter Name For Update Customer", Toast.LENGTH_SHORT).show();
            }
        }else{
                Toast.makeText(this, "Please select the customer that you need to update", Toast.LENGTH_SHORT).show();

        }
    }


    boolean check1=true;
    boolean check2=true;
    boolean check3=true;

    public void checkIncome(final String accid){
        final String accounttid=accid;
        reciptsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for(DataSnapshot snapshot:dataSnapshots){
                    addincome addincome=snapshot.getValue(addincome.class);

                    if(addincome.getCustomerid().equals(accounttid) && addincome.getUser().equals(CurrentUser.user.getId())){
                        check2=false;
                        break;
                    }
                }
                System.out.println(check1);
                checkCustomer(accid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkCustomer(final String accid){
        final String accounttid=accid;
        recordcredit_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for(DataSnapshot snapshot:dataSnapshots){
                    recordCreditNote addincome=snapshot.getValue(recordCreditNote.class);

                    if(addincome.getCustomerid().equals(accounttid) && addincome.getUser().equals(CurrentUser.user.getId())){
                        check2=false;
                        break;
                    }
                }

                System.out.println(check2);
                checkInvoices(accid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkInvoices(final String accid){
        final String accounttid=accid;
        invoicereference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for(DataSnapshot snapshot:dataSnapshots){
                    addInvoice addincome=snapshot.getValue(addInvoice.class);

                    if(addincome.getCustomerid().equals(accounttid) && addincome.getUser().equals(CurrentUser.user.getId())){
                        check2=false;
                        break;
                    }
                }

                System.out.println(check1+" - "+check2+" - "+check3);

                if(check1 && check2 && check3){
                    customerreference.child(accounttid).removeValue();
                    Toast.makeText(addCustomer.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    arrayList=new ArrayList<>();
                    customerAdapter=new CustomerAdapter(arrayList);
                    recyclerView.setAdapter(customerAdapter);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }else{
                    Toast.makeText(addCustomer.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
