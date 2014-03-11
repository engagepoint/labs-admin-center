package com.engagepoint.university.admincentre.entity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by aleksey.korotysh on 04.03.14.
 */
public class PropertiesDocumentTest {


    @Test
    public void testSetFile() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();

        propertiesDocument.setFile(true);
        Assert.assertTrue(propertiesDocument.isFile());
    }

    @Test
    public void testIsFile() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setFile(true);
        Assert.assertTrue(propertiesDocument.isFile());
    }

    @Test
    public void testGetAbsolutePath() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setAbsolutePath("123123123");
        Assert.assertNotNull(propertiesDocument.getAbsolutePath());
    }

    @Test
    public void testSetAbsolutePath() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setAbsolutePath("123123123");
        Assert.assertEquals(propertiesDocument.getAbsolutePath(), "123123123");
    }

    @Test
    public void testGetName() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setName("dsfsdg");
        Assert.assertNotNull(propertiesDocument.getName());
    }

    @Test
    public void testSetName() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setName("123123123");
        Assert.assertEquals(propertiesDocument.getName(), "123123123");

    }

    @Test
    public void testGetOldName() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setName("sdf");
        propertiesDocument.setName("sdf");
        Assert.assertNotNull(propertiesDocument.getOldName());

    }

    @Test
    public void testGetValue() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setValue("sdfsdf");
        Assert.assertNotNull(propertiesDocument.getValue());
    }

    @Test
    public void testSetValue() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setValue("123123123");
        Assert.assertEquals(propertiesDocument.getValue(), "123123123");
    }

    @Test
    public void testGetType() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setType("123123123");
        Assert.assertNotNull(propertiesDocument.getType());
    }

    @Test
    public void testSetType() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setType("123123123");
        Assert.assertTrue("worn" , propertiesDocument.getType().equals("123123123"));
    }

    @Test
    public void testIsEditable() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setEditable(true);
        Assert.assertTrue(propertiesDocument.isEditable());
    }

    @Test
    public void testSetEditable() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setEditable(true);
        Assert.assertTrue(propertiesDocument.isEditable());
    }

    @Test
    public void testEditAction() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        Assert.assertNull(propertiesDocument.editAction());
    }

    @Test
    public void testSaveAction() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        Assert.assertNull(propertiesDocument.saveAction());
    }

    @Test
    public void testIsDirectoryForAdding() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setDirectoryForAdding(true);
        Assert.assertTrue(propertiesDocument.isDirectoryForAdding());
    }

    @Test
    public void testSetDirectoryForAdding() throws Exception {
        PropertiesDocument propertiesDocument = new PropertiesDocument();
        propertiesDocument.setDirectoryForAdding(true);
        Assert.assertTrue(propertiesDocument.isDirectoryForAdding());
    }


}
