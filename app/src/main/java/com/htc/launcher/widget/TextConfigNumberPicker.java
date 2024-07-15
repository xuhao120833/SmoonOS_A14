package com.htc.launcher.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.htc.launcher.R;

import java.lang.reflect.Field;

public class TextConfigNumberPicker extends NumberPicker {

    public TextConfigNumberPicker(Context context) {
        super(context);
    }

    public TextConfigNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextConfigNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            //设置文字的颜色和大小
            ((EditText) view).setTextColor(getResources().getColor(R.color.black));
            ((EditText) view).setTextSize(23);
        }

        try {
            //设置分割线大小颜色
            Field mSelectionDivider = this.getFile("mSelectionDivider");
            mSelectionDivider.set(this, new ColorDrawable(getResources().getColor(R.color.transpant)));
            mSelectionDivider.set(this, 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //反射获取控件 mSelectionDivider mInputText当前选择的view
    public Field getFile(String fieldName) {
        try {
            //设置分割线的颜色值
            Field pickerFields = NumberPicker.class.getDeclaredField(fieldName);
            pickerFields.setAccessible(true);
            return pickerFields;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setMInputColor(int color) {
        Field mInputText = this.getFile("mInputText");
        try {
            ((EditText) mInputText.get(this)).setTextColor(color);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}