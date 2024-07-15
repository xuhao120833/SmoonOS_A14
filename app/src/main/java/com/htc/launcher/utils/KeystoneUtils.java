package com.htc.launcher.utils;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

/**
 * 梯形校正工具
 */
public class KeystoneUtils {

	public static final String PROP_KEYSTONE_LB_X = "persist.display.keystone_lbx";
	public static final String PROP_KEYSTONE_LB_Y = "persist.display.keystone_lby";
	public static final String PROP_KEYSTONE_LT_X = "persist.display.keystone_ltx";
	public static final String PROP_KEYSTONE_LT_Y = "persist.display.keystone_lty";
	public static final String PROP_KEYSTONE_RB_X = "persist.display.keystone_rbx";
	public static final String PROP_KEYSTONE_RB_Y = "persist.display.keystone_rby";
	public static final String PROP_KEYSTONE_RT_X = "persist.display.keystone_rtx";
	public static final String PROP_KEYSTONE_RT_Y = "persist.display.keystone_rty";

	public static final String ZOOM_VALUE = "zoom_value";
	public static final String ZOOM_SCALE = "zoom_scale";
	public static final String ZOOM_SCALE_OLD = "zoom_scale_old";

	public static final int minX=0;
	public static final int minY=0;
	//public static final int minH_size=480;//960/2=480 480-480/4=360
	//public static final int minV_size=270;//540/2=270 270-270/4=202

	public static  int minH_size=500;//960/2=480 480-480/4=360
	public static  int minV_size=500;//540/2=270 270-270/4=202
	public static  int lcd_w=1920;
	public static  int lcd_h=1080;


	public static int lb_X = 0;
	public static int lb_Y = 0;
	public static int rb_X = 0;
	public static int rb_Y= 0;
	public static int lt_X = 0;
	public static int lt_Y = 0;
	public static int rt_X = 0;
	public static int rt_Y = 0;

	public static void initKeystoneData(){

		lb_X = CoverX(PROP_KEYSTONE_LB_X);
		lb_Y = CoverY(PROP_KEYSTONE_LB_Y);
		rb_X =CoverX(PROP_KEYSTONE_RB_X);
		rb_Y =CoverY(PROP_KEYSTONE_RB_Y);
		lt_X =CoverX(PROP_KEYSTONE_LT_X);
		lt_Y =CoverY(PROP_KEYSTONE_LT_Y);
		rt_X =CoverX(PROP_KEYSTONE_RT_X);
		rt_Y =CoverY(PROP_KEYSTONE_RT_Y);
	}

	/**
	 * 获取左上角坐标
	 * @return
	 */
	public static int[] getKeystoneLeftAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LT_X) ;
		xy[1] =CoverY(PROP_KEYSTONE_LT_Y);
		return xy;
	}

	/**
	 * 获取左下角坐标
	 * @return
	 */
	public static int[] getKeystoneLeftAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LB_X) ;
		xy[1] =CoverY(PROP_KEYSTONE_LB_Y);
		return xy;
	}

	/**
	 * 获取右上角坐标
	 * @return
	 */
	public static int[] getKeystoneRightAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RT_X);
		xy[1] =CoverY(PROP_KEYSTONE_RT_Y);
		return xy;
	}

	/**
	 * 获取右下角坐标
	 * @return
	 */
	public static int[] getKeystoneRightAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RB_X);
		xy[1] =CoverY(PROP_KEYSTONE_RB_Y);
		return xy;
	}

	/************OppositeTo
	 ************/
	public static int[] getKeystoneOppositeToLeftAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RT_X);
		xy[1] =CoverY(PROP_KEYSTONE_LB_Y);
		return xy;
	}

	public static int[] getKeystoneOppositeToLeftAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_RB_X);
		xy[1] =CoverY(PROP_KEYSTONE_LT_Y);
		return xy;
	}

	public static int[] getKeystoneOppositeToRightAndTopXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LT_X);
		xy[1] =CoverY(PROP_KEYSTONE_RB_Y);
		return xy;
	}

	public static int[] getKeystoneOppositeToRightAndBottomXY() {
		int[] xy = new int[] { 0, 0 };
		xy[0] =CoverX(PROP_KEYSTONE_LB_X);
		xy[1] =CoverY(PROP_KEYSTONE_RT_Y);
		return xy;
	}
	/**
	 * 设置四角值
	 * @param type 左上 1  左下2  右上 3 右下  4
	 * @param xy 坐标
	 */
	public static void setkeystoneValue(int type , int[] xy){
		int x=xy[0];
		int y=xy[1];
		int[] xy_OppositeTo = new int[] { 0, 0 };
		switch (type) {
			case 1:

				xy_OppositeTo = getKeystoneOppositeToLeftAndTopXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x = 0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}

				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				Log.d("test3","x "+x+"y"+y);
				//y = lcd_h - y;
				lt_X =x;
				lt_Y =y;
				UpdateKeystone();
				break;
			case 2:
				xy_OppositeTo = getKeystoneOppositeToLeftAndBottomXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x=0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}

				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				lb_X=x;
				lb_Y=y;
				UpdateKeystone();
				break;
			case 3:
				xy_OppositeTo = getKeystoneOppositeToRightAndTopXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x=0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}
				//x = lcd_w - x;
				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				//y = lcd_h - y;
				rt_X =x;
				rt_Y =y;
				UpdateKeystone();
				break;
			case 4:
				xy_OppositeTo = getKeystoneOppositeToRightAndBottomXY();
				if(x>=minX && (x+xy_OppositeTo[0])<=minH_size){
					;
				}else if(x<minX){
					x=0;
				}else if((x+xy_OppositeTo[0])>minH_size){
					x = minH_size - xy_OppositeTo[0];
				}
				//x = lcd_w - x;
				if(y>=minY && (y+xy_OppositeTo[1])<=minV_size){
					;
				}else if(y<minY){
					y=0;
				}else if((y+xy_OppositeTo[1])>minV_size){
					y=minV_size - xy_OppositeTo[1];
				}
				rb_X =x;
				rb_Y =y;
				UpdateKeystone();
				break;
		}
	}

	private static int CoverX(String prop){

		return SystemProperties.getInt(prop,0);
	}

	private static int CoverY(String prop){
		return SystemProperties.getInt(prop,0);
	}

	private static IBinder flinger;

	public static void UpdateKeystone(){
		Log.d("UpdateKeystone","rb_X "+ rb_X+"rb_Y "+rb_Y);
		try {
			if (flinger==null)
			 flinger = ServiceManager.getService("SurfaceFlinger");

			if (flinger != null) {
				Parcel data = Parcel.obtain();
				data.writeInterfaceToken("android.ui.ISurfaceComposer");

				data.writeFloat((float)((double) lb_X *0.001));
				data.writeFloat((float)((double) lb_Y *0.001));
				data.writeFloat((float)((double) lt_X *0.001));
				data.writeFloat((float)((double) lt_Y *0.001));
				data.writeFloat((float)((double) rt_X *0.001));
				data.writeFloat((float)((double) rt_Y *0.001));
				data.writeFloat((float)((double) rb_X *0.001));
				data.writeFloat((float)((double) rb_Y *0.001));
				flinger.transact(1050, data, null, 0);
				data.recycle();
			} else {
				Log.i("tag","error get surfaceflinger service");
			}
		} catch (RemoteException ex) {
			Log.i("tag","error talk with surfaceflinger service");
		}
	}

	public static void UpdateKeystoneZOOM(boolean write){
		Log.d("UpdateKeystone","rb_X "+ rb_X+"rb_Y "+rb_Y);
		if (!write){
			SystemProperties.set("persist.sys.zoom.value",lb_X+","+lb_Y+","+lt_X+","+lt_Y+","+rt_X+","+rt_Y+","+rb_X+","+rb_Y);
			return;
		}
		try {
			if (flinger==null)
			 flinger = ServiceManager.getService("SurfaceFlinger");

			if (flinger != null) {
				Parcel data = Parcel.obtain();
				data.writeInterfaceToken("android.ui.ISurfaceComposer");

				data.writeFloat((float)((double) lb_X *0.001));
				data.writeFloat((float)((double) lb_Y *0.001));
				data.writeFloat((float)((double) lt_X *0.001));
				data.writeFloat((float)((double) lt_Y*0.001));
				data.writeFloat((float)((double) rt_X*0.001));
				data.writeFloat((float)((double) rt_Y*0.001));
				data.writeFloat((float)((double) rb_X*0.001));
				data.writeFloat((float)((double) rb_Y*0.001));
				flinger.transact(1050, data, null, 0);
				SystemProperties.set("persist.sys.zoom.value",lb_X+","+lb_Y+","+lt_X+","+lt_Y+","+rt_X+","+rt_Y+","+rb_X+","+rb_Y);
				data.recycle();

			} else {
				Log.i("tag","error get surfaceflinger service");
			}
		} catch (RemoteException ex) {
			Log.i("tag","error talk with surfaceflinger service");
		}
	}

	public static void resetKeystone(){
		lt_X = 0;
		lt_Y = 0;
		rt_X = 0;
		rt_Y = 0;
		rb_X = 0;
		rb_Y = 0;
		lb_X = 0;
		lb_Y = 0;
		UpdateKeystone();
	}

	public static void writeGlobalSettings(Context context, String key, int value){
		Settings.Global.putInt(context.getContentResolver(),key,value);
	}

	public static int readGlobalSettings(Context context,String key,int def){
		return   Settings.Global.getInt(context.getContentResolver(),key,def);
	}

}
