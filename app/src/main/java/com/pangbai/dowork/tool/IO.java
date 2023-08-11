package com.pangbai.dowork.tool;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.pangbai.dowork.Command.cmdExer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IO {
    public static boolean isExistFile(String File){
        return new File(File).exists();
    }
    public  static  long dealtByte =0;
    public static long finalByte=0;

    public static void copyAssetsDirToSDCard(Context context, String assetsDirName, String sdCardPath) {
        //   Log.d(TAG, "copyAssetsDirToSDCard() called with: context = [" + context + "], assetsDirName = [" + assetsDirName + "], sdCardPath = [" + sdCardPath + "]");
        try {
            String list[] = context.getAssets().list(assetsDirName);
            if (list.length == 0) {
                InputStream inputStream = context.getAssets().open(assetsDirName);
               // finalByte= inputStream.available();
                byte[] mByte = new byte[1024];
                int bt = 0;
                File file = new File(sdCardPath + File.separator
                        + assetsDirName.substring(assetsDirName.lastIndexOf('/')));
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(file);
                while ((bt = inputStream.read(mByte)) != -1) {
                    fos.write(mByte, 0, bt);
                   // dealtByte+=bt;
                }
                fos.flush();
                inputStream.close();
                fos.close();
            } else {
                String subDirName = assetsDirName;
                if (assetsDirName.contains("/")) {
                    subDirName = assetsDirName.substring(assetsDirName.lastIndexOf('/') + 1);
                }
                sdCardPath = sdCardPath + File.separator + subDirName;
                File file = new File(sdCardPath);
                if (!file.exists())
                    file.mkdirs();
                for (String s : list) {
                    copyAssetsDirToSDCard(context, assetsDirName + File.separator + s, sdCardPath);
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

//废弃方法
    public static void copyASFile(Context ct,String filename,String newFileName) {
        AssetManager assetManager = ct.getAssets();
        //Log.e(getApplication().toString(),filename);
        InputStream in = null;

        OutputStream out = null;

        try {
            //     String newFileName = "/storage/emulated/0/Android/data/"+ct. getPackageName() +"/files/"+filename;
            //  Log.e(getApplication().toString(),newFileName);
            //   newFileName= newFileName.replace("/gal","/");
            in = assetManager.open(filename);
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                //dealtByte+=read;
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("copyFile", e.getMessage());
            System.exit(1);
        }
    }



        public static boolean deleteFolder(File folder) {
             boolean result=  cmdExer.execute("rm -rf "+folder.getAbsolutePath(),Init.isRoot);
             return result;
        }

        public static String countDirSize(String path){
        boolean result= cmdExer.execute("du -sh "+path,Init.isRoot);
        if (result)
            return cmdExer.lastLine;
        else
            return null;
        }



}
