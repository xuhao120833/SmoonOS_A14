#include <jni.h>
#include <string>
#include <time.h>
#include <android/log.h>

#ifndef _XTDEFINE_HAED_H
#define _XTDEFINE_HAED_H
#define LOG_TAG "FC"

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)

#define PI 3.1415926

#define PX_LCD_W	1920
#define PX_LCD_H	1080

typedef struct _UPOINT_STRUCT
{
    int x;
    int y;
}UPOINT_STRUCT;

typedef struct _MBP_STRUCT
{
    int x[4];
    int y[4];
}MBP_STRUCT;

typedef struct _JZPARA_STRUCT
{
    double a;
    double b;
    double c;
    double d;
    double e;
    double f;
    double g;
    double h;
}JZPARA_STRUCT;

#endif