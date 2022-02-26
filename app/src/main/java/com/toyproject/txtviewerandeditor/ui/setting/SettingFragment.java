package com.toyproject.txtviewerandeditor.ui.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.toyproject.txtviewerandeditor.R;
import com.toyproject.txtviewerandeditor.databinding.FragmentSettingBinding;
import com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager.BuilderThemeInit;
import com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager.TextSizeAlertDialogLayout;

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
        String theme;
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
        theme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));
        editable = sharedPreferences.getBoolean(getString(R.string.editable), false);

        ArrayList<SettingListViewItem> settingListViewItemArrayList = new ArrayList<SettingListViewItem>();
        settingListViewItemArrayList.add(new SettingListViewItem("Text size", false, false));
        settingListViewItemArrayList.add(new SettingListViewItem("Light theme", theme.equals(getString(R.string.theme_light)), true));
        settingListViewItemArrayList.add(new SettingListViewItem("Editable", editable, true));


        settingListViewAdapter = new SettingListViewAdapter(settingListViewItemArrayList);

        listView = binding.listViewFragmentSetting;
        listView.setAdapter(settingListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;

                switch (position) {
                    case 0: // Text size
                        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
                        TextSizeAlertDialogLayout textSizeAlertDialogLayout = new TextSizeAlertDialogLayout(layoutInflater);
                        int textSize = sharedPreferences.getInt(getString(R.string.text_size), 20);

                        textSizeAlertDialogLayout.setTheme(getContext());
                        textSizeAlertDialogLayout.setNumberPicker(10, 50, textSize);

                        ConstraintLayout constraintLayout = textSizeAlertDialogLayout.getConstraintLayout();
                        NumberPicker numberPicker = constraintLayout.findViewById(R.id.number_picker_dialog_text_size);

                        // Text size 를 선택하는 AlertDialog
                        AlertDialog.Builder builderTextSize = BuilderThemeInit.init(getContext());
                        builderTextSize.setView(constraintLayout)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editor.putInt(getString(R.string.text_size), numberPicker.getValue());
                                        editor.apply();
                                    }
                                });
                        AlertDialog alertDialog = builderTextSize.create();
                        alertDialog.show();
                        break;
                    case 1: // Light theme
                    case 2: // Editable
                        boolean isChecked = ((SettingListViewItem) settingListViewAdapter.getItem(position)).getSwitchChecked();
                        ((SettingListViewItem) settingListViewAdapter.getItem(position)).setSwitchChecked(!isChecked);
                        settingListViewAdapter.notifyDataSetChanged();
                        break;
                }

                SettingListViewItem settingListViewItem = (SettingListViewItem) adapterView.getItemAtPosition(position);
            }
        });

        setTheme(theme, listView);

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