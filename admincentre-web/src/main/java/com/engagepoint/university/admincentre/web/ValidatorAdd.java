package com.engagepoint.university.admincentre.web;

import java.text.MessageFormat;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aleksey.korotysh on 13.03.14.
 */
@Named
public class ValidatorAdd {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorAdd.class.getName());

    protected static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            LOGGER.error(MessageFormat.format("Can not parse value {0} to Double", str), e);
            return false;
        }
        return true;
    }

    public static void clearInputs(UIComponent uiComponent, UIComponent parent) {
        ((HtmlInputText) uiComponent).resetValue();
        UIComponent sone2 = parent.findComponent("inputName2");
        HtmlInputText htmlInputText2 = (HtmlInputText) sone2;
        htmlInputText2.resetValue();
    }

    public void validateAlpha(FacesContext facesContext, UIComponent uiComponent, Object value) {
        String selectedValue;
        UIComponent parent = uiComponent.getParent();
        if (null != parent) {
            UIComponent sone = parent.findComponent("selectTypeMenu2");
            if (null != sone) {
                LOGGER.debug("----------SELECTED------------------------------------- {} ---------------------", parent.getId());
                SelectOneMenu selectOneMenu = (SelectOneMenu) sone;
                selectedValue = (String) selectOneMenu.getSubmittedValue();
                LOGGER.debug("++++++++++++++++++++++++++SELECTED VALUE++++++++++++++++++++++++++++++++++ " + selectedValue);

                if ("Integer".equals(selectedValue)) {
                    if (!StringUtils.isNumeric((String) value)) {
                        HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                        FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Integer Number");
                        clearInputs(uiComponent, parent);
                        throw new ValidatorException(facesMessage);
                    } else {
                        if (("Double".equals(selectedValue) || "Float".equals(selectedValue)) && (!isDouble((String) value))) {
                            HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                            FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Floating-Point Number");
                            clearInputs(uiComponent, parent);
                            throw new ValidatorException(facesMessage);
                        }
                    }
                } else {
                    if (("Double".equals(selectedValue) || "Float".equals(selectedValue)) && (!isDouble((String) value))) {
                        HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                        FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Floating-Point Number");
                        clearInputs(uiComponent, parent);
                        throw new ValidatorException(facesMessage);
                    }
                }
            }
        }
    }
}
