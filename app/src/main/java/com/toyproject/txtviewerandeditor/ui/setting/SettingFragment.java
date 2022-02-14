package com.toyproject.txtviewerandeditor.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

        /*
        final TextView textView = binding.textSlideshow;
        settingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        ArrayList<SettingListViewItem> settingListViewItemArrayList = new ArrayList<SettingListViewItem>();
        settingListViewItemArrayList.add(new SettingListViewItem("글자 크기", false, false));

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String presentTheme = sharedPreferences.getString(getString(R.string.theme), getString(R.string.theme_dark));
        settingListViewItemArrayList.add(new SettingListViewItem("밝은 테마", presentTheme.equals(getString(R.string.theme_light)), true));
        settingListViewItemArrayList.add(new SettingListViewItem("편집 여부", false, true));

        ListView listView = binding.listViewFragmentSetting;
        SettingListViewAdapter settingListViewAdapter = new SettingListViewAdapter(settingListViewItemArrayList);

        setTheme(presentTheme, listView);

        listView.setAdapter(settingListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;

                switch (position) {
                    case 0: // text size
                        // TODO: 2022-02-13 글자크기 설정 구현
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