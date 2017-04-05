package com.originalandtest.tx.downloaddemo;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.originalandtest.tx.downloaddemo.download.DownladManager;
import com.originalandtest.tx.downloaddemo.download.MediaUitl;

import java.io.File;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {
    private String path = "http://imtt.dd.qq.com/16891/756EE19C6AAB03B58BBE742DFAD138C1.apk?fsname=com.tencent.movieticket_7.5.1_77.apk&csr=1bbd";
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private final static int mId = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        findViewById(R.id.downLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("taxi", "onCreate pid=" + Process.myPid());
    }

    private void initPermission() {
        PermissionGen.with(this).addRequestCode(100).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    @PermissionSuccess(requestCode = 100)
    public void onPRSuc() {

    }

    @PermissionFail(requestCode = 100)
    public void onPRFail() {
        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
    }


    private void download() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("下载中");
        final UpdateThread updateThread = new UpdateThread();
        updateThread.start();
        DownladManager.getInstance(this).downloadVideo(path, MediaUitl.TYPE_DLOAD, new DownladManager.VideoCacheManagerDownLoadListener() {
            @Override
            public void onDownLoadSucceed(String filePath) {
                Log.e("taxi", "Activity下载成功");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "suc", Toast.LENGTH_SHORT).show();
                        //TODO 自动调用apk
                        mBuilder.setContentText("下载完成");
                        mBuilder.setProgress(0, 0, true);
                        notificationManager.notify(mId, mBuilder.build());
                    }
                });

                //直接安装应用。
                installApk(filePath);

            }

            @Override
            public void onDownLoading(int progress) {
                //TODO 通知栏显示进度
//                sendNotification(progress);
//                notificationManager.notify(mId, mBuilder.build());
                updateThread.update(progress);
            }

            @Override
            public void onDownLoadFailure() {
                Log.d("taxi", "下载失败");
            }
        });
    }

    private void installApk(String filePath) {
        Log.d("taxi", "install path="+filePath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void sendNotification(int progress) {
        if (mBuilder == null) {
            return;
        }

        //每500ms执行一次
        mBuilder.setProgress(100, progress, false);
        mBuilder.setContentText("当前进度:" + progress + "%");
        notificationManager.notify(mId, mBuilder.build());
    }


    class UpdateThread extends Thread {
        UpdateThread() {
            isRunning = true;
        }

        private int progress;

        private boolean isRunning = false;

        void update(int progress) {
            this.progress = progress;
        }


        @Override
        public void run() {
            while (isRunning) {
                loop();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void loop() {
            //发消息改变
            if (progress < 100) {
                mBuilder.setProgress(100, progress, false);
                mBuilder.setContentText("当前进度：" + progress + "%");
                notificationManager.notify(mId, mBuilder.build());
            } else if (progress == 100) {
                mBuilder.setProgress(100, progress, false);
                mBuilder.setContentText("当前进度：" + progress + "%");
                Log.e("taxi", "progress =100,停止线程-----------");
                isRunning = false;
            }
        }
    }
}

