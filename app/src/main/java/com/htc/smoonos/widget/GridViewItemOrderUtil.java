package com.htc.smoonos.widget;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.GridView;

public class GridViewItemOrderUtil {
	/**
	 * 让该gridView运行到一行的�?后一列，点击右键，跳到下�?行的第一列；运行到一行的第一列，点击左键，跳转到上一行的�?后一�?
	 * 
	 * @param gridView
	 *            该gridView
	 * @param size
	 *            总共的项目数目，�?1�?�?
	 * @param columnNum
	 *            列数目，即每�?行有多少列，�?1�?�?
	 * @param position
	 *            当前的位置，�?0�?�?
	 * @param keyCode
	 * 
	 * @param event
	 */
	public static void lastToNextFirst(GridView gridView, int size,
                                       int columnNum, int position, int keyCode, KeyEvent event) {
		boolean isLast = isAnyLineLast(size, columnNum, position);
		boolean isFirst = isAnyLineFirst(size, columnNum, position);
//		Log.i("tag", event.getAction() + "onkeyLis执行�?" + keyCode);

		
		if (isLast && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if(position!=size-1){
				gridView.setSelection(position + 1);
			}
		}

		if (isLast && keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if((position + columnNum)<size){
				gridView.setSelection(position + columnNum);
			}else{
				gridView.setSelection(size-1);
			}
		}

		if (isFirst && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if(position - 1>0){
				gridView.setSelection(position - 1);
			}
		}
	}

	/**
	 * 让该gridView运行到一行的�?后一列，点击右键，跳到下�?行的第一�?
	 * 
	 * @param gridView
	 *            该gridView
	 * @param size
	 *            总共的项目数目，�?1�?�?
	 * @param columnNum
	 *            列数目，即每�?行有多少列，�?1�?�?
	 * @param position
	 *            当前的位置，�?0�?�?
	 * @param keyCode
	 * 
	 * @param event
	 */
	public static void onlyTopTpBottom(GridView gridView, int size,
                                       int columnNum, int position, int keyCode, KeyEvent event) {
		boolean isLast = isAnyLineLast(size, columnNum, position);

		if (isLast && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			gridView.setSelection(position + 1);
		}

	}

	/**
	 * 判断是否（到某一页的完整�?后一行并且点击向下）
	 * 
	 * 
	 * @param size
	 *            总共的项目数目，�?1�?�?
	 * @param columnNum
	 *            列数目，即每�?行有多少列，�?1�?�?
	 * @param position
	 *            当前的位置，�?0�?�?
	 * @param rawInPage
	 *            每一页有完整的几行，�?1�?�?
	 * @param keyCode
	 * 
	 * @param event
	 */
	public static boolean allPageDown(int size, int columnNum, int position,
			int rawInPage, int keyCode, KeyEvent event) {
		// 每一个页有多少个完整的item
		int itemAPage = columnNum * rawInPage;
		// 每一个页的最后一行的第一个数�?
		int num = itemAPage - columnNum;
		// 求出�?共有多少�?
		int rawNum = size / itemAPage;
		for (int i = 1; i <= rawNum; i++) {
			for (int j = 0; j < columnNum; j++) {
				// 每行有多少列，进行多少次相加
				if (position == num + itemAPage * (i - 1) + j) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						// 如果有相等，则立即返回true，不再进行判�?
						return true;
					}
				}
			}

		}
		return false;
	}

	/**
	 * 判断是否（到某一页的第一行并且点击向上按钮）
	 * 
	 * 
	 * @param size
	 *            总共的项目数目，�?1�?�?
	 * @param columnNum
	 *            列数目，即每�?行有多少列，�?1�?�?
	 * @param position
	 *            当前的位置，�?0�?�?
	 * @param rawInPage
	 *            每一页有完整的几行，�?1�?�?
	 * @param keyCode
	 * 
	 * @param event
	 */
	public static boolean allPageUp(int size, int columnNum, int position,
			int rawInPage, int keyCode, KeyEvent event) {

		// 每一个页有多少个完整的item
		int itemAPage = columnNum * rawInPage;
		// 求出�?共有多少�?+1
		int rawNum = size / columnNum + 1;
		for (int i = 1; i <= rawNum; i++) {
			for (int j = 0; j < columnNum; j++) {
				// 每行有多少列，进行多少次相加
				if (position == itemAPage*i + j) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_UP
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						// 如果有相等，则立即返回true，不再进行判�?
						return true;
					}
				}
			}

		}
		return false;
	}

	/**
	 * 判断是否是每�?行的�?后一个项�?
	 * 
	 * @param size
	 *            总共的项目数目，�?1�?�?
	 * @param columnNum
	 *            列数目，即每�?行有多少列，�?1�?�?
	 * @param position
	 *            当前的位置，�?0�?�?
	 * @return true代表是，false代表不是
	 */
	public  static boolean isAnyLineLast(int size, int columnNum, int position) {
		// 求出�?共有多少�?
		int rawNum = size / columnNum + 1;
//		Log.i("tag", "�?共有多少�?" + rawNum);
		for (int i = 1; i <= rawNum; i++) {
			if (position == columnNum * i - 1) {
				// 如果当前位置是每�?行的�?后一列，则返回true
				return true;
			}
		}
		return false;

	}

	/**
	 * 判断是否是每�?行的第一个项�?
	 * 
	 * @param size
	 *            总共的项目数目，�?1�?�?
	 * @param columnNum
	 *            列数目，即每�?行有多少列，�?1�?�?
	 * @param position
	 *            当前的位置，�?0�?�?
	 * @return true代表是，false代表不是
	 */
	public static boolean isAnyLineFirst(int size, int columnNum, int position) {
		// 求出�?共有多少�?
		int rawNum = size / columnNum + 1;
		Log.i("tag", "�?共有多少�?" + rawNum);
		// 并不�?要判断第�?行，直接从第二行�?�?
		for (int i = 2; i <= rawNum; i++) {
			if (position == columnNum * i - columnNum) {
				// 如果当前位置是每�?行的�?后一列，则返回true
				return true;
			}
		}
		return false;

	}
}

