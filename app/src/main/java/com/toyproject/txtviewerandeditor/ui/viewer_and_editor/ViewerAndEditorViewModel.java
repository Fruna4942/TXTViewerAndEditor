package com.toyproject.txtviewerandeditor.ui.viewer_and_editor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewerAndEditorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ViewerAndEditorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is view_and_edit fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}