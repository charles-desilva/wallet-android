package com.vaofim.boffin;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

public class ViewFundsInflowRecords extends AppCompatActivity {

    TextView date1,date2;
    int datechooserid=2;
    CardView daterangercardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_funds_inflow_records);

        date1=findViewById(R.id.fundsinflowdate1);
        date2=findViewById(R.id.fundsinflowdate2);
        daterangercardview=findViewById(R.id.inflawrecorddaterangercardview);
    }

    public void goToHome(View view){
        onBackPressed();
    }

    public void datePick01(View view){

        Calendar calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int currentmont=++month;
                date1.setText(dayOfMonth+"/"+currentmont+"/"+year);
            }
        }, Year, Month, Day).show();

    }

    public void datePick02(View view){

        Calendar calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int currentmont=++month;
                date2.setText(dayOfMonth+"/"+currentmont+"/"+year);
            }
        }, Year, Month, Day).show();

    }

    public void showDateRanger(View view){
        if(datechooserid==1){
            daterangercardview.setVisibility(View.GONE);
            datechooserid=2;
        }else{
            daterangercardview.setVisibility(View.VISIBLE);
            datechooserid=1;
        }
    }


}
