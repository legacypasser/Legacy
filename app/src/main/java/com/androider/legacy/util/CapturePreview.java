package com.androider.legacy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Holder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.util.List;

/**
 * Created by Think on 2015/4/22.
 */
public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback, Camera.ShutterCallback{

    SurfaceHolder holder;
    public Camera camera;
    Camera.PictureCallback callback;

    public CapturePreview(PublishActivity context) {
        super(context);
        callback = context;
    }

    public void startPreview(){
        if(camera != null)
            return;
        camera = Camera.open(0);
        holder = getHolder();
        holder.addCallback(this);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(90);
        Camera.Parameters p = camera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        p.setJpegQuality(100);
        camera.setParameters(p);
        camera.cancelAutoFocus();
        camera.startPreview();
    }

    public void stopPreview(){
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void takePicture(){
        if(StoreInfo.getBool(StoreInfo.shutter))
            camera.takePicture(this, null, callback);
        else
            camera.takePicture(null, null, callback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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
        stopPreview();
    }

    @Override
    public void onShutter() {
        new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME).startTone(ToneGenerator.TONE_PROP_BEEP2);
    }
}
