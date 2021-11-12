package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 在 Android 平台绘制一张图片，使用SurfaceView绘制图片
 */
public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                //设置抗锯齿
                Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setAntiAlias(true);
                //通过这个方法获取
                Canvas canvas = holder.lockCanvas();
                //tip :需要在画布上设置背景颜色，
                // 如果在xml中设置SurfaceView的background 会把bitmap覆盖掉
                canvas.drawColor(Color.YELLOW);
                BitmapFactory.Options options
                        =new BitmapFactory.Options();
                options.inJustDecodeBounds=true;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher2,options);
                int bw = options.outWidth;
                int bh = options.outHeight;
                options.inJustDecodeBounds=false;
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher2,options);
                canvas.drawBitmap(bitmap, surfaceView.getWidth()/2-bw/2,surfaceView.getHeight()/2-bh/2,paint);
                //解除锁定将画面显示在界面上
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }

        });
    }
}