package com.pay.chaofun.chaofunskechpen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pay.chaofun.chaofunskechpen.utils.CpatureUtils;
import com.pay.chaofun.chaofunskechpen.widget.PreView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.pm.PackageManager.FEATURE_CAMERA;

/**
 * @author 炒饭chaofun
 *         <p>
 *         步骤：
 *         1. 关联相机
 *         2. 创建预览对象
 *         3. 构建预览对象的视图
 *         4.设置捕获图片监听
 *         5.捕获并存储
 *         6.释放资源
 *
 *         Mainfest.xml注意：
 *         1. 权限
 *         2. 当前activity的设置
 */
public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private PreView mPreview;
    private FrameLayout fm;
    private Context mContext;
    private boolean canCapture = true;
    private byte[] mResultData =null;

    public static String PICTURE_DATA="PICTURE_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camer_activity);
        fm = (FrameLayout) findViewById(R.id.camera_preview);
        mContext = getApplicationContext();
        if (checkCameraHardware(mContext)) {
            mCamera = getCameraInstance();
        }

        //加一张半透明Image测试
//        ImageView imageView = new ImageView(mContext);
//        imageView.setBackgroundColor(Color.GRAY);
//        imageView.setAlpha(0.8f);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        if (mCamera != null) {
            mPreview = new PreView(this, mCamera);
            fm.addView(mPreview, layoutParams);
//            fm.addView(imageView, layoutParams);
        }

    }

    public void capture(View view) {
        if (canCapture) {
            canCapture = false;
            mCamera.takePicture(null, null, mPicture);
        }

    }

    public void Done(View view){

        if (mResultData==null)
            return;;
        Intent intent=new Intent();
        intent.putExtra(PICTURE_DATA,mResultData);
        intent.setFlags(RESULT_OK);
        setResult(RESULT_OK,intent);
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                finish();

            }
        };
            timer.schedule(timerTask,500);
    }

    public void Cancel(View view){
        finish();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(FEATURE_CAMERA))
            return true;
        else
            return false;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }

        return c;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null)
            mCamera.release();
        mCamera = null;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {


            mResultData= data;

            File pictureFile = CpatureUtils.getOutputMediaFile(CpatureUtils.MEDIA_TYPE_IMAGE);
            if (pictureFile == null)
                return;

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCamera.startPreview();

            showCpatureImage(data);
            canCapture = true;

        }
    };

    public void showCpatureImage(byte[] data) {
        ImageView showImage = new ImageView(mContext);
        Bitmap resultBitmap = null;
        Bitmap tempBitmap = null;

        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            resultBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, false);
            showImage.setImageBitmap(resultBitmap);
        } catch (Exception e) {

        } finally {
            if (tempBitmap != null && !tempBitmap.isRecycled()) {
                tempBitmap.recycle();
                tempBitmap = null;
            }
        }

        if (showImage != null) {

            RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(540, 540);
            ivParams.addRule(Gravity.CENTER);
            fm.addView(showImage, ivParams);
        }
    }

}
