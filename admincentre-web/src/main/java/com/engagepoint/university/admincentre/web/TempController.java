package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.preferences.NodePreferences;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.BackingStoreException;

@ManagedBean
@SessionScoped
public class TempController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentsController.class.getName());

    public void onExportZip() throws IOException, BackingStoreException {
        File tmpFile = FileController.createTempZip();
        FileController.setPathToTempFile(tmpFile.getPath());
    }

    public void onImportZip(FileUploadEvent event) throws IOException {
        UploadedFile file = event.getFile();
        InputStream is = file.getInputstream();
        try {
          NodePreferences np = new NodePreferences(null, "");
            np.importNode(is);
            np.toString();
        } catch (BackingStoreException e) {
           LOGGER.warn("onImportZip: message " + e.getMessage());
        }
    }
}
