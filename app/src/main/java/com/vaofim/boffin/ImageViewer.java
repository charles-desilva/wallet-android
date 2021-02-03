package com.vaofim.boffin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageViewer extends AppCompatActivity {

    ImageView imageView;
    String path;
    String imagename;
    File file;
    Bitmap bm = null;
    InputStream is = null;
    BufferedInputStream bis = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        if(!getIntent().getStringExtra("path").equals("")){
            path=getIntent().getStringExtra("path");
            imagename=getIntent().getStringExtra("name");
            file=new File(getIntent().getStringExtra("img"));
//            Toast.makeText(this, getIntent().getStringExtra("img"), Toast.LENGTH_SHORT).show();
            imageView=findViewById(R.id.loadingimageView);

            Picasso.get().load(file).into(imageView);

        }else{
            onBackPressed();
        }
    }

    public void goToHome(View view){
        onBackPressed();
    }

    public void saveImage(View view){
        try {
            is = getContentResolver().openInputStream(Uri.fromFile(file));
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
            saveImageToExternalStorage(bm);
//            String savedImageURL = MediaStore.Images.Media.insertImage(getContentResolver(),bm,imagename,"Image By Boffin");
//            Toast.makeText(this, "Saved to "+savedImageURL, Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void printImage(View view){
        try {
            is = getContentResolver().openInputStream(Uri.fromFile(file));
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
            PrintHelper photoPrinter = new PrintHelper(getApplicationContext());
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            photoPrinter.printBitmap("Boffin Print", bm);
        }catch(Exception e){

        }
    }

    public void shareImage(View view){

    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/Boffin");
        if(myDir.exists()){
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            File file = new File(myDir, imagename);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(this, "Saved in gallery", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            myDir.mkdir();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            File file = new File(myDir, imagename);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(this, "Saved in gallery", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }
}
