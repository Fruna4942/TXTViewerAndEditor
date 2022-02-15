package com.toyproject.txtviewerandeditor.ui.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentSettingBinding;

import java.util.ArrayList;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private FragmentSettingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        String presentTheme;
        boolean editable;

        SettingListViewAdapter settingListViewAdapter;

        ListView listView;

        /*
        final TextView textView = binding.textSlideshow;
        settingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        presentTheme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));
        editable = sharedPreferences.getBoolean(getString(R.string.editable), false);

        ArrayList<SettingListViewItem> settingListViewItemArrayList = new ArrayList<SettingListViewItem>();
        settingListViewItemArrayList.add(new SettingListViewItem("Text size", false, false));
        settingListViewItemArrayList.add(new SettingListViewItem("Light theme", presentTheme.equals(getString(R.string.theme_light)), true));
        settingListViewItemArrayList.add(new SettingListViewItem("Editable", editable, true));


        settingListViewAdapter = new SettingListViewAdapter(settingListViewItemArrayList);

        listView = binding.listViewFragmentSetting;
        listView.setAdapter(settingListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;

                switch (position) {
                    case 0: // text size
                        // TODO: 2022-02-13 글자크기 설정 구현
                        /*
                        NumberPicker numberPicker = new NumberPicker(getActivity());
                        numberPicker.setMinValue(10);
                        numberPicker.setMaxValue(50);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Title")
                                .setView(numberPicker)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                         */
                        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
                        ConstraintLayout constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.dialog_text_size, null);

                        AlertDialog.Builder builder;
                        AlertDialog alertDialog;
                        int textSize;

                        NumberPicker numberPicker;
                        TextView textView;
                        Button buttonCancel;
                        Button buttonSet;

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setView(constraintLayout);
                        alertDialog = builder.create();
                        alertDialog.show();
                        textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

                        numberPicker = constraintLayout.findViewById(R.id.number_picker_dialog);
                        textView = constraintLayout.findViewById(R.id.text_example_dialog);
                        buttonCancel = constraintLayout.findViewById(R.id.button_cancel_dialog);
                        buttonSet = constraintLayout.findViewById(R.id.button_set_dialog);

                        textView.setTextSize(textSize);

                        numberPicker.setMinValue(10);
                        numberPicker.setMaxValue(50);
                        numberPicker.setValue(textSize);
                        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                                textView.setTextSize(numberPicker.getValue());
                            }
                        });

                        buttonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });
                        buttonSet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putInt(getString(R.string.text_size), numberPicker.getValue());
                                editor.apply();
                                alertDialog.dismiss();
                            }
                        });
                        break;
                    case 1: // light theme
                    case 2: // editable
                        boolean isChecked = ((SettingListViewItem) settingListViewAdapter.getItem(position)).getSwitchChecked();
                        ((SettingListViewItem) settingListViewAdapter.getItem(position)).setSwitchChecked(!isChecked);
                        settingListViewAdapter.notifyDataSetChanged();
                        break;
                }

                SettingListViewItem settingListViewItem = (SettingListViewItem) adapterView.getItemAtPosition(position);
            }
        });

        setTheme(presentTheme, listView);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setTheme(String presentTheme, ListView listView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            int dividerHeight = listView.getDividerHeight();
            if (presentTheme.equals(getString(R.string.theme_dark))) {
                listView.setDivider(new ColorDrawable(getContext().getColor(R.color.divider_dark)));
                listView.setDividerHeight(dividerHeight);
            } else if (presentTheme.equals(getString(R.string.theme_light))) {
                listView.setDivider(new ColorDrawable(getContext().getColor(R.color.divider_light)));
                listView.setDividerHeight(dividerHeight);
            }
        }
    }
}