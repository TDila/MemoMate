package com.vulcan.memomate.adapters;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vulcan.memomate.R;
import com.vulcan.memomate.entities.Memo;

import java.util.ArrayList;
import java.util.List;

public class MemosAdapter extends RecyclerView.Adapter<MemosAdapter.ViewHolder> {
    List<Memo> memos;
    Context context;

    public MemosAdapter(List<Memo> memos, Context context) {
        this.memos = memos;
        this.context = context;
    }

    @NonNull
    @Override
    public MemosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_container_memo,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemosAdapter.ViewHolder holder, int position) {
        holder.memoTitle.setText(memos.get(position).getTitle());
        if(memos.get(position).getSubtitle().trim().isEmpty()){
            holder.memoSubtitle.setVisibility(View.GONE);
        }else{
            holder.memoSubtitle.setText(memos.get(position).getSubtitle());
        }
        holder.memoDateTime.setText(memos.get(position).getDateTime());
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView memoTitle, memoSubtitle, memoDateTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memoTitle = itemView.findViewById(R.id.memoTitle);
            memoSubtitle = itemView.findViewById(R.id.memoSubTitle);
            memoDateTime = itemView.findViewById(R.id.memoDateTime);
        }
    }
}
