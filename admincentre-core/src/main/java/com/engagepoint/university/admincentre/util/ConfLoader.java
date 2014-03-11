package com.engagepoint.university.admincentre.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Bogdan Ponomarchuk
 */
public class ConfLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfLoader.class);
    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";

    Element rootElement;
    FileReader fr;
    Document rDoc;
    File configurationFile;

    public ConfLoader() {
        boolean fileWasFound = false;
        String configPath;
        configPath = System.getProperty("config.path");

        // checking if config file from console was entered
        if (configPath != null) {
            // checking if config file from console was entered right
            if (isFileExists(configPath)) {
                configurationFile = new File(configPath);
                fileWasFound = true;
            } else {
                LOGGER.warn("file wasn`t found " + configPath);
            }
        }
        // if config file wasn`t entered from console try to find it near .jar
        // package
        if (!fileWasFound) {
            configPath = System.getProperty("user.dir").concat("/config.xml");
            if (isFileExists(configPath)) {
                configurationFile = new File(configPath);
                fileWasFound = true;
            } else {
                LOGGER.info("file wasn`t found " + configPath);
            }
        }
        // if config file wasn`t entered from console and wasn`t found near .jar
        // package use default config
        if (!fileWasFound) {
            setDefaultConfigurationFile();
        }

    }

    private boolean isFileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            return true;
        }
        return false;
    }

    /**
     * Allows to get synchronization channel name from configuration file
     * 
     * @return name of channel
     */
    public String getChannelName() {
        read();
        return rDoc.getRootElement().getChild("synchronization").getAttributeValue("channelName");
    }

    /**
     * Allows to set synchronization channel name
     * 
     * @param channelName
     *            name of synchronization channel to configuration file
     */
    @Deprecated
    public void setChannelName(String channelName) {
            read();
            rDoc.getRootElement().getChild("synchronization").getAttribute("channelName")
                    .setValue(channelName);
            write();
    }

    /**
     * Allows to get synchronization mode from configuration file
     * 
     * @return synchronization mode
     */
    public String getMode() {
        read();
        return rDoc.getRootElement().getChild("synchronization").getAttributeValue("mode");
    }

    @Deprecated
    public void setMode(String mode) {
            read();
            rDoc.getRootElement().getChild("synchronization").getAttribute("mode").setValue(mode);
            write();
    }

    /**
     * Allows to get synchronization cluster name from configuration file
     * 
     * @return name of cluster
     */
    public String getClusterName() {
            read();
        return rDoc.getRootElement().getChild("synchronization").getAttributeValue("clusterName");
    }

    @Deprecated
    public void setClusterName(String clusterName) {
            read();
            rDoc.getRootElement().getChild("synchronization").getAttribute("clusterName")
                    .setValue(clusterName);
            write();

    }

    /**
     * Allows to get infinispan base location from configuration file
     * 
     * @return name of channel
     */
    public String getBasePath() {
        read();
        return rDoc.getRootElement().getChild("infinispan").getAttributeValue("basePath");
    }

    @Deprecated
    public void setBasePath(String path) {
        read();
        rDoc.getRootElement().getChild("infinispan").getAttribute("basePath").setValue(path);
        write();

    }

    private void write() {

        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());

        FileWriter fw;
        try {
            fw = new FileWriter(configurationFile);
            outputter.output(this.rDoc, fw);
            fw.close();
        } catch (IOException e) {
            LOGGER.warn("Error during writing config file: " + e.getMessage());
        }

    }

    private void read() {
        SAXBuilder parser = new SAXBuilder();

        try {
            fr = new FileReader(configurationFile);
        } catch (FileNotFoundException e) {
            LOGGER.warn("file wasn`t found: " + e.getMessage());
        }

        try {
            this.rDoc = parser.build(fr);
            fr.close();
        } catch (IOException ex) {
            LOGGER.warn("Error during reading config file: " + ex.getMessage());
        }
        // Element root = rDoc.getRootElement();
        catch (JDOMException e) {
            LOGGER.warn("Error during reading building DOM : " + e.getMessage());
        }
    }

    private File streamToFile(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        // read from is to buffer
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        return tempFile;
    }

    private void setDefaultConfigurationFile() {
        try {
            configurationFile = streamToFile(getClass().getClassLoader().getResourceAsStream(
                    "config.xml"));
            LOGGER.info("default configurations is used");
        } catch (IOException e) {
            LOGGER.error("error during getting default configurations");
        }
    }
}
