package com.toyproject.txtviewerandeditor.ui.file_explorer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentFileExplorerBinding;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// TODO: 2022-02-16 새로운 txt 파일 추가, 삭제 구현
public class FileExplorerFragment extends Fragment {

    private FileExplorerViewModel fileExplorerViewModel;
    private FragmentFileExplorerBinding binding;
    private FileExplorerRecyclerViewAdapter fileExplorerRecyclerViewAdapter;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            String filePath = fileExplorerRecyclerViewAdapter.getFilePath();
            if (!filePath.equals(Environment.getExternalStorageDirectory().getPath())) {
                File file = new File(filePath);
                File parentFile = file.getParentFile();
                String newFilePath = parentFile.getPath();
                fileExplorerRecyclerViewAdapter.changeDirectory(newFilePath, getFileExplorerRecyclerViewItemList(newFilePath));
                //recyclerView.setAdapter(fileExplorerRecyclerViewAdapter);
                //recyclerView.refreshDrawableState();
                fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                NavDirections navDirections = FileExplorerFragmentDirections.actionNavFileExplorerToNavViewerAndEditor();
                Navigation.findNavController(FileExplorerFragment.super.getView()).navigate(navDirections);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        fileExplorerViewModel =
                new ViewModelProvider(this).get(FileExplorerViewModel.class);

        binding = FragmentFileExplorerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String filePath = Environment.getExternalStorageDirectory().getPath();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String presentTheme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

        RecyclerView recyclerView;

        fileExplorerRecyclerViewAdapter = new FileExplorerRecyclerViewAdapter(filePath, getFileExplorerRecyclerViewItemList(filePath));

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileExplorerRecyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        setTheme(presentTheme, recyclerView);

        fileExplorerRecyclerViewAdapter.setOnItemClickListener(new FileExplorerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                String newFilePath = fileExplorerRecyclerViewAdapter.getRecyclerViewItemArrayList().get(pos).getFile().getPath();

                File file = new File(newFilePath);
                if (file.isDirectory()) {
                    fileExplorerRecyclerViewAdapter.changeDirectory(newFilePath, getFileExplorerRecyclerViewItemList(newFilePath));
                    //recyclerView.setAdapter(fileExplorerRecyclerViewAdapter);
                    //recyclerView.refreshDrawableState();
                    fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
                } else if (MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()).equals("txt")) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.file_path), file.getPath());
                    editor.apply();

                    NavDirections navDirections = FileExplorerFragmentDirections.actionNavFileExplorerToNavViewerAndEditor();
                    Navigation.findNavController(view).navigate(navDirections);
                }
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        onBackPressedCallback.setEnabled(true);
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    public void onPause() {
        super.onPause();

        onBackPressedCallback.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_file_and_folder, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        ConstraintLayout constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.dialog_one_input, null);

        switch (item.getItemId()) {
            case R.id.menu_add_file:
                AlertDialog.Builder builderAddFile = new AlertDialog.Builder(getContext());
                builderAddFile.setView(constraintLayout)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String presentPath = fileExplorerRecyclerViewAdapter.getFilePath();
                                EditText editText = constraintLayout.findViewById(R.id.edit_dialog_one_input);
                                String filePath = presentPath + "/" + editText.getText() + ".txt";
                                File file = new File(filePath);

                                if (file.exists()) {
                                    System.out.println(filePath + " : already exits");
                                    Toast toast = Toast.makeText(getContext(), "File '" + editText.getText() + ".txt' already exits", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                FileUtils.write(file, null, (String) null);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        });
                AlertDialog alertDialogAddFile = builderAddFile.create();
                alertDialogAddFile.show();
                break;
            case R.id.menu_add_folder:
                TextView textViewTitle = (TextView) constraintLayout.findViewById(R.id.title_dialog_one_input);
                TextView textViewMessage = (TextView) constraintLayout.findViewById(R.id.message_dialog_one_input);

                textViewTitle.setText(getString(R.string.new_folder));
                textViewMessage.setText(getString(R.string.new_folder_name));

                AlertDialog.Builder builderAddFolder = new AlertDialog.Builder(getContext());
                builderAddFolder.setView(constraintLayout)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String presentPath = fileExplorerRecyclerViewAdapter.getFilePath();
                                EditText editText = constraintLayout.findViewById(R.id.edit_dialog_one_input);
                                String filePath = presentPath + "/" + editText.getText();
                                File file = new File(filePath);

                                if (file.exists()) {
                                    System.out.println(filePath + " : already exits");
                                    Toast toast = Toast.makeText(getContext(), "Folder '" + editText.getText() + "' already exits", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                FileUtils.forceMkdir(file);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        });
                AlertDialog alertDialogAddFolder = builderAddFolder.create();
                alertDialogAddFolder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<FileExplorerRecyclerViewItem> getFileExplorerRecyclerViewItemList(String filePath) {
        ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList = new ArrayList<>();
        File presentFile = new File(filePath);
        File[] presentFileList = presentFile.listFiles();

        if (presentFile.exists()) {
            if (presentFileList != null)
                Arrays.sort(presentFileList);

            for (int i = 0; i < presentFileList.length; i++) {
                File file_i = presentFileList[i];
                boolean anotherFile = true;
                boolean isDirectory = file_i.isDirectory();

                if (isDirectory || MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file_i).toString()).equals("txt"))
                    anotherFile = false;

                if (!anotherFile) {
                    FileExplorerRecyclerViewItem fileExplorerRecyclerViewItem = new FileExplorerRecyclerViewItem(isDirectory, file_i);
                    fileExplorerRecyclerViewItemArrayList.add(fileExplorerRecyclerViewItem);
                }
            }
        }

        return fileExplorerRecyclerViewItemArrayList;
    }

    public void setTheme(String presentTheme, RecyclerView recyclerView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
            if (presentTheme.equals(getString(R.string.theme_dark))) {
                dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_dark));
            } else if (presentTheme.equals(getString(R.string.theme_light))) {
                dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_light));
            }
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }
}