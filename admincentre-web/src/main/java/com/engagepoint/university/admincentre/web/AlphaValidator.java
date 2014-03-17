package com.engagepoint.university.admincentre.web;

import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 * Created by aleksey.korotysh on 13.03.14.
 */
@ManagedBean
public class AlphaValidator {

    @Inject
    DocumentsController documentsController;


    public void validateAlpha(FacesContext facesContext, UIComponent
            uiComponent, Object value) throws ValidatorException {
        String selectedValue;
        UIComponent parent = uiComponent.getParent();
        if(null!=parent){
        UIComponent sone = parent.findComponent("selectTypeMenu2");
        if(null!=sone){
            System.out.println("----------SELECTED-------------------"+parent.getId()+"----------------------------------------------");
        SelectOneMenu selectOneMenu=(SelectOneMenu)sone;
        selectedValue = (String) selectOneMenu.getSubmittedValue();
            System.out.println("++++++++++++++++++++++++++SELECTED VALUE++++++++++++++++++++++++++++++++++"+selectedValue);
            if (selectedValue.equals("Integer")) {
                if (!StringUtils.isNumeric((String) value)) {
                    HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                    FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Integer number");
                    throw new ValidatorException(facesMessage);
                }
            }
            else if (selectedValue.equals("Double") || selectedValue.equals("Float")) {
                if (!StringUtils.isNumeric((String) value)) {
                    HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                    FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Integer number");
                    throw new ValidatorException(facesMessage);
                }
            }
        }
        }


//       else if (!StringUtils.isNumeric((String) value)) {
//            HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
//            FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": only numbers must be .");
//            throw new ValidatorException(facesMessage);
//    }
}
}