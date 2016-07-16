package com.pay.chaofun.chaofunskechpen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pay.chaofun.chaofunskechpen.utils.BitmapUtils;
import com.pay.chaofun.chaofunskechpen.widget.BorderView;
import com.pay.chaofun.chaofunskechpen.widget.PenStrockAndColorSelect;

public class MainActivity extends AppCompatActivity {



    public static final int PICTURE_REQUEST_GALLERY = 0x200;
    public static final int CAPTURE_PICTURE_REQUEST = 0x201;
    private BorderView mBorderView;
    private PenStrockAndColorSelect mPenStockAndColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activityi);

        Context mContext = getApplicationContext();
        LinearLayout mainRL = (LinearLayout) findViewById(R.id.ll_container);
        mBorderView = new BorderView(mContext);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(Gravity.CENTER);
        mainRL.addView(mBorderView, layoutParams);
        mBorderView.setCutoutImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));


        mPenStockAndColor = (PenStrockAndColorSelect) findViewById(R.id.strock_color_select);
        mPenStockAndColor.setCallback(new PenStrockAndColorSelect.ColorSelectorCallback() {

            @Override
            public void onColorSelectCancel(PenStrockAndColorSelect sender) {

            }

            @Override
            public void onColorSelectChange(PenStrockAndColorSelect sender) {
                int position = sender.getPenPosition();
                setPenTye(position);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_REQUEST_GALLERY && resultCode == RESULT_OK) {

            if (data == null)
                return;
            Bitmap resultBimtap = BitmapUtils.getBitmapPathFromData(data, getApplicationContext());
            mBorderView.setImageView(resultBimtap);
        }

        if (requestCode == CAPTURE_PICTURE_REQUEST && resultCode == RESULT_OK) {

            if (data == null)
                return;
            Bitmap resultBimtap = null;
            Bitmap tempBitmap = null;
            byte[] info = data.getByteArrayExtra(CameraActivity.PICTURE_DATA);

            try {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                tempBitmap = BitmapFactory.decodeByteArray(info, 0, info.length);
                resultBimtap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, false);
                mBorderView.setImageView(resultBimtap);
            } catch (Exception e) {

            } finally {
                if (tempBitmap != null && !tempBitmap.isRecycled()) {
                    tempBitmap.recycle();
                    tempBitmap = null;
                }
            }
        }
    }

    public void setPenStrock(View view) {
        mPenStockAndColor.setStrockORColor(PenStrockAndColorSelect.STROCK_TYPE);
        mPenStockAndColor.setVisibility(View.VISIBLE);
    }

    public void setPenClor(View view) {
        mPenStockAndColor.setStrockORColor(PenStrockAndColorSelect.COLOR_TYPE);
        mPenStockAndColor.setVisibility(View.VISIBLE);
    }

    public void undoPath(View view) {
        mBorderView.undo();
    }

    public void chooseGalleryPhoto(View view) {
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, PICTURE_REQUEST_GALLERY);
    }

    public void capture(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivityForResult(intent, CAPTURE_PICTURE_REQUEST);
    }


    public void saveResultImage(View view) {
        Bitmap skechResult = mBorderView.getResultBitmap();
        mBorderView.setCutoutImage(skechResult);
        BitmapUtils.saveBitmap(skechResult, "test_skech", getApplicationContext());

    }

    public void setEraser(View view) {
        mBorderView.setEraserType();
    }

    private void setPenTye(int position) {
        mPenStockAndColor.setVisibility(View.GONE);
        int type = mPenStockAndColor.getCURRENT_TYPE();
        switch (position) {
            case 1:
                mBorderView.setPenType_1();
                break;
            case 2:
                mBorderView.setPenType_2();

                break;
            case 3:
                mBorderView.setPenType_3();
                break;
            case 4:
                mBorderView.setPenType_4();
                break;
            case 5:
                mBorderView.setPenType_5();
                break;
            case 6:
                mBorderView.setPenType_6();
                break;
            case 7:
                mBorderView.setPenType_7();
                break;
            case 8:
                mBorderView.setPenType_8();
                break;
            default:
                mBorderView.setPenType_1();
                break;

        }
    }
}
