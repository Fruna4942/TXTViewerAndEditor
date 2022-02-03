package com.toyproject.txtviewerandeditor.ui.file_explore;

import android.graphics.drawable.Drawable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecyclerViewItem {
    private Drawable imageView;
    private String textView;
}
