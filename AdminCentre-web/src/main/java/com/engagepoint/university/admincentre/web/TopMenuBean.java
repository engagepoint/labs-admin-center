package com.engagepoint.university.admincentre.web;

import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.primefaces.component.menuitem.MenuItem;

@ManagedBean(name = "topMenu")
@RequestScoped
public class TopMenuBean {

    private static final String ACTIVE = "active";
    public static final String EDITOR_URL = "/pages/editor.xhtml";
    public static final String SETTINGS_URL = "/pages/settings.xhtml";
    private MenuModel model;        
    
    @PostConstruct
    public void initModel() {
        model = new DefaultMenuModel();
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        String viewId = viewRoot.getViewId();
        model.addMenuItem(getEditorItem(viewId, null));
        model.addMenuItem(getSettingsItem(viewId, null));
    }
    
    public static MenuItem getEditorItem(String viewId, String itemId) {
        MenuItem item = getMenuItem(viewId, itemId, "Editor", EDITOR_URL);
        if (EDITOR_URL.equals(viewId)) {
            item.setStyleClass(ACTIVE);
        }
        return item;
    }
    
    public static MenuItem getSettingsItem(String viewId, String itemId) {
        MenuItem item = getMenuItem(viewId, itemId, "Settings", SETTINGS_URL);
        if (SETTINGS_URL.equals(viewId)) {
            item.setStyleClass(ACTIVE);
        }
        return item;
    }
    
    public static MenuItem getMenuItem(String viewId, String itemId, String itemName, String url) {
        MenuItem item = new MenuItem();
        item.setValue(itemName);
        item.setUrl(url);
        if (itemId != null) {
            item.setId(itemId);
        }
        item.setIncludeViewParams(true);
        return item;
    }

    public MenuModel getModel() {
        return model;
    }

}
