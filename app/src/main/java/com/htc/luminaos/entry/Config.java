package com.htc.luminaos.entry;

import java.util.List;

/**
 * Author:
 * Date:
 * Description:
 */
public class Config {

    public String filterApps = "";

    public List<Apps> apps;
    //IP设置
    public boolean ipSetting = true;


    public  boolean displayPictureMode=true;
    public  boolean displayPictureModeShowCustom=true;

    public  boolean brightness=true;
    public  int brightnessLevel=1;
    public  boolean brightnessPQ=false;
    public  boolean contrast=false;
    public  boolean hue=false;
    public  boolean saturation=false;
    public  boolean sharpness=false;

    public  boolean red=false;
    public  boolean green=false;
    public  boolean blue=false;
    public  boolean displayVoiceMode=false;


    //自动梯形矫正
    public  boolean autoKeystone=true;

    //初始角度矫正
    public  boolean initAngleCorrect=true;

    //自动四角矫正
    public  boolean autoFourCorner=true;

    public  boolean manualKeystone=true;
    public  boolean resetKeystone=true;
    public  int manualKeystoneWidth=1000;
    public  int manualKeystoneHeight=1000;
    public  boolean autoFocus=true;
    public  boolean screenRecognition=true;
    public  boolean intelligentObstacle=true;
    public  boolean calibration=true;
    public  boolean projectMode=true;
    public  boolean displaySetting=false;
    public  boolean wholeZoom=true;

    //设备模式
    public boolean deviceMode = true;

    //设备型号
    public boolean deviceModel = true;
    public boolean uiVersion = true;
    public boolean androidVersion = true;
    public boolean resolution = true;
    public boolean memory = true;
    public int memoryScale = 1;
    public boolean storage = true;
    public int storageScale = 1;
    public boolean wlanMacAddress = true;
    public boolean updateFirmware = true;
    public boolean onlineUpdate = true;
    public boolean serialNumber = true;
}
