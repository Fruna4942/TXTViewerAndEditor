package com.toyproject.txtviewerandeditor.ui.viewer_and_editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentViewerAndEditorBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ViewerAndEditorFragment extends Fragment {

    private ViewerAndEditorViewModel viewerAndEditorViewModel;
    private FragmentViewerAndEditorBinding binding;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            ActivityCompat.finishAffinity(Objects.requireNonNull(getActivity()));
            /*
            System.runFinalization();
            System.exit(0);
             */
        }
    };
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewerAndEditorViewModel =
                new ViewModelProvider(this).get(ViewerAndEditorViewModel.class);

        binding = FragmentViewerAndEditorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textViewerAndEditor;

        setTheme();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String presentFile = sharedPreferences.getString(getString(R.string.present_file), getString(R.string.present_file_default_value));
        File file = new File(presentFile);
        if (!file.exists()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.present_file), getString(R.string.present_file_default_value));
            editor.apply();

            presentFile = getString(R.string.present_file_default_value);
        }

        if (presentFile.equals(getString(R.string.present_file_default_value))) {
            textView.setGravity(Gravity.CENTER);
            textView.setText("txt파일을 선택해 주세요");
        } else {
            textView.setGravity(Gravity.NO_GRAVITY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    byte[] bytes = Files.readAllBytes(Paths.get(presentFile));
                    textView.setText(new String(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        textView.append(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
        final TextView textView = binding.textHome;
        viewAndEditViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    public void setTheme() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String presentTheme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

            if (presentTheme.equals(getString(R.string.theme_dark))) {
                textView.setTextColor(getActivity().getColor(R.color.text_color_dark));
            } else if (presentTheme.equals(getString(R.string.theme_light))) {
                textView.setTextColor(getActivity().getColor(R.color.text_color_light));
            }
        }
    }
}