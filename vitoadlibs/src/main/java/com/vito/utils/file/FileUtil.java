package com.vito.utils.file;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.net.URI;

public class FileUtil {

    public static boolean deleteFile(URI uri){
        File file = new File(uri);
        if (file.exists()){
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        if (file.exists()){
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean deleteFile(Uri uri){
        File file = new File(uri.toString());
        if (file.exists()){
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean isFileExists(URI uri){
        File file = new File(uri);
        return file.exists();
    }

    public static boolean isFileExists(Uri uri){
//        File file = new File(uri.toString());
        File file = new File(uri.getPath());
        return file.exists();
    }


    public static File getDestinationInExternalPublicDir(Context context,String dirType) {
        File file = context.getExternalFilesDir(dirType);
        //File file = Environment.getExternalStoragePublicDirectory(dirType);
        if (file == null) {
            throw new IllegalStateException("Failed to get external storage public directory");
        } else if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalStateException(file.getAbsolutePath() +
                        " already exists and is not a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Unable to create directory: "+
                        file.getAbsolutePath());
            }
        }
        return file;
    }

}
