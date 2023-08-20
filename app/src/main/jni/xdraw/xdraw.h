#include <android/native_window_jni.h>

// X11
#include "X11/Xutil.h"
extern  Display* mdisplay;
extern Window desktop;
static bool showImage(ANativeWindow  *mANativeWindow, XImage *image);

static void draw2(ANativeWindow_Buffer *buffer, XImage* image);
static void draw(ANativeWindow_Buffer *buffer, XImage *image);


