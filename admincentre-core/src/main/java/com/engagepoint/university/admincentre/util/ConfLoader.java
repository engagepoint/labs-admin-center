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
    private static volatile ConfLoader instance;

    public static synchronized ConfLoader getInstance() {
        if (instance == null) {
            instance = new ConfLoader();
        }
        return instance;
    }
    private Element rootElement;
    private FileReader fr;
    private Document rDoc;
    private File configurationFile;
    private String basePath;
    private String mode;
    private String clusterName;
    private String channelName;

    /*
     * Checking if config file from console was entered
     * Checking if config file from console was entered right
     * If config file wasn`t entered from console try to find it near .jar package
     * If config file wasn`t entered from console and wasn`t found near .jar package use default config
     */
    private ConfLoader() {
        boolean fileWasFound = false;
        String configPath;
        configPath = System.getProperty("CONFIG_PATH");
        if (configPath != null) {
            if (isFileExists(configPath)) {
                configurationFile = new File(configPath);
                fileWasFound = true;
            } else {
                LOGGER.warn("Configuration file was not found at " + configPath);
            }
        }
        if (!fileWasFound) {
            configPath = System.getProperty("user.dir").concat("/config.xml");
            if (isFileExists(configPath)) {
                configurationFile = new File(configPath);
                fileWasFound = true;
            } else {
                LOGGER.warn("there is no configuration file in working dir: " + configPath);
            }
        }
        if (!fileWasFound) {
            setDefaultConfigurationFile();
        }
        readVariablesFromFile();
    }

    private void readVariablesFromFile() {
        read();
        this.channelName = rDoc.getRootElement().getChild("synchronization")
                .getAttributeValue("channelName");
        this.mode = rDoc.getRootElement().getChild("synchronization").getAttributeValue("mode");
        this.clusterName = rDoc.getRootElement().getChild("synchronization")
                .getAttributeValue("clusterName");
        this.basePath = rDoc.getRootElement().getChild("infinispan").getAttributeValue("basePath");
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
        return channelName;
    }

    /**
     * Allows to set synchronization channel name
     *
     * @param channelName name of synchronization channel to configuration file
     * @deprecated
     */
    @Deprecated
    public void setChannelName(String channelName) {
        this.channelName = channelName;
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
            return mode;
        }
    /*
     * @deprecated
     */
    
        @Deprecated
    public void setMode(String mode) {
        this.mode = mode;
        rDoc.getRootElement().getChild("synchronization").getAttribute("mode").setValue(mode);
        write();
    }


    /**
     * Allows to get synchronization cluster name from configuration file
     *
     * @return name of cluster
     */
        public String getClusterName() {
            return clusterName;
        }

    /*
     * @deprecated
     */
        @Deprecated
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
        rDoc.getRootElement().getChild("synchronization").getAttribute("clusterName")
                .setValue(clusterName);
        write();
    }

    /**
     * Allows to get Infinispan's base location from configuration file
     *
     * @return name of channel
     */
        public String getBasePath() {
            String path = basePath;
            int firstSlash = path.indexOf('/');
            if (path.indexOf("#{") == 0 && path.indexOf("}") == firstSlash - 1) {
                String systemDir = System.getProperty(path.substring(2, firstSlash - 1));
                if (systemDir != null) {
                    path = systemDir.concat(path.substring(firstSlash));
                }
            }
            return path;
        }

    /*
     * @deprecated
     */
        @Deprecated
    public void setBasePath(String path) {
        this.basePath = path;
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
                LOGGER.warn("Error during writing configuration file ", e);
            }
        }

    private void read() {
        SAXBuilder parser = new SAXBuilder();
        try {
            fr = new FileReader(configurationFile);
        } catch (FileNotFoundException e) {
            LOGGER.warn("File was not found ", e);
        }
        try {
            this.rDoc = parser.build(fr);
            fr.close();
        } catch (IOException ex) {
            LOGGER.warn("Error during reading configuration file ", ex);
        } catch (JDOMException e) {
            LOGGER.warn("Error during reading building DOM ", e);
        }
    }

    /*
     * Read from is to buffer
     */
    private File streamToFile(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        return tempFile;
    }

    private void setDefaultConfigurationFile() {
        try {
            configurationFile = streamToFile(getClass().getClassLoader().getResourceAsStream(
                    "config.xml"));
            LOGGER.info("Default configurations are used");
        } catch (IOException e) {
            LOGGER.error("Error during getting default configurations ", e);
        }
    }
}
