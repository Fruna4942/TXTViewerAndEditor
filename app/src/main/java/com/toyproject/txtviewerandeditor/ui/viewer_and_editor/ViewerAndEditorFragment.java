package com.toyproject.txtviewerandeditor.ui.viewer_and_editor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toyproject.txtviewerandeditor.MainActivity;
import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentViewerAndEditorBinding;
import com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager.BuilderThemeInit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import lombok.SneakyThrows;

public class ViewerAndEditorFragment extends Fragment {

    private ViewerAndEditorViewModel viewerAndEditorViewModel;
    private FragmentViewerAndEditorBinding binding;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) { // ViewerAndEditor 에서 Back 버튼이 눌렸을 때의 작업을 처리하는 콜백함수
        @Override
        public void handleOnBackPressed() {
            ActivityCompat.finishAffinity(Objects.requireNonNull(getActivity()));
            /*
            System.runFinalization();
            System.exit(0);
             */
        }
    };

    SharedPreferences sharedPreferences;

    String filePath = null;
    String fileEncoding = null;
    private boolean editable;
    private boolean isTextChanged = false;
    String theme;

    ScrollView scrollViewEditText;
    ScrollView scrollViewText;
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

        filePath = sharedPreferences.getString(getString(R.string.file_path), null);
        if (filePath != null) {
            fileEncoding = getCharset(filePath);
        }
        editable = sharedPreferences.getBoolean(getString(R.string.editable), false);
        theme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

        scrollViewText = binding.scrollTextViewerAndEditor;
        scrollViewEditText = binding.scrollEditTextViewerAndEditor;
        textView = binding.textViewerAndEditor;
        editText = binding.editTextViewerAndEditor;

        // TODO: 2022-02-19 더 빠른 rendering 방법 찾기
        // FileExplorer에서 지정된 파일에 따라 파일을 읽어 화면에 표시
        if (filePath == null) {
            //scrollViewEditText.setVisibility(View.GONE);
            //scrollViewText.setVisibility(View.VISIBLE);
            setTextPleaseSelect(textView);
            setTheme(textView);
        } else {
            if (editable) {
                scrollViewText.setVisibility(View.GONE);
                scrollViewEditText.setVisibility(View.VISIBLE);
                setEditText(filePath);
                setTheme(editText);

                // EditText 에서 수정이 일어나면 수정여부를 true 로 지정
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!isTextChanged) // 조건문 검사 하는 것이 자원을 덜 소모하는 듯?
                            isTextChanged = true;
                    }
                });
            } else {
                //scrollViewEditText.setVisibility(View.GONE);
                //scrollViewText.setVisibility(View.VISIBLE);
                setTextView(filePath);
                setTheme(textView);
            }
        }

        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // SharedPreference 에 설정된 파일의 이름을 ActionBar 의 Title로 지정
        if (filePath != null)
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(FilenameUtils.removeExtension((new File(filePath)).getName()));
    }

    @Override
    public void onResume() {
        super.onResume();

        onBackPressedCallback.setEnabled(true);
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        isTextChanged = false;
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

        // Editable 이 설정되어있으면 Save menu 추가
        if (filePath != null)
            if (editable)
                inflater.inflate(R.menu.menu_save_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save: // EditText 에서 수정한 내용 저장
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileUtils.write(new File(filePath), editText.getText(), fileEncoding);
                            isTextChanged = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default: // 저장하지 않고 Navigation drawer 클릭 시
                if (isTextChanged) {
                    DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

                    // 저장을 안내하는 AlertDialog
                    AlertDialog.Builder builder = BuilderThemeInit.init(getContext());
                    builder.setTitle("Changes are made")
                            .setMessage("Do you want to save changes?\n" +
                                    "If you choose \"Don't Save\", the data you changed will be lost.")
                            .setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String text;
                                    File file;
                                    BufferedWriter bufferedWriter;

                                    text = editText.getText().toString();
                                    file = new File(filePath);
                                    try {
                                        bufferedWriter = new BufferedWriter(new FileWriter(file));
                                        bufferedWriter.write(text);
                                        bufferedWriter.close();
                                        isTextChanged = false;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                        drawerLayout.closeDrawers();
                                    }
                                }
                            });
                    builder.setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTheme(TextView textView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String theme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));

            if (theme.equals(getString(R.string.theme_dark))) {
                textView.setTextColor(getActivity().getColor(R.color.text_color_dark));
            } else if (theme.equals(getString(R.string.theme_light))) {
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

    private void setTextPleaseSelect(TextView textView) {
        textView.setGravity(Gravity.CENTER);
        textView.setText("Please select txt file");
        textView.setTextSize(25);
    }

    private void setTextView(String presentFile) {
        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

        textView.setTextSize(textSize);
        //textView.setGravity(Gravity.NO_GRAVITY);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FileInputStream fileInputStream = new FileInputStream(new File(presentFile));
                    final String fileText = IOUtils.toString(fileInputStream, getCharset(presentFile));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(fileText);
                        }
                    });
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.file_path), null);
                }
            }
        }).start();
    }

    private void setEditText(String presentFile) {
        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

        editText.setTextSize(textSize);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FileInputStream fileInputStream = new FileInputStream(new File(presentFile));
                    final String fileText = IOUtils.toString(fileInputStream, getCharset(presentFile));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editText.setText(fileText);
                            isTextChanged = false;
                        }
                    });
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.file_path), null);
                }
            }
        }).start();
    }

    public String getCharset(String presentPath) {
        try {
            byte[] buf = new byte[4096];
            java.io.FileInputStream fis = new java.io.FileInputStream(presentPath);

            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println("Detected encoding = " + encoding);
            } else {
                System.out.println("No encoding detected.");
            }

            detector.reset();

            return encoding;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}