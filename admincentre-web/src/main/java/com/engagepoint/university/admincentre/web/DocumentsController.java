package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.datatransfer.DataBean;
import com.engagepoint.university.admincentre.entity.PropertiesDocument;
import com.engagepoint.university.admincentre.entity.TreeProperties;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "documentsController")
@ViewScoped
public class DocumentsController implements Serializable {

    private static final long serialVersionUID = 123L;
    private static List<TreeNode> keyFolder = new ArrayList<TreeNode>(100);
    private static List<TreeNode> keyDocument = new ArrayList<TreeNode>(100);
    private TreeNode root;
    private Document selectedDoc = new Document();
    private TreeNode selectedNode;
    private Document tempDoc = new Document(null, "", "", "");
    @Inject
    DataBean dataBean;
    private String searchKeyName;
    private String searchKeyValue;
    private TreeNode searchNode;

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

    public void search() {
        init();
        root = filterRootTree(root);
    }

    public TreeNode filterRootTree(TreeNode inputNode) {
        List<TreeNode> children = inputNode.getChildren();
        int size = children.size();
        for (int i = 0; i < size; i++) {
            TreeNode treeNode = children.get(i);
            if (children.get(i).getChildren().isEmpty()) {
                String keyName = ((Document) (treeNode.getData())).getName();
                String keyValue = ((Document) (treeNode.getData())).getValue();
                if (!isSuitableNode(keyName, keyValue)) {
                    children.get(i).setParent(null);
                    i--;
                    size = children.size();
                }
            } else {
                filterRootTree(children.get(i));
                if (children.get(i).getChildren().isEmpty()) {
                    children.get(i).setParent(null);
                    i--;
                    size = children.size();
                }
            }
        }
        return inputNode;
    }

    private boolean isSuitableNode(String keyName, String keyValue) {
        return (StringUtils.isEmpty(searchKeyName) && StringUtils.isEmpty(searchKeyValue))
                || (StringUtils.isEmpty(searchKeyName) && keyValue.toUpperCase().contains(searchKeyValue.toUpperCase()))
                || (StringUtils.isEmpty(searchKeyValue) && keyName.toUpperCase().contains(searchKeyName.toUpperCase()))
                || ((StringUtils.isNotEmpty(searchKeyName) && keyName.toUpperCase().contains(searchKeyName.toUpperCase()))
                && (StringUtils.isNotEmpty(searchKeyValue) && keyValue.toUpperCase().contains(searchKeyValue.toUpperCase())));
    }

    public static List<TreeNode> getKeyFolder() {
        return keyFolder;
    }

    public static void setKeyFolder(List<TreeNode> keyFolder) {
        DocumentsController.keyFolder = keyFolder;
    }

    public static List<TreeNode> getKeyDocument() {
        return keyDocument;
    }

    public static void setKeyDocument(List<TreeNode> keyDocument) {
        DocumentsController.keyDocument = keyDocument;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public Document getSelectedDoc() {
        return selectedDoc;
    }

    public void setSelectedDoc(Document selectedDoc) {
        this.selectedDoc = selectedDoc;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Document getTempDoc() {
        return tempDoc;
    }

    public void setTempDoc(Document tempDoc) {
        this.tempDoc = tempDoc;
    }

    public DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public TreeNode getSearchNode() {
        return searchNode;
    }

    public void setSearchNode(TreeNode searchNode) {
        this.searchNode = searchNode;
    }

    /**
     * Recursive method for collapse and expand a treeTable. The parameter
     * "node" is the node than you want to expand or collapse. If the parameter
     * "option" is false, all children of the "node" are collapsed. If "option"
     * == true - all children of the "node" are expanded. setSelected(false)
     * indicate than this node isn't selected
     *
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

    public String getSearchKeyName() {
        return searchKeyName;
    }

    public void setSearchKeyName(String searchKeyName) {
        this.searchKeyName = searchKeyName;
    }

    public String getSearchKeyValue() {
        return searchKeyValue;
    }

    public void setSearchKeyValue(String searchKeyValue) {
        this.searchKeyValue = searchKeyValue;
    }
}
