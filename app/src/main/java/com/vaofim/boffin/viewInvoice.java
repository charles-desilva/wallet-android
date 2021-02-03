package com.vaofim.boffin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Models.Currency;
import Models.UserCurrency;
import Models.addInvoice;
import Stables.CurrentCurrency;
import Stables.CurrentUser;

public class viewInvoice extends AppCompatActivity{

    FirebaseDatabase firebaseDatabase;
    DatabaseReference currencyreference;
    DatabaseReference usercurrency;
    DatabaseReference invoicereference;
    String currencyname= CurrentCurrency.get();
    RecyclerView recyclerView;
    ArrayList<addInvoice> arrayList;

    ViewInvoiceAdapter viewInvoiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invoice);

        firebaseDatabase=FirebaseDatabase.getInstance();
        currencyreference=firebaseDatabase.getReference("currency");
        usercurrency=firebaseDatabase.getReference("usercurrency");
        invoicereference=firebaseDatabase.getReference("invoicerecord");

        currencyreference.keepSynced(true);
        usercurrency.keepSynced(true);
        invoicereference.keepSynced(true);

        getInvoices();
    }

    public void getInvoices(){


        recyclerView=findViewById(R.id.viewinvoices1recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        invoicereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    arrayList=new ArrayList<>();

                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        final addInvoice customerobject=data.getValue(addInvoice.class);
                        if(customerobject.getUser().equals(CurrentUser.user.getId())){
                            arrayList.add(customerobject);
                        }
                    }

                    Collections.sort(arrayList, new Comparator<addInvoice>() {
                        @Override
                        public int compare(addInvoice o1, addInvoice o2) {
                            int val=0;
                            try {
                                Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(o1.getDate());
                                Date date2=new SimpleDateFormat("yyyy-MM-dd").parse(o2.getDate());
                                val=date1.compareTo(date2);
                            }catch(Exception e){

                            }
                            return val;
                        }
                    });

                    Collections.reverse(arrayList);

                    viewInvoiceAdapter=new ViewInvoiceAdapter(arrayList,currencyname,viewInvoice.this);
                    recyclerView.setAdapter(viewInvoiceAdapter);
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
}

class ViewInvoiceAdapter extends RecyclerView.Adapter<ViewInvoiceAdapter.ViewInvoiceHolder> {

    private List <addInvoice> list;
    String currencyname;
    Context context;

    public ViewInvoiceAdapter(List<addInvoice> list,String currentcy,Context context) {
        this.list = list;
        currencyname=currentcy;
        this.context=context;
    }

    class ViewInvoiceHolder extends RecyclerView.ViewHolder{
        TextView date,name1,currency,price,refno,type,username;
        ConstraintLayout constraintLayout;

        public ViewInvoiceHolder(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.singlerecordview_date);
            currency=itemView.findViewById(R.id.singlerecordview_currency);
            name1=itemView.findViewById(R.id.singlerecordview_name);
            username=itemView.findViewById(R.id.singlerecord_username);
            price=itemView.findViewById(R.id.singlerecordview_price);
            refno=itemView.findViewById(R.id.singlerecordview_refnumber);
            type=itemView.findViewById(R.id.singlerecordview_type);
            constraintLayout=itemView.findViewById(R.id.singlerecordviewconstraintlayout);
        }
    }

    @NonNull
    @Override
    public ViewInvoiceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlerecordview, viewGroup, false);
        return new ViewInvoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewInvoiceHolder viewInvoiceTab1Holder, int i) {
        addInvoice addInvoice=list.get(i);
        final String id=addInvoice.getId();
        Date date1=null;
        try {
            viewInvoiceTab1Holder.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(addInvoice.getDate())));
        }catch(Exception e){

        }



        if(addInvoice.getCustomername().length()>25){
            viewInvoiceTab1Holder.name1.setText(addInvoice.getCustomername().substring(0,25)+"..");
        }else{

            viewInvoiceTab1Holder.name1.setText(addInvoice.getCustomername());
        }

        if(addInvoice.getCategoryname().length()>25){
            viewInvoiceTab1Holder.username.setText(addInvoice.getCategoryname().substring(0,25)+"..");
        }else{

            viewInvoiceTab1Holder.username.setText(addInvoice.getCategoryname());
        }

        viewInvoiceTab1Holder.currency.setText(currencyname);
        viewInvoiceTab1Holder.price.setText(BigDecimal.valueOf(Double.parseDouble(addInvoice.getInvoicetotal())).setScale(2, RoundingMode.HALF_EVEN).toString()+"");
        viewInvoiceTab1Holder.refno.setText(addInvoice.getInvoicenuber());
        viewInvoiceTab1Holder.type.setText("");
        viewInvoiceTab1Holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(v.getContext(),recordInvoices.class)
                        .putExtra("status","1")
                        .putExtra("id",id)
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
