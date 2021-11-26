package com.example.myapplication.third;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 在 Android Camera进行视频的采集，
 * 使用 TextureView  来预览 Camera 数据，取到 NV21 的数据回调
 */
public class ThirdActivity2 extends AppCompatActivity {
    private ActivityResultLauncher launcher;
    private FrameLayout frameLayout2;
    private Camera camera;
    private ImageView preImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third2);
        frameLayout2 = findViewById(R.id.frameLayout2);
        preImage=findViewById(R.id.preImage);
        camera=Camera.open();
        getSupportFragmentManager().beginTransaction().add(new ThirdFragment2(), "ss").commit();

        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    TextureViewPreview preview = new TextureViewPreview(ThirdActivity2.this, camera);
                    frameLayout2.addView(preview);
                }
            }
        });

        Camera.Parameters parameters = camera.getParameters();
        //设置采集视频格式
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);
        //设置视频采集的回调
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                //这里的data就是NV21数据  ,这里将获取到的帧数据转换为bitmap
                // tip :这里要注意的是，录制视频（ mCamera.unlock();）是不能采集帧图片的
                Log.i("onPreviewFrame==","采集的视频数据=="+data);
                try {
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0,0,size.width,size.height),90,stream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, stream.size());
                    stream.close();
                    preImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openCamera(View view) {
        launcher.launch(Manifest.permission.CAMERA);
    }
}