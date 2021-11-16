package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 在 Android 平台使用 AudioRecord 和 AudioTrack API 完成音频 PCM 数据的采集和播放，
 * 并实现读写音频 wav 文件
 */
public class SecondActivity extends AppCompatActivity {

    private MaterialButton recordBtn, stopBtn,playBtn;
    //采样音频
    private AudioRecord audioRecord;
    //录音权限
    private static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    //采样率  44.1khz 也就是一秒采样44100次
    private static final int sampleRateInHz = 44100;
    //声道数
    private static final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    //返回的音频格式都是  pcm格式，不同的地方在于采样位数不同
    //采样位数可以理解为-------图片的分辨率
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //是否正在采样
    private boolean isRecording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        recordBtn = findViewById(R.id.recordBtn);
        stopBtn = findViewById(R.id.stopBtn);
        playBtn = findViewById(R.id.playBtn);
        //采集音频，不经过编码与压缩处理，最原始的PCM格式
        int audioRecordBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        //用来缓存录制音频数据
        byte[] buffSize = new byte[audioRecordBufferSize];
        //允许系统管理权限请求
        ActivityResultLauncher<String[]> resultLauncher = permissionResult(audioRecordBufferSize, buffSize);
        //点击事件处理
        click(resultLauncher);
    }


    private ActivityResultLauncher<String[]> permissionResult(int minBufferSize, byte[] buffSize) {
        return registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean isGranted = result.get(RECORD_AUDIO);
                    if (isGranted) {
                        Log.i("AudioRecord==", "。。。");
                        //开始采样声音
                        if (audioRecord == null) {
                            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                    sampleRateInHz,
                                    channelConfig,
                                    audioFormat,
                                    minBufferSize);
                        }
                        audioRecord.startRecording();
                        isRecording = true;
                        //将采样音频写入pcm文件
                        writeAudioToFile(minBufferSize, buffSize);
                    } else {
                        Log.i("AudioRecord==", "权限申请失败。。。");
                    }
                });
    }

    private void writeAudioToFile(int minBufferSize, byte[] buffSize) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int data = 0;
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(createPCMFile()));
                    while (data != AudioRecord.ERROR_INVALID_OPERATION) {
                        if (isRecording) {
                            data = audioRecord.read(buffSize, 0, minBufferSize);
                            bos.write(buffSize, 0, data);
                        } else {
                            return;
                        }
                        Log.i("AudioRecord==", "采样位置---" + data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void click(ActivityResultLauncher<String[]> resultLauncher) {
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //权限申请的回调
                resultLauncher.launch(new String[]{RECORD_AUDIO});
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecord.stop();
                isRecording = false;
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AudioRecord 采样的音频 需要 AudioTrack 进行播放
                createAudioTrack();
            }
        });
    }

    private File createPCMFile() {
        String path = getExternalCacheDir().getAbsolutePath() + File.separator + "采样文件";
        File parentFile = new File(path);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File file = new File(path, "test.pcm");
        Log.i("AudioRecord==", "pcm文件位置---" + file.getAbsolutePath());
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    byte[] audioData = new byte[]{};

    /**
     * 播放音视频
     */
    @SuppressLint("StaticFieldLeak")
    private void createAudioTrack() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String path = getExternalCacheDir().getAbsolutePath() + File.separator + "采样文件";
                File file = new File(path, "test.pcm");
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int len ;
                    while ((len = inputStream.read()) != -1) {
                        outputStream.write(len);
                    }
                    audioData = outputStream.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                AudioTrack track = new AudioTrack(
                        new AudioAttributes.Builder().build(),
                        new AudioFormat.Builder().build(),
                        audioData.length,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE);
                track.write(audioData, 0, audioData.length);
                track.play();
            }
        }.execute();
    }
}