package Process_Classes;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Models.Customer;
import Models.expenceCategory;
import Models.incomeCategory;
import Models.incomeSubCategory;
import Models.invoiceCategory;
import Stables.CurrentUser;

public class CheckAvailability {

    FirebaseDatabase databaseReference= FirebaseDatabase.getInstance();
    DatabaseReference incomeecatref=databaseReference.getReference("income_category");
    DatabaseReference incomeesubcatref=databaseReference.getReference("income_subcategory");

    boolean check=true;

        public boolean expenceCategoryCheck(ArrayList <expenceCategory> arrayList, String check){
            boolean b=true;
            for(expenceCategory expenceCategory:arrayList){
                if(expenceCategory.getCategory().toLowerCase().trim().equals(check.toLowerCase().trim())){
                    b=false;
                    break;
                }
            }
            return b;
        }

        public boolean incomeCategoryCheck(ArrayList <incomeCategory> arrayList,String check){
            boolean b=true;
            for(incomeCategory incomeCategory:arrayList){
                if(incomeCategory.getCategory().toLowerCase().trim().equals(check.toLowerCase().trim())){
                    b=false;
                    break;
                }
            }
            return b;
        }

    public boolean invoiceCategoryCheck(ArrayList <invoiceCategory> arrayList, String check){
        boolean b=true;
        for(invoiceCategory invoiceCategory:arrayList){
            if(invoiceCategory.getCategory().toLowerCase().trim().equals(check.toLowerCase().trim())){
                b=false;
                break;
            }
        }
        return b;
    }

    public boolean checkCustomerAvailability(ArrayList <Customer> arrayList,String check){
        boolean b=true;
        for(Customer customer:arrayList){
            if(customer.getName().toLowerCase().trim().equals(check.toLowerCase().trim())){
                b=false;
                break;
            }
        }
        return b;
    }
}
