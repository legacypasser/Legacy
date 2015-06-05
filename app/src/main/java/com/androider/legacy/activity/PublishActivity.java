package com.androider.legacy.activity;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.BoringLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androider.legacy.R;
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
import com.androider.legacy.util.DividerDecorator;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class PublishActivity extends SimpleActivity implements Camera.PictureCallback, ZBarScannerView.ResultHandler{

    private int thumbHeight;
    private int requiredWidth;
    private MaterialEditText des;
    private EditText title;
    private EditText price;
    ImageButton addScan;
    ImageButton addPhoto;
    CardView selfContent;
    LinearLayout whenCap;
    private Button publish;
    ImageButton finishCap;
    ArrayList<Bitmap> caped;
    ArrayList<String> capedPath;
    public static final String ISBN = "isbn";
    public ArrayList<Douban> beans = new ArrayList<>();
    GridView thumbs;
    LinearLayout list;
    GridAdapter thumbAdpter = new GridAdapter();
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
        thumbHeight = DensityUtil.dip2px(this, 96);
        requiredWidth = DensityUtil.dip2px(this, 150);
        des = (MaterialEditText)findViewById(R.id.des_to_publish);
        price = (MaterialEditText)findViewById(R.id.price_to_publish);
        title = (MaterialEditText)findViewById(R.id.title_to_publish);
        publish = (Button)findViewById(R.id.publish);
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
        loadingView = new LegacyProgress(this);
        loadingView.setTitle(R.string.publishing);
        addPhoto = (ImageButton)findViewById(R.id.add_photo);
        addScan = (ImageButton)findViewById(R.id.add_scan);
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
        whenCap = (LinearLayout)view.findViewById(R.id.when_cap);
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
    }

    private void setToScan(){
        surface.setVisibility(View.VISIBLE);
        scannerView = new ZBarScannerView(this);
        scannerView.setResultHandler(this);
        surface.addView(scannerView);
        scannerView.startCamera();
    }
    private void setToMain(){
        surface.removeAllViews();
        surface.setVisibility(View.GONE);

    }
    private void setToInput(){
        for (int i = 0; i < caped.size(); i++){
            ImageView view = new ImageView(this);
            view.setImageBitmap(caped.get(i));
            thumbAdpter.addThumb(view);
            paths.add(capedPath.get(i));
        }
        if(selfContent.getVisibility() == View.GONE)
            selfContent.setVisibility(View.VISIBLE);
    }

    private void myPublish(){
        if(!User.instance.alreadLogin){
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingView.show();
        Intent intent = new Intent(this, PublishService.class);
        if(!paths.isEmpty()){
            intent.putExtra("rawDes", des.getText().toString());
            intent.putExtra("rawTitle", title.getText().toString());
            intent.putExtra("rawPrice", price.getText().toString());
            intent.putStringArrayListExtra("paths", paths);
        }
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
                case Constants.fromDouban:
                    instance.addFormattedBook(msg.arg1);
                    break;
            }
        }
    }

    private void addFormattedBook(final int index){
        View itemView = LayoutInflater.from(this).inflate(R.layout.format_book, null);
        ImageView img = (ImageView)itemView.findViewById(R.id.douban_img);
        TextView title = (TextView)itemView.findViewById(R.id.douban_name);
        final TextView price = (MaterialEditText)itemView.findViewById(R.id.douban_price);
        title.setText(beans.get(index).name);
        ImageLoader.getInstance().displayImage(beans.get(index).img, img);
        price.addTextChangedListener(new WatcherSimplifier() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    beans.get(index).price = 0;
                } else {
                    beans.get(index).price = Integer.parseInt(price.getText().toString());
                }
            }
        });
        list.addView(itemView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadingView != null)
            loadingView.dismiss();
    }
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        long nowTime = System.currentTimeMillis();
        String theName =  nowTime + ".jpg";
        String thumbName = "s_" + nowTime + ".jpg";
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
        whenCap.addView(added);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(MainActivity.filePath + theName)));
            used.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bos.flush();
            bos.close();
            bos = new BufferedOutputStream(new FileOutputStream(new File(MainActivity.filePath + thumbName)));
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
        getMenuInflater().inflate(R.menu.menu_pub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
