package com.androider.legacy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.adapter.FormatAdapter;
import com.androider.legacy.adapter.GridAdapter;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Douban;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.ResultFragment;
import com.androider.legacy.service.NetService;
import com.androider.legacy.service.PublishService;
import com.androider.legacy.util.CapturePreview;
import com.androider.legacy.util.DensityUtil;
import com.androider.legacy.util.LegacyProgress;
import com.androider.legacy.util.WatcherSimplifier;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.gc.materialdesign.views.ButtonFlat;


import com.gc.materialdesign.views.ButtonFloat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class PublishActivity extends SimpleActivity implements Camera.PictureCallback, ZBarScannerView.ResultHandler{

    private int thumbHeight;
    private int thumbWidth;
    private MaterialEditText des;
    private AddFloatingActionButton publish;
    public static final String ISBN = "isbn";
    public Douban oneDou;
    GridView thumbs;
    RecyclerView list;
    GridAdapter thumbAdpter = new GridAdapter();
    FormatAdapter doubanAdapter;
    public LegacyProgress loadingView;
    public ArrayList<String> paths = new ArrayList<>();
    FrameLayout surface;
    public static PublishActivity instance;
    ZBarScannerView scannerView;

    CapturePreview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        instance = this;
        setToolBar();
        thumbHeight = DensityUtil.dip2px(this, 80);
        thumbWidth = DensityUtil.dip2px(this, 60);
        des = (MaterialEditText)findViewById(R.id.des_to_publish);
        publish = (AddFloatingActionButton)findViewById(R.id.publish);
        surface = (FrameLayout)findViewById(R.id.cap_view);
        thumbs = (GridView)findViewById(R.id.cap_holder);
        thumbs.setAdapter(thumbAdpter);
        list = (RecyclerView)findViewById(R.id.douban_list);
        doubanAdapter = new FormatAdapter();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(doubanAdapter);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPublish();
            }
        });
        loadingView = new LegacyProgress(this);
        loadingView.setTitle(R.string.publishing);
    }

    private void setToCap(){
        surface.setVisibility(View.VISIBLE);
        preview = new CapturePreview(this);
        surface.addView(preview);
        surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.takePicture();
            }
        });
        preview.startPreview();
    }

    private void setToScan(){
        surface.setVisibility(View.VISIBLE);
        scannerView = new ZBarScannerView(this);
        scannerView.setResultHandler(this);
        surface.addView(scannerView);
        scannerView.startCamera();
    }

    private void setToInput(){
        surface.removeAllViews();
        surface.setVisibility(View.GONE);
        des.setEnabled(true);
    }

    private void myPublish(){
        if(!User.alreadLogin){
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT);
            return;
        }
        loadingView.show();
        Holder.publishDes = des.getText().toString();
        Intent intent = new Intent(this, PublishService.class);
        intent.putExtra(Constants.intentType, Constants.myPublish);
        startService(intent);
    }

    public void publishFinished(){
        paths.clear();
        thumbAdpter.clearImg();
        des.setText("");
        loadingView.hide();
    }

    public NetHandler netHandler = new NetHandler(instance);

    @Override
    public void handleResult(Result result) {
        scannerView.stopCamera();
        Intent intent = new Intent(this, PublishService.class);
        intent.putExtra(Constants.intentType, Constants.fromDouban);
        intent.putExtra(ISBN, result.getContents());
        startService(intent);
        setToInput();
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
                case Constants.fromDouban:
                    instance.addFormattedBook();
                    break;
            }
        }
    }

    private void addFormattedBook(){
        doubanAdapter.addBook(oneDou);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadingView != null)
            loadingView.dismiss();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(90);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int iniWidth = bitmap.getWidth();
        int iniHeight = bitmap.getHeight();
        Bitmap used = Bitmap.createBitmap(bitmap, 0, 0, iniWidth, iniHeight, matrix, true);
        bitmap.recycle();
        matrix.reset();
        matrix.postScale((float)thumbWidth/iniHeight, (float)thumbHeight/iniWidth);
        Bitmap thumbnail = Bitmap.createBitmap(used, 0, 0, iniHeight, iniWidth, matrix, true);
        ImageView added = new ImageView(this);
        added.setImageBitmap(thumbnail);
        thumbAdpter.addThumb(added);
        String theName = System.currentTimeMillis() + ".jpg";
        File file = new File(MainActivity.filePath + theName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            used.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bos.flush();
            bos.close();
            used.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        paths.add(theName);
        setToInput();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.just_scan){
            setToScan();
        }else if(id == R.id.photo_cap){
            setToCap();
        }
        return super.onOptionsItemSelected(item);
    }
}
