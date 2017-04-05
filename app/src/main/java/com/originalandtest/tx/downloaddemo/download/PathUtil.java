package com.originalandtest.tx.downloaddemo.download;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class PathUtil {
//	private static final String BASEPATH = "/hywatch/";

    public static String getRootFilePath(Context context) {
        StorageManager storageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            String[] paths = (String[]) storageManager.getClass()
                    .getMethod("getVolumePaths", null)
                    .invoke(storageManager, null);
            for (int i = 0; i < paths.length; i++) {
                Log.e("locke", "paths[" + i + "]:" + paths[i]);
                if (checkFileWriteAble(paths[i])) {
                    return paths[i];
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "SDCard不可用！", Toast.LENGTH_SHORT).show();
        return null;
    }

    private static boolean checkFileWriteAble(String filePath) {
        return new File(filePath).canWrite();
    }

    public static String getExternCachePath(Context context) {
        // sdcard
        String info = context.getPackageName();
        String rootPath = getRootFilePath(context);
        if (TextUtils.isEmpty(rootPath)) {
            return "";
        }

        StringBuffer bf = new StringBuffer(rootPath);
        bf.append("/z_taxi_data/");
        bf.append(info);
        bf.append("/");

        String filePath = bf.toString();
        File file = new File(filePath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return filePath;
            }
        }
        return filePath;
    }

    public static String getFaceCachePath(Context context) {
        String path = getExternCachePath(context);
        path += "face.png";
        return path;
    }

    public static String getImageCachePath(Context context) {
        StringBuffer bf = new StringBuffer(getExternCachePath(context));
        bf.append("image/");
        String filePath = bf.toString();
        Log.e("locke", "filePath:" + filePath);

        if (new File(filePath).mkdirs()) {
            return filePath;
        }
        return filePath;
    }

    public static String getDiscoverCachePath(Context context) {
        String path = getExternCachePath(context);
        path += "discover.png";
        return path;
    }

    public static String getVoicePath(Context context) {
        StringBuffer bf = new StringBuffer(getExternCachePath(context));
        bf.append("voice/");
        String filePath = bf.toString();
        Log.e("locke", "filePath:" + filePath);

        if (new File(filePath).mkdirs()) {
            return filePath;
        }
        return filePath;
    }

    public static String getDownloadPath(Context context) {
        StringBuffer bf = new StringBuffer(getExternCachePath(context));
        bf.append("download/");
        String filePath = bf.toString();
        Log.e("locke", "filePath:" + filePath);

        if (new File(filePath).mkdirs()) {
            return filePath;
        }
        return filePath;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        if (uri == null) {
            return null;
        }

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
