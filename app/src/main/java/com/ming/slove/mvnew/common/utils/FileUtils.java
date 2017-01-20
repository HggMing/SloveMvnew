package com.ming.slove.mvnew.common.utils;

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
}
