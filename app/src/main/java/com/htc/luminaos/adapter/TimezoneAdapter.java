package com.htc.luminaos.adapter;

import android.app.AlarmManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.ScrollUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class TimezoneAdapter extends RecyclerView.Adapter<TimezoneAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<HashMap> list = null;
    private LayoutInflater mInflater;
    private int currentPosition=-1;
    AlarmManager alarm;
    private RecyclerView recyclerView;

    public void setCurrentPosition(int position){
        if(position>=0&&position<list.size()){
            this.currentPosition=position;
        }
    }

    public TimezoneAdapter(Context context, ArrayList<HashMap> list, RecyclerView recyclerView){
        this.mContext=context;
        this.list=list;
        this.mInflater= LayoutInflater.from(context);
         alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         this.recyclerView =recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.timezone_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final HashMap map = list.get(i);
        myViewHolder.name.setText(Objects.requireNonNull(map.get(Contants.KEY_DISPLAYNAME)).toString());
        myViewHolder.GMT.setText(Objects.requireNonNull(map.get(Contants.KEY_GMT)).toString());
        if (currentPosition == i){
            myViewHolder.status.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.status.setVisibility(View.GONE);
        }
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.setTimeZone((String) map
                        .get(Contants.KEY_ID));
                setCurrentPosition(i);
                notifyDataSetChanged();
            }
        });
        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (recyclerView==null)
                    return;

                if(b){
                    int[] amount = ScrollUtils.getScrollAmount(recyclerView, view);//计算需要滑动的距离
                    recyclerView.smoothScrollBy(amount[0], amount[1]);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView GMT;
        ImageView status;
        RelativeLayout rl_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            GMT = itemView.findViewById(R.id.GMT);
            status = itemView.findViewById(R.id.status);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }
}
