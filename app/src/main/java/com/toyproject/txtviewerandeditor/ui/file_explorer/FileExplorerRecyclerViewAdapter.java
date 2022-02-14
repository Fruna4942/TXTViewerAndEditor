package com.toyproject.txtviewerandeditor.ui.file_explorer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
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

        boolean isDirectory = fileExplorerRecyclerViewItem.getIsDirectory();
        Context context = holder.itemView.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (isDirectory)
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_folder_24_black));
            else
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_text_snippet_24_black));
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String presentTheme = sharedPreferences.getString(context.getString(R.string.theme), context.getString(R.string.theme_dark));

            if (presentTheme.equals(context.getString(R.string.theme_dark))) {
                if (isDirectory)
                    holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_folder_24_white));
                else
                    holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_text_snippet_24_white));
                holder.textView.setTextColor(context.getColor(R.color.text_color_dark));
            } else if (presentTheme.equals(context.getString(R.string.theme_light))) {
                if (isDirectory)
                    holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_folder_24_black));
                else
                    holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_text_snippet_24_black));
                holder.textView.setTextColor(context.getColor(R.color.text_color_light));
            }
        }

        holder.textView.setText(fileExplorerRecyclerViewItem.getFile().getName());

    }

    @Override
    public int getItemCount() {
        return fileExplorerRecyclerViewItemArrayList.size();
    }

    /*
    public void setTheme(Context context, FileExplorerRecyclerViewAdapter.ViewHolder viewHolder) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String presentTheme = sharedPreferences.getString(context.getString(R.string.theme), context.getString(R.string.theme_dark));

            TextView textView = viewHolder.textView;

            if (presentTheme.equals(context.getString(R.string.theme_dark))) {
                textView.setTextColor(context.getColor(R.color.text_color_dark));

            } else if (presentTheme.equals(context.getString(R.string.theme_light))) {
                textView.setTextColor(context.getColor(R.color.text_color_light));
            }
        }
    }*/

}
