package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.preferences.NodePreferences;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@SessionScoped
public class TempController {
    public void onExportZip() throws IOException, BackingStoreException {
        File tmpFile = FileController.createTempZip();
        FileController.setPathToTempFile(tmpFile.getPath());
    }
}
