package com.vaofim.boffin;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.List;

import Models.Account;
import Models.Currency;
import Models.Expences;
import Models.UserCurrency;
import Models.addincome;
import Stables.CurrentCurrency;
import Stables.CurrentUser;

public class viewExpences extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference account_reference;
    DatabaseReference usercurrency;
    DatabaseReference currencyreference;
    DatabaseReference expencereference;
    Spinner accountspinner;
    String currencyname= CurrentCurrency.get();
    RecyclerView recyclerView;
    ArrayList<Expences> arrayList;
    ViewExpenceAdapter viewPaymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expences);

        firebaseDatabase=FirebaseDatabase.getInstance();
        account_reference=firebaseDatabase.getReference("account");
        usercurrency=firebaseDatabase.getReference("usercurrency");
        currencyreference=firebaseDatabase.getReference("currency");
        expencereference=firebaseDatabase.getReference("expences");

        account_reference.keepSynced(true);
        usercurrency.keepSynced(true);
        currencyreference.keepSynced(true);
        expencereference.keepSynced(true);

        accountspinner = findViewById(R.id.viewexpencespinner);

        recyclerView=findViewById(R.id.viewexpencerecycleview);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getAccounts();
    }

    public void getAccounts(){
        account_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<String> namelist=new ArrayList <String> ();
                final ArrayList <String> idlist=new ArrayList <String> ();

                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                for(DataSnapshot data:iterable){
                    Account account=data.getValue(Account.class);
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        namelist.add(account.getAccountName());
                        idlist.add(account.getId());
                    }
                }

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(viewExpences.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                accountspinner.setAdapter(account_adapter);
                accountspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final String accountid=idlist.get(position);
                        arrayList=new ArrayList<>();
                        viewPaymentAdapter=new ViewExpenceAdapter(arrayList);
                        recyclerView.setAdapter(viewPaymentAdapter);
                        expencereference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                arrayList=new ArrayList<>();
                                viewPaymentAdapter=new ViewExpenceAdapter(arrayList);
                                recyclerView.setAdapter(viewPaymentAdapter);
                                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                for(DataSnapshot data:iterable){
                                    Expences account=data.getValue(Expences.class);
                                    if(account.getUser().equals(CurrentUser.user.getId()) && account.getAccountid().equals(accountid)){
                                        arrayList.add(account);
                                        viewPaymentAdapter=new ViewExpenceAdapter(arrayList);
                                        recyclerView.setAdapter(viewPaymentAdapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void goToHome(View view){
//        startActivity(new Intent(getApplicationContext(),home.class));
        onBackPressed();
    }

    class ViewExpenceAdapter extends RecyclerView.Adapter<ViewExpenceAdapter.ViewExpenceHolder> {

        private List<Expences> list;

        public ViewExpenceAdapter(List<Expences> list) {
            this.list = list;
        }

        class ViewExpenceHolder extends RecyclerView.ViewHolder{
            TextView date,name1,currency,price,refno,type,username;
            ConstraintLayout constraintLayout;

            public ViewExpenceHolder(@NonNull View itemView) {
                super(itemView);

                date=itemView.findViewById(R.id.singlerecordview_date);
                name1=itemView.findViewById(R.id.singlerecordview_name);
                currency=itemView.findViewById(R.id.singlerecordview_currency);
                price=itemView.findViewById(R.id.singlerecordview_price);
                refno=itemView.findViewById(R.id.singlerecordview_refnumber);
                type=itemView.findViewById(R.id.singlerecordview_type);
                username=itemView.findViewById(R.id.singlerecord_username);
                constraintLayout=itemView.findViewById(R.id.singlerecordviewconstraintlayout);
            }
        }

        @NonNull
        @Override
        public ViewExpenceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlerecordview, viewGroup, false);
            return new ViewExpenceAdapter.ViewExpenceHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewExpenceHolder ViewExpenceHolder, int i) {
            final Expences expences=list.get(i);
            final String id=expences.getId();
            try {
                ViewExpenceHolder.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(expences.getDate1())));
            }catch (Exception e){}


            if(expences.getSupplierName()!=null && !expences.getSupplierName().isEmpty()){
                if(expences.getSupplierName().length()>20){
                    ViewExpenceHolder.name1.setText(expences.getSupplierName().substring(0,20)+"..");
                }else{
                    ViewExpenceHolder.name1.setText(expences.getSupplierName());
                }
            }
            if(expences.getCategoryname().length()>25){
                ViewExpenceHolder.username.setText(expences.getCategoryname().substring(0,25)+"..");
            }else{

                ViewExpenceHolder.username.setText(expences.getCategoryname());
            }
            ViewExpenceHolder.currency.setText(currencyname);

            ViewExpenceHolder.price.setText(BigDecimal.valueOf(Double.parseDouble(expences.getInvoicetotal())).setScale(2, RoundingMode.HALF_EVEN).toString()+"");
            ViewExpenceHolder.refno.setText(expences.getInternalref());
            ViewExpenceHolder.type.setText("");

//            ViewExpenceHolder.type.setText("Expence");
            ViewExpenceHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(),addPayments.class)
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
}
