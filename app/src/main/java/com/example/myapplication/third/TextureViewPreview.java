package com.example.myapplication.third;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * Copyright © 2021/11/17  16:48
 * description
 * author: 陈汉三
 */

class TextureViewPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private Camera mCamera;

    public TextureViewPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            setLayoutParams(new FrameLayout.LayoutParams(size.width,size.height, Gravity.CENTER));
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            //setAlpha(0.5f);
            setRotation(90f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
