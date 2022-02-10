package com.toyproject.txtviewerandeditor.ui.file_explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toyproject.txtviewerandeditor.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    private String presentPath;
    private ArrayList<RecyclerViewItem> recyclerViewItemArrayList;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(String presentPath, ArrayList<RecyclerViewItem> recyclerViewItemArrayList) {
        this.presentPath = presentPath;
        this.recyclerViewItemArrayList = recyclerViewItemArrayList;
    }

    public String getPresentPath() {
        return presentPath;
    }

    public ArrayList<RecyclerViewItem> getRecyclerViewItemArrayList() {
        return recyclerViewItemArrayList;
    }

    public void changeDirectory(String presentPath, ArrayList<RecyclerViewItem> recyclerViewItemArrayList) {
        this.presentPath = presentPath;
        this.recyclerViewItemArrayList = recyclerViewItemArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.image_view);
            textView = view.findViewById(R.id.text_item_recycler);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_recycler_view, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        RecyclerViewItem recyclerViewItem = recyclerViewItemArrayList.get(position);

        holder.imageView.setImageDrawable(recyclerViewItem.getImageView());
        holder.textView.setText(recyclerViewItem.getFile().getName());
    }

    @Override
    public int getItemCount() {
        return recyclerViewItemArrayList.size();
    }
}
