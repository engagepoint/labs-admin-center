package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.preferences.NodePreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@RequestScoped
public class FileUploadController implements Serializable, ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class.getName());
    private Part file;

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public void upload() throws IOException {
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = file.getInputStream();
            outStream = new FileOutputStream(File.createTempFile("temp", "zip"));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            LOGGER.info("File is copied successfuly!");
        } catch (IOException e) {
            LOGGER.error("Can not create inputStream", e);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    @Override
    public void processAction(ActionEvent actionEvent) {
        try {
            upload();
        } catch (IOException e) {
            LOGGER.warn("Can not upload file", e);
        }
    }

    public void onImportZip() {
        InputStream is = null;
        try {
            is = file.getInputStream();
            NodePreferences np = new NodePreferences(null, "");
            np.importNode(is);
            np.toString();
            file.delete();
        } catch (IOException e) {
            LOGGER.warn("Can't import file. Exception -", e);
        }
    }
}
