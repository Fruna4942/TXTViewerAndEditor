package com.toyproject.txtviewerandeditor.ui.file_explorer;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileExplorerRecyclerViewItem {
    private Boolean isDirectory;
    private File file;
}
