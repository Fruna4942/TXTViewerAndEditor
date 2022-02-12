package com.toyproject.txtviewerandeditor.ui.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SettingListViewItem {
    private String titleText;
    private Boolean switchChecked;
    private Boolean switchVisibility;
}
