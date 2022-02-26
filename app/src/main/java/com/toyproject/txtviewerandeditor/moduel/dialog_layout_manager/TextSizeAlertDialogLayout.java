package com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.toyproject.txtviewerandeditor.R;

public class TextSizeAlertDialogLayout {
    ConstraintLayout constraintLayout;
    TextView textViewTitle;
    TextView textViewMessage;
    NumberPicker numberPicker;

    public TextSizeAlertDialogLayout(LayoutInflater layoutInflater) {
        this.constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.dialog_text_size, null);
        this.textViewTitle = constraintLayout.findViewById(R.id.title_dialog_text_size);
        this.textViewMessage = constraintLayout.findViewById(R.id.example_dialog_text_size);
        this.numberPicker = constraintLayout.findViewById(R.id.number_picker_dialog_text_size);
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public void setNumberPicker(int min, int max, int value) {
        numberPicker.setMinValue(10);
        numberPicker.setMaxValue(50);
        numberPicker.setValue(value);
        textViewMessage.setTextSize(value);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                textViewMessage.setTextSize(numberPicker.getValue());
            }
        });
    }

    public void setTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String presentTheme = sharedPreferences.getString(context.getString(R.string.theme), context.getString(R.string.theme_dark));

        if (presentTheme.equals(context.getString(R.string.theme_dark))) {
            textViewTitle.setTextColor(context.getColor(R.color.text_color_dark));
            textViewMessage.setTextColor(context.getColor(R.color.text_color_dark));
        } else {
            textViewTitle.setTextColor(context.getColor(R.color.text_color_light));
            textViewMessage.setTextColor(context.getColor(R.color.text_color_light));
        }
    }
}
