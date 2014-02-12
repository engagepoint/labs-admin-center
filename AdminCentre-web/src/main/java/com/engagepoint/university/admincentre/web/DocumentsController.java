package com.engagepoint.university.admincentre.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.engagepoint.university.admincentre.datatransfer.DataBean;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.preferences.NodePreferences;

@ManagedBean(name = "documentsController")
@SessionScoped
public class DocumentsController implements Serializable {

    private static final Logger logger = Logger.getLogger(DocumentsController.class.getName());
    private TreeNode root;
    private TreeNode selectedNode;
    @Any
    DataBean dataBean;

    @PostConstruct
    private void init() {
        root = new DefaultTreeNode("root", null);
        Preferences preferences = new NodePreferences(null, "");
        buildTree(preferences, root);
    }

    private void buildTree(Preferences preferences, TreeNode parentTreeNode) {

        TreeNode treeNode = new DefaultTreeNode(new Document(preferences.name(), "-", "File"),
                parentTreeNode);

        addLeaves((NodePreferences) preferences, parentTreeNode);
        try {
            if (preferences.childrenNames().length != 0) {
                for (int i = 0; i < preferences.childrenNames().length; i++) {
                    buildTree(preferences.node(preferences.childrenNames()[i]), treeNode);
                }

            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    };

    private void addLeaves(NodePreferences nodePreferences, TreeNode parentTreeNode) {
        try {

            for (int i = 0; i < nodePreferences.keys().length; i++) {
                Key key = nodePreferences.getKey(nodePreferences.keys()[i]);
                new DefaultTreeNode(new Document(key.getName(), key.getValue(), key.getType()
                        .toString()), parentTreeNode);
            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public DocumentsController() {

    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void deleteNode() {
        selectedNode.getParent().getChildren().remove(selectedNode);
    }

    public void addNode() {
        TreeNode newNode;
        if (selectedNode != null)
            newNode = new DefaultTreeNode(new Document("New Node", "-", "-"), selectedNode);
        else
            newNode = new DefaultTreeNode(new Document("New Node", "-", "-"), root);
        Document doc1 = (Document) selectedNode.getData();
        doc1.saveAction();
        selectedNode.setSelected(false);
        selectedNode.setExpanded(true);
        newNode.setSelected(true);
        Document doc = (Document) newNode.getData();
        doc.setEditable(true);
    }
}
