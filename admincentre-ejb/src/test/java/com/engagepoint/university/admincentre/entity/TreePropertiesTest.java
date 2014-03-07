package com.engagepoint.university.admincentre.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksey.korotysh on 04.03.14.
 */
public class TreePropertiesTest {

TreeProperties treeProperties;
    @Before
    public void init(){
       treeProperties = new TreeProperties();
    }


    @Test
    public void testGetType() throws Exception {
        treeProperties.setType("ssss");
        Assert.assertEquals(treeProperties.getType() , "ssss");
    }

    @Test
    public void testSetType() throws Exception {
        treeProperties.setType("ssss");
        Assert.assertEquals(treeProperties.getType() , "ssss");
    }

    @Test
    public void testGetData() throws Exception {
        treeProperties.setData("ssss");
        Assert.assertEquals(treeProperties.getData() , "ssss");
    }

    @Test
    public void testSetData() throws Exception {
        treeProperties.setData("ssss");
        Assert.assertEquals(treeProperties.getData() , "ssss");
    }

    @Test
    public void testGetChildren() throws Exception {
        TreeProperties tree = new TreeProperties();
        List<TreeProperties> list = new ArrayList<TreeProperties>();
        list.add(tree);
        treeProperties.setChildren(list);
        Assert.assertNotNull(treeProperties.getChildren());
    }

    @Test
    public void testSetChildren() throws Exception {
TreeProperties tree = new TreeProperties();
        List<TreeProperties> list = new ArrayList<TreeProperties>();
        list.add(tree);
        treeProperties.setChildren(list);
        Assert.assertNotNull(treeProperties.getChildren());
    }

    @Test
    public void testGetParent() throws Exception {
        TreeProperties tree = new TreeProperties();
        treeProperties.setParent(tree);
        Assert.assertNotNull(treeProperties.getParent());
    }

    @Test
    public void testSetParent() throws Exception {
        TreeProperties tree = new TreeProperties();
        treeProperties.setParent(tree);
        Assert.assertNotNull(treeProperties.getParent());

    }

    @Test
    public void testIsExpanded() throws Exception {
treeProperties.setExpanded(true);
        Assert.assertTrue(treeProperties.isExpanded());
    }

    @Test
    public void testSetExpanded() throws Exception {
        treeProperties.setExpanded(true);
        Assert.assertTrue(treeProperties.isExpanded());
    }

    @Test
    public void testAddChild() throws Exception {
        TreeProperties tree = new TreeProperties();
        List<TreeProperties> list = new ArrayList<TreeProperties>();
        list.add(tree);
        treeProperties.setChildren(list);
        Assert.assertEquals(1, treeProperties.getChildCount());
    }

    @Test
    public void testGetChildCount() throws Exception {
        TreeProperties tree = new TreeProperties();
        List<TreeProperties> list = new ArrayList<TreeProperties>();
        list.add(tree);
        treeProperties.setChildren(list);
        Assert.assertEquals(1, treeProperties.getChildCount());
    }

    @Test
    public void testIsLeaf() throws Exception {
Assert.assertTrue(treeProperties.isLeaf());
    }


}
