#include <dlfcn.h>// 相关函数头文件
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <jni.h>
#include "startxvfb.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>



void split(char *src, const char *separator, char **dest, int *num) {
	/*
		src 源字符串的首地址(buf的地址)
		separator 指定的分割字符
		dest 接收子字符串的数组
		num 分割后子字符串的个数
	*/
	char *pNext;
	int count = 0;
	if (src == NULL || strlen(src) == 0) //如果传入的地址为空或长度为0，直接终止
		return;
	if (separator == NULL || strlen(separator) == 0) //如未指定分割的字符串，直接终止
		return;
	pNext = (char *)strtok(src, separator); //必须使用(char *)进行强制类型转换(虽然不写有的编译器中不会出现指针错误)
	while (pNext != NULL) {
		*dest++ = pNext;
		++count;
		pNext = (char *)strtok(NULL, separator);  //必须使用(char *)进行强制类型转换
	}
	*num = count;
}


	
	
	
int isRunning=1;
extern  char* xbuffer;
int height;
int width;


JNIEXPORT jint JNICALL Java_com_pangbai_dowork_tool_jni_initxvfb(JNIEnv *env, jobject thiz, jstring j_screen) {
    char *resolution = (*env)->GetStringUTFChars(env, j_screen, NULL);
    char buf[100];
    snprintf(buf, sizeof(buf), "Xvfb :0 -ac -listen tcp -screen 0 %s", resolution);
    char *args[8] = { 0 };
    char *envs[1] = {"LD_LIBRARY_PATH=/data/data/com.pangbai.dowork/files/lib"};
    int num = 0;
    split(buf, " ", args, &num); // 调用函数进行分割
	(*env)->ReleaseStringUTFChars(env, j_screen, resolution);
    return main(num, args);
}




JNIEXPORT jint JNICALL Java_com_pangbai_dowork_tool_jni_startdraw(JNIEnv *env, jobject thiz,jobject jsurface,jint perline){
//获取Surface
ANativeWindow *mANativeWindow = NULL;

	
//设置矩形宽高
/*
jclass jcl = (*env)->FindClass(env, "com/pangbai/dowork/display/display");
	//jclass jcl =  (*env)->GetObjectClass(env,thiz);
 jmethodID mid =  (*env)->GetStaticMethodID(env,jcl, "updateDisplay", "(II)V");
   (*env)->CallStaticVoidMethod(env,thiz, mid,width,height );
 (*env)->DeleteLocalRef(env,jcl);
	 
 */
if(xbuffer==NULL)
	return 2001;
int a=(sizeof(*xbuffer) / height) / width ;
a++;
	if (a!=0)
		return a;
	
		
 while(isRunning==0){
	 mANativeWindow = ANativeWindow_fromSurface(env, jsurface);
if (mANativeWindow == NULL)
	continue;

	 ANativeWindow_Buffer buffer;
	 ANativeWindow_lock(mANativeWindow, &buffer, 0);
	 
    
	// draw(&buffer, xbuffer);
	//uint8_t *rect= (uint16_t *)buffer.bits;
	//ANativeWindow_setBuffersGeometry(mANativeWindow, width,height, WINDOW_FORMAT_RGB_565);
	// draw(&buffer, xbuffer,perline);
   // memcpy(buffer.bits, xbuffer, sizeof(__uint16_t) * height *width);

	 ANativeWindow_unlockAndPost(mANativeWindow);

ANativeWindow_release(mANativeWindow);
 }
return 2000;
	

}




