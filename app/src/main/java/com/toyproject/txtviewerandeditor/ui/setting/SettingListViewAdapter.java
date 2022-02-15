package com.toyproject.txtviewerandeditor.ui.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.toyproject.txtviewerandeditor.MainActivity;
import com.toyproject.txtviewerandeditor.R;

import java.util.ArrayList;

public class SettingListViewAdapter extends BaseAdapter {
    private ArrayList<SettingListViewItem> settingListViewItemArrayList = new ArrayList<SettingListViewItem>();
    private TextView textView;
    private Switch sw;

    public SettingListViewAdapter(ArrayList<SettingListViewItem> settingListViewItemArrayList) {
        this.settingListViewItemArrayList = settingListViewItemArrayList;
    }

    @Override
    public int getCount() {
        return settingListViewItemArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        final int position = i;
        return settingListViewItemArrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        final int position = i;
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_list_view_setting, viewGroup, false);
        }

        textView = (TextView) view.findViewById(R.id.title_text_item_list);
        sw = (Switch) view.findViewById(R.id.switch_item_list);

        setTheme(viewGroup);

        SettingListViewItem settingListViewItem = settingListViewItemArrayList.get(position);

        textView.setText(settingListViewItem.getTitleText());
        sw.setChecked(settingListViewItem.getSwitchChecked());
        sw.setVisibility((settingListViewItem.getSwitchVisibility()) ? View.VISIBLE : View.GONE);
        if (settingListViewItem.getSwitchVisibility()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            switch (position) {
                case 1: // light theme switch
                    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (isChecked) {
                                editor.putString(context.getString(R.string.theme), context.getString(R.string.theme_light));
                                editor.apply();
                                ActivityCompat.recreate((Activity) context);
                                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            }
                            else {
                                editor.putString(context.getString(R.string.theme), context.getString(R.string.theme_dark));
                                editor.apply();
                                ActivityCompat.recreate((Activity) context);
                                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            }
                        }
                    });
                    break;
                case 2: // edit switch
                    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (isChecked) {
                                editor.putBoolean(context.getString(R.string.editable), true);
                                editor.apply();
                            } else {
                                editor.putBoolean(context.getString(R.string.editable), false);
                                editor.apply();
                            }
                        }
                    });
                    break;
            }
        }

        return view;
    }

    public void setTheme(ViewGroup viewGroup) {
        Context context = viewGroup.getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String presentTheme = sharedPreferences.getString(context.getString(R.string.theme), context.getString(R.string.theme_dark));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (presentTheme.equals(context.getString(R.string.theme_dark))) {
                textView.setTextColor(context.getColor(R.color.text_color_dark));
            } else if (presentTheme.equals(context.getString(R.string.theme_light))) {
                textView.setTextColor(context.getColor(R.color.text_color_light));
            }
        }
    }
}
