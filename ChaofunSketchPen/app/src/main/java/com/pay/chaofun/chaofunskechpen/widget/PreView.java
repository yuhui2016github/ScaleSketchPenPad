package com.pay.chaofun.chaofunskechpen.widget;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by 炒饭 on 2016/7/16.
 */

public class PreView extends SurfaceView implements SurfaceHolder.Callback{

    private  SurfaceHolder mHolder;
    private Camera mCamera;

    public PreView(Context context,Camera camera) {
        super(context);
        mCamera=camera;
        mHolder=getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(mHolder.getSurface()==null)
            return;

        try {
            mCamera.stopPreview();
        }catch (Exception e)
        {

        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch (Exception e)
        {

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
