package com.htc.launcher.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.htc.launcher.entry.AppSimpleBean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class DBUtils extends SQLiteOpenHelper {
	
	
	private static DBUtils mInstance = null;
	private final static String DATABASE_NAME = "htc_launcher.db";
	private final static int VERSION = 1;
	private final String TABLENAME_FAVORITES = "table_favorites";// 我的收藏

	private final String TABLENAME_MAINAPP ="mainApp";
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
		// TODO Auto-generated method stub
		// 创建我的收藏
		String favorites_sql = "CREATE TABLE " + TABLENAME_FAVORITES
						+ " ( id integer primary key,  packagename text );";
		db.execSQL(favorites_sql);

		// 创建mainApp表
		String mainApp_sql = "CREATE TABLE " + TABLENAME_MAINAPP + " (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"name TEXT NOT NULL, " +
				"iconData BLOB NOT NULL, " +
				"action TEXT NOT NULL);";
		db.execSQL(mainApp_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String favorites_sql = "DROP TABLE IF EXISTS " + TABLENAME_FAVORITES;
		db.execSQL(favorites_sql);
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
		sharedPreferences.edit().putBoolean(Contants.MODIFY,true).apply();
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
				new String[] { packagename });
		db.close();
		sharedPreferences.edit().putBoolean(Contants.MODIFY,true).apply();
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
		sharedPreferences.edit().putBoolean(Contants.MODIFY,true).apply();
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
				new String[] { packagename });
		while (cs.moveToNext()) {
			isExist=true;
		}
		db.close();
		return isExist;
	}

	/***
	 * Time:2024/8/10
	 * Author:xuhao
	 * Usage:将从配置文件config中读出来的信息保存进本地的db数据库。
	 * @param name
	 * @param drawable
	 * @param action
	 */
	public void insertMainAppData(String name, Drawable drawable, String action) {

		long code = -1;
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("iconData", drawableToByteArray(drawable));  // 插入 BLOB 数据
		values.put("action", action);

		code = db.insert(TABLENAME_MAINAPP, null, values);
		if (code == -1) {
			System.out.println("插入数据失败");
		} else {
			System.out.println("插入数据成功，行ID：" + code);

		}
	}


	/***
	 * Time:2024/8/10
	 * Author:xuhao
	 * Usage:将drawable转化成drawableToByteArray，
	 * 方便保存到SQLite数据库中。
	 * @param drawable
	 * @return
	 */
	public byte[] drawableToByteArray(Drawable drawable) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}





}
