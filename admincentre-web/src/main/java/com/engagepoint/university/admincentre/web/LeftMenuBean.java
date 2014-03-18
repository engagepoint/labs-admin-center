package com.engagepoint.university.admincentre.web;

import com.engagepoint.component.menu.UIMenuItem;
import com.engagepoint.component.menu.model.DefaultMenuModel;
import com.engagepoint.component.menu.model.MenuModel;
import com.engagepoint.university.admincentre.datatransfer.DataBean;
import com.engagepoint.university.admincentre.synch.Synch;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name = "leftMenu")
@RequestScoped
public class LeftMenuBean {

    public static final String EDITOR_URL = "/editor.xhtml";
    public static final String SETTINGS_URL = "/settings.xhtml";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBean.class.getName());
    @EJB
    private Synch synch;
    private String clusterName;
    private MenuModel model;

    @PostConstruct
    public void init() {

        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        String viewId = viewRoot.getViewId();

        if (viewId.endsWith(EDITOR_URL)) {
            editorMenu();
        } else if (viewId.endsWith(SETTINGS_URL)) {
            settingsMenu();
        }
    }

    public String getInfo() {
        if (synch.isConnected()) {
            return "Connected. Channel name: " + synch.getChannelName()
                    + "; Cluster name: " + synch.getClusterName();
        } else {
            return "Disconnected.";
        }
    }

    private void editorMenu() {
        model = new DefaultMenuModel();
        UIMenuItem item = new UIMenuItem();
        item.setId("view_table");
        item.setValue("View_table");
        item.setUrl("/pages/editor.xhtml");
        item.setTitle("View table");
        model.addMenuItem(item);
    }

    private void settingsMenu() {
        model = new DefaultMenuModel();
        UIMenuItem item = new UIMenuItem();
        item.setId("import_export");
        item.setValue("Import_export");
        item.setUrl("/pages/settings.xhtml");
        item.setTitle("Import/Export");
        item.setActiveLinkSelection(true);
        model.addMenuItem(item);
    }

    public MenuModel getModel() {
        return model;
    }

    public void setModel(MenuModel model) {
        this.model = model;
    }
}
