package com.htc.launcher.utils;

import com.htc.launcher.R;

/**
 * Author:
 * Date:
 * Description:
 */
public interface Contants {
    int[] drawables = new int[]{R.drawable.background0,R.drawable.background_main,R.drawable.background_custom,R.drawable.background1,R.drawable.background2,R.drawable.background3
            ,R.drawable.background4,R.drawable.background5,R.drawable.background6,R.drawable.background7,R.drawable.background8, R.drawable.background9,R.drawable.background10,
            R.drawable.background11,R.drawable.background12};

    String FILE_NAME = "data";
    String MODIFY = "modify";
    String KEY_DEVELOPER_MODE = "developer_mode";

    // 1:配对成功 2：正在配对 3：删除配对 或者 配对失败
    int BOND_SUCCESSFUL =1;
    int BONDING = 2;
    int BOND_FAIL = 3;
    int REFRESH_FOUND = 8;
    int REFRESH_PAIR = 9;

    String KEY_GMT = "gmt";
    String KEY_OFFSET = "offset";
    int HOURS_1 = 60 * 60000;
    String KEY_ID = "id";
    String KEY_DISPLAYNAME = "name";

    String TimeOffTime = "timeOffTime";
    String TimeOffStatus = "timeOffStatus";
    String TimeOffIndex = "timeOffIndex";

    int PICTURE_NULL = 101;
    int PICTURE_RESULT = 102;
    int PICTURE_FIND = 103;
    int DISSMISS_DIALOG = 104;

    String WALLPAPER_DIR = "/storage/emulated/0/wallpaper";
    String WALLPAPER_MAIN = "/storage/emulated/0/wallpaper/background_main.png";
    String WALLPAPER_OTHER = "/storage/emulated/0/wallpaper/background_other.png";
}
