package com.htc.luminaos.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.entry.AppInfoBean;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.ScrollUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {

    Context mContext;
    RecyclerView recyclerView;
    List<AppInfoBean> infoBeans;
    private PackageManager mPm;

    public AppsAdapter(Context mContext,List<AppInfoBean> infoBeans, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.infoBeans = infoBeans;
        this.recyclerView = recyclerView;
        this.mPm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final AppInfoBean info = infoBeans.get(i);
        myViewHolder.icon.setImageDrawable(info.getAppicon());
        myViewHolder.name.setText(info.getAppname());
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.startNewApp(mContext,info.getApppackagename());
            }
        });

        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (recyclerView==null)
                    return;

                int position = ((RecyclerView) view.getParent()).getChildAdapterPosition(view);
                if(hasFocus&&position!=0){
                    int[] amount = ScrollUtils.getScrollAmount(recyclerView, view);//计算需要滑动的距离
                    recyclerView.smoothScrollBy(amount[0], amount[1]);
                }

                AnimationSet animationSet = new AnimationSet(true);
                view.bringToFront();
                if (hasFocus) {

                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                            1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(150);
                    animationSet.addAnimation(scaleAnimation);
                    animationSet.setFillAfter(true);
                    view.startAnimation(animationSet);
                } else {
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                            1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animationSet.addAnimation(scaleAnimation);
                    scaleAnimation.setDuration(150);
                    animationSet.setFillAfter(true);
                    view.startAnimation(animationSet);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoBeans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.app_name);
            rl_item = itemView.findViewById(R.id.rl_item);
            icon = itemView.findViewById(R.id.app_icon);
        }
    }
}
