package com.engagepoint.university.admincentre.web;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *@author artem.lysenko
 */
public class DocumentTest {

    public DocumentTest() {
    }

    /**
     * Test of setFile method, of class Document.
     */
    @Test
    public void testSetFile() {
        System.out.println("setFile");
        boolean file = false;
        Document instance = new Document();
        instance.setFile(file);
        assertEquals(instance.getType(), "");
        assertNotEquals(instance.getType(), "File");
        assertNotNull(instance.getType());
    }

    /**
     * Test of isFile method, of class Document.
     */
    @Test
    public void testIsFile() {
        System.out.println("isFile");
        Document instance = new Document();
        instance.setType("File");
        boolean expResult = true;
        boolean result = instance.isFile();
        assertEquals(expResult, result);
        instance.setType("");
        expResult = false;
        result = instance.isFile();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAbsolutePath method, of class Document.
     */
    @Test
    public void testGetAbsolutePath() {
        System.out.println("getAbsolutePath");
        Document instance = new Document();
        instance.setAbsolutePath("absolutePath");
        String expResult = "absolutePath";
        String result = instance.getAbsolutePath();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAbsolutePath method, of class Document.
     */
    @Test
    public void testSetAbsolutePath() {
        System.out.println("setAbsolutePath");
        String absolutePath = "absolutePath";
        Document instance = new Document();
        instance.setAbsolutePath(absolutePath);
        String expResult = "absolutePath";
        String result = instance.getAbsolutePath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class Document.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Document instance = new Document();
        instance.setName("getName");
        String expResult = "getName";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Document.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "setName";
        Document instance = new Document();
        instance.setName(name);
        assertEquals(name, instance.getName());
    }

    /**
     * Test of getOldName method, of class Document.
     */
    @Test
    public void testGetOldName() {
        System.out.println("getOldName");
        Document instance = new Document();
        instance.setName("veryOldName");
        instance.setName("oldName");
        String expResult = "veryOldName";
        String result = instance.getOldName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue & setValue methods, of class Document.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        Document instance = new Document();
        String result;
        String expResult = "someString";
        instance.setValue(expResult);
        result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class Document.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Document instance = new Document();
        String expResult = "int";
        instance.setType("int");
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class Document.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String type = "byte array";
        Document instance = new Document();
        instance.setType(type);
        String result = instance.getType();
        String expResult = type;
        assertEquals(expResult, result);
    }

    /**
     * Test of isEditable method, of class Document.
     */
    @Test
    public void testIsEditable() {
        System.out.println("isEditable");
        Document instance = new Document();
        boolean expResult = false;
        instance.setEditable(expResult);
        boolean result = instance.isEditable();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEditable method, of class Document.
     */
    @Test
    public void testSetEditable() {
        System.out.println("setEditable");
        Document instance = new Document();
        instance.setEditable(false);
        boolean expResult = false;
        boolean result = instance.isEditable();
        assertEquals(expResult, result);
    }

    /**
     * Test of editAction method, of class Document.
     */
    @Test
    public void testEditAction() {
        System.out.println("editAction");
        Document instance = new Document();
        boolean expResult = true;
        instance.editAction();
        boolean result = instance.isEditable();
        assertEquals(expResult, result);
        assertNull(instance.editAction());
    }

    /**
     * Test of saveAction method, of class Document.
     */
    @Test
    public void testSaveAction() {
        System.out.println("saveAction");
        Document instance = new Document();
        String nullResult = instance.saveAction();
        boolean expResult = false;
        boolean result = instance.isEditable();
        assertEquals(expResult, result);
        assertNull(nullResult);
    }

    /**
     * Test of isDirectoryForAdding method, of class Document.
     */
    @Test
    public void testIsDirectoryForAdding() {
        System.out.println("isDirectoryForAdding");
        Document instance = new Document();
        boolean expResult = false;
        boolean result = instance.isDirectoryForAdding();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDirectoryForAdding method, of class Document.
     */
    @Test
    public void testSetDirectoryForAdding() {
        System.out.println("setDirectoryForAdding");
        Document instance = new Document();
        instance.setDirectoryForAdding(true);
        assertTrue(instance.isDirectoryForAdding());
    }

    /**
     * Test of hashCode method, of class Document.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");

        Document instance1 = new Document("absolutePath", "name", "value", "type", true);
        Document instance2 = new Document("absolutePath", "name", "value", "type", true);
        Document instance3 = new Document("absolutePath", "name", "value", "type", true);
        Document instance4 = new Document("absolutePath1", "name1", "value1", "type1", false);

        int hash1 = instance1.hashCode();
        int hash1again = instance1.hashCode();
        int hash2 = instance2.hashCode();
        int hash3 = instance3.hashCode();
        int hash4 = instance4.hashCode();

        assertNotNull(hash1);
        assertTrue((hash1 == hash1again)
                && (hash1 == hash2)
                && (hash2 == hash3)
                && (hash1 == hash3)
                && (hash1 != hash4));
    }

    /**
     * Test of equals method, of class Document.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Document instance1 = new Document("absolutePath", "name", "value", "type", true);
        Document instance2 = new Document("absolutePath", "name", "value", "type", true);
        Document instance3 = new Document("absolutePath1", "name1", "value1", "type1", false);
        assertTrue(instance1.equals(instance2) && !(instance2.equals(instance3)));
    }

    /**
     * Test of toString method, of class Document.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Document instance = new Document();
        instance.setName("toString");
        String expResult = instance.getName();
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}