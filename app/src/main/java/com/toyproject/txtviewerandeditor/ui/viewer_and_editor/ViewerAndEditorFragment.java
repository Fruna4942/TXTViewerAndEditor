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
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentViewerAndEditorBinding;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import lombok.SneakyThrows;

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

    SharedPreferences sharedPreferences;
    String presentFile;
    private boolean editable;
    private boolean isTextChanged = false;

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
        presentFile = setPresentFile(sharedPreferences);
        editable = sharedPreferences.getBoolean(getString(R.string.editable), false);

        scrollViewText = binding.scrollTextViewerAndEditor;
        scrollViewEditText = binding.scrollEditTextViewerAndEditor;
        textView = binding.textViewerAndEditor;
        editText = binding.editTextViewerAndEditor;

        // 해당 Fragment에서 On Back Pressed시 앱 종료
        onBackPressedCallback.setEnabled(true);
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        // API29 미만에서의 Fragment Theme 지정
        if (editable) {
            setTheme(editText);
        } else {
            setTheme(textView);
        }

        // TODO: 2022-02-19 더 빠른 읽기 및 setText 방법 찾기
        // FileExplorer에서 지정된 파일에 따라 파일을 읽어 화면에 표시
        if (presentFile.equals(getString(R.string.present_file_default_value))) {
            //crollViewEditText.setVisibility(View.GONE);
            //scrollViewText.setVisibility(View.VISIBLE);
            setTextPleaseSelect(scrollViewText, textView);
        } else {
            if (editable) {
                scrollViewText.setVisibility(View.GONE);
                scrollViewEditText.setVisibility(View.VISIBLE);
                setEditText(presentFile);
            } else {
                //scrollViewEditText.setVisibility(View.GONE);
                //scrollViewText.setVisibility(View.VISIBLE);
                setTextView(presentFile);
            }
        }

        // EditText에서 수정이 일어나면 수정여부를 true로 지정
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

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

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
                    isTextChanged = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                if (isTextChanged) {
                    DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Changes are made")
                            .setMessage("Do you want to save changes?\n" +
                                    "If you choose \"Don't Save\", the data you changed will be lost.")
                            .setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String text;
                                    File file;
                                    BufferedWriter bufferedWriter;

                                    text = editText.getText().toString();
                                    file = new File(presentFile);
                                    try {
                                        bufferedWriter = new BufferedWriter(new FileWriter(file));
                                        bufferedWriter.write(text);
                                        bufferedWriter.close();
                                        isTextChanged = false;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                        drawerLayout.closeDrawers();
                                    }
                                }
                            });
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

    private void setTextView(String presentFile) {
        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

        textView.setTextSize(textSize);
        //textView.setGravity(Gravity.NO_GRAVITY);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String charset = getCharset(presentFile);
                            BufferedReader bufferedReader;

                            if (charset != null)
                                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(presentFile), charset));
                            else
                                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(presentFile)));

                            long start, middle, end;
                            start = System.currentTimeMillis();

                            /*
                            String bufferReadLine;
                            StringBuilder stringBuilder = new StringBuilder();
                            while ((bufferReadLine = bufferedReader.readLine()) != null) {
                                textView.append(bufferReadLine + "\n");
                            }
                            //String strTemp = textView.getText().toString();
                            //textView.setText(strTemp.substring(0, strTemp.length() - 1));
                            end = System.currentTimeMillis();
                            System.out.println("읽기끝 : " + (end - start) / 1000.0 + "sec");
                             */

                            String bufferReadLine;
                            StringBuilder stringBuilder = new StringBuilder();
                            while ((bufferReadLine = bufferedReader.readLine()) != null) {
                                stringBuilder.append(bufferReadLine + "\n");
                            }
                            stringBuilder.setLength(stringBuilder.length() - 1);

                            middle = System.currentTimeMillis();
                            System.out.println("읽기끝 : " + (middle - start) / 1000.0 + "sec");

                            textView.setText(stringBuilder);

                            end = System.currentTimeMillis();
                            System.out.println("setText 끝 : " + (end - middle) / 1000.0 + "sec");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setEditText(String presentFile) {
        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

        editText.setTextSize(textSize);
        //editText.setGravity(Gravity.NO_GRAVITY);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String charset = getCharset(presentFile);
                            BufferedReader bufferedReader;

                            if (charset != null)
                                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(presentFile), charset));
                            else
                                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(presentFile)));

                            long start, end;
                            start = System.currentTimeMillis();
                            System.out.println("읽기시작");

                            /*
                            String bufferReadLine;
                            StringBuilder stringBuilder = new StringBuilder();
                            while ((bufferReadLine = bufferedReader.readLine()) != null) {
                                editText.append(bufferReadLine + "\n");
                            }
                            String strTemp = editText.getText().toString();
                            editText.setText(strTemp.substring(0, strTemp.length() - 1));
                            */

                            String bufferReadLine;
                            StringBuilder stringBuilder = new StringBuilder();
                            while ((bufferReadLine = bufferedReader.readLine()) != null) {
                                stringBuilder.append(bufferReadLine + "\n");
                            }
                            stringBuilder.setLength(stringBuilder.length() - 1);
                            editText.setText(stringBuilder);

                            end = System.currentTimeMillis();
                            System.out.println("읽기 & setText 끝 : " + (end - start) / 1000.0 + "sec");

                            isTextChanged = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @SneakyThrows
    public String getCharset(String presentPath) {
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
    }
}