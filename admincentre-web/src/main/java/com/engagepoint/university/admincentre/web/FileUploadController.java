package com.engagepoint.university.admincentre.web;


import com.engagepoint.university.admincentre.preferences.NodePreferences;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.Part;
import java.io.*;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

@ManagedBean
@RequestScoped
public class FileUploadController implements Serializable, ActionListener {

    private Part file;
    //    private String path;
    private static final Logger logger = Logger.getLogger(FileUploadController.class.getName());


//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public void upload() throws IOException {
        // path = new String("D:\\temp.zip");

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
                System.err.println("File is copied successful!");
            } catch (IOException e) {
                logger.severe("Cannot create inputStream");
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
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        try {
            upload();
        } catch (IOException e) {
            logger.severe("Cannot upload");
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
                e.printStackTrace();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }

    }
}