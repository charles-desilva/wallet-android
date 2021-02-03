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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Models.Account;
import Models.Customer;
import Models.Expences;
import Models.addincome;
import Stables.CurrentUser;

public class createaccount extends AppCompatActivity {

    public EditText createaccounttaccountname,createaccountshortcode;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference accountreference;
    DatabaseReference expencereference,reciptsreference;
    public Button editbtn;
    String existid;
    int statusid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        firebaseDatabase=FirebaseDatabase.getInstance();

        expencereference=firebaseDatabase.getReference("expences");
        expencereference.keepSynced(true);

        reciptsreference=firebaseDatabase.getReference("recipts");
        reciptsreference.keepSynced(true);

        createaccounttaccountname=findViewById(R.id.createaccounttaccountname);
        createaccountshortcode=findViewById(R.id.createaccountshortcode);
        editbtn=findViewById(R.id.createaccountedit);

        accountreference=firebaseDatabase.getReference("account");
        accountreference.keepSynced(true);
        accountreference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList <Account> accountlists=new ArrayList();
                Iterable <DataSnapshot> datalist=dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1:datalist){
                    Account account=dataSnapshot1.getValue(Account.class);
                    if(account.getUser().equals(CurrentUser.user.getId())){
                        accountlists.add(account);
                    }
                }

                Collections.sort(accountlists,new Comparator<Account>()
                {
                    public int compare(Account f1, Account f2)
                    {
                        return f1.getAccountName().toLowerCase().compareTo(f2.getAccountName().toLowerCase());
                    }
                });

                CreateAccountAdapter createAccountAdapter=new CreateAccountAdapter(accountlists);
                RecyclerView recyclerView=findViewById(R.id.createaccountview);
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(createAccountAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToHome(View view){
//        startActivity(new Intent(this,home.class));
        onBackPressed();
    }

    public void doSave(View view){
        if(!createaccounttaccountname.getText().toString().isEmpty() && !createaccountshortcode.getText().toString().isEmpty()){
            String key=accountreference.push().getKey();

            accountreference.child(key).setValue(new Account(key,createaccounttaccountname.getText().toString(),createaccountshortcode.getText().toString(),2, CurrentUser.user.getId()));

            Toast.makeText(this, "Record Saved", Toast.LENGTH_SHORT).show();

            clearFields(view);

        }else{
            Toast.makeText(this, "Please Fill Fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearFields(View view){
        createaccounttaccountname.setText("");
        createaccountshortcode.setText("");
    }

    class CreateAccountAdapter extends RecyclerView.Adapter<CreateAccountAdapter.AccountViewHolder>{
        private List<Account> accountlist;
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference accountreference;
        boolean check1=true;
        boolean check2=true;


        public class AccountViewHolder extends RecyclerView.ViewHolder{
            public TextView id,name;
            ImageButton deletebtn,editbtn;


            public AccountViewHolder(View view){
                super(view);

                id=view.findViewById(R.id.singlecontentid);
                name=view.findViewById(R.id.singlecontentdisplay);
                deletebtn=view.findViewById(R.id.singlecontentdelete);
                editbtn=view.findViewById(R.id.singlecontentedit);
            }
        }

        public CreateAccountAdapter(List<Account> list){
            this.accountlist=list;
        }

        @NonNull
        @Override
        public CreateAccountAdapter.AccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_singlecontent, viewGroup, false);

            return new CreateAccountAdapter.AccountViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CreateAccountAdapter.AccountViewHolder accountViewHolder, int i) {
            final Account account=accountlist.get(i);
            accountViewHolder.id.setText(account.getId());
            accountViewHolder.name.setText(account.getAccountName());

            accountreference=firebaseDatabase.getReference("account");

            accountViewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(createaccount.this);
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
                            accountreference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Iterable <DataSnapshot> datalist=dataSnapshot.getChildren();
                                    for(DataSnapshot child:datalist){
                                        Account account1=child.getValue(Account.class);
                                        if(account1.getId().equals(account.getId())){
                                            if(account1.getStatus()==2){
                                                checkExpence(account1.getId());
                                            }else{
                                                Toast.makeText(createaccount.this, "Inbuilt Recodes Cannot Be Delete", Toast.LENGTH_SHORT).show();
                                            }

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

            accountViewHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    createaccounttaccountname.setText(account.getAccountName());
                    createaccountshortcode.setText(account.getShortCode());
                    existid=account.getId();
                    statusid=account.getStatus();

                }
            });
        }




        public void checkExpence(final String accid){
            final String accounttid=accid;
            expencereference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                    for(DataSnapshot snapshot:dataSnapshots){
                        Expences expences=snapshot.getValue(Expences.class);

                        if(expences.getAccountid().equals(accounttid) && expences.getUser().equals(CurrentUser.user.getId())){
                            check1=false;
                            break;
                        }
                    }

                    System.out.println(check1);

                    checkIncome(accid);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void checkIncome(final String accid){
            final String accounttid=accid;
            reciptsreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                    for(DataSnapshot snapshot:dataSnapshots){
                        addincome addincome=snapshot.getValue(addincome.class);

                        if(addincome.getAccountid().equals(accounttid) && addincome.getUser().equals(CurrentUser.user.getId())){
                            check2=false;
                            break;
                        }
                    }

                    System.out.println(check2);

                    if(check1 && check2){
                        accountreference.child(accid).removeValue();
                        Toast.makeText(createaccount.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(createaccount.this, "This record cannot be delete because of existing record..", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        @Override
        public int getItemCount() {
            return accountlist.size();
        }
    }

    public void doEditNow(View view){
        if(!createaccounttaccountname.getText().toString().isEmpty() && !createaccountshortcode.getText().toString().isEmpty() && existid!=null){
            accountreference.child(existid).setValue(new Account(existid,createaccounttaccountname.getText().toString(),createaccountshortcode.getText().toString(),statusid,CurrentUser.user.getId()));
            Toast.makeText(createaccount.this, "Updated", Toast.LENGTH_SHORT).show();
            createaccounttaccountname.setText("");
            createaccountshortcode.setText("");
        }else{
            Toast.makeText(createaccount.this, "Please Fill Data For Update", Toast.LENGTH_SHORT).show();
        }
    }
}
