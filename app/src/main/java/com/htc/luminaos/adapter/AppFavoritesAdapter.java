package com.htc.luminaos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.htc.luminaos.R;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.utils.ScrollUtils;
import com.htc.luminaos.widget.RLRelativelayout;

import java.util.ArrayList;


/**
 * @author 作�?�：hxd
 * @version 创建时间 2020/9/8 下午3:50:51 类说�?
 */
public class AppFavoritesAdapter extends BaseAdapter implements View.OnFocusChangeListener {

    private Context mContext;
    private ArrayList<AppInfoBean> mList = new ArrayList<AppInfoBean>();
    private LayoutInflater inflater;

    public AppFavoritesAdapter(Context context, ArrayList<AppInfoBean> list) {
        this.mContext = context;
        this.mList = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return mList.size();
    }

    @Override
    public AppInfoBean getItem(int position) {

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewholder = null;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_appfavorites_gridview_item, null);
            viewholder.addapp_icon_iv = (ImageView) convertView.findViewById(R.id.addapp_icon_iv);
            viewholder.addapp_name = convertView.findViewById(R.id.addapp_name);
            viewholder.appapp_iv = (ImageView) convertView.findViewById(R.id.appapp_iv);
			viewholder.rlRelativelayout = (RLRelativelayout)convertView.findViewById(R.id.appsfavor);


//			if (fontFace != null) {
//				viewholder.addapp_name.setTypeface(fontFace);
//			}

            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        AppInfoBean bean = getItem(position);

        viewholder.addapp_icon_iv.setBackground(bean.getAppicon());
        viewholder.addapp_name.setText(bean.getAppname());


        if (bean.isCheck()) {
            viewholder.appapp_iv.setVisibility(View.VISIBLE);
        } else {
            viewholder.appapp_iv.setVisibility(View.GONE);
        }

		viewholder.rlRelativelayout.setOnFocusChangeListener(this);

        return convertView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
//        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        AnimationSet animationSet = new AnimationSet(true);
        v.bringToFront();
        if (hasFocus) {

            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                    1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(150);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                    1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.addAnimation(scaleAnimation);
            scaleAnimation.setDuration(150);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        }

    }

    static class ViewHolder {
        private ImageView addapp_icon_iv;
        private TextView addapp_name;
        private ImageView appapp_iv;

		private RLRelativelayout rlRelativelayout;
    }

}
