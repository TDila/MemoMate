package com.vulcan.memomate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vulcan.memomate.R;
import com.vulcan.memomate.entities.Memo;
import com.vulcan.memomate.listeners.MemosListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MemosAdapter extends RecyclerView.Adapter<MemosAdapter.ViewHolder> {
    private List<Memo> memos;
    private Context context;
    private MemosListener memosListener;
    private Timer timer;
    private List<Memo> memosSource;

    public MemosAdapter(List<Memo> memos, Context context, MemosListener memosListener) {
        this.memos = memos;
        this.context = context;
        this.memosListener = memosListener;
        this.memosSource = memos;
    }

    @NonNull
    @Override
    public MemosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_container_memo,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemosAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.memoTitle.setText(memos.get(position).getTitle());
        if(memos.get(position).getSubtitle().trim().isEmpty()){
            holder.memoSubtitle.setVisibility(View.GONE);
        }else{
            holder.memoSubtitle.setText(memos.get(position).getSubtitle());
        }
        holder.memoDateTime.setText(memos.get(position).getDateTime());
        holder.layoutMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memosListener.onMemoClicked(memos.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView memoTitle, memoSubtitle, memoDateTime;
        LinearLayout layoutMemo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memoTitle = itemView.findViewById(R.id.memoTitle);
            memoSubtitle = itemView.findViewById(R.id.memoSubTitle);
            memoDateTime = itemView.findViewById(R.id.memoDateTime);
            layoutMemo = itemView.findViewById(R.id.layoutMemo);
        }
    }

    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    memos = memosSource;
                }else{
                    ArrayList<Memo> temp = new ArrayList<>();
                    for(Memo memo : memosSource){
                        if(memo.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || memo.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || memo.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(memo);
                        }
                    }
                    memos = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}
