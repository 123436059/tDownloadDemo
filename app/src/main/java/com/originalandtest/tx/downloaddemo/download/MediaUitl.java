package com.originalandtest.tx.downloaddemo.download;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;


public class MediaUitl {
    // private static final String TAG = "AdUtils";
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VOICE = 2;
    public static final int TYPE_DLOAD = 3;

    public static Bitmap getScaleImg(Bitmap bitmap, int newWidth, int newHeight) {

        if (bitmap == null) {
            return null;
        }
        Bitmap bm = bitmap;

        if (newWidth == 0 || newHeight == 0) {
            return bm;
        }
        // 图片源
        // Bitmap bm = BitmapFactory.decodeStream(getResources()
        // .openRawResource(ad_info_id));
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth1 = newWidth;
        int newHeight1 = newHeight;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth1) / width;
        float scaleHeight = ((float) newHeight1) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        if (bm != null && bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return newbm;
    }

    public static Bitmap getOriginalBitmap(Context context, String url) {
        return BitmapFactory.decodeFile(getAdPicturePath(context, url));
    }

    public static String getAdName(String url) {
        if (url == null || "".equals(url)) {
            return null;
        }
        return String.valueOf(url.hashCode());// Utils.md5(url)
    }

    public static String getFileName(String url){
        if (url == null || "".equals(url)) {
            return null;
        }
        // 从路径中获取
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static String getAdPicturePath(Context context, String url) {
        if (url == null) {
            return null;
        }
        return PathUtil.getVoicePath(context) + getAdName(url);
    }

//	public static String getAdPicturePath(Context context) {
//		return Configuration.PIC_ROOT_PATH;
//	}

    public static String getNetworkVoicePath(Context context, String url) {
        if (url == null) {
            return null;
        }
        return PathUtil.getVoicePath(context) + getAdName(url);
    }

    public static String getNetworkDownloadPath(Context context, String url){
        if (url == null) {
            return null;
        }
        return PathUtil.getDownloadPath(context) + getFileName(url);
    }

    public static String getLocalVoicePath(Context context) {
        return PathUtil.getVoicePath(context);
    }


    public static boolean checkAdPicExist(Context context, String url) {
        if (url == null || "".equals(url)) {
            return false;
        }
        return new File(getAdPicturePath(context,url)).exists();
    }

    public static String identifyFileType(String path) {
        // 文件类型
        String fileType = "";
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] buffer = new byte[2];
            // 文件类型代码
            String filecode = "";

            // 通过读取出来的前两个字节来判断文件类型
            if (inputStream.read(buffer) != -1) {
                for (int i = 0; i < buffer.length; i++) {
                    // 获取每个字节与0xFF进行与运算来获取高位，这个读取出来的数据不是出现负数
                    // 并转换成字符串
                    filecode += Integer.toString((buffer[i] & 0xFF));
                }
                // 把字符串再转换成Integer进行类型判断
                switch (Integer.parseInt(filecode)) {
                    case 7790:
                        fileType = "exe";
                        break;
                    case 7784:
                        fileType = "midi";
                        break;
                    case 8297:
                        fileType = "rar";
                        break;
                    case 8075:
                        fileType = "zip";
                        break;
                    case 255216:
                        fileType = "jpg";
                        break;
                    case 7173:
                        fileType = "gif";
                        break;
                    case 6677:
                        fileType = "bmp";
                        break;
                    case 13780:
                        fileType = "png";
                        break;
                    default:
                        fileType = "unknown type: " + filecode;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileType = "unknown type";
        }
        return fileType;
    }

    public static String parseLength(long bytes) {
        if (bytes < 0) {
            return "大小： ";
        } else if (bytes < 1024) {
            return "大小： " + bytes + "B";
        } else if (bytes < 1024 * 1024) {
            float f = (float) bytes / 1024;
            return subFloat(f) + "KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            float f = (float) bytes / 1024 / 1024;
            return subFloat(f) + "MB";
        } else {
            float f = (float) bytes / 1024 / 1024 / 1024;
            return subFloat(f) + "GB";
        }
    }

    private static String subFloat(float f) {
        final String data = String.valueOf(f);
        int index = data.indexOf(".");
        if (-1 == index || (data.length() - index <= 3)) {
            return data;
        }
        return "大小： " + data.substring(0, index + 3);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
