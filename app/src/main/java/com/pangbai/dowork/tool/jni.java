package com.pangbai.dowork.tool;

import android.view.Surface;

public class jni {

    public static native String startx(Surface face);
    public static native int init(boolean state ,String ip ,int id);
    public static native String movePoint(int x,int y);
    public  static native String inputKey(int x,String s);

    public  static native int initxvfb();
    public  static native int startdraw(Surface face,int a);


    static {
//        System.loadLibrary("android-ash");
        System.loadLibrary("Xau");
        System.loadLibrary("Xdmcp");
        System.loadLibrary("xdraw");
        System.loadLibrary("bz2");


        //	System.loadLibrary("crypto");


        System.loadLibrary("xserver");

    }
}

