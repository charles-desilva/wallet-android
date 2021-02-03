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
import com.google.firebase.storage.FirebaseStorage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import Models.Account;
import Models.Currency;
import Models.UserCurrency;
import Models.addincome;
import Stables.CurrentCurrency;
import Stables.CurrentUser;
import Stables.ValidateDate;

public class viewPayment extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference account_reference;
    DatabaseReference usercurrency;
    DatabaseReference currencyreference;
    DatabaseReference addincomereference;
    Spinner accountspinner;
    String currencyname= CurrentCurrency.get();
    RecyclerView recyclerView;
    ArrayList <addincome> arrayList;
    ViewPaymentAdapter viewPaymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payment);

        firebaseDatabase=FirebaseDatabase.getInstance();
        account_reference=firebaseDatabase.getReference("account");
        usercurrency=firebaseDatabase.getReference("usercurrency");
        currencyreference=firebaseDatabase.getReference("currency");
        addincomereference=firebaseDatabase.getReference("recipts");


        account_reference.keepSynced(true);
        usercurrency.keepSynced(true);
        currencyreference.keepSynced(true);
        addincomereference.keepSynced(true);

        accountspinner = findViewById(R.id.viewpaymentspinner);

        recyclerView=findViewById(R.id.viewpaymentrecycleview);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getAccounts();

    }

    public void getAccounts(){
        account_reference.addValueEventListener(new ValueEventListener() {
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

                ArrayAdapter<String> account_adapter = new ArrayAdapter<String>(viewPayment.this,R.layout.spinner_dropdown_design, namelist);
                account_adapter.setDropDownViewResource(R.layout.spinner_field);
                accountspinner.setAdapter(account_adapter);
                accountspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final String accountid=idlist.get(position);
                        arrayList=new ArrayList<>();
                        viewPaymentAdapter=new ViewPaymentAdapter(arrayList);
                        recyclerView.setAdapter(viewPaymentAdapter);
                        addincomereference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                arrayList=new ArrayList<>();
                                viewPaymentAdapter=new ViewPaymentAdapter(arrayList);
                                recyclerView.setAdapter(viewPaymentAdapter);
                                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                                for(DataSnapshot data:iterable){
                                    addincome account=data.getValue(addincome.class);
                                    if(account.getUser().equals(CurrentUser.user.getId()) && account.getAccountid().equals(accountid)){
                                        arrayList.add(account);
                                        viewPaymentAdapter=new ViewPaymentAdapter(arrayList);
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

    class ViewPaymentAdapter extends RecyclerView.Adapter<ViewPaymentAdapter.ViewPaymentHolder> {

        private List<addincome> list;

        public ViewPaymentAdapter(List<addincome> list) {
            this.list = list;
        }

        class ViewPaymentHolder extends RecyclerView.ViewHolder{
            TextView date,name1,currency,price,refno,type,username;
            ConstraintLayout constraintLayout;

            public ViewPaymentHolder(@NonNull View itemView) {
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
        public ViewPaymentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_singlerecordview, viewGroup, false);
            return new ViewPaymentAdapter.ViewPaymentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewPaymentHolder ViewPaymentHolder, int i) {
            final addincome addincome=list.get(i);
            final String id=addincome.getId();
            ViewPaymentHolder.date.setText(addincome.getDate());
            if(addincome.getCustomername().length()>25){
                ViewPaymentHolder.username.setText(addincome.getCustomername().substring(0,25)+"..");
            }else{
                ViewPaymentHolder.username.setText(addincome.getCustomername());
            }

            if(addincome.getCategoryname().length()>10){
                ViewPaymentHolder.name1.setText(addincome.getCategoryname().substring(0,10)+"..");
            }else{

                ViewPaymentHolder.name1.setText(addincome.getCategoryname());
            }
//            ViewPaymentHolder.name1.setText(addincome.getCategoryname());
            ViewPaymentHolder.currency.setText(currencyname);
            ViewPaymentHolder.price.setText(BigDecimal.valueOf(Double.parseDouble(addincome.getTotlareceived())).setScale(2, RoundingMode.HALF_EVEN).toString()+"");
            ViewPaymentHolder.refno.setText(addincome.getInternalrefno());


            account_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();
                    for(DataSnapshot data:iterable){
                        Account account=data.getValue(Account.class);
                        if(account.getId().equals(addincome.getAccountid())){
                            ViewPaymentHolder.type.setText(account.getShortCode());
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            ViewPaymentHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(),add_income.class)
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
