package com.toyproject.txtviewerandeditor.ui.viewer_and_editor;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentViewerAndEditorBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private SharedPreferences sharedPreferences;
    String presentFile;
    private boolean editable;
    private TextView textView;
    private EditText editText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        viewerAndEditorViewModel =
                new ViewModelProvider(this).get(ViewerAndEditorViewModel.class);

        binding = FragmentViewerAndEditorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editable = sharedPreferences.getBoolean(getString(R.string.editable), false);
        presentFile = setPresentFile(sharedPreferences);

        textView = binding.textViewerAndEditor;
        editText = binding.editTextViewerAndEditor;

        if (editable) {
            setTheme(editText);
        } else {
            setTheme(textView);
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
    public void onResume() {
        super.onResume();

        onBackPressedCallback.setEnabled(true);
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        ScrollView scrollViewEditText;
        ScrollView scrollViewText;

        scrollViewText = binding.scrollTextViewerAndEditor;
        scrollViewEditText = binding.scrollEditTextViewerAndEditor;

        if (presentFile.equals(getString(R.string.present_file_default_value))) {
            //crollViewEditText.setVisibility(View.GONE);
            //scrollViewText.setVisibility(View.VISIBLE);
            setTextPleaseSelect(scrollViewText, textView);
        } else {
            if (editable) {
                scrollViewText.setVisibility(View.GONE);
                scrollViewEditText.setVisibility(View.VISIBLE);
                setText(presentFile, editText);
            } else {
                //scrollViewEditText.setVisibility(View.GONE);
                //scrollViewText.setVisibility(View.VISIBLE);
                setText(presentFile, textView);
            }
        }
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

        boolean editable;

        editable = sharedPreferences.getBoolean(getString(R.string.editable), false);

        if (!presentFile.equals(getString(R.string.present_file_default_value)))
            if (editable)
                inflater.inflate(R.menu.menu_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                String text;
                File file;
                BufferedWriter bufferedWriter;

                text = editText.getText().toString();
                file = new File(presentFile);
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(text);
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void setTheme(TextView textView) {
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

    private void setTheme(EditText editText) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String presentTheme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

            if (presentTheme.equals(getString(R.string.theme_dark))) {
                editText.setTextColor(getActivity().getColor(R.color.text_color_dark));
            } else if (presentTheme.equals(getString(R.string.theme_light))) {
                editText.setTextColor(getActivity().getColor(R.color.text_color_light));
            }
        }
    }

    private String setPresentFile(SharedPreferences sharedPreferences) {
        String presentFile = sharedPreferences.getString(getString(R.string.present_file), getString(R.string.present_file_default_value));

        File file = new File(presentFile);
        if (!file.exists()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.present_file), getString(R.string.present_file_default_value));
            editor.apply();

            presentFile = getString(R.string.present_file_default_value);
        }
        return presentFile;
    }

    private void setTextPleaseSelect(ScrollView scrollView, TextView textView) {
        scrollView.setFillViewport(true);
        textView.setGravity(Gravity.CENTER);
        textView.setText("Please select txt file");
        textView.setTextSize(25);
    }

    private void setText(String presentFile, TextView textView) {
        File file = new File(presentFile);
        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

        textView.setTextSize(textSize);
        //textView.setGravity(Gravity.NO_GRAVITY);
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
                StringBuilder stringBuilder = new StringBuilder();
                String bufferReadLine;
                while ((bufferReadLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(bufferReadLine).append("\n");
                }
                stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));
                textView.setText(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setText(String presentFile, EditText editText) {
        File file = new File(presentFile);
        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

        editText.setTextSize(textSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(presentFile));
                editText.setText(new String(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String bufferReadLine;
                while ((bufferReadLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(bufferReadLine).append("\n");
                }
                stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));
                editText.setText(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}