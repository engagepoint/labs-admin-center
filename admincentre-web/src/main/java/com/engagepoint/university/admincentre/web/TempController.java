package com.engagepoint.university.admincentre.web;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@SessionScoped
public class TempController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TempController.class.getName());

    public void onExportZip() throws IOException, BackingStoreException {
        try {
            File tmpFile = FileController.createTempZip();
            FileController.setPathToTempFile(tmpFile.getPath());
        } catch (IOException e) {
            LOGGER.warn("Something wrong with the temp file and it's path ", e);
        }
    }
}
