package com.androider.legacy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androider.legacy.R;
import com.androider.legacy.adapter.GridAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.fragment.ResultFragment;
import com.androider.legacy.service.NetService;
import com.androider.legacy.service.PublishService;
import com.androider.legacy.util.CapturePreview;
import com.androider.legacy.util.DensityUtil;
import com.androider.legacy.util.LegacyProgress;
import com.gc.materialdesign.views.ButtonFlat;


import com.gc.materialdesign.views.ButtonFloat;
import com.rengwuxian.materialedittext.MaterialEditText;

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

    private int thumbHeight;
    private int thumbWidth;
    private MaterialEditText des;
    private ButtonFlat addImg;
    private ButtonFloat publish;
    private LinearLayout holder;
    CapturePreview preview;
    ButtonFlat capSwitch;
    View pusher;
    GridView thumbs;
    GridAdapter thumbAdpter = new GridAdapter();
    public LegacyProgress loadingView;
    public ArrayList<String> paths = new ArrayList<>();

    public static PublishActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        instance = this;
        thumbHeight = DensityUtil.dip2px(this, 60);
        thumbWidth = DensityUtil.dip2px(this, 80);
        des = (MaterialEditText)findViewById(R.id.des_to_publish);
        addImg = (ButtonFlat)findViewById(R.id.start_cap);
        publish = (ButtonFloat)findViewById(R.id.publish);
        holder = (LinearLayout)findViewById(R.id.img_holder);
        capSwitch = (ButtonFlat)findViewById(R.id.cap_switch);
        thumbs = (GridView)findViewById(R.id.cap_holder);
        pusher = findViewById(R.id.pusher);
        thumbs.setAdapter(thumbAdpter);
        capSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pusher.getVisibility() == View.VISIBLE){
                    pusher.setVisibility(View.GONE);
                    addImg.setVisibility(View.GONE);
                    capSwitch.setText(getResources().getString(R.string.cap_start));
                    preview.camera.stopPreview();
                }else{
                    pusher.setVisibility(View.VISIBLE);
                    addImg.setVisibility(View.VISIBLE);
                    capSwitch.setText(getResources().getString(R.string.cap_finish));
                    preview.camera.startPreview();
                }
            }
        });
        preview = new CapturePreview(this);
        holder.addView(preview);
        setToolBar();
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.takePicture();
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPublish();
            }
        });
        loadingView = new LegacyProgress(this);
        loadingView.setTitle(R.string.publishing);

    }

    private void myPublish(){
        loadingView.show();
        Holder.publishDes = des.getText().toString();
        Intent intent = new Intent(this, PublishService.class);
        intent.putExtra(Constants.intentType, Constants.myPublish);
        startService(intent);
    }

    public void publishFinished(){
        paths.clear();
        thumbs.removeAllViews();
        des.setText("");
        loadingView.hide();
    }

    public static NetHandler netHandler = new NetHandler(instance);

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
}
