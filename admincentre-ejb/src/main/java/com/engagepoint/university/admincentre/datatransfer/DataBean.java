package com.engagepoint.university.admincentre.datatransfer;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.ejb.Stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.entity.PropertiesDocument;
import com.engagepoint.university.admincentre.entity.TreeProperties;
import com.engagepoint.university.admincentre.preferences.NodePreferences;

@Stateful
public class DataBean {

    private TreeProperties root;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBean.class.getName());

    public TreeProperties getPreferencesTree() {
        root = new TreeProperties("root", null);
        Preferences preferences = new NodePreferences(null, "");
        buildTree((NodePreferences) preferences, root);
        return root;
    }

    private void buildTree(NodePreferences preferences, TreeProperties parentTreeNode) {
        TreeProperties treeNode = new TreeProperties(new PropertiesDocument(preferences
                .getCurrentNode().getId(), preferences.name(), "-", "File"), parentTreeNode);

        addLeaves(preferences, treeNode);
        try {
            if (preferences.childrenNames().length != 0) {
                for (int i = 0; i < preferences.childrenNames().length; i++) {
                    buildTree((NodePreferences) preferences.node(preferences.childrenNames()[i]),
                            treeNode);
                }
            }
        } catch (BackingStoreException backingStoreException) {
            LOGGER.warn("Tree building exception", backingStoreException);
        }
    }

    private void addLeaves(NodePreferences nodePreferences, TreeProperties parentTreeNode) {
        try {
            for (int i = 0; i < nodePreferences.keys().length; i++) {
                Key key = nodePreferences.getKey(nodePreferences.keys()[i]);
                new TreeProperties(new PropertiesDocument(key.getParentNodeId(), key.getName(),
                        key.getValue(), key.getType().toString()), parentTreeNode);
            }
        } catch (BackingStoreException e) {
            LOGGER.error("error during constructing tree on key from node: "
                    + nodePreferences.name());
        } catch (IOException e) {
            LOGGER.error("error during constructing tree on reading key from node: "
                    + nodePreferences.name());
        }
    }

    public TreeProperties editDocument(PropertiesDocument selectedDocument) {
        if (selectedDocument != null) {
            String absPath = selectedDocument.getAbsolutePath();
            NodePreferences currentNode = (NodePreferences) new NodePreferences(null, "")
                    .node(absPath);

            if (selectedDocument.isFile()) {
                TreeProperties selectedNode = getNodeByDoc(selectedDocument, root);
                selectedNode.getParent().getChildren().remove(selectedNode);
                currentNode.changeNodeName(selectedDocument.getName());
                selectedDocument.setAbsolutePath(currentNode.absolutePath());
            } else {
                try {
                    currentNode.remove(selectedDocument.getOldName());
                    currentNode.put(selectedDocument.getName(),
                            KeyType.valueOf(selectedDocument.getType()),
                            selectedDocument.getValue());
                } catch (IOException iOException) {
                    LOGGER.error(this.getClass().getName(),
                            "public void editDocument(ActionEvent event)", iOException);
                }
            }
        }
        return getPreferencesTree();
    }

    public TreeProperties deleteDocument(PropertiesDocument selectedDocument) {
        if (selectedDocument != null) {
            TreeProperties selectedNode = getNodeByDoc(selectedDocument, root);
            selectedNode.getParent().getChildren().remove(selectedNode);
            String absPath = selectedDocument.getAbsolutePath();
            if (selectedDocument.isFile()) {
                try {
                    new NodePreferences(null, "").node(absPath).removeNode();
                } catch (BackingStoreException backingStoreException) {
                    LOGGER.error(DataBean.class.getName(), "public void deleteNode()",
                            backingStoreException);
                }
            } else {
                new NodePreferences(null, "").node(absPath).remove(selectedDocument.getName());
            }
        }
        return root;
    }

    public TreeProperties addDocument(PropertiesDocument selectedDocument,
            PropertiesDocument temporaryDocument) {
        selectedDocument.setDirectoryForAdding(true);
        String newName = temporaryDocument.getName();
        String selectedAbsolutePath = selectedDocument.getAbsolutePath();
        String path;

        if (temporaryDocument.isFile()) {
            path = "/".equals(selectedAbsolutePath) ? "/" + newName : selectedAbsolutePath + "/"
                    + newName;
            new NodePreferences(null, "").node(path);
            new TreeProperties(new PropertiesDocument(path, newName, "-", "File",
                    temporaryDocument.isFile()), returnDirectoryForAdding(root));
        } else {
            path = selectedAbsolutePath;
            try {
                ((NodePreferences) new NodePreferences(null, "").node(path)).put(newName,
                        KeyType.valueOf(temporaryDocument.getType()), temporaryDocument.getValue());

            } catch (IOException e) {
                LOGGER.warn("Cannot complete this:\n"
                        + ".put(newName, KeyType.valueOf(temporaryDocument.getType()), temporaryDocument.getValue());\n }", e);
            }
            new TreeProperties(new PropertiesDocument(path, newName, temporaryDocument.getValue(),
                    temporaryDocument.getType(), temporaryDocument.isFile()),
                    returnDirectoryForAdding(root));
        }
        return getPreferencesTree();
    }

    public TreeProperties getNodeByDoc(PropertiesDocument document, TreeProperties root) {
        TreeProperties foundTree = null;
        for (TreeProperties treeNode : root.getChildren()) {
            PropertiesDocument currentDocument = (PropertiesDocument) treeNode.getData();

            if (document.equals(currentDocument)) {
                return treeNode;
            } else {
                foundTree = getNodeByDoc(document, treeNode);
            }
        }
        return foundTree;
    }

    private TreeProperties returnDirectoryForAdding(TreeProperties rootNode) {
        TreeProperties selectedNode = null;
        for (TreeProperties treeNode : rootNode.getChildren()) {
            if (((PropertiesDocument) treeNode.getData()).isDirectoryForAdding()) {
                selectedNode = treeNode;
            }
            returnDirectoryForAdding(treeNode);
        }
        return selectedNode;
    }

    public TreeProperties getRoot() {
        return root;
    }

    public void setRoot(TreeProperties root) {
        this.root = root;
    }
}
