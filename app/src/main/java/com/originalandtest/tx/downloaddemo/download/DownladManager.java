package com.originalandtest.tx.downloaddemo.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;


public class DownladManager {
    private static DownladManager mVideoCacheManager;

    private static final String TAG = "VideoCacheManager";

    private Map<String, DownLoadTask> mVideoDownLoadList = new HashMap<String, DownLoadTask>();

    private Context mContext;

    public static interface VideoCacheManagerDownLoadListener {
        void onDownLoadSucceed(String filePath);
        void onDownLoading(int progress);
        void onDownLoadFailure();
    }

    private DownladManager(Context context) {
        mContext = context;
    }

    public static synchronized DownladManager getInstance(Context context) {
        if (null == mVideoCacheManager) {
            mVideoCacheManager = new DownladManager(context);
        }
        return mVideoCacheManager;
    }

    public void downloadVideo(String url, VideoCacheManagerDownLoadListener listener){
        downloadVideo(url, MediaUitl.TYPE_VOICE, listener);
    }

    public void downloadVideo(final String url, int type, final VideoCacheManagerDownLoadListener listener) {
        if (url == null || url.equals("")) {
            if (listener != null) {
                listener.onDownLoadFailure();
                return;
            }
        }

        if (!TextUtils.isEmpty(loadVideoFromSdcard(url,type))) {
            if (Configuration.DEBUG_VERSION) {
                Log.d(TAG, "video has download!!!!");
            }
            if (listener != null) {
                listener.onDownLoadSucceed(loadVideoFromSdcard(url,type));
                return;
            }
        }

        DownLoadTask item = mVideoDownLoadList.get(url);
        if (item != null) {
            if (Configuration.DEBUG_VERSION) {
                Log.d(TAG, "video:" + url + " is downloading!!!");
            }
            return;
        }

        //开启handler设置

        item = new DownLoadTask(new DownLoadTask.VideoLoadListener() {

            @Override
            public void onDownLoadSucceed(String filePath) {
                mVideoDownLoadList.remove(url);
                if (Configuration.DEBUG_VERSION) {
                    Log.d(TAG, "video download complete!!!");
                }

                if (listener != null) {
                    Log.e("taxi", "manager finish");
                    listener.onDownLoadSucceed(filePath);
                }


                Log.w("taxi", "download pid ="+Process.myPid());
            }

            @Override
            public void onDownLoadFailure() {
                if (Configuration.DEBUG_VERSION) {
                    Log.d(TAG, "video download failure!!!");
                }
                mVideoDownLoadList.remove(url);
                if (listener != null) {
                    listener.onDownLoadFailure();
                }
            }

            @Override
            public void onDownloadPause() {

            }

            @Override
            public void onDownloadCancel() {

            }

            @Override
            public void onDownLoading(int progress) {
                if(listener != null){
                    listener.onDownLoading(progress);
                }
            }
        }, url, type, mContext);

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);
        DefaultHttpClient httpClient = new DefaultHttpClient(params);
        item.httpClient = httpClient;
        mVideoDownLoadList.put(url, item);
        startDownloadVideo(item);
    }

    private void startDownloadVideo(DownLoadTask item) {
        ThreadPoolExecutorManger.getInstance().getVideoExecutor().execute(item);
    }

    private String loadVideoFromSdcard(String url, int type) {
        String targetPath = null;
        switch(type){
            case MediaUitl.TYPE_IMAGE:
                targetPath = MediaUitl.getAdPicturePath(mContext, url);
                break;
            case MediaUitl.TYPE_VOICE:
                targetPath = MediaUitl.getNetworkVoicePath(mContext, url);
                break;
            case MediaUitl.TYPE_DLOAD:
                targetPath = MediaUitl.getNetworkDownloadPath(mContext, url);
                break;
            default:
                targetPath = MediaUitl.getNetworkVoicePath(mContext, url);
        }

        if (new File(targetPath).exists()) {
            return targetPath;
        }
        return "";
    }
}
