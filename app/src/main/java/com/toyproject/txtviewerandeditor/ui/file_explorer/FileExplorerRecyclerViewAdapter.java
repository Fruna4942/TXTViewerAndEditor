package com.toyproject.txtviewerandeditor.ui.file_explorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.toyproject.txtviewerandeditor.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileExplorerRecyclerViewAdapter extends RecyclerView.Adapter<FileExplorerRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    private String directoryPath;
    private ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList;
    private OnItemClickListener onItemClickListener;

    public FileExplorerRecyclerViewAdapter(String directoryPath, ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList) {
        this.directoryPath = directoryPath;
        this.fileExplorerRecyclerViewItemArrayList = fileExplorerRecyclerViewItemArrayList;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public ArrayList<FileExplorerRecyclerViewItem> getRecyclerViewItemArrayList() {
        return fileExplorerRecyclerViewItemArrayList;
    }

    public void updateDirectory(String directoryPath, ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList) {
        this.directoryPath = directoryPath;
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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    FileExplorerRecyclerViewItem fileExplorerRecyclerViewItem = fileExplorerRecyclerViewItemArrayList.get(getAdapterPosition());

                    File file = fileExplorerRecyclerViewItem.getFile();
                    String fileName = file.getName();
                    String parentFilePath = file.getParent();

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(fileName)
                            .setItems(R.array.file_long_click_array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LayoutInflater layoutInflater = ((FragmentActivity) view.getContext()).getLayoutInflater();
                                    ConstraintLayout constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.dialog_one_input, null);
                                    TextView textViewTitle = constraintLayout.findViewById(R.id.title_dialog_one_input);
                                    TextView textViewMessage = constraintLayout.findViewById(R.id.message_dialog_one_input);
                                    EditText editText = constraintLayout.findViewById(R.id.edit_dialog_one_input);

                                    switch (i) {
                                        case 0:
                                            textViewTitle.setText(view.getContext().getString(R.string.rename));
                                            if (file.isDirectory()) {
                                                textViewMessage.setText(view.getContext().getString(R.string.new_folder_name));
                                                editText.setText(fileName);
                                            } else {
                                                textViewMessage.setText(view.getContext().getString(R.string.new_file_name));
                                                editText.setText(FilenameUtils.removeExtension(fileName));
                                            }
                                            editText.setSelectAllOnFocus(true);
                                            editText.requestFocus();

                                            AlertDialog.Builder builderRename = new AlertDialog.Builder(view.getContext());
                                            builderRename.setView(constraintLayout)
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                        }
                                                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            try {
                                                                File fileRenameTo;
                                                                if (file.isDirectory()) {
                                                                    fileRenameTo = new File(parentFilePath + "/" + editText.getText());
                                                                    FileUtils.moveDirectory(file, fileRenameTo);
                                                                } else {
                                                                    fileRenameTo = new File(parentFilePath + "/" + editText.getText() + ".txt");
                                                                    FileUtils.moveFile(file, fileRenameTo);
                                                                }
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            updateDirectory(parentFilePath, FileExplorerFragment.getFileExplorerRecyclerViewItemList(parentFilePath));
                                                            FileExplorerRecyclerViewAdapter.super.notifyDataSetChanged();
                                                        }
                                                    });
                                            AlertDialog alertDialogRename = builderRename.create();
                                            alertDialogRename.show();
                                            break;
                                        case 1:
                                            AlertDialog.Builder builderDelete = new AlertDialog.Builder(view.getContext());
                                            if (file.isDirectory()) {
                                                builderDelete.setMessage("Delete folder \"" + fileName + "\" and its contents?");
                                            } else {
                                                builderDelete.setMessage("Delete \"" + fileName + "\"?");
                                            }
                                            builderDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    try {
                                                        FileUtils.forceDelete(file);
                                                        updateDirectory(parentFilePath, FileExplorerFragment.getFileExplorerRecyclerViewItemList(parentFilePath));
                                                        FileExplorerRecyclerViewAdapter.super.notifyDataSetChanged();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            AlertDialog alertDialogDelete = builderDelete.create();
                                            alertDialogDelete.show();
                                            break;
                                    }
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return false;
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
