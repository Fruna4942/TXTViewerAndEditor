package com.toyproject.txtviewerandeditor.ui.file_explorer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FileExplorerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FileExplorerViewModel() {
        /*
        mText = new MutableLiveData<>();
        mText.setValue("This is file_explore fragment");

         */
    }

    public LiveData<String> getText() {
        return mText;
    }
}