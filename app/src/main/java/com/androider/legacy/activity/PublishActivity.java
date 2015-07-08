package com.androider.legacy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.adapter.GridAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Douban;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.service.PublishService;
import com.androider.legacy.util.CapturePreview;
import com.androider.legacy.util.ConnectDetector;
import com.androider.legacy.util.CustomProgressDialog;
import com.androider.legacy.util.DensityUtil;
import com.androider.legacy.util.LegacyApp;
import com.androider.legacy.util.StoreInfo;
import com.androider.legacy.util.WatcherSimplifier;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class PublishActivity extends AppCompatActivity implements Camera.PictureCallback, ZBarScannerView.ResultHandler{

    private int thumbHeight;
    private int requiredWidth;
    private MaterialEditText des;
    private EditText title;
    private EditText price;
    Button addScan;
    Button addPhoto;
    CardView selfContent;
    GridView whenCap;
    GridAdapter capAdapter;
    ImageButton finishCap;
    ArrayList<Bitmap> caped;
    ArrayList<String> capedPath;
    public ArrayList<Douban> beans = new ArrayList<>();
    GridView thumbs;
    LinearLayout list;
    GridAdapter thumbAdpter = new GridAdapter();
    public ArrayList<String> paths = new ArrayList<>();
    FrameLayout surface;
    public static PublishActivity instance;
    ZBarScannerView scannerView;
    CapturePreview preview;
    CustomProgressDialog dialog;
    private static final String jpgSuf = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        instance = this;
        thumbHeight = DensityUtil.dip2px(this, 96);
        requiredWidth = DensityUtil.dip2px(this, 150);
        Toolbar toolbar = (Toolbar)findViewById(R.id.simple_toolbar);
        setSupportActionBar(toolbar);
        des = (MaterialEditText)findViewById(R.id.des_to_publish);
        price = (MaterialEditText)findViewById(R.id.price_to_publish);
        title = (MaterialEditText)findViewById(R.id.title_to_publish);
        Button publish = (Button)findViewById(R.id.publish);
        selfContent = (CardView)findViewById(R.id.self_content);
        surface = (FrameLayout)findViewById(R.id.cap_view);
        thumbs = (GridView)findViewById(R.id.cap_holder);
        thumbs.setAdapter(thumbAdpter);
        list = (LinearLayout)findViewById(R.id.douban_list);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPublish();
            }
        });
        addPhoto = (Button)findViewById(R.id.add_photo);
        addScan = (Button)findViewById(R.id.add_scan);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToCap();
            }
        });
        addScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToScan();
            }
        });
    }

    private void setToCap(){
        caped = new ArrayList<>();
        capedPath = new ArrayList<>();
        surface.setVisibility(View.VISIBLE);
        preview = new CapturePreview(this);
        surface.addView(preview);
        View view = LayoutInflater.from(this).inflate(R.layout.preview_below, null);
        whenCap = (GridView)view.findViewById(R.id.when_cap);
        capAdapter = new GridAdapter();
        whenCap.setAdapter(capAdapter);
        finishCap = (ImageButton)view.findViewById(R.id.finish_cap);
        finishCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToMain();
                setToInput();
            }
        });
        surface.addView(view);
        surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.takePicture();
            }
        });
        preview.startPreview();
        if(!StoreInfo.getBool(getString(R.string.photo_instruct))){
            Toast.makeText(this, getString(R.string.how_to_photo), Toast.LENGTH_SHORT).show();
            StoreInfo.setBool(getString(R.string.photo_instruct), true);
        }
    }

    private void setToScan(){
        if(!ConnectDetector.isConnectedToNet()){
            Toast.makeText(this, getString(R.string.neet_net_to_scan), Toast.LENGTH_SHORT).show();
            return;
        }
        surface.setVisibility(View.VISIBLE);
        scannerView = new ZBarScannerView(this);
        scannerView.setResultHandler(this);
        surface.addView(scannerView);
        scannerView.startCamera();
    }
    private void  setToMain(){
        if(scannerView != null)
            scannerView.stopCamera();
        surface.removeAllViews();
        surface.setVisibility(View.GONE);
    }
    private void setToInput(){
        selfContent.setVisibility(View.VISIBLE);
        for (int i = 0; i < caped.size(); i++){
            ImageView view = new ImageView(this);
            view.setImageBitmap(caped.get(i));
            thumbAdpter.addThumb(view);
            paths.add(capedPath.get(i));
        }
    }

    private boolean selfValid(){
        if(thumbAdpter.getCount() != 0){
            return (!title.getText().toString().equals(Constants.emptyString)&&!price.getText().toString().equals(Constants.emptyString)
                    &&!des.getText().toString().equals(Constants.emptyString));
        }else
            return true;
    }


    private void myPublish(){
        if(!ConnectDetector.isConnectedToNet()){
            Toast.makeText(this, getString(R.string.no_net), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!StoreInfo.validLogin()){
            Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
            return;
        }
        if(list.getChildCount() == 0 && thumbAdpter.getCount() == 0){
            Toast.makeText(this, getString(R.string.empty_joke), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!selfValid()){
            Toast.makeText(this, getString(R.string.full_content), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PublishService.class);
        if(!paths.isEmpty()){
            intent.putExtra(getString(R.string.raw_des), des.getText().toString());
            intent.putExtra(getString(R.string.raw_title), title.getText().toString());
            intent.putExtra(getString(R.string.raw_price), price.getText().toString());
            intent.putStringArrayListExtra(getString(R.string.img_path), paths);
        }
        intent.putExtra(Constants.intentType, Constants.myPublish);
        startService(intent);
        dialog = CustomProgressDialog.createDialog(this);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if(surface.getVisibility() == View.VISIBLE)
            setToMain();
        else
            finish();
    }

    public void publishFinished(){
        paths.clear();
        thumbAdpter.clearImg();
        des.setText(Constants.emptyString);
        title.setText(Constants.emptyString);
        price.setText(Constants.emptyString);
        list.removeAllViews();
        dialog.dismiss();
        selfContent.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.pub_finish), Toast.LENGTH_SHORT).show();
    }
    public NetHandler netHandler = new NetHandler(instance);

    @Override
    public void handleResult(Result result) {
        final String str = result.getContents();
        LegacyClient.getInstance().callTask(Douban.url + str + Douban.suf, new LegacyTask.RequestCallback() {
            @Override
            public void onRequestDone(String result) {
                Douban one = new Douban(str);
                one.fill(result);
                beans.add(one);
                addFormattedBook(one);
            }
        });
        setToMain();
    }

    private static class NetHandler extends Handler {
        WeakReference<PublishActivity> activityWeakReference;
        NetHandler(PublishActivity publishActivity){
            activityWeakReference = new WeakReference<>(publishActivity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case Constants.myPublish:
                    instance.publishFinished();
                    break;
            }
        }
    }

    private void addFormattedBook(final Douban one){
        if(one.name == null){
            Toast.makeText(this, getString(R.string.scan_fail), Toast.LENGTH_LONG).show();
            return;
        }
        View itemView = LayoutInflater.from(this).inflate(R.layout.format_book, list, false);
        ImageView img = (ImageView)itemView.findViewById(R.id.douban_img);
        TextView title = (TextView)itemView.findViewById(R.id.douban_name);
        final TextView price = (MaterialEditText)itemView.findViewById(R.id.douban_price);
        title.setText(one.name);
        ImageLoader.getInstance().displayImage(one.img, img);
        price.addTextChangedListener(new WatcherSimplifier() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(Constants.emptyString)) {
                    one.price = 0;
                } else {
                    one.price = Integer.parseInt(price.getText().toString());
                }
            }
        });
        list.addView(itemView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if(thumbAdpter.getCount() >= 5 || capAdapter.getCount() >= 5){
            Toast.makeText(this, getString(R.string.to_many_photo), Toast.LENGTH_SHORT).show();
            return;
        }
        long nowTime = System.currentTimeMillis();
        String theName =  nowTime + jpgSuf;
        String thumbName = "s_" + nowTime + jpgSuf;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Matrix matrix = new Matrix();
        matrix.reset();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int iniWidth = bitmap.getWidth();
        int iniHeight = bitmap.getHeight();
        float scale = (float)requiredWidth/iniHeight;
        matrix.setScale(scale, scale);
        matrix.postRotate(90);
        Bitmap used = Bitmap.createBitmap(bitmap, 0, 0, iniHeight*4/3, iniHeight, matrix, true);
        bitmap.recycle();
        scale = (float)thumbHeight/used.getHeight();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = Bitmap.createBitmap(used, 0, 0, used.getWidth(), used.getHeight(), matrix, true);
        ImageView added = new ImageView(this);
        added.setImageBitmap(thumbnail);
        caped.add(thumbnail);
        capedPath.add(theName);
        capAdapter.addThumb(added);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(LegacyApp.filePath + theName)));
            used.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bos.flush();
            bos.close();
            bos = new BufferedOutputStream(new FileOutputStream(new File(LegacyApp.filePath + thumbName)));
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bos.flush();
            bos.close();
            used.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        preview.camera.startPreview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
