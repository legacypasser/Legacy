package com.androider.legacy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.androider.legacy.activity.PublishActivity;

import java.io.IOException;

/**
 * Created by Think on 2015/4/22.
 */
public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder holder;
    Camera camera;

    public CapturePreview(Context context) {
        super(context);
        camera = Camera.open(0);
        holder = getHolder();
        holder.addCallback(this);
        camera.setDisplayOrientation(90);

    }

    public void takePicture(){
        camera.takePicture(null, null, PublishActivity.instance);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(holder.getSurface() == null)
            return;
        camera.stopPreview();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}
