package com.toyproject.txtviewerandeditor.ui.file_explorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentFileExplorerBinding;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileExplorerFragment extends Fragment{

    private FileExplorerViewModel fileExplorerViewModel;
    private FragmentFileExplorerBinding binding;
    private FileExplorerRecyclerViewAdapter fileExplorerRecyclerViewAdapter;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            String presentPath = fileExplorerRecyclerViewAdapter.getPresentPath();
            if (!presentPath.equals(Environment.getExternalStorageDirectory().getPath())) {
                File file = new File(presentPath);
                File parentFile = file.getParentFile();
                presentPath = parentFile.getPath();
                fileExplorerRecyclerViewAdapter.changeDirectory(presentPath, getFileExplorerRecyclerViewItemList(presentPath));
                //recyclerView.setAdapter(fileExplorerRecyclerViewAdapter);
                //recyclerView.refreshDrawableState();
                fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
            }
            else {
                NavDirections navDirections = FileExplorerFragmentDirections.actionNavFileExplorerToNavViewerAndEditor();
                Navigation.findNavController(FileExplorerFragment.super.getView()).navigate(navDirections);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fileExplorerViewModel =
                new ViewModelProvider(this).get(FileExplorerViewModel.class);

        binding = FragmentFileExplorerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String presentPath = Environment.getExternalStorageDirectory().getPath();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String presentTheme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

        RecyclerView recyclerView;

        fileExplorerRecyclerViewAdapter = new FileExplorerRecyclerViewAdapter(presentPath, getFileExplorerRecyclerViewItemList(presentPath));

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fileExplorerRecyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        setTheme(presentTheme, recyclerView);

        fileExplorerRecyclerViewAdapter.setOnItemClickListener(new FileExplorerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                String presentPath = fileExplorerRecyclerViewAdapter.getRecyclerViewItemArrayList().get(pos).getFile().getPath();

                File file = new File(presentPath);
                if (file.isDirectory()) {
                    fileExplorerRecyclerViewAdapter.changeDirectory(presentPath, getFileExplorerRecyclerViewItemList(presentPath));
                    //recyclerView.setAdapter(fileExplorerRecyclerViewAdapter);
                    //recyclerView.refreshDrawableState();
                    fileExplorerRecyclerViewAdapter.notifyDataSetChanged();
                } else if (MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()).equals("txt")) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.present_file), file.getPath());
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

    public ArrayList<FileExplorerRecyclerViewItem> getFileExplorerRecyclerViewItemList(String presentPath) {
        ArrayList<FileExplorerRecyclerViewItem> fileExplorerRecyclerViewItemArrayList = new ArrayList<>();
        File presentFile = new File(presentPath);
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