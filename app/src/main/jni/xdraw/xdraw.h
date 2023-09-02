#include <android/native_window_jni.h>

// X11
#include "X11/Xutil.h"
extern  Display* mdisplay;
extern Window desktop;
static bool showImage(ANativeWindow  *mANativeWindow, XImage *image);

static void draw2(ANativeWindow_Buffer *buffer, XImage* image);
static void draw(ANativeWindow_Buffer *buffer, XImage *image);
int checkClientOnline();
void destroySrc();
int presskey(Display *dsp,int s);
int releasekey(Display *dsp,int s);
int xclick(int x,int y,bool isleft);
bool isRunning;
