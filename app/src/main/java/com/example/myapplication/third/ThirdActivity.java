package com.example.myapplication.third;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 在 Android Camera进行视频的采集，
 * 分别使用 SurfaceView、TextureView 来预览 Camera 数据，取到 NV21 的数据回调
 */
public class ThirdActivity extends AppCompatActivity {
    private MaterialButton openCamera;
    private FrameLayout frameLayout;
    private Camera camera;
    private ActivityResultLauncher<String> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        openCamera = findViewById(R.id.openCamera);
        frameLayout = findViewById(R.id.frameLayout);
        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                
                if (result) {
                    CameraPreview cameraPreview = new CameraPreview(ThirdActivity.this, Camera.open());
                    frameLayout.addView(cameraPreview);
                }
            }
        });
        initClick();
    }

    private void initClick() {
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(Manifest.permission.CAMERA);
            }
        });
    }
}