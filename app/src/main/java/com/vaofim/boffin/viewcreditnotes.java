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
import Models.recordCreditNote;
import Stables.CurrentCurrency;
import Stables.CurrentUser;

public class viewcreditnotes extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference currencyreference;
    DatabaseReference usercurrency;
    DatabaseReference creditreference;


    public String currencyname= CurrentCurrency.get();
    RecyclerView recyclerView;
    ArrayList<recordCreditNote> arrayList;

    ViewCreditNotesAdapter viewCreditNotesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcreditnotes);

        firebaseDatabase=FirebaseDatabase.getInstance();
        currencyreference=firebaseDatabase.getReference("currency");
        usercurrency=firebaseDatabase.getReference("usercurrency");
        creditreference=firebaseDatabase.getReference("recordcredit");

        currencyreference.keepSynced(true);
        usercurrency.keepSynced(true);
        creditreference.keepSynced(true);


        getCreditNotes();
    }


    public void getCreditNotes(){

        recyclerView=findViewById(R.id.viewcreditnotesrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        creditreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    arrayList=new ArrayList<>();
                    Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterator){
                        final recordCreditNote customerobject=data.getValue(recordCreditNote.class);
                        if(customerobject.getUser().equals(CurrentUser.user.getId())){
                            arrayList.add(customerobject);
                        }
                    }

                    Collections.sort(arrayList, new Comparator<recordCreditNote>() {
                        @Override
                        public int compare(recordCreditNote o1, recordCreditNote o2) {
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


                    viewCreditNotesAdapter=new ViewCreditNotesAdapter(arrayList,currencyname,viewcreditnotes.this);
                    recyclerView.setAdapter(viewCreditNotesAdapter);
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

class ViewCreditNotesAdapter extends RecyclerView.Adapter<ViewCreditNotesAdapter.ViewCreditNotesHolder> {

    private List<recordCreditNote> list;
    String currencyname;
    Context context;

    public ViewCreditNotesAdapter(List<recordCreditNote> list,String currentcy,Context context) {
        this.list = list;
        currencyname=currentcy;
        this.context=context;
    }

    class ViewCreditNotesHolder extends RecyclerView.ViewHolder{
        TextView date,name1,currency,price,refno,type;
        ConstraintLayout constraintLayout;

        public ViewCreditNotesHolder(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.singlerecordview_date);
            name1=itemView.findViewById(R.id.singlerecordview_name);
            currency=itemView.findViewById(R.id.singlerecordview_currency);
            price=itemView.findViewById(R.id.singlerecordview_price);
            refno=itemView.findViewById(R.id.singlerecordview_refnumber);
            type=itemView.findViewById(R.id.singlerecordview_type);
            constraintLayout=itemView.findViewById(R.id.singlerecordviewconstraintlayout);
        }
    }

    @NonNull
    @Override
    public ViewCreditNotesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlerecordview, viewGroup, false);
        return new ViewCreditNotesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewCreditNotesHolder ViewInvoiceTab2Holder, int i) {
        recordCreditNote recordCreditNote=list.get(i);
        final String id=recordCreditNote.getId();

        try {
            ViewInvoiceTab2Holder.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(recordCreditNote.getDate())));
        }catch (Exception e){}
        if(recordCreditNote.getCustomername().length()>25){
            ViewInvoiceTab2Holder.name1.setText(recordCreditNote.getCustomername().substring(0,25)+"..");
        }else{

            ViewInvoiceTab2Holder.name1.setText(recordCreditNote.getCustomername());
        }

//            ViewInvoiceTab2Holder.name1.setText(recordCreditNote.getCustomername());
        ViewInvoiceTab2Holder.currency.setText(currencyname);
        ViewInvoiceTab2Holder.price.setText(BigDecimal.valueOf(Double.parseDouble(recordCreditNote.getCreditnotenet())).setScale(2, RoundingMode.HALF_EVEN).toString()+"");
        ViewInvoiceTab2Holder.refno.setText(recordCreditNote.getCreditnotenumber());
//        ViewInvoiceTab2Holder.type.setText("Credit Note");
        ViewInvoiceTab2Holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(v.getContext(),recordCreditNotes.class)
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