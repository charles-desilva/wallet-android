package com.vaofim.boffin;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Models.Expences;
import Models.ReportTable;
import Models.addincome;
import Models.incomeCategory;
import Process_Classes.FileUtils;
import Stables.CurrentUser;

public class z_income_report_pdf extends AppCompatActivity {

    String accountid,accountname;
    String categoryid,categoryname;
    String startdate,enddate;
    String title;
    String entryid,entryname;
    String reporttitleid,reporttitlename;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference account_reference;
    DatabaseReference expencescategory;

    double totalall=0.0;
    RecyclerView recyclerView;
    ReportAdapter reportAdapter;
    ArrayList <ReportTable> reportTabarray;
    ScrollView scrollView;

    JSONArray allinflowjsonarray=new JSONArray();

    private ConstraintLayout layout;
    private Bitmap bitmap;

    public static Bitmap bitScroll;

    TextView zincomereportpdfrundate,zincomereportpdfruntime,zincomereportpdfentityname,zincomereportpdfaccounttype,zincomereportpdfcategory,zincomereportpdfstartperiod,zincomereportpdfendperiod,zincomereportpdfreporttitle,zincomereportpdftotalname,zincomereportpdftotalprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_income_report_pdf);

        layout=findViewById(R.id.reportviewlayout1);
        scrollView=findViewById(R.id.reportscrollview1);

        recyclerView=findViewById(R.id.z_incomereport1_recyclerview);

        firebaseDatabase=FirebaseDatabase.getInstance();

        zincomereportpdfrundate=findViewById(R.id.zincomereportpdfrundate);
        zincomereportpdfruntime=findViewById(R.id.zincomereportpdfruntime);
        zincomereportpdfentityname=findViewById(R.id.zincomereportpdfentityname);
        zincomereportpdfaccounttype=findViewById(R.id.zincomereportpdfaccounttype);
        zincomereportpdfstartperiod=findViewById(R.id.zincomereportpdfstartperiod);
        zincomereportpdfendperiod=findViewById(R.id.zincomereportpdfendperiod);
        zincomereportpdfreporttitle=findViewById(R.id.zincomereportpdfreporttitle);
        zincomereportpdftotalname=findViewById(R.id.zincomereportpdftotalname);
        zincomereportpdftotalprice=findViewById(R.id.zincomereportpdftotalprice);

        accountid=getIntent().getStringExtra("accountid");
        accountname=getIntent().getStringExtra("accountname");
        categoryid=getIntent().getStringExtra("categoryid");
        categoryname=getIntent().getStringExtra("categoryname");
        title=getIntent().getStringExtra("title");
        startdate=getIntent().getStringExtra("startdate");
        enddate=getIntent().getStringExtra("enddate");
        entryid=getIntent().getStringExtra("entityid");
        entryname=getIntent().getStringExtra("entityname");
        reporttitlename=getIntent().getStringExtra("reporttitlename");
        reporttitleid=getIntent().getStringExtra("reporttitleid");

        recyclerView=findViewById(R.id.z_incomereport1_recyclerview);

        zincomereportpdfaccounttype.setText(accountname);
        account_reference=firebaseDatabase.getReference("expences");
        account_reference.keepSynced(true);
        expencescategory=firebaseDatabase.getReference("expences_category");
        expencescategory.keepSynced(true);

        zincomereportpdfrundate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        zincomereportpdfruntime.setText(new SimpleDateFormat("HH:mm:ss a").format(new Date()));

        zincomereportpdfaccounttype.setText(accountname);

        if(!startdate.equals("") || startdate!=null){
            zincomereportpdfstartperiod.setText(startdate);
        }else{
            zincomereportpdfstartperiod.setText("-");
        }


        if(!enddate.equals("") || enddate!=null){
            zincomereportpdfendperiod.setText(enddate);
        }else{
            zincomereportpdfendperiod.setText("-");
        }

        zincomereportpdfentityname.setText(entryname);

        if(!categoryid.equals("all")){
            zincomereportpdfreporttitle.setText("Funds Inflow ("+categoryname+")");
        }

        reportTabarray=new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        reportAdapter=new ReportAdapter(reportTabarray);
        recyclerView.setAdapter(reportAdapter);


        doRecyclerProcess();
    }





    public void goToHome(View view){
        onBackPressed();
    }

    public void saveAsPdf(View view){
        bitScroll = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
        saveBitmap(bitScroll);
//        layoutToImage();
    }


    String date;
    String dirpath;
    public void imageToPDF() throws FileNotFoundException {
        try {
            final String filename= "boffinreport"+new SimpleDateFormat("yymmddhhmmss").format(new Date())+System.currentTimeMillis();
            final ProgressDialog dialog = ProgressDialog.show(z_income_report_pdf.this, "Generating",
                    "Your requested file will be at "+"Storage/Boffin/"+File.separator+date, true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {

                        dirpath = android.os.Environment.getExternalStorageDirectory().toString();

                    try {
                        Image image=null;
                        try {
                            image = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator +"Boffin"+File.separator+date+File.separator+"JPG"+File.separator+ "imgboffin.jpg");
                        } catch (BadElementException bee) {
                        }
                        Rectangle A4 = PageSize.A4;

                        float scalePortrait = Math.min(A4.getWidth() / image.getWidth(),
                                A4.getHeight() / image.getHeight());

                        float scaleLandscape = Math.min(A4.getHeight() / image.getWidth(),
                                A4.getWidth() / image.getHeight());
                        boolean isLandscape = scaleLandscape > scalePortrait;

                        float w;
                        float h;
                        if (isLandscape) {
                            A4 = A4.rotate();
                            w = image.getWidth() * scaleLandscape;
                            h = image.getHeight() * scaleLandscape;
                        } else {
                            w = image.getWidth() * scalePortrait;
                            h = image.getHeight() * scalePortrait;
                        }

                        Document document = new Document(A4, 10, 10, 10, 10);

                        try {
                            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/Boffin/"+File.separator+date+File.separator+filename+".pdf"));
                        } catch (DocumentException e) {
                            throw new IOException(e);
                        }
                        document.open();
                        try {
                            image.scaleAbsolute(w, h);
                            float posH = (A4.getHeight() - h) / 2;
                            float posW = (A4.getWidth() - w) / 2;

                            image.setAbsolutePosition(posW, posH);
                            image.setBorder(Image.NO_BORDER);
                            image.setBorderWidth(0);

                            try {
                                document.newPage();
                                document.add(image);
                                dialog.dismiss();
                            } catch (DocumentException de) {
                                throw new IOException(de);
                            }
                        } finally {
                            document.close();
                        }
                    } catch(Exception e) {
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doRecyclerProcess(){
        final ProgressDialog dialog = ProgressDialog.show(z_income_report_pdf.this, "Processing",
                "Please wait till the report calculation ending.", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if(reporttitlename.equals("Outflow")){
            account_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                        double total=0.0;
                        Map<String,Expences> map=new HashMap();
                        for(DataSnapshot data:iterator){
                            Expences expense=data.getValue(Expences.class);
                            if(categoryid.equals("all")){
                                if(expense.getUser().equals(CurrentUser.user.getId()) && expense.getAccountid().equals(accountid)){
                                    if(startdate!=null && !startdate.equals("") && enddate!=null && !enddate.equals("")){
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
                                                        System.out.println(tmpexpences.getInvoicetotal());
                                                        Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                        Double b2=Double.parseDouble(expense.getInvoicenet());
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
                                                    System.out.println(tmpexpences.getInvoicetotal());
                                                    Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                    Double b2=Double.parseDouble(expense.getInvoicenet());
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
                                    if(startdate!=null && !startdate.equals("") && enddate!=null && !enddate.equals("")){
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
                                                        System.out.println(tmpexpences.getInvoicetotal());
                                                        Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                        Double b2=Double.parseDouble(expense.getInvoicenet());
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
                                                    System.out.println(tmpexpences.getInvoicetotal());
                                                    Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                    Double b2=Double.parseDouble(expense.getInvoicenet());
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
                            }
                        }

                        Iterator it = map.entrySet().iterator();
                        boolean b=true;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            Expences expense= (Expences) pair.getValue();

                            total+=new BigDecimal(Double.parseDouble(expense.getInvoicenet())).setScale(2, RoundingMode.CEILING).doubleValue();
                            reportTabarray.add(new ReportTable(" - "+expense.getCategoryname(),new BigDecimal(Double.parseDouble(expense.getInvoicenet())).setScale(2, RoundingMode.CEILING).doubleValue()+""));
                        }

                        zincomereportpdftotalprice.setText(total+"");

                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(reporttitlename.equals("Inflow")){
            account_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        Iterable <DataSnapshot> iterator=dataSnapshot.getChildren();
                        double total=0.0;
                        Map<String,Expences> map=new HashMap();
                        for(DataSnapshot data:iterator){
                            Expences expense=data.getValue(Expences.class);
                            if(categoryid.equals("all")){
                                if(expense.getUser().equals(CurrentUser.user.getId()) && expense.getAccountid().equals(accountid)){
                                    if(startdate!=null && !startdate.equals("") && enddate!=null && !enddate.equals("")){
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
                                                        System.out.println(tmpexpences.getInvoicetotal());
                                                        Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                        Double b2=Double.parseDouble(expense.getInvoicenet());
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
                                                    System.out.println(tmpexpences.getInvoicetotal());
                                                    Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                    Double b2=Double.parseDouble(expense.getInvoicenet());
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
                                    if(startdate!=null && !startdate.equals("") && enddate!=null && !enddate.equals("")){
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
                                                        System.out.println(tmpexpences.getInvoicetotal());
                                                        Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                        Double b2=Double.parseDouble(expense.getInvoicenet());
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
                                                    System.out.println(tmpexpences.getInvoicetotal());
                                                    Double b1=Double.parseDouble(tmpexpences.getInvoicenet());
                                                    Double b2=Double.parseDouble(expense.getInvoicenet());
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
                            }
                        }

                        Iterator it = map.entrySet().iterator();
                        boolean b=true;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            Expences expense= (Expences) pair.getValue();

                            total+=new BigDecimal(Double.parseDouble(expense.getInvoicenet())).setScale(2, RoundingMode.CEILING).doubleValue();
                            reportTabarray.add(new ReportTable(" - "+expense.getCategoryname(),new BigDecimal(Double.parseDouble(expense.getInvoicenet())).setScale(2, RoundingMode.CEILING).doubleValue()+""));
                        }

                        zincomereportpdftotalprice.setText(total+"");

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

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public void saveBitmap(Bitmap bitmap) {
        date=new SimpleDateFormat("yymmddhhmmss").format(new Date());
        if(!new File(Environment.getExternalStorageDirectory() + File.separator + "Boffin").exists()){
            new File(Environment.getExternalStorageDirectory() + File.separator + "Boffin").mkdir();
        }

        if(!new File(Environment.getExternalStorageDirectory() + File.separator + "Boffin"+File.separator+date).exists()){
            new File(Environment.getExternalStorageDirectory() + File.separator + "Boffin"+File.separator+date).mkdir();
        }

        if(!new File(Environment.getExternalStorageDirectory() + File.separator + "Boffin"+File.separator+date+File.separator+"JPG").exists()){
            new File(Environment.getExternalStorageDirectory() + File.separator + "Boffin"+File.separator+date+File.separator+"JPG").mkdir();
        }
        String mPath = Environment.getExternalStorageDirectory() + File.separator +"Boffin"+File.separator+date+File.separator+"JPG"+File.separator+ "imgboffin.jpg";
        File imagePath = new File(mPath);
        if(!imagePath.exists()){
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                imageToPDF();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }else{
            imagePath.delete();
            Toast.makeText(this, "PDF Creation Error, Retry Again !", Toast.LENGTH_SHORT).show();
        }


    }

    boolean isWithinRange(String date) {
        boolean b=false;
        try {
            Date checkdate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
            b=(checkdate.before(new SimpleDateFormat("dd/MM/yyyy").parse(enddate)) && checkdate.after(new SimpleDateFormat("dd/MM/yyyy").parse(startdate)));
        }catch(Exception e){

        }

        return b;
    }

}
