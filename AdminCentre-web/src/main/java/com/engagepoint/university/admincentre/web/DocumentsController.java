package com.engagepoint.university.admincentre.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "documentsController")
@SessionScoped
public class DocumentsController implements Serializable {

    private static final Logger logger = Logger.getLogger(DocumentsController.class.getName());
    private TreeNode root;
    private Document selectedDoc = new Document();
    private TreeNode selectedNode;

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }


    public DocumentsController() {

        root = new DefaultTreeNode("root", null);

        //Nodes

        TreeNode file1 = new DefaultTreeNode(new Document("/File1", "-", "File"), root);
        TreeNode file2 = new DefaultTreeNode(new Document("/File2", "-", "File"), root);
        TreeNode file3 = new DefaultTreeNode(new Document("/File3", "-", "File"), root);

        TreeNode file2_1 = new DefaultTreeNode(new Document("/File2/File2.1", "-", "File"), file2);
        TreeNode file2_2 = new DefaultTreeNode(new Document("/File2/File2.2", "-", "File"), file2);

        TreeNode file2_1_1 = new DefaultTreeNode(new Document("File2.1.1", "-", "File"), file2_1);
        TreeNode file2_2_1 = new DefaultTreeNode(new Document("File2.1.1", "-", "File"), file2_2);

        //SubNodes of file1
        TreeNode subFile1_1 = new DefaultTreeNode(new Document("SubFile1.1", "-", "SubFile"), file1);
        TreeNode subFile1_2 = new DefaultTreeNode(new Document("SubFile1.2", "-", "SubFile"), file1);

        //Keys in file1 including the file1's subfiles
        TreeNode key1_1_1 = new DefaultTreeNode(new Document("Key1.1.1", "key1.1 value", "String"), subFile1_1);
        TreeNode key1_1_2 = new DefaultTreeNode(new Document("Key1.1.2", "true", "Boolean"), subFile1_1);
        TreeNode key1_2_1 = new DefaultTreeNode(new Document("Key1.2.1", "123456789", "Int"), subFile1_2);

        //Keys in file2
        TreeNode key2_1 = new DefaultTreeNode(new Document("Key2.1", "??? view of Byte Array", "Byte[]"), file2);
        TreeNode key2_2 = new DefaultTreeNode(new Document("Key2.2", "9999999999.9", "Double"), file2);
        TreeNode key2_3 = new DefaultTreeNode(new Document("Key2.3", "0.123", "Float"), file2);

        //file3
        TreeNode subFile3_1 = new DefaultTreeNode(new Document("SubFile3.1", "-", "SubFile"), file3);
        TreeNode subFile3_1_1 = new DefaultTreeNode(new Document("SubFile3.1.1", "-", "SubFile"), subFile3_1);
        TreeNode subFile3_1_2 = new DefaultTreeNode(new Document("SubFile3.1.2", "-", "SubFile"), subFile3_1);
        TreeNode subFile3_1_3 = new DefaultTreeNode(new Document("SubFile3.1.3", "-", "SubFile"), subFile3_1);

        TreeNode key3_1_1_1 = new DefaultTreeNode(new Document("Key3.1.1.1", "9999999999", "Long"), subFile3_1_1);
        TreeNode key3_1_1_2 = new DefaultTreeNode(new Document("Key3.1.1.2", "123456789", "Int"), subFile3_1_1);

        TreeNode key3_1_2_1 = new DefaultTreeNode(new Document("Key3.1.2.1", "James Gosling", "String"), subFile3_1_2);
        TreeNode key3_1_2_2 = new DefaultTreeNode(new Document("Key3.1.2.2", "Linus Torvalds", "String"), subFile3_1_2);

        TreeNode key3_1_3_1 = new DefaultTreeNode(new Document("Key3.1.3.1", "0.12345", "Float"), subFile3_1_3);
        TreeNode key3_1_3_2 = new DefaultTreeNode(new Document("Key3.1.3.2", "0.123456789", "Double"), subFile3_1_3);
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

    public void deleteNode() {
        selectedNode = getNodeByDoc(selectedDoc.getName(), root);
        selectedNode.getParent().getChildren().remove(selectedNode);
    }

    public void addNode() {
//        selectedNode = getNodeByDoc(selectedDoc.getName(), root);
//        selectedNode.toString();
//
//        TreeNode newNode;
//        if (selectedDoc != null)
//            newNode = new DefaultTreeNode(new Document("New Node", "-", "-"), selectedNode);
//        else
//            newNode = new DefaultTreeNode(new Document("New Node", "-", "-"), root);
//        Document doc1 = (Document) selectedNode.getData();
//        doc1.saveAction();
//        selectedNode.setSelected(false);
//        selectedNode.setExpanded(true);
//        newNode.setSelected(true);
//        Document doc = (Document) newNode.getData();
//        doc.setEditable(true);
    }

    public TreeNode getNodeByDoc(String name, TreeNode root) {
        for (TreeNode treeNode : root.getChildren()) {
            Document document = (Document) treeNode.getData();
            String documentName = document.getName();
            if (documentName.equals(name)) {
                selectedNode = treeNode;
            }
            getNodeByDoc(name, treeNode);
        }
        return selectedNode;
    }

    static List<TreeNode> keyFolder = new ArrayList<TreeNode>();
    static List<TreeNode> keyvsvakueFolder = new ArrayList<TreeNode>();

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


    public List<TreeNode> searchByKeyValue(String keyName, String keyValue, TreeNode node) {
        if (node.getChildren() != null) {
            for (TreeNode treeNode : node.getChildren()) {
                Document document = (Document) treeNode.getData();
                String documentName = document.getName();
                String documentValue = document.getValue();
                if (keyName.equals(documentName) && (keyValue.equals(documentValue))) {
                    keyvsvakueFolder.add(treeNode.getParent());
                }
                searchByKeyValue(keyName, keyValue, treeNode);
            }
        }

        return keyvsvakueFolder;
    }

    public List<TreeNode> buttonSearch(String keyName, String keyValue) {
        if (keyValue != null) {
            for (TreeNode a : searchByKeyValue(keyName, keyValue, root)) {
                System.out.println("KeyName = " + keyName + "; KeyValue = " + keyValue + " lies in such folders: ");
                Document document = (Document) a.getData();
                System.out.println(document.getName());
            }
        } else {
            if (keyName != null) {
                searchByKeyName(keyName, root);
                for (TreeNode a : keyFolder) {
                    System.out.println("KeyName = " + keyName + "; KeyValue = " + keyValue + " lies in such folders: ");
                    Document document = (Document) a.getData();
                    System.out.println(document.getName());
                }
            }
        }
        return null;
    }
//
//
//    public static void main(String[] args) {
//        DocumentsController documentsController = new DocumentsController();
//        documentsController.buttonSearch("Key3.1.3.1", "0.12345");
//    }
}
