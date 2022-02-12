package com.toyproject.txtviewerandeditor.ui.file_explorer;

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

public class FileExplorerRecyclerViewAdapter extends RecyclerView.Adapter<FileExplorerRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    private String presentPath;
    private ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList;
    private OnItemClickListener onItemClickListener;

    public FileExplorerRecyclerViewAdapter(String presentPath, ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList) {
        this.presentPath = presentPath;
        this.fileExplorerRecyclerViewItemArrayList = fileExplorerRecyclerViewItemArrayList;
    }

    public String getPresentPath() {
        return presentPath;
    }

    public ArrayList<FileExplorerRecyclerViewItem> getRecyclerViewItemArrayList() {
        return fileExplorerRecyclerViewItemArrayList;
    }

    public void changeDirectory(String presentPath, ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList) {
        this.presentPath = presentPath;
        this.fileExplorerRecyclerViewItemArrayList = fileExplorerRecyclerViewItemArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.image_view_item_recycler);
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
    public FileExplorerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_recycler_view_file_explorer, parent, false);
        FileExplorerRecyclerViewAdapter.ViewHolder viewHolder = new FileExplorerRecyclerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FileExplorerRecyclerViewAdapter.ViewHolder holder, int position) {
        FileExplorerRecyclerViewItem fileExplorerRecyclerViewItem = fileExplorerRecyclerViewItemArrayList.get(position);

        holder.imageView.setImageDrawable(fileExplorerRecyclerViewItem.getImageView());
        holder.textView.setText(fileExplorerRecyclerViewItem.getFile().getName());
    }

    @Override
    public int getItemCount() {
        return fileExplorerRecyclerViewItemArrayList.size();
    }
}
