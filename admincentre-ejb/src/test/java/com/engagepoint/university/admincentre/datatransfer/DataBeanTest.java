package com.engagepoint.university.admincentre.datatransfer;

import com.engagepoint.university.admincentre.entity.Node;
import com.engagepoint.university.admincentre.entity.PropertiesDocument;
import com.engagepoint.university.admincentre.entity.TreeProperties;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by aleksey.korotysh on 04.03.14.
 */
@Ignore
public class  DataBeanTest {
    TreeProperties parentTreeNode;
    DataBean dataBean = new DataBean();
    NodePreferences nodePreferences;
    Node node;

    @Before
    public void init() {
        nodePreferences = new NodePreferences(null, "");
        dataBean = new DataBean();
        node = new Node();
        nodePreferences.setCurrentNode(node);

    }

    @Test
    public void testGetPreferencesTree() throws Exception {

    }

    @Test(expected=NullPointerException.class)
    public void testEditDocument() throws Exception {
        PropertiesDocument selectedDocument = null;
        List<String> keyIdList = new LinkedList<String>();
        keyIdList.add("");
        List<String> childNodeIdList = new LinkedList<String>();
        childNodeIdList.add("");
        node.setKeyIdList(keyIdList);
        node.setChildNodeIdList(childNodeIdList);
        dataBean.editDocument(selectedDocument);
        Assert.assertTrue(!selectedDocument.getOldName().equals(null));


    }

    @Test
    public void testDeleteDocument() throws Exception {
        dataBean.editDocument(null);
    }

    @Test
    public void testAddDocument() throws Exception {
        dataBean.addDocument(null, null);
    }

    @Test
    public void testGetNodeByDoc() throws Exception {
        dataBean.getNodeByDoc(null, null);

    }

}
