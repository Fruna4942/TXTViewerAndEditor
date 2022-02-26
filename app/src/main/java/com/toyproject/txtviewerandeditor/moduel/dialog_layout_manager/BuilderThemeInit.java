package com.toyproject.txtviewerandeditor.moduel.dialog_layout_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.toyproject.txtviewerandeditor.R;

public class BuilderThemeInit {
    public static AlertDialog.Builder init (Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString(context.getString(R.string.theme), context.getString(R.string.theme_dark));

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        } else {
            if (theme.equals(context.getString(R.string.theme_dark))) {
                builder = new AlertDialog.Builder(context, R.style.AlertDialogThemeDark);
            } else {
                builder = new AlertDialog.Builder(context, R.style.AlertDialogThemeLight);
            }
        }

        return builder;
    }
}
