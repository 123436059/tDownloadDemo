package com.originalandtest.tx.downloaddemo.download;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownLoadTask implements Runnable {
    private static final String TAG = "VideoDoanLoadTask";

    private int mMediaType = MediaUitl.TYPE_VOICE;
    public String mUrl;

    long downloadPosition;

    boolean mPauseRequired;

    boolean mCancelRequired;

    public HttpClient httpClient;

    private Context mContext;

    private static final int BUFFER = 4096;

    private VideoLoadListener mListener;

    public interface VideoLoadListener {

        void onDownLoadSucceed(String filePath);
        void onDownLoading(int progress);
        void onDownLoadFailure();
        void onDownloadPause();
        void onDownloadCancel();
    }

    public DownLoadTask(VideoLoadListener listener, String url, Context context) {
        this(listener, url, MediaUitl.TYPE_VOICE, context);
    }

    public DownLoadTask(VideoLoadListener listener, String url, int mediaType, Context context) {
        mContext = context;
        mListener = listener;
        mUrl = url;

        switch(mediaType){
            case MediaUitl.TYPE_IMAGE:
            case MediaUitl.TYPE_VOICE:
            case MediaUitl.TYPE_DLOAD:
                this.mMediaType = mediaType;
                break;
            default:
                this.mMediaType = MediaUitl.TYPE_VOICE;
        }

        determineStatusAndProgress();

        disableConnectionReuseIfNecessary();
    }

    private String getFilePath(){
        String path = null;
        switch(mMediaType){
            case MediaUitl.TYPE_IMAGE:
                path = MediaUitl.getNetworkVoicePath(mContext, mUrl);
                break;
            case MediaUitl.TYPE_VOICE:
                path = MediaUitl.getAdPicturePath(mContext, mUrl);
                break;
            case MediaUitl.TYPE_DLOAD:
                path = MediaUitl.getNetworkDownloadPath(mContext, mUrl);
                break;
        }
        return path;
    }

    private void determineStatusAndProgress() {

        File file = new File(getFilePath() + ".temp");
        if (file.isFile() && file.exists()) {
            downloadPosition = file.length();
        } else {
            file.getParentFile().mkdirs();
        }
    }
/*不过在Android 2.2版本之前, HttpURLConnection一直存在着一些令人厌烦的bug.
 比如说对一个可读的InputStream调用close()方法时，就有可能会导致连接池失效了。
 那么我们通常的解决办法就是直接禁用掉连接池的功能：*/
    private void disableConnectionReuseIfNecessary() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    @Override
    public void run() {
        FileOutputStream fos = null;
        InputStream is = null;
        HttpGet request = null;
        try {
            request = new HttpGet(mUrl);
            request.setHeader("Connection", "keep-alive");
            if (downloadPosition > 0) {
                request.setHeader("Range", "bytes=" + downloadPosition + "-");
            }
            onStart();
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (null == entity) {
                throw new IOException("entity is null");
            }

            is = entity.getContent();
            if (null == is) {
                throw new IOException("inputstream is null");
            }

            long length = entity.getContentLength();
            if (Configuration.DEBUG_VERSION) {
                Log.i(TAG, "content length is " + length);
            }

            fos = new FileOutputStream(getFilePath() + ".temp", true);

            byte[] buffer = new byte[BUFFER];
            int bytesRead = is.read(buffer);

            //加一个控制的标识。 暂停，加入数据库 。
            while (-1 != bytesRead) {
                boolean pauseRequired = false;
                boolean cancelRequired = false;
                synchronized (this) {
                    pauseRequired = mPauseRequired;
                    cancelRequired = mCancelRequired;
                }
                if (!pauseRequired) {
                    fos.write(buffer, 0, bytesRead);
                    downloadPosition += bytesRead;
                    bytesRead = is.read(buffer);
                } else {
                    if (cancelRequired) {
                        onCancel();
                    } else {
                        boolean complete = -1 == is.read();
                        if (complete) {
                            reameTo(mUrl);
                            onComplete();
                        } else {
                            onPause();
                        }
                    }
                    break;
                }
                if(mListener != null){
                    mListener.onDownLoading((int) ((100*downloadPosition)/length));
                    Log.e("taxi",(int) ((100*downloadPosition)/length)+"" );
                }
            }

            //判断download file大小，如果下载成功，则成功。
            Log.e("taxi", "1");
            boolean pauseRequired = false;
            boolean cancelRequired = false;
            synchronized (this) {
                pauseRequired = mPauseRequired;
                cancelRequired = mCancelRequired;
            }



            if (!pauseRequired) {
                if (cancelRequired) {
                    onCancel();
                } else {
                    Log.e("taxi", "2");
                    //加完整性判断
                    onComplete();
                    Log.e("taxi", "3");
                    reameTo(mUrl);
                    Log.e("taxi", "4");
                }
            }
        } catch (Exception e) {
            if (Configuration.DEBUG_VERSION) {
                e.printStackTrace();
            }
            onException();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
            if (null != request) {
                request.abort();
            }
            if (null != is) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                }
            }
        }
    }

    private void onStart() {

    }

    private void onException() {
        if (mListener != null) {
            mListener.onDownLoadFailure();
            downloadPosition = 0;
        }
    }

    private void onCancel() {
        if (mListener != null) {
//            mListener.onDownLoadFailure();
            mListener.onDownloadCancel();
        }
    }

    private void onPause() {
        if (mListener != null) {
            mListener.onDownloadPause();
        }
    }

    private void onComplete() {
        if (mListener != null) {
            Log.e("taxi", "finish---------------");
            mListener.onDownLoadSucceed(getFilePath());
            downloadPosition = 0;
        }
    }

    private synchronized boolean reameTo(String url) {
        final String targetPath = getFilePath();
        final String tempPath = getFilePath() + ".temp";
        File file = new File(tempPath);
        if (file.exists()) {
            return file.renameTo(new File(targetPath));
        } else {
            return false;
        }
    }

}
