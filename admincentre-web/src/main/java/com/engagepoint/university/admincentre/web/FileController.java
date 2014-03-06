package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.preferences.NodePreferences;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.*;
import java.util.prefs.BackingStoreException;

@ManagedBean
@RequestScoped
public class FileController implements Serializable {

    private static String pathToTempFile;
    private StreamedContent downloadFile;
    private UploadedFile uploadFile;

    public static void setPathToTempFile(String pathToTempFile) {
        FileController.pathToTempFile = pathToTempFile;
    }

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

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public UploadedFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(UploadedFile uploadFile) {
        this.uploadFile = uploadFile;
    }


    public static File createTempZip() throws IOException, BackingStoreException {
        File tmpFile = File.createTempFile("temp", ".zip");
        new NodePreferences(null, "").exportNode(tmpFile.getPath());
        tmpFile.deleteOnExit();
        return tmpFile;
    }

}