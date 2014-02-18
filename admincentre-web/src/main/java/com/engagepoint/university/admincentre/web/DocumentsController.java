package com.engagepoint.university.admincentre.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.preferences.NodePreferences;

@ManagedBean(name = "documentsController")
@SessionScoped
public class DocumentsController implements Serializable {

    private static final Logger logger = Logger.getLogger(DocumentsController.class.getName());
    private TreeNode root;
    private Document selectedDoc = new Document();
    private TreeNode selectedNode;

    // @Inject
    // DataBean dataBean;

    @PostConstruct
    private void init() {
        NodePreferences preferences = new NodePreferences(null, "");
        // TreeProperties treeProperties =
        // dataBean.getPreferencesTree(preferences);
        root = new DefaultTreeNode("root", null);

        buildTree(preferences, root);
    }

    private void buildTree(NodePreferences preferences, TreeNode parentTreeNode) {

        TreeNode treeNode = new DefaultTreeNode(new Document(preferences.absolutePath(),
                preferences.name(), "-", "File"), parentTreeNode);

        addLeaves(preferences, treeNode);
        try {
            if (preferences.childrenNames().length != 0) {
                for (int i = 0; i < preferences.childrenNames().length; i++) {
                    buildTree((NodePreferences) preferences.node(preferences.childrenNames()[i]),
                            treeNode);
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
                new DefaultTreeNode(new Document(nodePreferences.absolutePath(), key.getName(),
                        key.getValue(), key.getType().toString()), parentTreeNode);
            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public DocumentsController() {

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

    public void editDocument(ActionEvent event) {
    	 if (selectedDoc != null) {
            String absPath = selectedDoc.getAbsolutePath();
            NodePreferences currentNode= (NodePreferences) new NodePreferences(null, "").node(absPath);        
            if ("File".equals(selectedDoc.getType())) {
                selectedNode = getNodeByDoc(selectedDoc.getName(), root);
                selectedNode.getParent().getChildren().remove(selectedNode);
                // selectedNode.getParent().getChildren().remove(selectedNode);
                currentNode.changeNodeName(selectedDoc.getName());
                selectedDoc.setAbsolutePath(currentNode.absolutePath());
                buildTree(currentNode, selectedNode.getParent());
            	
            } else {
                try {
                	currentNode.put(
                            selectedDoc.getName(), KeyType.valueOf(selectedDoc.getType()),
                            selectedDoc.getValue());
                	currentNode.remove(selectedDoc.getOldName());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteNode() {
        if (selectedDoc != null) {
        selectedNode = getNodeByDoc(selectedDoc.getName(), root);
        selectedNode.getParent().getChildren().remove(selectedNode);
        String absPath = selectedDoc.getAbsolutePath();
        if ("File".equals(selectedDoc.getType())) {
            try {
                new NodePreferences(null, "").node(absPath).removeNode();
            } catch (BackingStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            new NodePreferences(null, "").node(absPath).remove(selectedDoc.getName());
        }
        }
    }

    public void addNode() {
        // selectedNode = getNodeByDoc(selectedDoc.getName(), root);
        // selectedNode.toString();
        //
        // TreeNode newNode;
        // if (selectedDoc != null)
        // newNode = new DefaultTreeNode(new Document("New Node", "-", "-"),
        // selectedNode);
        // else
        // newNode = new DefaultTreeNode(new Document("New Node", "-", "-"),
        // root);
        // Document doc1 = (Document) selectedNode.getData();
        // doc1.saveAction();
        // selectedNode.setSelected(false);
        // selectedNode.setExpanded(true);
        // newNode.setSelected(true);
        // Document doc = (Document) newNode.getData();
        // doc.setEditable(true);
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
                System.out.println("KeyName = " + keyName + "; KeyValue = " + keyValue
                        + " lies in such folders: ");
                Document document = (Document) a.getData();
                System.out.println(document.getName());
            }
        } else {
            if (keyName != null) {
                searchByKeyName(keyName, root);
                for (TreeNode a : keyFolder) {
                    System.out.println("KeyName = " + keyName + "; KeyValue = " + keyValue
                            + " lies in such folders: ");
                    Document document = (Document) a.getData();
                    System.out.println(document.getName());
                }
            }
        }
        return null;
    }
    //
    //
    // public static void main(String[] args) {
    // DocumentsController documentsController = new DocumentsController();
    // documentsController.buttonSearch("Key3.1.3.1", "0.12345");
    // }

}
