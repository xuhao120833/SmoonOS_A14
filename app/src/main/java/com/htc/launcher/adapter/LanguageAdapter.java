package com.htc.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.internal.app.LocalePicker;
import com.htc.launcher.R;
import com.htc.launcher.entry.Language;
import com.htc.launcher.utils.ScrollUtils;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class LanguageAdapter  extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder>{

    List<Language> languageList = new ArrayList<>();
    Context mContext;
    String cur_language="";
    private RecyclerView recyclerView;

    public LanguageAdapter(List<Language> languageList, Context mContext,RecyclerView recyclerView){
        this.mContext = mContext;
        this.languageList = languageList;
        this.recyclerView = recyclerView;
    }

    public void updateList(List<Language> languageList){
        this.languageList = languageList;
    }

    public void setCur_language(String cur_language) {
        this.cur_language = cur_language;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_item,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Language language = languageList.get(i);
        myViewHolder.language_name.setText(language.getLabel());
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cur_language.equals(language.getLocale().getLanguage()+language.getLocale().getCountry())) {
                    LocalePicker.updateLocale(language.getLocale());
                    setCur_language(language.getLocale().getLanguage()+language.getLocale().getCountry());
                    notifyDataSetChanged();
                }
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

        if (cur_language.equals(language.getLocale().getLanguage()+language.getLocale().getCountry())){
            myViewHolder.status.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView language_name;
        ImageView status;
        RelativeLayout rl_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            language_name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }
}
