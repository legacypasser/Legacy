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
import com.androider.legacy.adapter.GridAdapter;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
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


public class PublishActivity extends SimpleActivity implements Camera.PictureCallback{

    MenuItem inform;
    private int thumbHeight;
    private int thumbWidth;
    private MaterialEditText des;
    private MaterialEditText price;
    private AddFloatingActionButton publish;
    CapturePreview preview;


    View pusher;
    GridView thumbs;
    GridAdapter thumbAdpter = new GridAdapter();
    public LegacyProgress loadingView;
    public ArrayList<String> paths = new ArrayList<>();
    int scanCode = 0;
    public static PublishActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        instance = this;
        thumbHeight = DensityUtil.dip2px(this, 60);
        thumbWidth = DensityUtil.dip2px(this, 80);
        des = (MaterialEditText)findViewById(R.id.des_to_publish);
        price = (MaterialEditText)findViewById(R.id.price_to_publish);
        publish = (AddFloatingActionButton)findViewById(R.id.publish);
        LinearLayout holder = (LinearLayout)findViewById(R.id.img_holder);
        thumbs = (GridView)findViewById(R.id.cap_holder);
        pusher = findViewById(R.id.pusher);
        thumbs.setAdapter(thumbAdpter);
        preview = new CapturePreview(this);
        des.addTextChangedListener(watcher);
        price.addTextChangedListener(watcher);
        holder.addView(preview);
        setToolBar();
        setToCap();
        loadingView = new LegacyProgress(this);
        loadingView.setTitle(R.string.publishing);
    }


    private void setToCap(){
        preview.camera.startPreview();
        publish.setVisibility(View.VISIBLE);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.takePicture();
            }
        });
        des.setEnabled(false);
        price.setEnabled(false);
    }
    WatcherSimplifier watcher = new WatcherSimplifier() {
        @Override
        public void afterTextChanged(Editable s) {
            if(des.getText().toString().equals("") || price.getText().toString().equals(""))
                setToInput();
            else
                setToPub();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == scanCode){
            String scannedCode = data.getStringExtra("barcode");
            Toast.makeText(this, scannedCode, Toast.LENGTH_SHORT).show();
        }
    }


    private void setToInput(){
        preview.camera.stopPreview();
        publish.setVisibility(View.GONE);
        des.setEnabled(true);
        price.setEnabled(true);
    }

    private void setToPub(){
        publish.setVisibility(View.VISIBLE);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPublish();
            }
        });
        preview.camera.stopPreview();
    }

    private void myPublish(){
        if(!User.alreadLogin){
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT);
            return;
        }
        loadingView.show();
        Holder.publishDes = des.getText().toString();
        Holder.price = price.getText().toString();
        Intent intent = new Intent(this, PublishService.class);
        intent.putExtra(Constants.intentType, Constants.myPublish);
        startService(intent);
    }

    public void publishFinished(){
        paths.clear();
        thumbAdpter.clearImg();
        des.setText("");
        price.setText("");
        loadingView.hide();
        setToCap();
    }

    public NetHandler netHandler = new NetHandler(instance);

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
        Bitmap used = Bitmap.createBitmap(bitmap, iniWidth / 10, 0, iniWidth / 3, iniHeight, matrix, true);
        bitmap.recycle();
        int usedHeight = used.getHeight();
        int usedWidth = used.getWidth();
        matrix.reset();
        matrix.postScale((float)thumbWidth/usedWidth, (float)thumbHeight/usedHeight);
        Bitmap thumbnail = Bitmap.createBitmap(used, 0, 0, usedWidth, usedHeight, matrix, true);
        ImageView added = new ImageView(this);
        added.setImageBitmap(thumbnail);
        thumbAdpter.addThumb(added);
        String theName = System.currentTimeMillis() + ".jpg";
        File file = new File(MainActivity.filePath + theName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            used.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            used.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        paths.add(theName);
        camera.startPreview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pub, menu);
        inform = menu.findItem(R.id.inform);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.photo_switch){
            if (pusher.getVisibility() == View.VISIBLE) {
                if(thumbAdpter.getCount() == 0){
                    Toast.makeText(instance, "请先拍几张",Toast.LENGTH_SHORT).show();
                }else{
                    pusher.setVisibility(View.GONE);
                    setToInput();
                    item.setIcon(R.drawable.ic_photo_camera_white_18dp);
                    inform.setTitle(R.string.take_photo);
                }
            } else {
                pusher.setVisibility(View.VISIBLE);
                setToCap();
                item.setIcon(R.drawable.ic_edit_white_24dp);
                inform.setTitle(R.string.edit_des);
            }
        }else if(id == R.id.just_scan){
            Intent intent = new Intent(this, ScanningActivity.class);
            startActivityForResult(intent, scanCode);
        }
        return super.onOptionsItemSelected(item);
    }
}
