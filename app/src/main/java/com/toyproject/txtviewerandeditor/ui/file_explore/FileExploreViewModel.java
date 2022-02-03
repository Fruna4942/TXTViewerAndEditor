package com.toyproject.txtviewerandeditor.ui.file_explore;

import android.os.Environment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class FileExploreViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FileExploreViewModel() {
        /*
        mText = new MutableLiveData<>();
        mText.setValue("This is file_explore fragment");

         */
    }

    public LiveData<String> getText() {
        return mText;
    }
}