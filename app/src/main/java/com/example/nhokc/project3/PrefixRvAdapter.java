package com.example.nhokc.project3;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.nhokc.project3.databinding.ItemRvPrefixBinding;

public class PrefixRvAdapter extends RecyclerView.Adapter<PrefixRvAdapter.ViewHolder>{
    private ItemRvPrefixBinding binding;
    private IOnRecyclerViewItemClickListener listener;
    private IOnRecyclerViewItemLongClickListener longClickListener;
    private IPrefix iPrefix;
    public void setData(IPrefix iPrefix){
        this.iPrefix = iPrefix;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_rv_prefix,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String prefix = iPrefix.getPrefix(position);
        holder.binding.txtPrefix.setText(prefix);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecyclerViewItemClicked(position, 1);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickListener.onRecyclerViewItemLongClicked(position,1);
                return false;
            }
        });
    }
    public void setOnItemClickListener(IOnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }
    public void setLongClickListener(IOnRecyclerViewItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemCount() {
        return iPrefix.getCount();
    }

    public void addItem(int i) {
        notifyItemInserted(i);
    }
    public void updateItem(int i) {
        notifyItemMoved(i,i);
        notifyItemInserted(i);
    }
    public void deleteItem(int i) {
        notifyItemMoved(i,i);
    }
    public interface IPrefix{
        int getCount();
        String getPrefix(int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ItemRvPrefixBinding binding;
    public ViewHolder(ItemRvPrefixBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
}
