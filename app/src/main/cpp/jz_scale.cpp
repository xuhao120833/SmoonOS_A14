#include <jni.h>
#include <string>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include "scdefine.h"

/////////////////////////////////////////////////////


///////////////////////////////////////////////////
int jz_para(JZPARA_STRUCT *pJz, float *u, float *v, float *x, float *y)
{
    int n,i,j,k,max;
    double ajz[12][12];
    double temp,eps = 1e-7;
    n = 8;
    for(i=0;i<4;i++){
        ajz[i*2+0][0] = u[i]*x[i];
        ajz[i*2+0][1] = u[i]*y[i];
        ajz[i*2+0][2] = -x[i];
        ajz[i*2+0][3] = -y[i];
        ajz[i*2+0][4] = -1;
        ajz[i*2+0][5] = 0;
        ajz[i*2+0][6] = 0;
        ajz[i*2+0][7] = 0;
        ajz[i*2+0][8] = -u[i];
        ajz[i*2+1][0] = v[i]*x[i];
        ajz[i*2+1][1] = v[i]*y[i];
        ajz[i*2+1][2] = 0;
        ajz[i*2+1][3] = 0;
        ajz[i*2+1][4] = 0;
        ajz[i*2+1][5] = -x[i];
        ajz[i*2+1][6] = -y[i];
        ajz[i*2+1][7] = -1;
        ajz[i*2+1][8] = -v[i];
    }
    for(i=0;i<n;i++){
        max=i;
        for(j=i+1;j<n;j++){
            if(fabs(ajz[j][i])>fabs(ajz[max][i])){
                max=j;
            }
        }
        for(j=0;j<=n+1;j++){
            temp = ajz[i][j];
            ajz[i][j]=ajz[max][j];
            ajz[max][j] = temp;
        }
        if(fabs(ajz[i][i]) < eps){
            LOGD("CPP: mb xzjz err i=%d\n",i);
            return 0;
        }
        for(j=n;j>=0;j--){
            ajz[i][j]= ajz[i][j]/ajz[i][i];
        }
        for(j=0;j<n;j++){
            if(j!=i) {
                temp=ajz[j][i]/ajz[i][i];
                for(k=0;k<=n;k++){
                    ajz[j][k] -= ajz[i][k]*temp;
                }
            }
        }
    }
    pJz->g = ajz[0][n];
    pJz->h = ajz[1][n];
    pJz->a = ajz[2][n];
    pJz->b = ajz[3][n];
    pJz->c = ajz[4][n];
    pJz->d = ajz[5][n];
    pJz->e = ajz[6][n];
    pJz->f = ajz[7][n];
    return 1;
}

void ratio_tra_point(int *pRet, int *px4, int *py4, int oldRatio, int newRatio, float scale, int w, int h)
{
    int i,ret1;
    int tx,ty,tw,th,nw,nh;
    int xost,yost;
    int ratioID=oldRatio;
    int ratioTO=newRatio;
    int piW = w;
    int piH = h;
    float px[4];
    float py[4];
    float pu[4];
    float pv[4];
    float gx[4];
    float gy[4];
    float rx[4];
    float ry[4];
    JZPARA_STRUCT JzD;

    for(i=0;i<4;i++){
        pu[i] = px4[i];
        pv[i] = py4[i];
    }

    if(ratioID==1){//16:10;
        xost = (piW-(16*piH)/10)/2;
    }else if(ratioID==2){//4:3
        xost = (piW-(4*piH)/3)/2;
    }else{//16:9
        xost = 0;
    }
    //printf("ratioID=%d xost=%d",ratioID,xost);
    tx = w/2;
    ty = h/2;
    px[0] = -tx+xost;
    py[0] = -ty;
    px[1] = tx-xost;
    py[1] = -ty;
    px[2] = -tx+xost;
    py[2] = ty;
    px[3] = tx-xost;
    py[3] = ty;

    if(ratioTO==ratioID){
        for(i=0;i<4;i++){
            rx[i] = px[i];
            ry[i] = py[i];
        }
    }else{
        tw = px[1]-px[0];
        th = py[2]-py[0];
        nw = tw;
        nh = th;
        if(ratioTO==0){//16:9
            nh = tw*9/16;
            if(nh>th){
                nh = th;
                nw = th*16/9;
            }
        }else if(ratioTO==1){//16:10
            nh = tw*10/16;
            if(nh>th){
                nh = th;
                nw = th*16/10;
            }
        }else if(ratioTO==2){//4:3
            nh = tw*3/4;
            if(nh>th){
                nh = th;
                nw = th*4/3;
            }
        }
        xost = (tw-nw)/2;
        yost = (th-nh)/2;
        rx[0] = px[0]+xost;
        ry[0] = py[0]+yost;
        rx[1] = px[1]-xost;
        ry[1] = py[1]+yost;
        rx[2] = px[2]+xost;
        ry[2] = py[2]-yost;
        rx[3] = px[3]-xost;
        ry[3] = py[3]-yost;
    }
    ret1 = jz_para(&JzD, pu, pv, px, py);
    //printf("jz_para ret1=%d ret2=%d\n", ret1, ret2);

    if(ret1){
        for(i=0;i<4;i++){
            tx = rx[i]*scale;
            ty = ry[i]*scale;
            gx[i] = (JzD.a*tx+JzD.b*ty+JzD.c)/(JzD.g*tx+JzD.h*ty+1);
            gy[i] = (JzD.d*tx+JzD.e*ty+JzD.f)/(JzD.g*tx+JzD.h*ty+1);
        }
        pRet[0] = gx[0];
        pRet[1] = h-gy[0];
        pRet[2] = w-gx[1];
        pRet[3] = h-gy[1];
        pRet[4] = gx[2];
        pRet[5] = gy[2];
        pRet[6] = w-gx[3];
        pRet[7] = gy[3];
        pRet[8] = 1;
        LOGD("CPP: TP LT(%d,%d) RT(%d,%d) LB(%d,%d) RB(%d,%d)\n",(int)pRet[0],(int)pRet[1],(int)pRet[2],(int)pRet[3],(int)pRet[4],(int)pRet[5],(int)pRet[6],(int)pRet[7]);
    }
}
