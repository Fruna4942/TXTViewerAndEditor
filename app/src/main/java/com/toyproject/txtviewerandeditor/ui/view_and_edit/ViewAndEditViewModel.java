package com.toyproject.txtviewerandeditor.ui.view_and_edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewAndEditViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ViewAndEditViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is view_and_edit fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}