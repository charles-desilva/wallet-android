package com.vaofim.boffin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Models.Expences;
import Models.ReportTable;
import Models.addincome;
import Models.incomeCategory;
import Stables.CurrentUser;

public class report1 extends AppCompatActivity {

    WebView webView;
    String accountid,accountname;
    String categoryid,categoryname;
    String startdate,enddate;
    String entryname,entrynumber;
    String reporttitleid,reporttitlename;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference account_reference;
    DatabaseReference expencescategory;

    String designcss="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report1);

        accountid=getIntent().getStringExtra("accountid");
        accountname=getIntent().getStringExtra("accountname");
        categoryid=getIntent().getStringExtra("categoryid");
        categoryname=getIntent().getStringExtra("categoryname");
        startdate=getIntent().getStringExtra("startdate");
        enddate=getIntent().getStringExtra("enddate");
//        entryid=getIntent().getStringExtra("entityid");
        entryname=getIntent().getStringExtra("entityname");
        entrynumber=getIntent().getStringExtra("entrynumber");
        reporttitlename=getIntent().getStringExtra("reporttitlename");
        reporttitleid=getIntent().getStringExtra("reporttitleid")+"";

        if(startdate.isEmpty()){
            startdate="-";
        }

        if(enddate.isEmpty()){
            enddate="-";
        }

        firebaseDatabase=FirebaseDatabase.getInstance();

        expencescategory=firebaseDatabase.getReference("expences_category");
        expencescategory.keepSynced(true);

        webView=findViewById(R.id.report1webview);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());

        doProcess();
    }

    private void doWebView(String content) {
        webView.loadData(content, "text/html", null);
    }

    private class WebViewClient extends android.webkit.WebViewClient
    {

        public final ProgressDialog dialog = ProgressDialog.show(report1.this, "Processing",
                "Please wait till the report calculation ending.", true);


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            dialog.dismiss();
        }
    }

    public void goToHome(View view){
        onBackPressed();
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + " Boffin Report ";
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }
    public void printPDF(View view) {
        createWebPrintJob(webView);
    }

    private void doProcess(){
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                "Please wait till the report calculation ending.", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String firstcontent="<html>\n" +
                "\n" +
                "<body style=\"background-color: white\">\n" +
                "\n" +
                "    <div style=\"padding: 5%;\">\n" +
                "        <div>\n" +
                "\n" +
                "            <h3 style=\"text-align: center; color:#1E88E5\">Boffin "+reporttitlename+" Report</h3>\n" +
                "            <br>\n" +
                "            <table style=\"width: 100%\">\n" +
                "                <tr>\n" +
                "                    <td><strong>Run Date : &nbsp;</strong>"+new SimpleDateFormat("dd/MM/yyyy").format(new Date())+"</td>\n" +
                "                    <td style=\"float: right;white-space: nowrap;\"><strong>Run Time : &nbsp;</strong>"+new SimpleDateFormat("hh:mm a").format(new Date())+"</td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "            <hr>\n" +
                "            <p><b>VAT Registered Name :</b> &nbsp;"+entryname+"</p>\n" +
                "            <p><b>VAT Registered Number :</b> &nbsp;"+entrynumber+"</p>\n" +
                "            <p><b>Account :</b> &nbsp;"+accountname+"</p>\n" +
                "            <p><b>Category :</b> &nbsp;"+categoryname+"</p>\n" +
                "            <p><b>Start Period :</b> &nbsp;"+startdate+"</p>\n" +
                "            <p><b>End Period :</b> &nbsp;"+enddate+"</p>\n" +
                "\n" +
                "        </div>\n" +
                "\n" +
                "        <div>\n" +
                "\n" +
                "            <div style=\"width: 100%;\">\n" +
                "\n" +
                "                <table style=\"width: 100%; border: 0px solid lightgrey ; padding: 0%;\">\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td></td>\n" +
                "                        <td style=\"text-align: right;width: 10%;\">£</td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td colspan=\"2\">\n" +
                "                            <hr>\n" +
                "                        </td>\n" +
                "                    </tr>\n";

        if(reporttitlename.equals("Outflow")){
            DatabaseReference databaseReference=firebaseDatabase.getReference("expences");
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                        double total=0.0;
                        Map<String, Expences> map=new HashMap();
                        Map<String, Expences> subcategorymap=new HashMap();
                        for(DataSnapshot data:iterator){
                            Expences expense=data.getValue(Expences.class);
                            if(categoryid.equals("all")){
                                if(expense.getUser().equals(CurrentUser.user.getId()) && expense.getAccountid().equals(accountid)){
                                    if(startdate!=null && !startdate.equals("-") && enddate!=null && !enddate.equals("-")){

                                        if(isWithinRange(expense.getDate1())){
                                            if(map.size()==0){
                                                map.put(expense.getCategoryid(),expense);
                                            }else{
                                                Iterator it = map.entrySet().iterator();
                                                boolean b=true;
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry)it.next();
                                                    if(pair.getKey().toString().equals(expense.getCategoryid())){
                                                        Expences tmpexpences= (Expences) pair.getValue();
                                                        Double b1=Double.parseDouble(tmpexpences.getInvoicetotal());
                                                        Double b2=Double.parseDouble(expense.getInvoicetotal());
                                                        tmpexpences.setInvoicenet(b1+b2+"");
                                                        map.put(pair.getKey().toString(),tmpexpences);
                                                        b=false;
                                                    }
                                                }

                                                if(b){
                                                    map.put(expense.getCategoryid().toString(),expense);
                                                }
                                            }
                                        }
                                    }else{
                                        if(map.size()==0){
                                            map.put(expense.getCategoryid(),expense);
                                        }else{
                                            Iterator it = map.entrySet().iterator();
                                            boolean b=true;
                                            while (it.hasNext()) {
                                                Map.Entry pair = (Map.Entry)it.next();
                                                if(pair.getKey().toString().equals(expense.getCategoryid())){
                                                    Expences tmpexpences= (Expences) pair.getValue();
                                                    Double b1=Double.parseDouble(tmpexpences.getInvoicetotal());
                                                    Double b2=Double.parseDouble(expense.getInvoicetotal());
                                                    tmpexpences.setInvoicenet(b1+b2+"");
                                                    map.put(pair.getKey().toString(),tmpexpences);
                                                    b=false;
                                                }
                                            }

                                            if(b){
                                                map.put(expense.getCategoryid().toString(),expense);
                                            }
                                        }
                                    }


                                }
                            }else{
                                if(expense.getUser().equals(CurrentUser.user.getId()) && expense.getAccountid().equals(accountid) && expense.getCategoryid().equals(categoryid)){
                                    if(startdate!=null && !startdate.equals("-") && enddate!=null && !enddate.equals("-")){
                                        if(isWithinRange(expense.getDate1())){
                                            if(map.size()==0){
                                                map.put(expense.getCategoryid(),expense);
                                            }else{
                                                Iterator it = map.entrySet().iterator();
                                                boolean b=true;
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry)it.next();
                                                    if(pair.getKey().toString().equals(expense.getCategoryid())){
                                                        Expences tmpexpences= (Expences) pair.getValue();
                                                        Double b1=Double.parseDouble(tmpexpences.getInvoicetotal());
                                                        Double b2=Double.parseDouble(expense.getInvoicetotal());
                                                        tmpexpences.setInvoicenet(b1+b2+"");
                                                        map.put(pair.getKey().toString(),tmpexpences);
                                                        b=false;
                                                    }
                                                }

                                                if(b){
                                                    map.put(expense.getCategoryid().toString(),expense);
                                                }
                                            }
                                        }
                                    }else{
                                        if(map.size()==0){
                                            subcategorymap.put(expense.getSubcategoryid(),expense);
                                        }else{
                                            Iterator it = subcategorymap.entrySet().iterator();
                                            boolean b=true;
                                            while (it.hasNext()) {
                                                Map.Entry pair = (Map.Entry)it.next();
                                                if(pair.getKey().toString().equals(expense.getSubcategoryid())){
                                                    Expences tmpexpences= (Expences) pair.getValue();
                                                    Double b1=Double.parseDouble(tmpexpences.getInvoicetotal());
                                                    Double b2=Double.parseDouble(expense.getInvoicetotal());
                                                    tmpexpences.setInvoicenet(b1+b2+"");
                                                    subcategorymap.put(pair.getKey().toString(),tmpexpences);
                                                    b=false;
                                                }
                                            }

                                            if(b){
                                                subcategorymap.put(expense.getSubcategoryid().toString(),expense);
                                            }
                                        }

                                        map=subcategorymap;
                                    }







                                }
                            }
                        }

                        String loopcontent="";

                        if(map.size()>0){
                            Iterator it = map.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                Expences expense= (Expences) pair.getValue();

                                total+=new BigDecimal(Double.parseDouble(expense.getInvoicetotal())).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

                                if(categoryid.equals("all")){
                                    loopcontent+="\n" +
                                            "                    <tr>\n" +
                                            "                        <td>"+expense.getCategoryname()+"</td>\n" +
                                            "                        <td style=\"width: 10% ; text-align: right\">"+new BigDecimal(Double.parseDouble(expense.getInvoicetotal())).setScale(2, RoundingMode.HALF_EVEN).toString()+"</td>\n" +
                                            "                    </tr>\n";
                                }else{
                                    loopcontent+="\n" +
                                            "                    <tr>\n" +
                                            "                        <td>"+expense.getSubcategoryname()+"</td>\n" +
                                            "                        <td style=\"width: 10% ; text-align: right\">"+new BigDecimal(Double.parseDouble(expense.getInvoicetotal())).setScale(2, RoundingMode.HALF_EVEN).toString()+"</td>\n" +
                                            "                    </tr>\n";
                                }

                            }
                        }else{
                            final AlertDialog.Builder builder = new AlertDialog.Builder(report1.this);
                            builder.setCancelable(true);
                            builder.setTitle("Oops!");
                            builder.setMessage("No record found according to the details.");
                            builder.setIcon(R.drawable.ic_warning_black_24dp);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goBack();
                                }
                            });
                            builder.show();
                        }
                        String secondcontent="\n" +
                                "                    <tr>\n" +
                                "                        <td></td>\n" +
                                "                        <td><br></td>\n" +
                                "                    </tr>\n" +
                                "\n" +
                                "                    <tr>\n" +
                                "                        <td>Total "+reporttitlename+"</td>\n" +
                                "                        <td style=\"border-bottom: 2px solid darkgray ; border-top:1px solid darkgrey; text-align: right\">\n" +
                                "                            "+new BigDecimal(Double.parseDouble(total+"")).setScale(2, RoundingMode.HALF_EVEN).toString()+"\n" +
                                "                        </td>\n" +
                                "                    </tr>\n" +
                                "\n" +
                                "                </table>\n" +
                                "\n" +
                                "            </div>\n" +
                                "        </div>\n" +
                                "    </div>\n" +
                                "\n" +
                                "    <footer>\n" +
                                "        <div style=\"text-align: center\">\n" +
                                "\n" +
                                "            <p style=\"color: #1E88E5\">Powered by Boffin - "+new SimpleDateFormat("yyyy").format(new Date())+" © All Right Reserved</p>\n" +
                                "\n" +
                                "        </div>\n" +
                                "    </footer>\n" +
                                "\n" +
                                "</body>\n" +
                                "\n" +
                                "</html>";



                        doWebView(firstcontent+loopcontent+secondcontent);

                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            //Inflow -------------------------------------------------------------------------------------------------------
        }else if(reporttitlename.equals("Inflow")){
            DatabaseReference databaseReference=firebaseDatabase.getReference("recipts");
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                        double total=0.0;
                        Map<String,addincome> map=new HashMap();
                        for(DataSnapshot data:iterator){
                            addincome expense=data.getValue(addincome.class);
                            if(categoryid.equals("all")){
                                if(expense.getUser().equals(CurrentUser.user.getId()) && expense.getAccountid().equals(accountid)){
                                    if(startdate!=null && !startdate.equals("-") && enddate!=null && !enddate.equals("-")){
                                        if(isWithinRange(expense.getDate())){
                                            if(map.size()==0){
                                                map.put(expense.getCategoryid(),expense);
                                            }else{
                                                Iterator it = map.entrySet().iterator();
                                                boolean b=true;
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry)it.next();
                                                    if(pair.getKey().toString().equals(expense.getCategoryid())){
                                                        addincome tmpexpences= (addincome) pair.getValue();
                                                        Double b1=Double.parseDouble(tmpexpences.getTotlareceived());
                                                        Double b2=Double.parseDouble(expense.getTotlareceived());
                                                        tmpexpences.setTotlareceived(b1+b2+"");
                                                        map.put(pair.getKey().toString(),tmpexpences);
                                                        b=false;
                                                    }
                                                }

                                                if(b){
                                                    map.put(expense.getCategoryid().toString(),expense);
                                                }
                                            }
                                        }
                                    }else{
                                        if(map.size()==0){
                                            map.put(expense.getCategoryid(),expense);
                                        }else{
                                            Iterator it = map.entrySet().iterator();
                                            boolean b=true;
                                            while (it.hasNext()) {
                                                Map.Entry pair = (Map.Entry)it.next();
                                                if(pair.getKey().toString().equals(expense.getCategoryid())){
                                                    addincome tmpexpences= (addincome) pair.getValue();
                                                    Double b1=Double.parseDouble(tmpexpences.getTotlareceived());
                                                    Double b2=Double.parseDouble(expense.getTotlareceived());
                                                    tmpexpences.setTotlareceived(b1+b2+"");
                                                    map.put(pair.getKey().toString(),tmpexpences);
                                                    b=false;
                                                }
                                            }

                                            if(b){
                                                map.put(expense.getCategoryid().toString(),expense);
                                            }
                                        }
                                    }


                                }
                            }else{
                                if(expense.getCategoryid().equals(categoryid) && expense.getUser().equals(CurrentUser.user.getId()) && expense.getAccountid().equals(accountid)){
                                    System.out.println("Sub category Income Recipt");
                                    if(startdate!=null && !startdate.equals("-") && enddate!=null && !enddate.equals("-")){
                                        if(isWithinRange(expense.getDate())){
                                            if(map.size()==0){
                                                map.put(expense.getSubcategoryid(),expense);
                                            }else{
                                                Iterator it = map.entrySet().iterator();
                                                boolean b=true;
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry)it.next();
                                                    if(pair.getKey().toString().equals(expense.getSubcategoryid())){
                                                        addincome tmpexpences= (addincome) pair.getValue();
                                                        Double b1=Double.parseDouble(tmpexpences.getTotlareceived());
                                                        Double b2=Double.parseDouble(expense.getTotlareceived());
                                                        tmpexpences.setTotlareceived(b1+b2+"");
                                                        map.put(pair.getKey().toString(),tmpexpences);
                                                        b=false;
                                                    }
                                                }

                                                if(b){
                                                    map.put(expense.getSubcategoryid().toString(),expense);
                                                }
                                            }
                                        }
                                    }else{
                                        if(map.size()==0){
                                            map.put(expense.getSubcategoryid(),expense);
                                        }else{
                                            Iterator it = map.entrySet().iterator();
                                            boolean b=true;
                                            while (it.hasNext()) {
                                                Map.Entry pair = (Map.Entry)it.next();
                                                if(pair.getKey().toString().equals(expense.getSubcategoryid())){
                                                    addincome tmpexpences= (addincome) pair.getValue();
                                                    Double b1=Double.parseDouble(tmpexpences.getTotlareceived());
                                                    Double b2=Double.parseDouble(expense.getTotlareceived());
                                                    tmpexpences.setTotlareceived(b1+b2+"");
                                                    map.put(pair.getKey().toString(),tmpexpences);
                                                    b=false;
                                                }
                                            }

                                            if(b){
                                                map.put(expense.getSubcategoryid().toString(),expense);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        String loopcontent="";

                        if(map.size()>0){
                            Iterator it = map.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                addincome expense= (addincome) pair.getValue();

                                total+=new BigDecimal(Double.parseDouble(expense.getTotlareceived())).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

                                if(categoryid.equals("all")){
                                    loopcontent+="\n" +
                                            "                    <tr>\n" +
                                            "                        <td>"+expense.getCategoryname()+"</td>\n" +
                                            "                        <td style=\"width: 10% ; text-align: right\">"+new BigDecimal(Double.parseDouble(expense.getTotlareceived())).setScale(2, RoundingMode.HALF_EVEN).toString()+"</td>\n" +
                                            "                    </tr>\n";
                                }else{
                                    loopcontent+="\n" +
                                            "                    <tr>\n" +
                                            "                        <td>"+expense.getSubcategoryname()+"</td>\n" +
                                            "                        <td style=\"width: 10% ; text-align: right\">"+new BigDecimal(Double.parseDouble(expense.getTotlareceived())).setScale(2, RoundingMode.HALF_EVEN).toString()+"</td>\n" +
                                            "                    </tr>\n";
                                }

                            }
                        }else{
                            final AlertDialog.Builder builder = new AlertDialog.Builder(report1.this);
                            builder.setCancelable(true);
                            builder.setTitle("Oops!");
                            builder.setMessage("No record found according to the details.");
                            builder.setIcon(R.drawable.ic_warning_black_24dp);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Close",new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goBack();
                                }
                            });
                            builder.show();
                        }



                        String secondcontent="\n" +
                                "                    <tr>\n" +
                                "                        <td></td>\n" +
                                "                        <td><br></td>\n" +
                                "                    </tr>\n" +
                                "\n" +
                                "                    <tr>\n" +
                                "                        <td>Total "+reporttitlename+"</td>\n" +
                                "                        <td style=\"border-bottom: 2px solid darkgray ; border-top:1px solid darkgrey; text-align: right\">\n" +
                                "                            "+new BigDecimal(Double.parseDouble(total+"")).setScale(2, RoundingMode.HALF_EVEN).toString()+"\n" +
                                "                        </td>\n" +
                                "                    </tr>\n" +
                                "\n" +
                                "                </table>\n" +
                                "\n" +
                                "            </div>\n" +
                                "        </div>\n" +
                                "    </div>\n" +
                                "\n" +
                                "    <footer>\n" +
                                "        <div style=\"text-align: center\">\n" +
                                "\n" +
                                "            <p style=\"color: #1E88E5\">Powered by Boffin - "+new SimpleDateFormat("yyyy").format(new Date())+" © All Right Reserved</p>\n" +
                                "\n" +
                                "        </div>\n" +
                                "    </footer>\n" +
                                "\n" +
                                "</body>\n" +
                                "\n" +
                                "</html>";



                        doWebView(firstcontent+loopcontent+secondcontent);
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    boolean isWithinRange(String date) {
        boolean b=false;
        try {
            Date checkDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
            Date startDate=new SimpleDateFormat("dd/MM/yyyy").parse(startdate);
            Date endDate=new SimpleDateFormat("dd/MM/yyyy").parse(enddate);

            if(checkDate.getTime()>=startDate.getTime() && checkDate.getTime()<=endDate.getTime()){
                b=true;
            }
        }catch(Exception e){

        }
        return b;
    }

    public void goBack(){
        onBackPressed();
    }
}
