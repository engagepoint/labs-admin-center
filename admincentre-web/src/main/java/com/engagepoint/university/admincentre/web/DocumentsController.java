package com.engagepoint.university.admincentre.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.engagepoint.university.admincentre.datatransfer.DataBean;
import com.engagepoint.university.admincentre.entity.PropertiesDocument;
import com.engagepoint.university.admincentre.entity.TreeProperties;

@ManagedBean(name = "documentsController")
@ViewScoped
public class DocumentsController implements Serializable {

    private static final long serialVersionUID = 123L;
    private static List<TreeNode> keyFolder = new ArrayList<TreeNode>(100);
    private static List<TreeNode> keyvsvakueFolder = new ArrayList<TreeNode>(100);
    private TreeNode root;
    private Document selectedDoc = new Document();
    private TreeNode selectedNode;
    private Document tempDoc = new Document(null, "", "", "");
    @Inject
    DataBean dataBean;

    public DocumentsController() {
    }

    @PostConstruct
    void init() {
        TreeProperties treeProperties = dataBean.getPreferencesTree();
        root = new DefaultTreeNode("root", null);
        buildTree(treeProperties, root);
    }

    private void buildTree(TreeProperties treeProperties, TreeNode treeNode) {
        for (TreeProperties child : treeProperties.getChildren()) {
            PropertiesDocument propertiesDocument = (PropertiesDocument) child
                    .getData();
            TreeNode childTreeNode = new DefaultTreeNode(
                    new Document(propertiesDocument.getAbsolutePath(),
                    propertiesDocument.getName(),
                    propertiesDocument.getValue(),
                    propertiesDocument.getType()), treeNode);
            buildTree(child, childTreeNode);
        }
        collapsingORexpanding(root, true);
    }

    public void editDocument(ActionEvent event) {
        TreeProperties editedTree = dataBean
                .editDocument(getPropertiesDocumentFromDocument(selectedDoc));
        root = new DefaultTreeNode("root", null);
        buildTree(editedTree, root);
    }

    public void deleteNode() {
        TreeProperties editedTree = dataBean
                .deleteDocument(getPropertiesDocumentFromDocument(selectedDoc));
        root = new DefaultTreeNode("root", null);
        buildTree(editedTree, root);
    }

    public void addNode() {
        TreeProperties editedTree = dataBean.addDocument(
                getPropertiesDocumentFromDocument(selectedDoc),
                getPropertiesDocumentFromDocument(tempDoc));
        resetTempDoc();
        selectedDoc.setDirectoryForAdding(false);
        root = new DefaultTreeNode("root", null);
        buildTree(editedTree, root);
    }

    private PropertiesDocument getPropertiesDocumentFromDocument(
            Document document) {
        return new PropertiesDocument(document.getAbsolutePath(),
                document.getName(), document.getValue(), document.getType(), document.getOldName(),
                document.isFile());
    }

    public void resetTempDoc() {
        tempDoc = new Document(null, "", "", "");
    }

    public List<TreeNode> searchByKeyName(String keyName, TreeNode node) {
        if (node.getChildren() != null) {
            for (TreeNode a : node.getChildren()) {
                Document document = (Document) a.getData();
                String documentName = document.getName();
                if (keyName.equals(documentName)) {
                    keyFolder.add(a.getParent());
                }
                searchByKeyName(keyName, a);
            }
        }
        return keyFolder;
    }

    public List<TreeNode> searchByKeyValue(String keyName, String keyValue,
            TreeNode node) {
        if (node.getChildren() != null) {
            for (TreeNode treeNode : node.getChildren()) {
                Document document = (Document) treeNode.getData();
                String documentName = document.getName();
                String documentValue = document.getValue();
                if (keyName.equals(documentName)
                        && (keyValue.equals(documentValue))) {
                    keyvsvakueFolder.add(treeNode.getParent());
                }
                searchByKeyValue(keyName, keyValue, treeNode);
            }
        }
        return keyvsvakueFolder;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRoot() {
        return root;
    }

    public Document getSelectedDoc() {
        return selectedDoc;
    }

    public void setSelectedDoc(Document selectedDoc) {
        this.selectedDoc = selectedDoc;
    }

    public Document getTempDoc() {
        return tempDoc;
    }

    public void setTempDoc(Document tempDoc) {
        this.tempDoc = tempDoc;
    }

    /**
     * Recursive method for collapse and expand a treeTable.
     * The parameter "node" is the node than you want to expand or collapse.
     * If the parameter "option" is false, all children of the "node" are collapsed.
     * If "option" == true - all children of the "node" are expanded.
     * setSelected(false) indicate than this node isn't selected
     * @param node
     * @param option 
     */
    public void collapsingORexpanding(TreeNode node, boolean option) {
        if (node.getChildren().isEmpty()) {
            node.setSelected(false);
        } else {
            for (TreeNode s : node.getChildren()) {
                collapsingORexpanding(s, option);
            }
            node.setExpanded(option);
            node.setSelected(false);
        }
    }
}