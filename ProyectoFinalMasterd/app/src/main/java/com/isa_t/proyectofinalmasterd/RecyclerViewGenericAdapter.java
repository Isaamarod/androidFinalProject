package com.isa_t.proyectofinalmasterd;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

public abstract class RecyclerViewGenericAdapter<T> extends RecyclerView.Adapter<RecyclerViewGenericAdapter<T>.ViewHolder>{
    private List<T> data;
    private AdapterView.OnItemClickListener listener;
    private int itemLayoutId;

    public RecyclerViewGenericAdapter(Activity c, List<T> data, int itemLayoutId, AdapterView.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
        this.itemLayoutId = itemLayoutId;
    }

    public T getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(itemLayoutId, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null)
                        listener.onItemClick(null, view, ViewHolder.this.getAdapterPosition(), 0);
                }
            });
        }
    }
}
