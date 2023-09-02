
#include <X11/Xutil.h>
#include <X11/extensions/XTest.h>

#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include <xdraw.h>
#include <jni.h>
#include <unistd.h>
#include <X11/keysym.h>
#include<X11/Xatom.h>
#include <pthread.h>
#include "clipboard.h"
#include "X11/cursorfont.h"
//extern  Display* display;


JNIEXPORT jstring JNICALL
Java_com_pangbai_dowork_tool_jni_xclick(JNIEnv *env, jobject thiz, jint x, jint y, bool isleft) {
    xclick(x, y, isleft);

    return NULL;
}

JNIEXPORT jstring JNICALL
Java_com_pangbai_dowork_tool_jni_inputKey(JNIEnv *env, jobject thiz, jint mode, jstring str) {
    char *key;
    key = (*env)->GetStringUTFChars(env, str, 0);

    Atom CLIPBOARD = XInternAtom(mdisplay, "CLIPBOARD", True);

    XSetSelectionOwner(mdisplay, CLIPBOARD, None, CurrentTime);

//XSelectionRequestEvent* req = &event.xselectionrequest;
    //  XChangeProperty(display, desktop,CLIPBOARD , XA_STRING, 8, PropModeReplace, (unsigned char *)key, 6);

    /*  XEvent re = {0};
      re.xselection.type = SelectionNotify;
      re.xselection.display = req->display;
      re.xselection.requestor = req->requestor;
      re.xselection.selection = req->selection;
      re.xselection.property = req->property;
      re.xselection.target = req->target;
      XSendEvent(display, req->requestor, 0, 0, &re); // event is sent, but data is not in my clipboard
    */
    XFlush(mdisplay);
    // XStoreBytes(display, key, 7);

    presskey(mdisplay, XK_Shift_L);
    presskey(mdisplay, XK_Insert);


    releasekey(mdisplay, XK_Shift_L);

    releasekey(mdisplay, XK_Insert);

/*
  presskey(display,XK_v) ;
  releasekey(display, XK_v);int presskey(Display *dsp,int s)

  */

    /*    KeySym keysym = XStringToKeysym(key);
      KeyCode keycode = NoSymbol;

      keycode = XKeysymToKeycode( display , keysym );
   if(keycode==NoSymbol)
          return NULL;

      XTestFakeKeyEvent( display , keycode , True  , 0 ); // 键盘按下event
       XTestFakeKeyEvent( display , keycode , False , 0 ); // 键盘释放event
     XFlush( display );
     */
    (*env)->ReleaseStringUTFChars(env, str, key);

    return NULL;
}


/***
*
屏幕触摸板
*/


int xclick(int x, int y, bool isleft) {
    XEvent event;
    int key;

    /* get info about current pointer position */
    XQueryPointer(mdisplay, RootWindow(mdisplay, DefaultScreen(mdisplay)),
                  &event.xbutton.root, &event.xbutton.window,
                  &event.xbutton.x_root, &event.xbutton.y_root,
                  &event.xbutton.x, &event.xbutton.y,
                  &event.xbutton.state);

    XTestFakeMotionEvent(mdisplay, -1, x, y, 0);
    if (isleft)
        key = 1;
    else
        key = 3;
    XTestFakeButtonEvent(mdisplay, key, 1, 0);
    XTestFakeButtonEvent(mdisplay, key, 0, 0);
    /* place the mouse where it was */
    // XTestFakeMotionEvent(mdisplay, -1, event.xbutton.x, event.xbutton.y, 0);
    return 0;
}


/**
 * 模拟鼠标滚动
 */


/**
*按下事件*
     */

int presskey(Display *dsp, int s) {  //键盘按
    if (dsp == NULL)
        return -1;
    KeyCode key = XKeysymToKeycode(dsp, s);
    if (key == NoSymbol)
        return -1;
    XTestFakeKeyEvent(dsp, key, True, 0);
    XFlush(dsp);
    return 0;
}


/**
**鼠标移动
**/


int move(Display *dsp, int x, int y) //鼠标移动
{
    XWarpPointer(dsp, None, desktop, 0, 0, 0, 0, x, y);
    if (0 == XTestFakeMotionEvent(dsp, -1, x, y, CurrentTime)) {
        printf("Cannot move!\n");
        return -1;
    }
    return 0;
}


/**
*鼠标按键控制
*/

int buttonpress(Display *dsp, int type) //鼠标按，type=1表示左键，3是右键，2是中键
{
    if (0 == XTestFakeButtonEvent(dsp, type, 1, CurrentTime)) {
        printf("press failed\n");
        return -1;
    }
    return 0;
}


int buttonrelease(Display *dsp, int type) //鼠标释放
{
    if (0 == XTestFakeButtonEvent(dsp, type, 0, CurrentTime)) {
        printf("release failed\n");
        return -1;
    }
    return 0;
}

/**
**按键
*/


int releasekey(Display *dsp, int s) { //键盘release
    if (dsp == NULL)
        return -1;
    KeyCode key = XKeysymToKeycode(dsp, s);
    if (key == NoSymbol)
        return -1;
    XTestFakeKeyEvent(dsp, key, False, 0);
    XFlush(dsp);
    return 0;
}

//int main(){     //测试用的会在程序结束后，在光标前输出c
//    Display *dsp=dspopen();
//KeySym keysym=XStringToKeysym(s);
//    presskey(dsp,keysym);
//    releasekey(dsp,keysym);
//    dspclose(dsp);

//    return 0;
//}





JNIEXPORT jint JNICALL
Java_com_pangbai_dowork_tool_jni_mouseScroll(JNIEnv *env, jclass clazz, jboolean up) {
    if (mdisplay == NULL)
        return -1;
    int tmpButton = -1;
    if ((bool) up) {
        tmpButton = 4;
    } else {
        tmpButton = 5;
    }
    buttonpress(mdisplay, tmpButton);
    buttonrelease(mdisplay, tmpButton);
    return 0;
    // TODO: implement mouseScroll()
}

JNIEXPORT jint JNICALL
Java_com_pangbai_dowork_tool_jni_pressKey(JNIEnv *env, jclass clazz, jint key) {
    if (mdisplay == NULL)
        return -1;
    buttonpress(mdisplay, key);
    buttonrelease(mdisplay, key);
    // TODO: implement relaseKey()
    return 0;
}

void *thread_function(void *arg) {
    copy( (char *)arg,1);
    printf("这是新线程\n");
    return NULL;
}
//copy ,in cause of theard
char *inputStr;
        JNIEXPORT jint JNICALL
Java_com_pangbai_dowork_tool_jni_inputString(JNIEnv *env, jclass clazz, jstring str) {
    if (mdisplay==NULL)
        return -1;
    const char *cString = (*env)->GetStringUTFChars(env, str,NULL);
    if (cString == NULL) {
        return -2; /* OutOfMemoryError already thrown */
    }
    inputStr = (char *)malloc(strlen(cString) + 1);
    if (inputStr == NULL) {
        return -3;  // 内存分配失败
    }
    strcpy(inputStr, cString);
    (*env)->ReleaseStringUTFChars(env, str, cString);
    pthread_t thread_id;


    // 创建新线程，传递线程函数和参数
    if (pthread_create(&thread_id, NULL, thread_function, inputStr) != 0) {
        fprintf(stderr, "无法创建线程\n");
        return 1;
    }



    KeySym ctrlKey = XKeysymToKeycode(mdisplay, XK_Control_L);
    KeySym vKey = XKeysymToKeycode(mdisplay, XK_v);

    // 模拟按下 Ctrl 键
    XTestFakeKeyEvent(mdisplay, ctrlKey, True, 0);
    XFlush(mdisplay);

    // 模拟按下 V 键
    XTestFakeKeyEvent(mdisplay, vKey, True, 0);
    XFlush(mdisplay);

    // 释放 V 键
    XTestFakeKeyEvent(mdisplay, vKey, False, 0);
    XFlush(mdisplay);

    // 释放 Ctrl 键
    XTestFakeKeyEvent(mdisplay, ctrlKey, False, 0);
    XFlush(mdisplay);
    //pthread_join(thread_id,NULL);


    // TODO: implement inputString()
}


JNIEXPORT jint JNICALL
Java_com_pangbai_dowork_tool_jni_inputKeyByString(JNIEnv *env, jclass clazz, jstring key) {
    const char *cString = (*env)->GetStringUTFChars(env, key,NULL);
    if (mdisplay==NULL)
        return -1;
    KeySym keySym = XStringToKeysym(cString);

    presskey(mdisplay, keySym);
    releasekey(mdisplay,keySym);

    (*env)->ReleaseStringUTFChars(env, key, cString);
    return 0;
    // TODO: implement inputKeyByString()
}

Cursor cursor=-1;

JNIEXPORT jint JNICALL
Java_com_pangbai_dowork_tool_jni_cursorControl(JNIEnv *env, jclass clazz, jint statue, jint x,
                                               jint y) {


    if (statue==1){
        cursor = XCreateFontCursor(mdisplay, XC_top_left_arrow);
        XDefineCursor(mdisplay, DefaultRootWindow(mdisplay), cursor);
        XFlush(mdisplay);
    } else if (statue==-1){
       if(cursor!=-1)
           XFreeCursor(mdisplay, cursor);
       cursor=-1;
    } else if (statue==0){
        if (cursor==-1)
            return -1;
        move(mdisplay,x,y);

    }
    // TODO: implement cursorControl()
}