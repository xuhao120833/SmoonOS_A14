package com.htc.luminaos.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htc.luminaos.entry.AppSimpleBean;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class DBUtils extends SQLiteOpenHelper {

    private static String TAG = "DBUtils";
    private static DBUtils mInstance = null;
    private final static String DATABASE_NAME = "htc_launcher.db";
    private final static int VERSION = 3;
    private final String TABLENAME_FAVORITES = "table_favorites";// 我的收藏

    private final String TABLENAME_MAINAPP = "mainApp";

    private final String TABLENAME_LISTMODULES = "listModules";

    private final String TABLENAME_BRANDLOGO = "brandLogo";
    private SharedPreferences sharedPreferences;

    public static DBUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBUtils(context);
        }
        return mInstance;
    }

    private DBUtils(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        sharedPreferences = ShareUtil.getInstans(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // TODO Auto-generated method stub
            // 创建我的收藏表
            Log.d(TAG, " 创建我的收藏数据表 ");
            String favorites_sql = "CREATE TABLE " + TABLENAME_FAVORITES
                    + " ( id integer primary key,  packagename text );";
            db.execSQL(favorites_sql);

            // 创建mainApp表
            Log.d(TAG, " 创建mainApp数据表 ");
            String mainApp_sql = "CREATE TABLE " + TABLENAME_MAINAPP + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tag TEXT , " +
                    "appName TEXT , " +
                    "iconData BLOB , " +
                    "action TEXT );";
            db.execSQL(mainApp_sql);

			// 创建listModules表
            Log.d(TAG, " 创建 listModules 表 ");
            String listModules_sql = "CREATE TABLE " + TABLENAME_LISTMODULES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tag TEXT , " +
                    "iconData BLOB , " +
                    "hashtable_data TEXT , " + // 用于存储序列化的 Hashtable
                    "action TEXT );";
            db.execSQL(listModules_sql);

            // 创建brand品牌表
            Log.d(TAG," 创建brand品牌表");
            String brandLogo_sql = "CREATE TABLE " + TABLENAME_BRANDLOGO + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "iconData BLOB );";
            db.execSQL(brandLogo_sql);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, " 执行onUpgrade ");

        // TODO Auto-generated method stub
        String favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_FAVORITES;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_MAINAPP;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_LISTMODULES;
        db.execSQL(favorites_sql);

        favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_BRANDLOGO;
        db.execSQL(favorites_sql);

        onCreate(db);
    }

    /**
     * 添加收藏
     *
     * @param packagename
     * @return
     */
    public long addFavorites(String packagename) {
        long code = -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("packagename", packagename);
        code = db.insert(TABLENAME_FAVORITES, null, cv);
        sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
        db.close();
        return code;
    }

    /**
     * 获取收藏
     *
     * @return
     */
    public ArrayList<AppSimpleBean> getFavorites() {
        ArrayList<AppSimpleBean> list = new ArrayList<AppSimpleBean>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select id , packagename  from " + TABLENAME_FAVORITES;
        Cursor cs = db.rawQuery(sql, null);
        while (cs.moveToNext()) {
            AppSimpleBean bean = new AppSimpleBean();
            bean.setId(cs.getInt(0));
            bean.setPackagename(cs.getString(1));
            list.add(bean);
        }
        db.close();
        return list;
    }

    /**
     * 删除收藏
     *
     * @param packagename
     * @return
     */
    public int deleteFavorites(String packagename) {
        int code = -1;
        SQLiteDatabase db = getWritableDatabase();
        code = db.delete(TABLENAME_FAVORITES, "packagename=?",
                new String[]{packagename});
        db.close();
        sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
        return code;
    }

    /**
     * 清空收藏
     */
    public void clearFavorites() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "delete from " + TABLENAME_FAVORITES;
        db.execSQL(sql);
        db.close();
        sharedPreferences.edit().putBoolean(Contants.MODIFY, true).apply();
    }

    /**
     * 查询数据库中条数
     *
     * @return
     */
    public long getCount() {
        long count = 0;
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select count(*) from " + TABLENAME_FAVORITES;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public boolean isExistData(String packagename) {
        boolean isExist = false;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cs = db.rawQuery("select id , packagename  from "
                        + TABLENAME_FAVORITES + " where packagename = ?",
                new String[]{packagename});
        while (cs.moveToNext()) {
            isExist = true;
        }
        db.close();
        return isExist;
    }

    /***
     * Time:2024/8/10
     * Author:xuhao
     * Usage:将从配置文件config中读出来的信息保存进本地的db数据库。
     * @param tag
     * @param appName
     * @param drawable
     * @param action
     */
//    public void insertMainAppData(String tag, String appName, Drawable drawable, String action) {
//
//        long code = -1;
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("tag", tag);
//        values.put("appName", appName);
//        values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
//        values.put("action", action);
//
//        code = db.insert(TABLENAME_MAINAPP, null, values);
//        if (code == -1) {
//            Log.d(TAG, "MainApp插入数据失败");
////			System.out.println("插入数据失败");
//        } else {
//            Log.d(TAG, "MainApp插入数据成功，行ID：" + code);
////			System.out.println("插入数据成功，行ID：" + code);
//        }
//    }
    public void insertMainAppData(String tag, String appName, Drawable drawable, String action) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tag", tag);
        values.put("appName", appName);
        values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
        values.put("action", action);

        // 检查是否存在相同的 tag
        String selection = "tag = ?";
        String[] selectionArgs = { tag };
        int rowsAffected = db.update(TABLENAME_MAINAPP, values, selection, selectionArgs);

        if (rowsAffected == 0) {
            // 如果没有记录被更新，说明不存在这个 tag，执行插入操作
            long code = db.insert(TABLENAME_MAINAPP, null, values);
            if (code == -1) {
                Log.d(TAG, "MainApp插入数据失败");
            } else {
                Log.d(TAG, "MainApp插入数据成功，行ID：" + code);
            }
        } else {
            Log.d(TAG, "MainApp数据更新成功，更新行数：" + rowsAffected);
        }
    }


//    public void insertListModulesData(String tag, Drawable drawable, Hashtable hashtable, String action) {
//        long code = -1;
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("tag", tag);
//        values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
//        //hashtable序列化存入数据库
//        saveHashtableToDatabase(hashtable,values);
//        values.put("action", action);
//
//        code = db.insert(TABLENAME_LISTMODULES, null, values);
////        db.update()
//        if (code == -1) {
//            Log.d(TAG, "ListModules插入数据失败");
////			System.out.println("插入数据失败");
//        } else {
//            Log.d(TAG, "ListModules插入数据成功，行ID：" + code);
////			System.out.println("插入数据成功，行ID：" + code);
//        }
//    }

    public void insertListModulesData(String tag, Drawable drawable, Hashtable hashtable, String action) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tag", tag);
        values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
        // hashtable序列化存入数据库
        saveHashtableToDatabase(hashtable, values);
        values.put("action", action);

        // 检查是否存在相同的 tag
        String selection = "tag = ?";
        String[] selectionArgs = { tag };
        int rowsAffected = db.update(TABLENAME_LISTMODULES, values, selection, selectionArgs);

        if (rowsAffected == 0) {
            // 如果没有记录被更新，说明不存在这个 tag，执行插入操作
            long code = db.insert(TABLENAME_LISTMODULES, null, values);
            if (code == -1) {
                Log.d(TAG, "ListModules插入数据失败");
            } else {
                Log.d(TAG, "ListModules插入数据成功，行ID：" + code);
            }
        } else {
            Log.d(TAG, "ListModules数据更新成功，更新行数：" + rowsAffected);
        }
    }


//    public void insertBrandLogoData(Drawable drawable) {
//        long code = -1;
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("iconData", drawableToByteArray(drawable));
//        code = db.insert(TABLENAME_BRANDLOGO, null, values);
//        if (code == -1) {
//            Log.d(TAG, "ListModules插入数据失败");
////			System.out.println("插入数据失败");
//        } else {
//            Log.d(TAG, "BrandLogo插入数据成功，行ID：" + code);
////			System.out.println("插入数据成功，行ID：" + code);
//        }
//    }

    public void insertBrandLogoData(Drawable drawable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("iconData", drawableToByteArray(drawable));

        // 检查是否存在数据（假设表中只有一个logo）
        int rowsAffected = db.update(TABLENAME_BRANDLOGO, values, null, null);

        if (rowsAffected == 0) {
            // 如果没有记录被更新，说明表中还没有数据，执行插入操作
            long code = db.insert(TABLENAME_BRANDLOGO, null, values);
            if (code == -1) {
                Log.d(TAG, "BrandLogo插入数据失败");
            } else {
                Log.d(TAG, "BrandLogo插入数据成功，行ID：" + code);
            }
        } else {
            Log.d(TAG, "BrandLogo数据更新成功，更新行数：" + rowsAffected);
        }
    }


    public Hashtable<String, String> getHashtableFromListModules(String tag) {
        SQLiteDatabase db = getWritableDatabase();
        Hashtable<String, String> hashtable = null;

        // 查询数据库获取保存的JSON字符串，通过tag筛选
        Cursor cursor = db.query(
                "listModules",            // 表名
                new String[]{"hashtable_data"}, // 要查询的列
                "tag = ?",                     // 查询条件
                new String[]{tag},             // 查询条件的参数
                null,                          // 不进行分组
                null,                          // 不进行分组后的筛选
                null                           // 不进行排序
        );

        if (cursor != null && cursor.moveToFirst()) {
            String jsonString = cursor.getString(cursor.getColumnIndex("hashtable_data"));

            Log.d(TAG," 读取到的jsonString的值为 "+jsonString);

            // 使用Gson将JSON字符串反序列化为Hashtable
            Gson gson = new Gson();
            Type type = new TypeToken<Hashtable<String, String>>() {}.getType();
            hashtable = gson.fromJson(jsonString, type);

            Log.d(TAG," 获取到的阿拉伯语言 "+hashtable.get("ar"));

            cursor.close();  // 关闭Cursor
        }

        return hashtable;  // 返回反序列化后的Hashtable
    }

    public String getActionFromListModules(String tag){

        SQLiteDatabase db = getWritableDatabase();
        String action = null;
        Cursor cursor = null;
        try {
             cursor = db.query(
                    "listModules",            // 表名
                    new String[]{"action"},        // 要查询的列
                    "tag = ?",                     // 查询条件
                    new String[]{tag},             // 查询条件的参数
                    null,                          // 不进行分组
                    null,                          // 不进行分组后的筛选
                    null                           // 不进行排序
            );

            // 检查是否有结果，并提取 action
            if (cursor != null && cursor.moveToFirst()) {
                action = cursor.getString(cursor.getColumnIndex("action"));
            }
            Log.d(TAG, "getActionFromListModules action " +action);
        } catch (Exception e) {
            Log.d(TAG, "getActionFromListModules查询失败", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return action;
    }

    public Drawable getDrawableFromListModules(String tag) {
        SQLiteDatabase db = getWritableDatabase();
        Drawable drawable =null;
        // 查询数据库获取保存的JSON字符串，通过tag筛选
        Cursor cursor = db.query(
                "listModules",            // 表名
                new String[]{"iconData"}, // 要查询的列
                "tag = ?",                     // 查询条件
                new String[]{tag},             // 查询条件的参数
                null,                          // 不进行分组
                null,                          // 不进行分组后的筛选
                null                           // 不进行排序
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                byte[] iconData = cursor.getBlob(cursor.getColumnIndex("iconData"));
                drawable = byteArrayToDrawable(iconData);  // 将字节数组转换为 Drawable
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭 Cursor 和数据库连接
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return drawable;
    }

    public Drawable getDrawableFromBrandLogo(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Drawable drawable = null;
        Cursor cursor = db.query(
                TABLENAME_BRANDLOGO,             // 表名
                new String[]{"iconData"},        // 要查询的列
                "id = ?",                        // 查询条件
                new String[]{String.valueOf(id)}, // 查询条件的参数
                null,                            // 不进行分组
                null,                            // 不进行分组后的筛选
                null                             // 不进行排序
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                byte[] iconData = cursor.getBlob(cursor.getColumnIndex("iconData"));
                drawable = byteArrayToDrawable(iconData);  // 将字节数组转换为 Drawable
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭 Cursor 和数据库连接
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return drawable;  // 返回 Drawable
    }



    /***
     * Time:2024/8/10
     * Author:xuhao
     * Usage:将drawable转化成ByteArray，
     * 方便保存到SQLite数据库中。
     * @param drawable
     * @return
     */
    public byte[] drawableToByteArray(Drawable drawable) {

        if(drawable ==null){
            return null;
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /***
     * Time:2024/8/12
     * Author:xuhao
     * Usage:通过name去查询数据库的数据，返回drawable
     * @param tag
     * @return
     */
    public Drawable getIconDataByTag(String tag) {
        SQLiteDatabase db = getReadableDatabase();
        Drawable drawable = null;
        Cursor cursor = null;

        try {
            // 查询数据库中的数据
            cursor = db.query(
                    TABLENAME_MAINAPP,                   // 表名
                    new String[]{"iconData"},            // 需要查询的列名
                    "tag = ?",                          // 查询条件
                    new String[]{tag},                  // 查询条件的参数
                    null,                                // Group By
                    null,                                // Having
                    null                                 // Order By
            );

            // 检查是否查找到结果
            if (cursor != null && cursor.moveToFirst()) {
                byte[] iconData = cursor.getBlob(cursor.getColumnIndex("iconData"));
                drawable = byteArrayToDrawable(iconData);  // 将字节数组转换为 Drawable
            }
            Log.d(TAG, "查询数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 关闭 Cursor 和数据库连接
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return drawable;
    }

    /***
     * Time:2024/8/12
     * Author:xuhao
     * Usage:将ByteArray转化成drawable。
     * @param byteArray
     * @return
     */
    private Drawable byteArrayToDrawable(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return new BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }

    public String getAppNameByTag(String tag) {
        SQLiteDatabase db = getReadableDatabase();
        String appName = null;

        // 查询条件
        String selection = "tag = ?";
        String[] selectionArgs = {tag};

        // 查询数据库
        Cursor cursor = db.query(
                TABLENAME_MAINAPP,   // 表名
                new String[]{"appName"}, // 查询的列
                selection,          // 查询条件
                selectionArgs,      // 查询条件参数
                null,               // 分组
                null,               // 分组条件
                null                // 排序
        );

        // 处理查询结果
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                appName = cursor.getString(cursor.getColumnIndex("appName"));
            }
            cursor.close();
        }

        return appName;
    }

    public String getActionByTag(String tag) {
        SQLiteDatabase db = getReadableDatabase();
        String action = null;

        // 查询条件
        String selection = "tag = ?";
        String[] selectionArgs = {tag};

        // 查询数据库
        Cursor cursor = db.query(
                TABLENAME_MAINAPP,   // 表名
                new String[]{"action"}, // 查询的列
                selection,          // 查询条件
                selectionArgs,      // 查询条件参数
                null,               // 分组
                null,               // 分组条件
                null                // 排序
        );

        // 处理查询结果
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                action = cursor.getString(cursor.getColumnIndex("action"));
            }
            cursor.close();
        }

        return action;
    }

    public void saveHashtableToDatabase(Hashtable<String, String> hashtable, ContentValues values) {
        // 使用Gson将Hashtable序列化为JSON字符串
        Gson gson = new Gson();
        String jsonString = gson.toJson(hashtable);

        Log.d(TAG,"序列化之后的hashtable_data值为 "+jsonString);

        // 创建ContentValues用于插入数据
        values.put("hashtable_data", jsonString);  // 将序列化后的字符串存入ContentValues
    }


    public void deleteTable(){
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = -1;
        try {
            rowsAffected =db.delete(TABLENAME_FAVORITES, null, null);
            rowsAffected =db.delete(TABLENAME_MAINAPP, null, null);
            rowsAffected =db.delete(TABLENAME_LISTMODULES, null, null);
            rowsAffected =db.delete(TABLENAME_BRANDLOGO, null, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
