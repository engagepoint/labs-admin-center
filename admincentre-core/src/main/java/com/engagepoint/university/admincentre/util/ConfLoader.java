package com.engagepoint.university.admincentre.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * 
 * @author Bogdan Ponomarchuk
 */
public class ConfLoader {


    Element rootElement;
    FileReader fr;
    Document rDoc;

    /**
     * Allows to get synchronization channel name from config.xml
     * 
     * @return name of channel
     */
    public String getChannelName() {
        try {
            read();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rDoc.getRootElement().getChild("synchronization").getAttributeValue("channelName");

    }

    /**
     * Allows to set synchronization channel name
     * 
     * @param channelName
     *            name of synchronization channel to config.xml
     */
    public void setChannelName(String channelName) {
        try {
            read();
            rDoc.getRootElement().getChild("synchronization").getAttribute("channelName")
                    .setValue(channelName);
            write();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Allows to get synchronization channel name from config.xml
     * 
     * @return name of channel
     */
    public String getMode() {
        try {
            read();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rDoc.getRootElement().getChild("synchronization").getAttributeValue("mode");

    }

    public void setMode(String mode) {
        try {
            read();
            rDoc.getRootElement().getChild("synchronization").getAttribute("mode").setValue(mode);
            write();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Allows to get synchronization channel name from config.xml
     * 
     * @return name of channel
     */
    public String getClusterName() {
        try {
            read();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rDoc.getRootElement().getChild("synchronization").getAttributeValue("clusterName");
         
    }

    public void setClusterName(String clusterName) {
        try {
            read();
            rDoc.getRootElement().getChild("synchronization").getAttribute("clusterName")
                    .setValue(clusterName);
            write();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Allows to get synchronization channel name from config.xml
     * 
     * @return name of channel
     */
    public String getBasePath() {
        try {
            read();
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rDoc.getRootElement().getChild("infinispan").getAttributeValue("basePath");
    }

    public void setBasePath(String path) {
      try {
            read();
            rDoc.getRootElement().getChild("infinispan").getAttribute("basePath").setValue(path);
            write();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void write() {

        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        try {
            FileWriter fw = new FileWriter("config.xml");
            outputter.output(this.rDoc, fw);
            fw.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void read() throws FileNotFoundException {
        SAXBuilder parser = new SAXBuilder();
        fr = new FileReader("config.xml");
        try {
            this.rDoc = parser.build(fr);
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Element root = rDoc.getRootElement();
        catch (JDOMException e) {
            Logger.getLogger(ConfLoader.class.getName()).log(Level.SEVERE, null, e);
        }
    }

       
}
