package com.ming.slove.mvnew.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MingN on 2017/1/17.
 */

public class FileUtils {

    public static File createMediaFile(String path) {
        File folderDir = makeDirNoMedia(path);
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date()) + ".JPEG";
        return new File(folderDir, fileName);
    }

    public static File makeDirNoMedia(String storagePath) {
        File f = new File(storagePath);
        if (!f.exists()) {
            boolean a = f.mkdirs();
            try {
                boolean b = new File(storagePath, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    /**
     * 通过uri获取文件路径
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 文件路径转Uri
     * @param context
     * @param path
     * @return
     */
    public static Uri FilePathToUri(Context context, String path) {

        if (path != null) {
            path = Uri.decode(path);
            ContentResolver cr = context.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(")
                    .append(MediaStore.Images.ImageColumns.DATA)
                    .append("=")
                    .append("'" + path + "'")
                    .append(")");
            Cursor cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID},
                    buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur
                    .moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                // set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
                //do nothing
            } else {
                Uri uri_temp = Uri
                        .parse("content://media/external/images/media/"
                                + index);
                if (uri_temp != null) {
                    return uri_temp;
                }
            }
        }
        return null;
    }
}
