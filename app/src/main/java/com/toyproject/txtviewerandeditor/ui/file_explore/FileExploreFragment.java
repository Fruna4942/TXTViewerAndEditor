package com.toyproject.txtviewerandeditor.ui.file_explore;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentFileExploreBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileExploreFragment extends Fragment {

    private FileExploreViewModel fileExploreViewModel;
    private FragmentFileExploreBinding binding;
    private RecyclerView recyclerView;
    private File file;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fileExploreViewModel =
                new ViewModelProvider(this).get(FileExploreViewModel.class);

        binding = FragmentFileExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerView;

        /*
        final TextView textView = binding.textGallery;
        fileExploreViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */

        String rootPath = Environment.getExternalStorageDirectory().getPath();
        file = new File(rootPath);
        ArrayList<RecyclerViewItem> recyclerViewItemArrayList = new ArrayList<>();
        if (file != null) {
            File[] fileList = file.listFiles();
            if (fileList != null)
                Arrays.sort(fileList);

            for (int i = 0; i < fileList.length; i++) {
                File file_i = fileList[i];
                int drawableId = -1;

                if (file_i.isDirectory())
                    drawableId = R.drawable.ic_baseline_folder_24_black;
                else if (MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file_i).toString()).equals("txt"))
                    drawableId = R.drawable.ic_baseline_text_snippet_24_black;

                if (drawableId != -1) {
                    RecyclerViewItem recyclerViewItem = new RecyclerViewItem(file_i, ContextCompat.getDrawable(getContext(), drawableId));
                    recyclerViewItemArrayList.add(recyclerViewItem);
                }
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(recyclerViewItemArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                //temp
                Toast toast = Toast.makeText(getContext(), String.format("item pos : %d", pos), Toast.LENGTH_SHORT);
                toast.show();
                // TODO: 2022-02-05 디렉토리 이동 기능 구현 필요
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}