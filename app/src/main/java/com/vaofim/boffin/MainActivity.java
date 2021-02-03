package com.vaofim.boffin;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vaofim.boffin.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import Models.Offline;
import Models.User;
import Stables.CurrentUser;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userreference;
    File datafile1;
    File datafile2;
    BufferedReader bufferedReader;
    String userid;
    boolean checkall;
    private NotificationManagerCompat notificationManager ;
    NotificationCompat.Builder builder;
    String CHANNELID = "default";
    int notificationId=1;
    int PROGRESS_MAX = 100;
    int PROGRESS_CURRENT = 0;
    File file;

    DatabaseReference offlineref;
    FirebaseStorage firebaseStorage;
    StorageReference imgreference;

    private static final int PERMISSIONCODE=1240;

    String permissions[]={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NOTIFICATION_POLICY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebaseDatabase=FirebaseDatabase.getInstance();

            datafile1=new File(getApplicationContext().getFilesDir(),"data.voi");
            datafile2=new File(getApplicationContext().getFilesDir(),"record.voi");
            userreference=firebaseDatabase.getReference("user");
            userreference.keepSynced(true);
            checkall=false;
            file=new File(getApplicationContext().getFilesDir().getAbsolutePath());



            new Thread(new Runnable() {
                @Override
                public void run() {
                    getAccess();
                }
            }).start();

                if(datafile2.exists()){
                    if(isNetworkAvailable()){
                        doSync();
                    }else{
                        checkUser();
                    }
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                startActivity(new Intent(getApplicationContext(),gettingstart.class));
//                            activity.finish();
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private boolean getAccess(){
        List<String> permissioneeded=new ArrayList<>();
        for(String s:permissions){
            if(ContextCompat.checkSelfPermission(this,s)!= PackageManager.PERMISSION_GRANTED){
                permissioneeded.add(s);
            }
        }

        if(!permissioneeded.isEmpty()){
            ActivityCompat.requestPermissions(this,permissioneeded.toArray(new String[permissioneeded.size()]),PERMISSIONCODE);
            return false;
        }

        return true;
    }

    public void checkUser() throws Exception{
        if(datafile1.exists()){
            bufferedReader=new BufferedReader(new FileReader(datafile1));

            userid=bufferedReader.readLine().trim();

            checkCredentialsFile();
        }else{
            doLoad();
        }
    }

    public void doSync(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkUser();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkCredentialsFile(){
        userreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> datalist=dataSnapshot.getChildren();

                for(DataSnapshot child:datalist){
                    User user1=child.getValue(User.class);
                    if(userid.equals(user1.getId())){
                        CurrentUser.user=user1;

                        syncImages();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    startActivity(new Intent(getApplicationContext(),homenew.class));
                                }catch (Exception e){

                                }
                            }
                        }).start();
                        return;
                    }
                }

                doLoad();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void syncImages() {
        if(isNetworkAvailable()){
            offlineref=firebaseDatabase.getReference("offline").child(CurrentUser.user.getId());
            firebaseStorage=FirebaseStorage.getInstance();
            imgreference=firebaseStorage.getReference("images").child(CurrentUser.user.getId());

            if(isNetworkAvailable()){
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        offlineref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable <DataSnapshot> iterable=dataSnapshot.getChildren();

                                for(DataSnapshot data:iterable){
                                    notificationManager  = NotificationManagerCompat.from(getApplicationContext());
                                    builder = new NotificationCompat.Builder(getApplicationContext(), CHANNELID);
                                    builder.setContentTitle("Boffin")
                                            .setContentText("Sync in process")
                                            .setSmallIcon(R.drawable.ic_sync)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true);
                                    notificationManager.notify(notificationId, builder.build());
                                    final Offline offline=data.getValue(Offline.class);
                                    File existfile=new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/"+offline.getPath());
                                    if(existfile.exists()){
                                        UploadTask uploadTask=imgreference.child(offline.getPath()).putFile(Uri.fromFile(existfile));
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                offlineref.child(offline.getId()).removeValue();

                                                List<UploadTask> list1=imgreference.getActiveUploadTasks();
                                                if(list1.size()==0){
                                                    builder.setContentTitle("Boffin")
                                                            .setContentText("Sync Complete")
                                                            .setSmallIcon(R.drawable.ic_sync)
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                                    builder.setProgress(PROGRESS_MAX, PROGRESS_MAX, false);
                                                    notificationManager.notify(notificationId, builder.build());
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }, 100);
            }
        }
    }

    private void doLoad(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    startActivity(new Intent(getApplicationContext(),login.class));
                }catch (Exception e){

                }
            }
        }).start();
    }



}
