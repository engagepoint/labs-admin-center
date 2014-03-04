package com.engagepoint.university.admincentre.web;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

@ManagedBean
@SessionScoped
public class TempController {

    public void onExportZip() throws IOException, BackingStoreException {
        File tmpFile = FileController.createTempZip();
        FileController.setPathToTempFile(tmpFile.getPath());
    }

    public void onImportZip(FileUploadEvent event) throws IOException {
        UploadedFile file = event.getFile();
    }
}
