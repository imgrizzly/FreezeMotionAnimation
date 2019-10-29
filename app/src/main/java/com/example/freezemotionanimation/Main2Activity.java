package com.example.freezemotionanimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "Main2Activity";
    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHoler;
    private ImageView mImageView;
    private String mFilePath;
    private String sFileName;
    private Switch aSwitch;
    private List <String> lOnionSkin  = new ArrayList<>();
    private int iCaptureNum = 0;


    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "sFileName: " + sFileName);
            try {
                FileOutputStream fos = new FileOutputStream(sFileName);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "onPictureTaken: ");

            if (mCamera == null) {
                mCamera = getCamera();
                if (mCamera != null) {
                    setPreview(mCamera, mHoler);
                }
            } else {
                setPreview(mCamera, mHoler);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
//                    aSwitch.setText("Only Today's");
                    try {
                        FileInputStream fis = null;
                        fis = new FileInputStream(lOnionSkin.get(0));
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.d("You are :", "Checked");
                }
                else {
                    FileInputStream fis = null;
                    mImageView.setImageBitmap(null);
                    Log.d("You are :", " Not Checked");
                    try {
                        fis = new FileInputStream(lOnionSkin.get(1));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });
        mPreview = findViewById(R.id.mPreview);
        mImageView = findViewById(R.id.mImageView);
//        mImageView.setAlpha(0);
        Log.d(TAG, "onCreate: surfaceview layout size is" + mPreview.getLayoutParams().width + "," + mPreview.getLayoutParams().height);
//        mPreview.setLayoutParams(new FrameLayout.LayoutParams((int) 600, 600));

        mHoler = mPreview.getHolder();
        mHoler.addCallback(this);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.autoFocus(null);
            }
        });
        mFilePath = getFilesDir().getPath();
//        mFilePath = mFilePath + "/" + "temp.png";
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mCamera != null) {
                setPreview(mCamera, mHoler);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void capture(View view) {
        iCaptureNum = iCaptureNum + 1;
        sFileName = mFilePath + "/" + "temp"+iCaptureNum + ".png";
        lOnionSkin.add(sFileName);

        Log.d(TAG, "onCreate: surfaceview layout size is" + mPreview.getLayoutParams().width + "," + mPreview.getLayoutParams().height);

        Camera.Parameters parameters = mCamera.getParameters();
        Log.d(TAG, "now preview width is " + parameters.getPreviewSize().width);
        Log.d(TAG, "now preview height is " + parameters.getPreviewSize().height);

        List<Camera.Size> allPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = allPreviewSizes.get(0); // get top size
        for (int i = 0; i < allPreviewSizes.size(); i++) {
            if (allPreviewSizes.get(i).width > size.width)
                size = allPreviewSizes.get(i);
            Log.d(TAG, "line 127 capture: " + allPreviewSizes.get(i).width + ", " + allPreviewSizes.get(i).height);
        }

        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        Camera.Size size1 = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size1.width)
                size1 = allSizes.get(i);
            Log.d(TAG, "line 126capture: " + allSizes.get(i).width + ", " + allSizes.get(i).height);
        }
        parameters.setPictureFormat(ImageFormat.JPEG);
//        parameters.setPreviewSize(1920, 1080);
//        try {
//            mCamera.setParameters(parameters);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        parameters.setPictureSize(1920, 1080);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if (b) {
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        });

    }


    /*获取相机对象*/
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    /*
     * 预览
     * */
    private void setPreview(Camera camera, SurfaceHolder holder) {
        try {
            Log.d(TAG, "onCreate: surfaceview layout size is" + mPreview.getLayoutParams().width + "," + mPreview.getLayoutParams().height);

            /*设置预览角度*/
            camera.setDisplayOrientation(90);
            /**
             * 调节预览大小
             */
//            Camera.Parameters parameters = camera.getParameters();
//            parameters.setPreviewSize(720, 720);
//            try {
//                camera.setParameters(parameters);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            Log.d(TAG, "setPreview: ");
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*释放相机对象*/
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setPreview(mCamera, mHoler);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.stopPreview();
        setPreview(mCamera, mHoler);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

}