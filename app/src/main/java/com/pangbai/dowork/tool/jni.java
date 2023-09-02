package com.pangbai.dowork.tool;

import android.view.Surface;

public class jni {

    public static native String startx(Surface face);
    public static native int init(boolean state ,String ip ,int id);
    public static native String xclick(int x,int y,boolean isleft);
  //  public  static native String inputKey(int x,String s);

    public  static native int initxvfb(String screen);

    public static native void stopDraw();
    public  static native int startdraw(Surface face,int a);
    public static  native  int fullScreen(int height,int width);
    public static  native  int mouseScroll(boolean up);
    public static native int inputString(String str);
    public static native  int pressKey(int key);
    public static native  int inputKeyByString(String key);
    public static native int cursorControl(int statue,int x,int y);

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

