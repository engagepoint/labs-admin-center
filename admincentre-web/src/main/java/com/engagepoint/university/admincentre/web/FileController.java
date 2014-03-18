package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.preferences.NodePreferences;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.prefs.BackingStoreException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@RequestScoped
public class FileController implements Serializable {

    private static String pathToTempFile;
    private static final long serialVersionUID = 111L;
    private StreamedContent downloadFile;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentsController.class.getName());

    public FileController() throws IOException {
        if (pathToTempFile.isEmpty()) {
            return;
        }
        File file = new File(pathToTempFile);
        file.deleteOnExit();
        InputStream stream = new FileInputStream(file);
        if (pathToTempFile.contains("zip")) {
            downloadFile = new DefaultStreamedContent(stream, "zip", "Export.zip");
        }
    }

    public static void setPathToTempFile(String pathToTempFile) {
        FileController.pathToTempFile = pathToTempFile;
    }

    public static File createTempZip() throws IOException, BackingStoreException {
        File tmpFile = File.createTempFile("temp", ".zip");
        try {
            new NodePreferences(null, "").exportNode(tmpFile.getPath());
            tmpFile.deleteOnExit();
        } catch (BackingStoreException bse) {
            LOGGER.warn("createTempZip()", bse);
        } catch (IOException ioe) {
            LOGGER.warn("createTempZip() /n", ioe);
        }
        return tmpFile;
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }


}