package com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.toyproject.txtviewerandeditor.R;

public class OneInputAlertDialogLayout {
    ConstraintLayout constraintLayout;
    TextView textViewTitle;
    TextView textViewMessage;
    EditText editText;

    public OneInputAlertDialogLayout(LayoutInflater layoutInflater) {
        this.constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.dialog_one_input, null);
        this.textViewTitle = constraintLayout.findViewById(R.id.title_dialog_one_input);
        this.textViewMessage = constraintLayout.findViewById(R.id.message_dialog_one_input);
        this.editText = constraintLayout.findViewById(R.id.edit_dialog_one_input);
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public String getEditTextString() {
        return editText.getText().toString();
    }

    public void setTexts(String title, String message, String editText) {
        this.textViewTitle.setText(title);
        this.textViewMessage.setText(message);
        this.editText.setText(editText);
    }

    public void setFocusAndSelectAll(boolean focusAndSelectAll) {
        if (focusAndSelectAll) {
            editText.setSelectAllOnFocus(true);
            editText.requestFocus();
        }
    }

    public void setFocus(boolean focus) {
        if (focus) {
            editText.requestFocus();
        }
    }

    public void setTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String presentTheme = sharedPreferences.getString(context.getString(R.string.theme), context.getString(R.string.theme_dark));

        if (presentTheme.equals(context.getString(R.string.theme_dark))) {
            textViewTitle.setTextColor(context.getColor(R.color.text_color_dark));
            textViewMessage.setTextColor(context.getColor(R.color.text_color_dark));
            editText.setTextColor(context.getColor(R.color.text_color_dark));
        } else {
            textViewTitle.setTextColor(context.getColor(R.color.text_color_light));
            textViewMessage.setTextColor(context.getColor(R.color.text_color_light));
            editText.setTextColor(context.getColor(R.color.text_color_light));
        }
    }
}
