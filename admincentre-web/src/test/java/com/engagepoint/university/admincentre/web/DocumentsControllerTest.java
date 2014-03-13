package com.engagepoint.university.admincentre.web;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author artem.lysenko
 */
public class DocumentsControllerTest {

    public DocumentsControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSelectedNode method, of class DocumentsController.
     */
    @Test
    public void testGetSelectedNode() {
        System.out.println("getSelectedNode");
        DocumentsController instance = new DocumentsController();
        TreeNode root = new DefaultTreeNode("root", null);
        TreeNode selectedNode = root;
        instance.setSelectedNode(selectedNode);
        TreeNode expResult = selectedNode;
        TreeNode result = instance.getSelectedNode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSelectedNode method, of class DocumentsController.
     */
    @Test
    public void testSetSelectedNode() {
        System.out.println("setSelectedNode");
        DocumentsController instance = new DocumentsController();
        TreeNode root = new DefaultTreeNode("root", null);
        TreeNode selectedNode = root;
        instance.setSelectedNode(selectedNode);
        TreeNode expResult = selectedNode;
        TreeNode result = instance.getSelectedNode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSelectedDoc method, of class DocumentsController.
     */
    @Test
    public void testGetSelectedDoc() {
        System.out.println("getSelectedDoc");
        DocumentsController instance = new DocumentsController();
        Document selectedDoc = new Document();
        instance.setSelectedDoc(selectedDoc);
        Document expResult = selectedDoc;
        Document result = instance.getSelectedDoc();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSelectedDoc method, of class DocumentsController.
     */
    @Test
    public void testSetSelectedDoc() {
        System.out.println("setSelectedDoc");
        DocumentsController instance = new DocumentsController();
        Document selectedDoc = new Document();
        instance.setSelectedDoc(selectedDoc);
        Document expResult = selectedDoc;
        Document result = instance.getSelectedDoc();
        assertEquals(expResult, result);
    }

    @Test
    public void testResetTempDoc() {
        System.out.println("resetTempDoc");
        DocumentsController instance = new DocumentsController();
        Document doc = new Document("absPath", "name", "value", "Node");
        instance.setTempDoc(doc);
        assertNotNull(instance.getTempDoc());
        instance.resetTempDoc();
        assertNull(instance.getTempDoc().getAbsolutePath());
    }

    /**
     * Test of getTempDoc method, of class DocumentsController.
     */
    @Test
    public void testGetTempDoc() {
        System.out.println("getTempDoc");
        DocumentsController instance = new DocumentsController();
        Document doc = new Document("absPath", "name", "value", "Node");
        instance.setTempDoc(doc);
        Document expResult = doc;
        Document result = instance.getTempDoc();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTempDoc method, of class DocumentsController.
     */
    @Test
    public void testSetTempDoc() {
        System.out.println("setTempDoc");
        DocumentsController instance = new DocumentsController();
        Document doc = new Document("absPath", "name", "value", "Node");
        instance.setTempDoc(doc);
        assertEquals(doc, instance.getTempDoc());
    }
}
