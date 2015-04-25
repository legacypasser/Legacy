package com.androider.legacy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.CapturePreview;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PublishActivity extends SimpleActivity implements Camera.PictureCallback{

    private MaterialEditText des;
    private AddFloatingActionButton addImg;
    private AddFloatingActionButton publish;
    private FrameLayout holder;
    CapturePreview preview;

    public static PublishActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        instance = this;
        des = (MaterialEditText)findViewById(R.id.des_to_publish);
        addImg = (AddFloatingActionButton)findViewById(R.id.start_cap);
        publish = (AddFloatingActionButton)findViewById(R.id.publish);
        holder = (FrameLayout)findViewById(R.id.img_holder);
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


    }

    private void myPublish(){
        Holder.publishDes = des.getText().toString();
        Intent intent = new Intent(this, NetService.class);
        intent.putExtra(Constants.intentType, Constants.myPublish);
        startService(intent);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        String theName = System.currentTimeMillis() + ".png";
        File file = new File(MainActivity.filePath + theName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, bos);
            bos.flush();
            bos.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Holder.paths.add(theName);
    }
}
