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
import com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager.BuilderThemeInit;
import com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager.OneInputAlertDialogLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileExplorerFragment extends Fragment {

    private FileExplorerViewModel fileExplorerViewModel;
    private FragmentFileExplorerBinding binding;
    private FileExplorerRecyclerViewAdapter fileExplorerRecyclerViewAdapter;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) { // FileExplorer 에서 Back 버튼이 눌렸을 때의 작업을 처리하는 콜백함수
        @Override
        public void handleOnBackPressed() {
            String directoryPath = fileExplorerRecyclerViewAdapter.getDirectoryPath();
            if (!directoryPath.equals(Environment.getExternalStorageDirectory().getPath())) { // Root 디렉토리가 아니면 부모 디렉토리로 이동
                File directory = new File(directoryPath);
                File parentDirectory = directory.getParentFile();
                String parentDirectoryPath = parentDirectory.getPath();
                fileExplorerRecyclerViewAdapter.updateDirectory(parentDirectoryPath, getFileExplorerRecyclerViewItemList(parentDirectoryPath));
                fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
            } else { // Root 디렉토리면 ViewerAndEditor Fragment 로 Navigate
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
                if (file.isDirectory()) { // 디렉토리 클릭 시 해당 디렉토리로 이동
                    // Adapter 의 Item List 를 새로운 디렉토리 내부의 요소들로 변경
                    fileExplorerRecyclerViewAdapter.updateDirectory(newFilePath, getFileExplorerRecyclerViewItemList(newFilePath));
                    fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
                } else if (MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()).equals("txt")) { // Text 파일 클릭 시 ViewerAndEditor Fragment 로 Navigate
                    // 해당파일을 SharedPreferences 에 저장
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
        OneInputAlertDialogLayout oneInputAlertDialogLayout = new OneInputAlertDialogLayout(layoutInflater);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

        oneInputAlertDialogLayout.setTheme(getContext());
        oneInputAlertDialogLayout.setFocus(true);

        switch (item.getItemId()) {
            case R.id.menu_add_file: // 새로운 Text 파일 추가
                oneInputAlertDialogLayout.setTexts(getString(R.string.new_text_file), getString(R.string.new_file_name), null);
                ConstraintLayout constraintLayoutAddFile = oneInputAlertDialogLayout.getConstraintLayout();
                EditText editTextAddFile = constraintLayoutAddFile.findViewById(R.id.edit_dialog_one_input);

                // 새로운 Text 파일의 이름을 입력받는 AlertDialog
                AlertDialog.Builder builderAddFile = BuilderThemeInit.init(getContext());
                builderAddFile.setView(oneInputAlertDialogLayout.getConstraintLayout())
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String presentPath = fileExplorerRecyclerViewAdapter.getDirectoryPath();
                                String filePath = presentPath + "/" + editTextAddFile.getText() + ".txt";
                                File file = new File(filePath);

                                if (file.exists()) {
                                    Toast toast = Toast.makeText(getContext(), "File '" + editTextAddFile.getText() + ".txt' already exits", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                FileUtils.write(file, null, (String) null);
                                                
                                                String directoryPath = fileExplorerRecyclerViewAdapter.getDirectoryPath();
                                                fileExplorerRecyclerViewAdapter.updateDirectory(directoryPath, getFileExplorerRecyclerViewItemList(directoryPath));
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
                                                    }
                                                });
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
            case R.id.menu_add_folder: // 새로운 디렉토리 추가
                oneInputAlertDialogLayout.setTexts(getString(R.string.new_folder), getString(R.string.new_folder_name), null);
                ConstraintLayout constraintLayoutAddFolder = oneInputAlertDialogLayout.getConstraintLayout();
                EditText editTextAddFolder = constraintLayoutAddFolder.findViewById(R.id.edit_dialog_one_input);

                // 새로운 디렉토리의 이름을 입력받는 AlertDialog
                AlertDialog.Builder builderAddFolder = BuilderThemeInit.init(getContext());
                builderAddFolder.setView(constraintLayoutAddFolder)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String presentPath = fileExplorerRecyclerViewAdapter.getDirectoryPath();
                                String filePath = presentPath + "/" + editTextAddFolder.getText();
                                File file = new File(filePath);

                                if (file.exists()) {
                                    Toast toast = Toast.makeText(getContext(), "Folder '" + editTextAddFolder.getText() + "' already exits", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                FileUtils.forceMkdir(file);
                                                
                                                String directoryPath = fileExplorerRecyclerViewAdapter.getDirectoryPath();
                                                fileExplorerRecyclerViewAdapter.updateDirectory(directoryPath, getFileExplorerRecyclerViewItemList(directoryPath));
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
                                                    }
                                                });
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

    public static ArrayList<FileExplorerRecyclerViewItem> getFileExplorerRecyclerViewItemList(String directoryPath) {
        ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists()) {
            File[] fileList = directory.listFiles();
            if (fileList != null)
                Arrays.sort(fileList);

            for (int i = 0; i < fileList.length; i++) {
                File file_i = fileList[i];
                boolean isDirectory = file_i.isDirectory();

                if (isDirectory || MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file_i).toString()).equals("txt")) {
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