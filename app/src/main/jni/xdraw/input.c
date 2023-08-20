
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
//extern  Display* display;
int presskey(Display *dsp,int s);
int releasekey(Display *dsp,int s);
int touch(int x,int y);

JNIEXPORT jstring JNICALL Java_com_pangbai_dowork_tool_jni_movePoint(JNIEnv *env, jobject thiz,jint x,jint y){
  touch(x,y);
      
   return NULL;
    }
    
 JNIEXPORT jstring JNICALL Java_com_pangbai_dowork_tool_jni_inputKey(JNIEnv *env, jobject thiz,jint mode,jstring str){
   char* key;
  key=  (*env)->GetStringUTFChars(env,str,0);
 
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
  
  presskey(mdisplay,XK_Shift_L) ;
  presskey(mdisplay,XK_Insert) ;
  
  
  releasekey(mdisplay,XK_Shift_L);

  releasekey(mdisplay,XK_Insert);

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
     (*env)->ReleaseStringUTFChars(env,str, key);

     return NULL;   
        }
        
        
        
        
        /***
        *
        屏幕触摸板
        */
        
    
int touch(int x,int y){
    XEvent event;
 
  /* get info about current pointer position */
  XQueryPointer(mdisplay, RootWindow(mdisplay, DefaultScreen(mdisplay)),
      &event.xbutton.root, &event.xbutton.window,
      &event.xbutton.x_root, &event.xbutton.y_root,
      &event.xbutton.x, &event.xbutton.y,
      &event.xbutton.state);
 
  XTestFakeMotionEvent(mdisplay, -1, x, y, 0);
  XTestFakeButtonEvent(mdisplay, 1, 1, 0);
  XTestFakeButtonEvent(mdisplay, 1, 0, 0);
  /* place the mouse where it was */
  XTestFakeMotionEvent(mdisplay, -1, event.xbutton.x, event.xbutton.y, 0);
return 0;
}
        
     

   /**
   *按下事件*
        */
        
        
int presskey(Display *dsp,int s){  //键盘按
    if(dsp==NULL)
        return -1;
    KeyCode key=XKeysymToKeycode(dsp,s);
    if(key==NoSymbol)
        return -1;
    XTestFakeKeyEvent(dsp,key,True,0);
    XFlush(dsp);
    return 0;
}



/**
**鼠标移动
**/


int move(Display *dsp,int x,int y) //鼠标移动
{
    XWarpPointer(dsp, None, desktop, 0, 0, 0, 0, x, y);
    if(0==XTestFakeMotionEvent(dsp,-1,x,y,CurrentTime))
    {
        printf("Cannot move!\n");
        return -1;
    }
    return 0;
}


/**
*鼠标按键控制
*/

int buttonpress(Display *dsp,int type) //鼠标按，type=1表示左键，3是右键，2是中键
{
    if(0==XTestFakeButtonEvent(dsp,type,1,CurrentTime))
    {
        printf("press failed\n");
        return -1;
    }
    return 0;
}


int buttonrelease(Display *dsp,int type) //鼠标释放
{
    if(0==XTestFakeButtonEvent(dsp,type,0,CurrentTime))
    {
        printf("release failed\n");
        return -1;
    }
    return 0;
}

/**
**按键
*/


int releasekey(Display *dsp,int s){ //键盘release
    if(dsp==NULL)
        return -1;
    KeyCode key=XKeysymToKeycode(dsp,s);
    if(key==NoSymbol)
        return -1;
    XTestFakeKeyEvent(dsp,key,False,0);
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
        
    
        
        
