package com.engagepoint.university.admincentre.web;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.engagepoint.component.menu.UIMenuItem;
import com.engagepoint.component.menu.UIMenuSection;
import com.engagepoint.component.menu.model.DefaultMenuModel;
import com.engagepoint.component.menu.model.MenuModel;
import com.engagepoint.university.admincentre.exception.SynchronizationException;
import com.engagepoint.university.admincentre.synch.Synch;

@ManagedBean(name = "leftMenu")
@RequestScoped
public class LeftMenuBean {
	
	@EJB
	private Synch synch;
	
	private String clusterName;
	
	public String getClusterName() {
		return clusterName;
	}
	
	public void setClusterName(String clusterName) {
		try {
			synch.autoConnect(clusterName);
			this.clusterName = clusterName;
		} catch (IllegalStateException e) {
		} catch (SynchronizationException e) {
		}
	}
	
	public String getInfo(){
		if(synch.isConnected()){
			return "Connected. Channel name: " + synch.getChannelName()
					+ "; Cluster name: " + synch.getClusterName();
		}else{
			return "Disconnected.";
		}
	}
	
	private MenuModel model;

	@PostConstruct
	 public void init() {

		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
	    String viewId = viewRoot.getViewId();
		
	    if(viewId.endsWith("/editor.xhtml")){
	    	editorMenu();
	    }else if(viewId.endsWith("/settings.xhtml")){
	    	settingsMenu();
	    }else if(viewId.endsWith("/import_export.xhtml")){
	    	settingsMenu();
	    }

	}

	private void editorMenu(){
		model = new DefaultMenuModel();

		// menuItem creation

		UIMenuItem item = new UIMenuItem();
		item.setId("view_table");
		item.setValue("View_table");
		item.setUrl("#");
		item.setTitle("View table");

		model.addMenuItem(item);

		
		item = new UIMenuItem();
		item.setId("search");
		item.setValue("Search");
		item.setUrl("#");
		item.setTitle("Search");
		
		model.addMenuItem(item);
	}
	
	private void settingsMenu(){
		model = new DefaultMenuModel();

		// menuItem creation

		UIMenuItem item = new UIMenuItem();
		item.setId("synch");
		item.setValue("Synch");
		item.setUrl("/pages/settings.xhtml");
		item.setTitle("Synchronization");

		model.addMenuItem(item);

		
		item = new UIMenuItem();
		item.setId("import_export");
		item.setValue("Import_export");
		item.setUrl("/pages/import_export.xhtml");
		item.setTitle("Import/Export");
		
		model.addMenuItem(item);
                
	}
	
	public MenuModel getModel() {
		return model;
	}

	public void setModel(MenuModel model) {
		this.model = model;
	}
	
//	public String autoConnect(){
//		try {
//			return "leftMenu";
//		} catch (IllegalStateException e) {
//			return "";
//		} catch (SynchronizationException e) {
//			return "";
//		}
//	}
}
